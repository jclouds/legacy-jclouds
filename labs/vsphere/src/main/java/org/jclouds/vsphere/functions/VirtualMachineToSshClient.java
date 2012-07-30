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
package org.jclouds.vsphere.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.logging.Logger;
import org.jclouds.ssh.SshClient;

import com.google.common.base.Function;
import com.google.common.net.HostAndPort;
import com.google.inject.Inject;
import com.vmware.vim25.VirtualMachineToolsStatus;
import com.vmware.vim25.mo.VirtualMachine;

@Singleton
public class VirtualMachineToSshClient implements Function<VirtualMachine, SshClient> {

	@Resource
	@Named(ComputeServiceConstants.COMPUTE_LOGGER)
	protected Logger logger = Logger.NULL;

	private final SshClient.Factory sshClientFactory;

	@Inject
	public VirtualMachineToSshClient(SshClient.Factory sshClientFactory) {
		this.sshClientFactory = sshClientFactory;
	}

	@Override
	public SshClient apply(final VirtualMachine vm) {
		SshClient client = null;
		String clientIpAddress = vm.getGuest().getIpAddress();
		String sshPort = "22";
		while(!vm.getGuest().getToolsStatus().equals(VirtualMachineToolsStatus.toolsOk)) {
               try {
                  Thread.sleep(1000l);
               } catch (InterruptedException e) {
                  logger.error("Problem in waiting vmware tools", e);
               }
		   clientIpAddress = vm.getGuest().getIpAddress();
		}
		LoginCredentials loginCredentials = LoginCredentials.builder()
				.user("toor").password("password").authenticateSudo(true)
				.build();
		checkNotNull(clientIpAddress, "clientIpAddress");
		client = sshClientFactory.create(
				HostAndPort.fromParts(clientIpAddress, Integer.parseInt(sshPort)),
				loginCredentials);
		checkNotNull(client);
		return client;
	}

}