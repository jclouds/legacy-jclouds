/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.virtualbox.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.List;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.callables.RunScriptOnNode;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.logging.Logger;
import org.jclouds.net.IPSocket;
import org.jclouds.ssh.SshClient;
import org.jclouds.virtualbox.domain.BridgedIf;
import org.jclouds.virtualbox.statements.GetIPAddressFromMAC;
import org.jclouds.virtualbox.statements.ScanNetworkWithPing;
import org.jclouds.virtualbox.util.MachineUtils;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.INetworkAdapter;
import org.virtualbox_4_1.NetworkAttachmentType;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

@Singleton
public class IMachineToSshClient implements Function<IMachine, SshClient> {

	@Resource
	@Named(ComputeServiceConstants.COMPUTE_LOGGER)
	protected Logger logger = Logger.NULL;

	private final SshClient.Factory sshClientFactory;
	private final RunScriptOnNode.Factory scriptRunnerFactory;
	private final Supplier<NodeMetadata> hostSupplier;
	private final MachineUtils machineUtils;

	@Inject
	public IMachineToSshClient(SshClient.Factory sshClientFactory,
			RunScriptOnNode.Factory scriptRunnerFactory,
			Supplier<NodeMetadata> hostSupplier, MachineUtils machineUtils) {
		this.sshClientFactory = sshClientFactory;
		this.scriptRunnerFactory = scriptRunnerFactory;
		this.hostSupplier = hostSupplier;
		this.machineUtils = machineUtils;
	}

	@Override
	public SshClient apply(final IMachine vm) {
		INetworkAdapter networkAdapter = vm.getNetworkAdapter(0L);

		SshClient client = null;
		checkNotNull(networkAdapter);

		String clientIpAddress = null;
		String sshPort = "22";

		// TODO: we need a way to align the default login credentials
		// from the iso with the vmspec -> IMachineToNodeMetadata using YamlImage ?
		LoginCredentials loginCredentials = LoginCredentials.builder()
				.user("toor").password("password").authenticateSudo(true)
				.build();

		if (networkAdapter.getAttachmentType()
				.equals(NetworkAttachmentType.NAT)) {
			for (String nameProtocolnumberAddressInboudportGuestTargetport : networkAdapter
					.getNatDriver().getRedirects()) {
				Iterable<String> stuff = Splitter.on(',').split(
						nameProtocolnumberAddressInboudportGuestTargetport);
				String protocolNumber = Iterables.get(stuff, 1);
				String hostAddress = Iterables.get(stuff, 2);
				String inboundPort = Iterables.get(stuff, 3);
				String targetPort = Iterables.get(stuff, 5);
				if ("1".equals(protocolNumber) && "22".equals(targetPort)) {
					clientIpAddress = hostAddress;
					sshPort = inboundPort;
				}
			}
		} else if (networkAdapter.getAttachmentType().equals(
				NetworkAttachmentType.Bridged)) {
			String network = "1.1.1.1";
			clientIpAddress = getIpAddressFromBridgedNIC(networkAdapter, network);
		} else if (networkAdapter.getAttachmentType().equals(
                        NetworkAttachmentType.HostOnly)) {
	             clientIpAddress = machineUtils.getIpAddressFromHostOnlyNIC(vm.getName());
		}
		
		checkNotNull(clientIpAddress, "clientIpAddress");
		client = sshClientFactory.create(
				new IPSocket(clientIpAddress, Integer.parseInt(sshPort)),
				loginCredentials);
		checkNotNull(client);
		return client;
	}

	private String getIpAddressFromBridgedNIC(INetworkAdapter networkAdapter,
			String network) {
		// RetrieveActiveBridgedInterfaces
		List<BridgedIf> activeBridgedInterfaces = new RetrieveActiveBridgedInterfaces(scriptRunnerFactory).apply(hostSupplier.get());
		BridgedIf activeBrigedIf = checkNotNull(Iterables.get(activeBridgedInterfaces, 0), "activeBridgrdIf");
		network = activeBrigedIf.getIpAddress();
		
		// scan ip
		RunScriptOnNode ipScanRunScript = scriptRunnerFactory.create(
				hostSupplier.get(), new ScanNetworkWithPing(network),
				RunScriptOptions.NONE);
		ExecResponse execResponse = ipScanRunScript.init().call();
		checkState(execResponse.getExitStatus() == 0);

		// retrieve ip from mac
		RunScriptOnNode getIpFromMACAddressRunScript = scriptRunnerFactory
				.create(hostSupplier.get(), new GetIPAddressFromMAC(
						networkAdapter.getMACAddress()),
						RunScriptOptions.NONE);
		ExecResponse ipExecResponse = getIpFromMACAddressRunScript.init()
				.call();
		checkState(ipExecResponse.getExitStatus() == 0);
		return checkNotNull(ipExecResponse.getOutput(), "ipAddress");
	}
}