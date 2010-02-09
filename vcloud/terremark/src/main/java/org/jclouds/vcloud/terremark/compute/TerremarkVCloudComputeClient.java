/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.vcloud.terremark.compute;

import static org.jclouds.vcloud.terremark.options.AddInternetServiceOptions.Builder.withDescription;

import java.net.InetAddress;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeState;
import org.jclouds.vcloud.compute.BaseVCloudComputeClient;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VAppStatus;
import org.jclouds.vcloud.terremark.TerremarkVCloudClient;
import org.jclouds.vcloud.terremark.domain.InternetService;
import org.jclouds.vcloud.terremark.domain.Node;
import org.jclouds.vcloud.terremark.domain.Protocol;
import org.jclouds.vcloud.terremark.domain.PublicIpAddress;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
@Singleton
public class TerremarkVCloudComputeClient extends BaseVCloudComputeClient {

   private final TerremarkVCloudClient client;

   @Inject
   protected TerremarkVCloudComputeClient(TerremarkVCloudClient client,
            Predicate<String> successTester, @Named("NOT_FOUND") Predicate<VApp> notFoundTester,
            Map<VAppStatus, NodeState> vAppStatusToNodeState) {
      super(client, successTester, notFoundTester, vAppStatusToNodeState);
      this.client = client;
   }

   @Override
   protected Map<String, String> parseResponse(VApp vAppResponse) {
      return ImmutableMap.<String, String> of("id", vAppResponse.getId(), "username", "vcloud",
               "password", "p4ssw0rd");
   }

   @Override
   public Map<String, String> start(String vDCId, String name, String templateId, int minCores,
            int minMegs, Long diskSize, Map<String, String> properties, int... portsToOpen) {
      Map<String, String> response = super.start(vDCId, name, templateId, minCores, minMegs, null,
               properties, portsToOpen);// trmk does not support resizing the primary disk
      if (portsToOpen.length > 0)
         createPublicAddressMappedToPorts(response.get("id"), portsToOpen);
      return response;
   }

   public InetAddress createPublicAddressMappedToPorts(String vAppId, int... ports) {
      VApp vApp = client.getVApp(vAppId);
      PublicIpAddress ip = null;
      InetAddress privateAddress = Iterables.getLast(vApp.getNetworkToAddresses().values());
      for (int port : ports) {
         InternetService is = null;
         Protocol protocol;
         switch (port) {
            case 22:
               protocol = Protocol.TCP;
               break;
            case 80:
            case 8080:
               protocol = Protocol.HTTP;
               break;
            case 443:
               protocol = Protocol.HTTPS;
               break;
            default:
               protocol = Protocol.HTTP;
               break;
         }
         if (ip == null) {
            logger.debug(">> creating InternetService in vDC %s:%s:%d", vApp.getVDC().getId(),
                     protocol, port);
            is = client.addInternetServiceToVDC(vApp.getVDC().getId(), vApp.getName() + "-" + port,
                     protocol, port, withDescription(String.format(
                              "port %d access to serverId: %s name: %s", port, vApp.getId(), vApp
                                       .getName())));
            ip = is.getPublicIpAddress();
         } else {
            logger.debug(">> adding InternetService %s:%s:%d", ip.getAddress().getHostAddress(),
                     protocol, port);
            is = client.addInternetServiceToExistingIp(ip.getId(), vApp.getName() + "-" + port,
                     protocol, port, withDescription(String.format(
                              "port %d access to serverId: %s name: %s", port, vApp.getId(), vApp
                                       .getName())));
         }
         logger.debug("<< created InternetService(%s) %s:%s:%d", is.getId(), is
                  .getPublicIpAddress().getAddress().getHostAddress(), is.getProtocol(), is
                  .getPort());
         logger.debug(">> adding Node %s:%d -> %s:%d", is.getPublicIpAddress().getAddress()
                  .getHostAddress(), is.getPort(), privateAddress.getHostAddress(), port);
         Node node = client.addNode(is.getId(), privateAddress, vApp.getName() + "-" + port, port);
         logger.debug("<< added Node(%s)", node.getId());
      }
      return ip != null ? ip.getAddress() : null;
   }

   @Override
   public void stop(String id) {
      VApp vApp = client.getVApp(id);
      Set<PublicIpAddress> ipAddresses = deleteInternetServicesAndNodesAssociatedWithVApp(vApp);
      deletePublicIpAddressesWithNoServicesAttached(ipAddresses);
      super.stop(id);
   }

   private Set<PublicIpAddress> deleteInternetServicesAndNodesAssociatedWithVApp(VApp vApp) {
      Set<PublicIpAddress> ipAddresses = Sets.newHashSet();
      SERVICE: for (InternetService service : client.getAllInternetServicesInVDC(vApp.getVDC()
               .getId())) {
         for (Node node : client.getNodes(service.getId())) {
            if (vApp.getNetworkToAddresses().containsValue(node.getIpAddress())) {
               ipAddresses.add(service.getPublicIpAddress());
               logger.debug(">> deleting Node(%s) %s:%d -> %s:%d", node.getId(), service
                        .getPublicIpAddress().getAddress().getHostAddress(), service.getPort(),
                        node.getIpAddress().getHostAddress(), node.getPort());
               client.deleteNode(node.getId());
               logger.debug("<< deleted Node(%s)", node.getId());
               SortedSet<Node> nodes = client.getNodes(service.getId());
               if (nodes.size() == 0) {
                  logger.debug(">> deleting InternetService(%s) %s:%d", service.getId(), service
                           .getPublicIpAddress().getAddress().getHostAddress(), service.getPort());
                  client.deleteInternetService(service.getId());
                  logger.debug("<< deleted InternetService(%s)", service.getId());
                  continue SERVICE;
               }
            }
         }
      }
      return ipAddresses;
   }

   private void deletePublicIpAddressesWithNoServicesAttached(Set<PublicIpAddress> ipAddresses) {
      IPADDRESS: for (PublicIpAddress address : ipAddresses) {
         SortedSet<InternetService> services = client
                  .getInternetServicesOnPublicIp(address.getId());
         if (services.size() == 0) {
            logger.debug(">> deleting PublicIpAddress(%s) %s", address.getId(), address
                     .getAddress().getHostAddress());
            client.deletePublicIp(address.getId());
            logger.debug("<< deleted PublicIpAddress(%s)", address.getId());
            continue IPADDRESS;
         }
      }
   }

   @Override
   public Set<InetAddress> getPrivateAddresses(String id) {
      VApp vApp = client.getVApp(id);
      return Sets.newHashSet(vApp.getNetworkToAddresses().values());
   }

   @Override
   public Set<InetAddress> getPublicAddresses(String id) {
      VApp vApp = client.getVApp(id);
      Set<InetAddress> ipAddresses = Sets.newHashSet();
      for (InternetService service : client.getAllInternetServicesInVDC(vApp.getVDC().getId())) {
         for (Node node : client.getNodes(service.getId())) {
            if (vApp.getNetworkToAddresses().containsValue(node.getIpAddress())) {
               ipAddresses.add(service.getPublicIpAddress().getAddress());
            }
         }
      }
      return ipAddresses;
   }
}