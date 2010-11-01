/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.getLast;
import static org.jclouds.vcloud.terremark.options.AddInternetServiceOptions.Builder.withDescription;

import java.net.URI;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.strategy.PopulateDefaultLoginCredentialsForImageStrategy;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpResponseException;
import org.jclouds.vcloud.compute.internal.VCloudExpressComputeClientImpl;
import org.jclouds.vcloud.domain.Status;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.TaskStatus;
import org.jclouds.vcloud.domain.TasksList;
import org.jclouds.vcloud.domain.VCloudExpressVApp;
import org.jclouds.vcloud.domain.VCloudExpressVAppTemplate;
import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;
import org.jclouds.vcloud.terremark.TerremarkECloudClient;
import org.jclouds.vcloud.terremark.TerremarkVCloudClient;
import org.jclouds.vcloud.terremark.TerremarkVCloudExpressClient;
import org.jclouds.vcloud.terremark.domain.InternetService;
import org.jclouds.vcloud.terremark.domain.Node;
import org.jclouds.vcloud.terremark.domain.Protocol;
import org.jclouds.vcloud.terremark.domain.PublicIpAddress;
import org.jclouds.vcloud.terremark.options.TerremarkInstantiateVAppTemplateOptions;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
@Singleton
public class TerremarkVCloudComputeClient extends VCloudExpressComputeClientImpl {

   protected final TerremarkVCloudClient client;
   protected final PopulateDefaultLoginCredentialsForImageStrategy credentialsProvider;
   protected final Provider<String> passwordGenerator;
   protected final Map<String, Credentials> credentialStore;

   @Inject
   protected TerremarkVCloudComputeClient(TerremarkVCloudClient client,
            PopulateDefaultLoginCredentialsForImageStrategy credentialsProvider,
            @Named("PASSWORD") Provider<String> passwordGenerator, Predicate<URI> successTester,
            Map<Status, NodeState> vAppStatusToNodeState, Map<String, Credentials> credentialStore) {
      super(client, successTester, vAppStatusToNodeState);
      this.client = client;
      this.credentialsProvider = credentialsProvider;
      this.passwordGenerator = passwordGenerator;
      this.credentialStore = credentialStore;
   }

   @Override
   public VCloudExpressVApp start(@Nullable URI VDC, URI templateId, String name,
            InstantiateVAppTemplateOptions options, int... portsToOpen) {
      if (options.getDiskSizeKilobytes() != null) {
         logger.warn("trmk does not support resizing the primary disk; unsetting disk size");
      }
      // we only get IP addresses after "deploy"
      if (portsToOpen.length > 0 && !options.shouldBlock())
         throw new IllegalArgumentException("We cannot open ports on terremark unless we can deploy the vapp");
      String password = null;
      VCloudExpressVAppTemplate template = client.getVAppTemplate(templateId);
      if (template.getDescription().indexOf("Windows") != -1
               && options instanceof TerremarkInstantiateVAppTemplateOptions) {
         password = passwordGenerator.get();
         TerremarkInstantiateVAppTemplateOptions.class.cast(options).getProperties().put("password", password);
      }
      Credentials defaultCredentials = credentialsProvider.execute(template);

      VCloudExpressVApp vAppResponse = super.start(VDC, templateId, name, options, portsToOpen);
      if (password != null) {
         credentialStore.put("node#" + vAppResponse.getHref().toASCIIString(), new Credentials(
                  defaultCredentials.identity, password));
      }
      if (portsToOpen.length > 0)
         createPublicAddressMappedToPorts(vAppResponse.getHref(), portsToOpen);
      return vAppResponse;
   }

   public String createPublicAddressMappedToPorts(URI vAppId, int... ports) {
      VCloudExpressVApp vApp = client.getVApp(vAppId);
      PublicIpAddress ip = null;
      String privateAddress = getLast(vApp.getNetworkToAddresses().values());
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
            if (client instanceof TerremarkVCloudExpressClient) {
               is = TerremarkVCloudExpressClient.class.cast(client).addInternetServiceToVDC(
                        vApp.getVDC().getHref(),
                        vApp.getName() + "-" + port,
                        protocol,
                        port,
                        withDescription(String.format("port %d access to serverId: %s name: %s", port, vApp.getName(),
                                 vApp.getName())));
               ip = is.getPublicIpAddress();
            } else {
               logger.debug(">> creating InternetService in vDC %s:%s:%d", vApp.getVDC().getName(), protocol, port);
               // http://support.theenterprisecloud.com/kb/default.asp?id=706&Lang=1&SID=
               // response with a 500 error code means we should look for an existing public ip to
               // use
               try {
                  ip = TerremarkECloudClient.class.cast(client).activatePublicIpInVDC(vApp.getVDC().getHref());
               } catch (HttpResponseException e) {
                  if (e.getResponse().getStatusCode() == 500) {
                     logger.warn(">> no more ip addresses available, looking for one to re-use");
                     for (PublicIpAddress existingIp : client.getPublicIpsAssociatedWithVDC(vApp.getVDC().getHref())) {
                        Set<InternetService> services = client.getInternetServicesOnPublicIp(existingIp.getId());
                        if (services.size() == 0) {
                           ip = existingIp;
                           break;
                        }
                     }
                     if (ip == null)
                        throw e;
                  } else {
                     throw e;
                  }
               }
               is = client.addInternetServiceToExistingIp(ip.getId(), vApp.getName() + "-" + port, protocol, port,
                        withDescription(String.format("port %d access to serverId: %s name: %s", port, vApp.getName(),
                                 vApp.getName())));
            }
         } else {
            logger.debug(">> adding InternetService %s:%s:%d", ip.getAddress(), protocol, port);
            is = client.addInternetServiceToExistingIp(ip.getId(), vApp.getName() + "-" + port, protocol, port,
                     withDescription(String.format("port %d access to serverId: %s name: %s", port, vApp.getName(),
                              vApp.getName())));
         }
         logger.debug("<< created InternetService(%s) %s:%s:%d", is.getName(), is.getPublicIpAddress().getAddress(), is
                  .getProtocol(), is.getPort());
         logger.debug(">> adding Node %s:%d -> %s:%d", is.getPublicIpAddress().getAddress(), is.getPort(),
                  privateAddress, port);
         Node node = client.addNode(is.getId(), privateAddress, vApp.getName() + "-" + port, port);
         logger.debug("<< added Node(%s)", node.getName());
      }
      return ip != null ? ip.getAddress() : null;
   }

   private Set<PublicIpAddress> deleteInternetServicesAndNodesAssociatedWithVApp(VCloudExpressVApp vApp) {
      Set<PublicIpAddress> ipAddresses = Sets.newHashSet();
      SERVICE: for (InternetService service : client.getAllInternetServicesInVDC(vApp.getVDC().getHref())) {
         for (Node node : client.getNodes(service.getId())) {
            if (vApp.getNetworkToAddresses().containsValue(node.getIpAddress())) {
               ipAddresses.add(service.getPublicIpAddress());
               logger.debug(">> deleting Node(%s) %s:%d -> %s:%d", node.getName(), service.getPublicIpAddress()
                        .getAddress(), service.getPort(), node.getIpAddress(), node.getPort());
               client.deleteNode(node.getId());
               logger.debug("<< deleted Node(%s)", node.getName());
               Set<Node> nodes = client.getNodes(service.getId());
               if (nodes.size() == 0) {
                  logger.debug(">> deleting InternetService(%s) %s:%d", service.getName(), service.getPublicIpAddress()
                           .getAddress(), service.getPort());
                  client.deleteInternetService(service.getId());
                  logger.debug("<< deleted InternetService(%s)", service.getName());
                  continue SERVICE;
               }
            }
         }
      }
      return ipAddresses;
   }

   private void deletePublicIpAddressesWithNoServicesAttached(Set<PublicIpAddress> ipAddresses) {
      IPADDRESS: for (PublicIpAddress address : ipAddresses) {
         Set<InternetService> services = client.getInternetServicesOnPublicIp(address.getId());
         if (services.size() == 0) {
            logger.debug(">> deleting PublicIpAddress(%s) %s", address.getId(), address.getAddress());
            try {
               client.deletePublicIp(address.getId());
               logger.debug("<< deleted PublicIpAddress(%s)", address.getId());
            } catch (UnsupportedOperationException e) {
               logger.trace("cannot delete PublicIpAddress(%s) as it is unsupported", address.getId());
            }
            continue IPADDRESS;
         }
      }
   }

   /**
    * deletes the internet service and nodes associated with the vapp. Deletes the IP address, if
    * there are no others using it. Finally, it powers off and deletes the vapp. Note that we do not
    * call undeploy, as terremark does not support the command.
    */
   @Override
   public void stop(URI id) {
      VCloudExpressVApp vApp = client.getVApp(id);
      Set<PublicIpAddress> ipAddresses = deleteInternetServicesAndNodesAssociatedWithVApp(vApp);
      deletePublicIpAddressesWithNoServicesAttached(ipAddresses);
      if (vApp.getStatus() != Status.OFF) {
         try {
            powerOffAndWait(vApp);
         } catch (IllegalStateException e) {
            logger.warn("<< %s vApp(%s)", e.getMessage(), vApp.getName());
            blockOnLastTask(vApp);
            powerOffAndWait(vApp);
         }
         vApp = client.getVApp(id);
         logger.debug("<< %s vApp(%s)", vApp.getStatus(), vApp.getName());
      }
      logger.debug(">> deleting vApp(%s)", vApp.getName());
      client.deleteVApp(id);
      logger.debug("<< deleted vApp(%s))", vApp.getName());
   }

   private void powerOffAndWait(VCloudExpressVApp vApp) {
      logger.debug(">> powering off vApp(%s), current status: %s", vApp.getName(), vApp.getStatus());
      Task task = client.powerOffVApp(vApp.getHref());
      if (!taskTester.apply(task.getHref()))
         throw new RuntimeException(String.format("failed to %s %s: %s", "powerOff", vApp.getName(), task));
   }

   void blockOnLastTask(VCloudExpressVApp vApp) {
      TasksList list = client.findTasksListInOrgNamed(null);
      try {
         Task lastTask = getLast(filter(list.getTasks(), new Predicate<Task>() {

            @Override
            public boolean apply(Task input) {
               return input.getStatus() == TaskStatus.QUEUED || input.getStatus() == TaskStatus.RUNNING;
            }

         }));
         if (!taskTester.apply(lastTask.getHref()))
            throw new RuntimeException(String.format("failed to %s %s: %s", "powerOff", vApp.getName(), lastTask));
      } catch (NoSuchElementException ex) {

      }
   }

   /**
    * @returns empty set if the node is not found
    */
   @Override
   public Set<String> getPrivateAddresses(URI id) {
      VCloudExpressVApp vApp = client.getVApp(id);
      if (vApp != null)
         return Sets.newHashSet(vApp.getNetworkToAddresses().values());
      else
         return ImmutableSet.<String> of();
   }

   /**
    * @returns empty set if the node is not found
    */
   @Override
   public Set<String> getPublicAddresses(URI id) {
      VCloudExpressVApp vApp = client.getVApp(id);
      if (vApp != null) {
         Set<String> ipAddresses = Sets.newHashSet();
         for (InternetService service : client.getAllInternetServicesInVDC(vApp.getVDC().getHref())) {
            for (Node node : client.getNodes(service.getId())) {
               if (vApp.getNetworkToAddresses().containsValue(node.getIpAddress())) {
                  ipAddresses.add(service.getPublicIpAddress().getAddress());
               }
            }
         }
         return ipAddresses;
      } else {
         return ImmutableSet.<String> of();
      }
   }
}