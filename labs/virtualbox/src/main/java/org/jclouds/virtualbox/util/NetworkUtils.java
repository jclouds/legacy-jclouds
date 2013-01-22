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
package org.jclouds.virtualbox.util;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.compute.options.RunScriptOptions.Builder.runAsRoot;

import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.util.concurrent.Uninterruptibles;
import org.jclouds.compute.callables.RunScriptOnNode;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.location.Provider;
import org.jclouds.logging.Logger;
import org.jclouds.scriptbuilder.domain.Statements;
import org.jclouds.virtualbox.domain.BridgedIf;
import org.jclouds.virtualbox.domain.NetworkAdapter;
import org.jclouds.virtualbox.domain.NetworkInterfaceCard;
import org.jclouds.virtualbox.domain.NetworkSpec;
import org.jclouds.virtualbox.functions.IpAddressesLoadingCache;
import org.jclouds.virtualbox.functions.RetrieveActiveBridgedInterfaces;
import org.jclouds.virtualbox.statements.EnableNetworkInterface;
import org.jclouds.virtualbox.statements.GetIPAddressFromMAC;
import org.jclouds.virtualbox.statements.ScanNetworkWithPing;
import org.virtualbox_4_2.HostNetworkInterfaceType;
import org.virtualbox_4_2.IDHCPServer;
import org.virtualbox_4_2.IHostNetworkInterface;
import org.virtualbox_4_2.INetworkAdapter;
import org.virtualbox_4_2.NetworkAttachmentType;
import org.virtualbox_4_2.VirtualBoxManager;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;

/**
 * Utilities to manage VirtualBox networks on guests
 * 
 * @author Andrea Turli
 */

@Singleton
public class NetworkUtils {

   // TODO parameterize
   public static final int MASTER_PORT = 2222;
   private static final String VIRTUALBOX_HOST_GATEWAY = "10.0.2.15";

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final Supplier<VirtualBoxManager> manager;
   private final MachineUtils machineUtils;
   private final Supplier<NodeMetadata> host;
   private final Supplier<URI> providerSupplier;
   private final IpAddressesLoadingCache ipAddressesLoadingCache;
   private final RunScriptOnNode.Factory scriptRunnerFactory;
   private final Supplier<NodeMetadata> hostSupplier;

   @Inject
   public NetworkUtils(Supplier<VirtualBoxManager> manager, MachineUtils machineUtils, Supplier<NodeMetadata> host,
                       @Provider Supplier<URI> providerSupplier, IpAddressesLoadingCache ipAddressesLoadingCache,
                       Supplier<NodeMetadata> hostSupplier, RunScriptOnNode.Factory scriptRunnerFactory) {
      this.manager = manager;
      this.machineUtils = machineUtils;
      this.host = checkNotNull(host, "host can't be null");
      this.providerSupplier = checkNotNull(providerSupplier, "endpoint to virtualbox web server can't be null");
      this.ipAddressesLoadingCache = ipAddressesLoadingCache;
      this.scriptRunnerFactory = scriptRunnerFactory;
      this.hostSupplier = hostSupplier;
   }

   public NetworkSpec createNetworkSpecWhenVboxIsLocalhost() {
      NetworkAdapter natAdapter = NetworkAdapter.builder().networkAttachmentType(NetworkAttachmentType.NAT).build();

      NetworkInterfaceCard natIfaceCard = NetworkInterfaceCard.builder().addNetworkAdapter(natAdapter).slot(1L).build();
      NetworkAdapter hostOnlyAdapter = NetworkAdapter.builder().networkAttachmentType(NetworkAttachmentType.HostOnly)
            .build();
      // create new hostOnly interface if needed, otherwise use the one already
      // there with dhcp enabled ...
      String hostOnlyIfName = getHostOnlyIfOrCreate();
      NetworkInterfaceCard hostOnlyIfaceCard = NetworkInterfaceCard.builder().addNetworkAdapter(hostOnlyAdapter)
            .addHostInterfaceName(hostOnlyIfName).slot(0L).build();
      return createNetworkSpecForHostOnlyNATNICs(natIfaceCard, hostOnlyIfaceCard);
   }

   public NetworkInterfaceCard createHostOnlyNIC(long port) {
      NetworkAdapter hostOnlyAdapter = NetworkAdapter.builder().networkAttachmentType(NetworkAttachmentType.HostOnly)
            .build();
      // create new hostOnly interface if needed, otherwise use the one already
      // there with dhcp enabled ...
      String hostOnlyIfName = getHostOnlyIfOrCreate();
      return NetworkInterfaceCard.builder().addNetworkAdapter(hostOnlyAdapter).addHostInterfaceName(hostOnlyIfName)
            .slot(port).build();
   }

   public boolean enableNetworkInterface(NodeMetadata nodeMetadata, NetworkInterfaceCard networkInterfaceCard) {
      ListenableFuture<ExecResponse> execEnableNetworkInterface = machineUtils.runScriptOnNode(nodeMetadata,
              new EnableNetworkInterface(networkInterfaceCard), RunScriptOptions.NONE);
      ExecResponse execEnableNetworkInterfaceResponse = Futures.getUnchecked(execEnableNetworkInterface);
      return execEnableNetworkInterfaceResponse.getExitStatus() == 0;
   }

   private NetworkSpec createNetworkSpecForHostOnlyNATNICs(NetworkInterfaceCard natIfaceCard,
         NetworkInterfaceCard hostOnlyIfaceCard) {
      return NetworkSpec.builder().addNIC(hostOnlyIfaceCard).addNIC(natIfaceCard).build();
   }

   public String getHostOnlyIfOrCreate() {
      IHostNetworkInterface availableHostInterfaceIf = returnExistingHostNetworkInterfaceWithDHCPenabledOrNull(manager
            .get().getVBox().getHost().getNetworkInterfaces());
      if (availableHostInterfaceIf == null) {
         final String hostOnlyIfName = createHostOnlyIf();
         assignDHCPtoHostOnlyInterface(hostOnlyIfName);
         return hostOnlyIfName;
      } else {
         return availableHostInterfaceIf.getName();
      }
   }

   private void assignDHCPtoHostOnlyInterface(final String hostOnlyIfName) {
      List<IHostNetworkInterface> availableNetworkInterfaces = manager.get().getVBox().getHost().getNetworkInterfaces();

      IHostNetworkInterface iHostNetworkInterfaceWithHostOnlyIfName = Iterables.getOnlyElement(Iterables.filter(
            availableNetworkInterfaces, new Predicate<IHostNetworkInterface>() {

               @Override
               public boolean apply(IHostNetworkInterface iHostNetworkInterface) {
                  return iHostNetworkInterface.getName().equals(hostOnlyIfName);
               }
            }));

      String hostOnlyIfIpAddress = iHostNetworkInterfaceWithHostOnlyIfName.getIPAddress();
      String dhcpIpAddress = hostOnlyIfIpAddress.substring(0, hostOnlyIfIpAddress.lastIndexOf(".")) + ".254";
      String dhcpNetmask = "255.255.255.0";
      String dhcpLowerIp = hostOnlyIfIpAddress.substring(0, hostOnlyIfIpAddress.lastIndexOf(".")) + ".2";
      String dhcpUpperIp = hostOnlyIfIpAddress.substring(0, hostOnlyIfIpAddress.lastIndexOf(".")) + ".253";
      NodeMetadata hostNodeMetadata = getHostNodeMetadata();

      ExecResponse response = scriptRunnerFactory
            .create(
                  hostNodeMetadata,
                  Statements.exec(String
                        .format(
                              "VBoxManage dhcpserver add --ifname %s --ip %s --netmask %s --lowerip %s --upperip %s --enable",
                              hostOnlyIfName, dhcpIpAddress, dhcpNetmask, dhcpLowerIp, dhcpUpperIp)),
                  runAsRoot(false).wrapInInitScript(false)).init().call();
      checkState(response.getExitStatus() == 0);
   }

   private String createHostOnlyIf() {
      NodeMetadata hostNodeMetadata = getHostNodeMetadata();
      ExecResponse createHostOnlyResponse = scriptRunnerFactory
            .create(hostNodeMetadata, Statements.exec("VBoxManage hostonlyif create"),
                  runAsRoot(false).wrapInInitScript(false)).init().call();
      String output = createHostOnlyResponse.getOutput();
      checkState(createHostOnlyResponse.getExitStatus() == 0, "cannot create hostonly interface ");
      checkState(output.contains("'"), "cannot create hostonly interface");
      return output.substring(output.indexOf("'") + 1, output.lastIndexOf("'"));
   }

   private NodeMetadata getHostNodeMetadata() {
      return NodeMetadataBuilder.fromNodeMetadata(host.get())
            .publicAddresses(ImmutableList.of(providerSupplier.get().getHost())).build();
   }

   private IHostNetworkInterface returnExistingHostNetworkInterfaceWithDHCPenabledOrNull(
         Iterable<IHostNetworkInterface> availableNetworkInterfaces) {
      checkNotNull(availableNetworkInterfaces);
      return Iterables.getFirst(filterAvailableNetworkInterfaceByHostOnlyAndDHCPenabled(availableNetworkInterfaces),
            null);
   }

   private Iterable<IHostNetworkInterface> filterAvailableNetworkInterfaceByHostOnlyAndDHCPenabled(
         Iterable<IHostNetworkInterface> availableNetworkInterfaces) {
      return Iterables.filter(availableNetworkInterfaces,
            new Predicate<IHostNetworkInterface>() {
               @Override
               public boolean apply(IHostNetworkInterface iHostNetworkInterface) {
                  // this is an horrible workaround cause
                  // iHostNetworkInterface.getDhcpEnabled is working only for
                  // windows host
                  boolean match = false;
                  List<IDHCPServer> availableDHCPservers = manager.get().getVBox().getDHCPServers();
                  for (IDHCPServer idhcpServer : availableDHCPservers) {
                     if (idhcpServer.getEnabled()
                           && idhcpServer.getNetworkName().equals(iHostNetworkInterface.getNetworkName()))
                        match = true;
                  }
                  return iHostNetworkInterface.getInterfaceType().equals(HostNetworkInterfaceType.HostOnly) && match;
               }
            });
   }

   public String getValidHostOnlyIpFromVm(String machineNameOrId) {
      long nicSlot = 0;
      int count = 0;
      String ipAddress = "";
      while (nicSlot < 4 && ipAddress.isEmpty()) {
         MachineNameOrIdAndNicSlot machineNameOrIdAndNicSlot =
                 MachineNameOrIdAndNicSlot.fromParts(machineNameOrId, nicSlot);
         while (count < 10 && ipAddress.isEmpty()) {
            Uninterruptibles.sleepUninterruptibly(3, TimeUnit.SECONDS);
            ipAddress = getIpAddressFromNicSlot(machineNameOrIdAndNicSlot);
            if (!isValidIpForHostOnly(ipAddress)) {
               ipAddressesLoadingCache.invalidate(machineNameOrIdAndNicSlot);
               ipAddress = "";
            }
            count++;
         }
         nicSlot++;
      }
      return checkNotNull(Strings.emptyToNull(ipAddress),
              String.format("Cannot find a valid IP address for the %s's HostOnly NIC", machineNameOrId));
   }

   public String getIpAddressFromNicSlot(String machineNameOrId, long nicSlot) {
      MachineNameOrIdAndNicSlot machineNameOrIdAndNicSlot = MachineNameOrIdAndNicSlot.fromParts(machineNameOrId,
            nicSlot);
      return getIpAddressFromNicSlot(machineNameOrIdAndNicSlot);
   }

   public String getIpAddressFromNicSlot(MachineNameOrIdAndNicSlot machineNameOrIdAndNicSlot) {
      try {
         return ipAddressesLoadingCache.get(machineNameOrIdAndNicSlot);
      } catch (ExecutionException e) {
         logger.error("Problem in using the ipAddressCache", e.getCause());
         throw Throwables.propagate(e);
      }
   }

   public boolean isValidIpForHostOnly(String ip) {
      return !ip.isEmpty() && isIpv4(ip) && !ipBelongsToNatRange(ip) && !ipEqualsToNatGateway(ip);
   }

   public static boolean isIpv4(String s) {
      String IP_V4_ADDRESS_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
      Pattern pattern = Pattern.compile(IP_V4_ADDRESS_PATTERN);
      Matcher matcher = pattern.matcher(s);
      return matcher.matches();
   }

   private static boolean ipEqualsToNatGateway(String ip) {
      return ip.equals(VIRTUALBOX_HOST_GATEWAY);
   }

   private static boolean ipBelongsToNatRange(String ip) {
      return ip.startsWith("10.0.3");
   }

   protected String getIpAddressFromBridgedNIC(INetworkAdapter networkAdapter) {
      // RetrieveActiveBridgedInterfaces
      List<BridgedIf> activeBridgedInterfaces = new RetrieveActiveBridgedInterfaces(scriptRunnerFactory)
            .apply(hostSupplier.get());
      BridgedIf activeBridgedIf = checkNotNull(Iterables.get(activeBridgedInterfaces, 0), "activeBridgedInterfaces");
      String network = activeBridgedIf.getIpAddress();

      // scan ip
      RunScriptOnNode ipScanRunScript = scriptRunnerFactory.create(hostSupplier.get(),
            new ScanNetworkWithPing(network), RunScriptOptions.NONE);
      ExecResponse execResponse = ipScanRunScript.init().call();
      checkState(execResponse.getExitStatus() == 0);

      // retrieve ip from mac
      RunScriptOnNode getIpFromMACAddressRunScript = scriptRunnerFactory.create(hostSupplier.get(),
            new GetIPAddressFromMAC(networkAdapter.getMACAddress()), RunScriptOptions.NONE);
      ExecResponse ipExecResponse = getIpFromMACAddressRunScript.init().call();
      checkState(ipExecResponse.getExitStatus() == 0);
      return checkNotNull(ipExecResponse.getOutput(), "ipAddress");
   }
}
