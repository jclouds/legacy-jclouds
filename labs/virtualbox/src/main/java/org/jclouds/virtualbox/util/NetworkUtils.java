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
import org.virtualbox_4_1.HostNetworkInterfaceType;
import org.virtualbox_4_1.IDHCPServer;
import org.virtualbox_4_1.IHostNetworkInterface;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.INetworkAdapter;
import org.virtualbox_4_1.ISession;
import org.virtualbox_4_1.LockType;
import org.virtualbox_4_1.NetworkAttachmentType;
import org.virtualbox_4_1.VirtualBoxManager;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.Uninterruptibles;
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
   public NetworkUtils(Supplier<VirtualBoxManager> manager, MachineUtils machineUtils,
         MachineController machineController,
         Supplier<NodeMetadata> host,
         @Provider Supplier<URI> providerSupplier,
         IpAddressesLoadingCache ipAddressesLoadingCache,
         Supplier<NodeMetadata> hostSupplier,
         RunScriptOnNode.Factory scriptRunnerFactory) {
      this.manager = manager;
      this.machineUtils = machineUtils;
      this.host = checkNotNull(host, "host");
      this.providerSupplier = checkNotNull(providerSupplier,
            "endpoint to virtualbox websrvd is needed");
      this.ipAddressesLoadingCache = ipAddressesLoadingCache;
      this.scriptRunnerFactory = scriptRunnerFactory;
      this.hostSupplier = hostSupplier;
   }

   public NetworkSpec createNetworkSpecWhenVboxIsLocalhost() {
      NetworkAdapter natAdapter = NetworkAdapter.builder()
            .networkAttachmentType(NetworkAttachmentType.NAT)
            .build();

      NetworkInterfaceCard natIfaceCard = NetworkInterfaceCard.builder()
            .addNetworkAdapter(natAdapter)
            .slot(1L)
            .build();
      NetworkAdapter hostOnlyAdapter = NetworkAdapter.builder()
            .networkAttachmentType(NetworkAttachmentType.HostOnly)
               .build();
      // create new hostOnly interface if needed, otherwise use the one already there with dhcp enabled ...
      String hostOnlyIfName = getHostOnlyIfOrCreate();
      NetworkInterfaceCard hostOnlyIfaceCard = NetworkInterfaceCard.builder().addNetworkAdapter(hostOnlyAdapter)
               .addHostInterfaceName(hostOnlyIfName).slot(0L).build();      
      return createNetworkSpecForHostOnlyNATNICs(natIfaceCard, hostOnlyIfaceCard);
   }
   
   public boolean enableNetworkInterface(NodeMetadata nodeMetadata, NetworkInterfaceCard networkInterfaceCard) {
      ExecResponse execResponse = null;
      try {
         execResponse = machineUtils.runScriptOnNode(nodeMetadata, 
               new EnableNetworkInterface(networkInterfaceCard), RunScriptOptions.NONE).get();
      } catch (InterruptedException e) {
         logger.error(e.getMessage());
      } catch (ExecutionException e) {
         logger.error(e.getMessage());
      }
      if(execResponse == null)
         return false;     
      return execResponse.getExitStatus() == 0;
   }
   
   private NetworkSpec createNetworkSpecForHostOnlyNATNICs(NetworkInterfaceCard natIfaceCard,
            NetworkInterfaceCard hostOnlyIfaceCard) {
      return NetworkSpec.builder()
            .addNIC(hostOnlyIfaceCard)
            .addNIC(natIfaceCard)
            .build();
   }

   public String getHostOnlyIfOrCreate() {     
      IHostNetworkInterface availableHostInterfaceIf = returnExistingHostNetworkInterfaceWithDHCPenabledOrNull(manager
               .get().getVBox().getHost().getNetworkInterfaces());
      if (availableHostInterfaceIf==null) {
         final String hostOnlyIfName = createHostOnlyIf();
         assignDHCPtoHostOnlyInterface(hostOnlyIfName);
         return hostOnlyIfName;
      } else {
         return availableHostInterfaceIf.getName();
      }
   }

   private void assignDHCPtoHostOnlyInterface(final String hostOnlyIfName) {
      List<IHostNetworkInterface> availableNetworkInterfaces = manager.get().getVBox().getHost()
               .getNetworkInterfaces();
      
      IHostNetworkInterface iHostNetworkInterfaceWithHostOnlyIfName = Iterables.getOnlyElement(Iterables.filter(availableNetworkInterfaces, new Predicate<IHostNetworkInterface>() {

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
               .create(hostNodeMetadata,
                        Statements.exec(String
                                 .format("VBoxManage dhcpserver add --ifname %s --ip %s --netmask %s --lowerip %s --upperip %s --enable",
                                          hostOnlyIfName, dhcpIpAddress, dhcpNetmask, dhcpLowerIp, dhcpUpperIp)), runAsRoot(false).wrapInInitScript(false)).init().call();
      checkState(response.getExitStatus()==0);
   }

   private String createHostOnlyIf() {
      final String hostOnlyIfName;
      NodeMetadata hostNodeMetadata = getHostNodeMetadata();
      ExecResponse createHostOnlyResponse = scriptRunnerFactory
               .create(hostNodeMetadata, Statements.exec("VBoxManage hostonlyif create"),
                        runAsRoot(false).wrapInInitScript(false)).init().call();
      String output = createHostOnlyResponse.getOutput();
      checkState(createHostOnlyResponse.getExitStatus()==0);
      checkState(output.contains("'"), "cannot create hostonlyif");
      hostOnlyIfName = output.substring(output.indexOf("'") + 1, output.lastIndexOf("'"));
      return hostOnlyIfName;
   }

   private NodeMetadata getHostNodeMetadata() {
      NodeMetadata hostNodeMetadata = NodeMetadataBuilder
            .fromNodeMetadata(host.get())
            .publicAddresses(
                  ImmutableList.of(providerSupplier.get().getHost()))
            .build();
      return hostNodeMetadata;
   }

   private IHostNetworkInterface returnExistingHostNetworkInterfaceWithDHCPenabledOrNull(Iterable<IHostNetworkInterface> availableNetworkInterfaces) {
      checkNotNull(availableNetworkInterfaces);
      return Iterables.getFirst(filterAvailableNetworkInterfaceByHostOnlyAndDHCPenabled(availableNetworkInterfaces), null);
   }

   /**
    * @param availableNetworkInterfaces 
    * @param hostOnlyIfIpAddress
    * @return
    */
   private Iterable<IHostNetworkInterface> filterAvailableNetworkInterfaceByHostOnlyAndDHCPenabled(Iterable<IHostNetworkInterface> availableNetworkInterfaces) {
      Iterable<IHostNetworkInterface> filteredNetworkInterfaces = Iterables.filter(availableNetworkInterfaces, new Predicate<IHostNetworkInterface>() {
         @Override
         public boolean apply(IHostNetworkInterface iHostNetworkInterface) {
            // this is an horrible workaround cause iHostNetworkInterface.getDhcpEnabled is working only for windows host
            boolean match = false;
            List<IDHCPServer> availableDHCPservers = manager.get().getVBox().getDHCPServers();
            for (IDHCPServer idhcpServer : availableDHCPservers) {
               if(idhcpServer.getEnabled() && idhcpServer.getNetworkName().equals(iHostNetworkInterface.getNetworkName()))
                  match  = true;
            }
            return iHostNetworkInterface.getInterfaceType().equals(HostNetworkInterfaceType.HostOnly) &&
                    match;
            }
      });
      return filteredNetworkInterfaces;
   }

   
   public String getIpAddressFromNicSlot(String machineNameOrId, long nicSlot) {
      MachineNameOrIdAndNicSlot machineNameOrIdAndNicSlot = 
            MachineNameOrIdAndNicSlot.fromParts(machineNameOrId, nicSlot);
      try {
         String ipAddress = ipAddressesLoadingCache.get(machineNameOrIdAndNicSlot);
         while(!isValidIpForHostOnly(machineNameOrIdAndNicSlot, ipAddress)) {
            ipAddressesLoadingCache.invalidate(machineNameOrIdAndNicSlot);
            ipAddress = ipAddressesLoadingCache.get(machineNameOrIdAndNicSlot);
         }
         return ipAddress;
      } catch (ExecutionException e) {
         logger.error("Problem in using the ipAddressCache", e.getCause());
         throw Throwables.propagate(e);
      }
   }
 
   public static boolean isIpv4(String s) {
      String IP_V4_ADDRESS_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
      Pattern pattern = Pattern.compile(IP_V4_ADDRESS_PATTERN);
      Matcher matcher = pattern.matcher(s);
      return matcher.matches();
   }
   
   public boolean isValidIpForHostOnly(MachineNameOrIdAndNicSlot machineNameOrIdAndNicSlot, String ip) {
      final String vmNameOrId = machineNameOrIdAndNicSlot.getMachineNameOrId();
      IMachine machine = manager.get().getVBox().findMachine(vmNameOrId);
      long slot = machineNameOrIdAndNicSlot.getSlot();
      
      if(ip.equals(VIRTUALBOX_HOST_GATEWAY) || !isValidHostOnlyIpAddress(ip, slot, machine)) {
         // restart vm
         logger.debug("reset node (%s) to refresh guest properties.", vmNameOrId);
         machineUtils.lockSessionOnMachineAndApply(vmNameOrId, LockType.Shared,
               new Function<ISession, Void>() {
                  @Override
                  public Void apply(ISession session) {
                     session.getConsole().reset();
                     long time = 15;
                     logger.debug("Waiting %s secs for the reset of (%s) ...", time, vmNameOrId);
                     Uninterruptibles.sleepUninterruptibly(time, TimeUnit.SECONDS);
                     return null;
                  }
               });
         return false;
      }
      return true;
   }

   public static  boolean isValidHostOnlyIpAddress(String ip, long slot,
         IMachine machine) {
      boolean result = isIpv4(ip) && machine.getNetworkAdapter(slot).getAttachmentType().equals(NetworkAttachmentType.HostOnly)
            && !ipBelongsToNatRange(ip);
      return result;
   }

   private static boolean ipBelongsToNatRange(String ip) {
      return ip.startsWith("10.0.3");
   }
   
   protected String getIpAddressFromBridgedNIC(INetworkAdapter networkAdapter,
         String network) {
      // RetrieveActiveBridgedInterfaces
      List<BridgedIf> activeBridgedInterfaces = new RetrieveActiveBridgedInterfaces(scriptRunnerFactory).apply(hostSupplier.get());
      BridgedIf activeBridgedIf = checkNotNull(Iterables.get(activeBridgedInterfaces, 0), "activeBridgedInterfaces");
      network = activeBridgedIf.getIpAddress();
      
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
