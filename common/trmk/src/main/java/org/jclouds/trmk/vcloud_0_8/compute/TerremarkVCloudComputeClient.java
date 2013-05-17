/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.trmk.vcloud_0_8.compute;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.getLast;
import static org.jclouds.trmk.vcloud_0_8.options.AddInternetServiceOptions.Builder.withDescription;

import java.net.URI;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.logging.Logger;
import org.jclouds.trmk.vcloud_0_8.TerremarkVCloudClient;
import org.jclouds.trmk.vcloud_0_8.domain.InternetService;
import org.jclouds.trmk.vcloud_0_8.domain.Node;
import org.jclouds.trmk.vcloud_0_8.domain.Protocol;
import org.jclouds.trmk.vcloud_0_8.domain.PublicIpAddress;
import org.jclouds.trmk.vcloud_0_8.domain.Status;
import org.jclouds.trmk.vcloud_0_8.domain.Task;
import org.jclouds.trmk.vcloud_0_8.domain.TaskStatus;
import org.jclouds.trmk.vcloud_0_8.domain.TasksList;
import org.jclouds.trmk.vcloud_0_8.domain.VApp;
import org.jclouds.trmk.vcloud_0_8.domain.VAppTemplate;
import org.jclouds.trmk.vcloud_0_8.options.InstantiateVAppTemplateOptions;
import org.jclouds.trmk.vcloud_0_8.suppliers.InternetServiceAndPublicIpAddressSupplier;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
@Singleton
public class TerremarkVCloudComputeClient {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   protected final TerremarkVCloudClient client;
   protected final Provider<String> passwordGenerator;
   protected final InternetServiceAndPublicIpAddressSupplier internetServiceAndPublicIpAddressSupplier;
   protected final Map<Status, NodeMetadata.Status> vAppStatusToNodeStatus;
   protected final Predicate<URI> taskTester;

   @Inject
   protected TerremarkVCloudComputeClient(TerremarkVCloudClient client,
         @Named("PASSWORD") Provider<String> passwordGenerator, Predicate<URI> successTester,
         Map<Status, NodeMetadata.Status> vAppStatusToNodeStatus, Map<String, Credentials> credentialStore,
         InternetServiceAndPublicIpAddressSupplier internetServiceAndPublicIpAddressSupplier) {
      this.client = client;
      this.passwordGenerator = passwordGenerator;
      this.internetServiceAndPublicIpAddressSupplier = internetServiceAndPublicIpAddressSupplier;
      this.vAppStatusToNodeStatus = vAppStatusToNodeStatus;
      this.taskTester = successTester;
   }

   protected Status getStatus(VApp vApp) {
      return vApp.getStatus();
   }
   
   public ComputeServiceAdapter.NodeAndInitialCredentials<VApp> startAndReturnCredentials(@Nullable URI VDC, URI templateId, String name, InstantiateVAppTemplateOptions options,
            int... portsToOpen) {
      // we only get IP addresses after "deploy"
      if (portsToOpen.length > 0 && !options.shouldBlock())
         throw new IllegalArgumentException("We cannot open ports on terremark unless we can deploy the vapp");
      String password = null;
      VAppTemplate template = client.getVAppTemplate(templateId);
      if (template.getDescription().indexOf("Windows") != -1) {
         password = passwordGenerator.get();
         options.getProperties().put("password", password);
      }
      checkNotNull(options, "options");
      logger.debug(">> instantiating vApp vDC(%s) template(%s) name(%s) options(%s) ", VDC, templateId, name, options);

      VApp vAppResponse = client.instantiateVAppTemplateInVDC(VDC, templateId, name, options);
      logger.debug("<< instantiated VApp(%s)", vAppResponse.getName());
      if (options.shouldDeploy()) {
         logger.debug(">> deploying vApp(%s)", vAppResponse.getName());

         Task task = client.deployVApp(vAppResponse.getHref());
         if (options.shouldBlock()) {
            if (!taskTester.apply(task.getHref())) {
               throw new RuntimeException(String.format("failed to %s %s: %s", "deploy", vAppResponse.getName(), task));
            }
            logger.debug("<< deployed vApp(%s)", vAppResponse.getName());
            if (options.shouldPowerOn()) {
               logger.debug(">> powering vApp(%s)", vAppResponse.getName());
               task = client.powerOnVApp(vAppResponse.getHref());
               if (!taskTester.apply(task.getHref())) {
                  throw new RuntimeException(String.format("failed to %s %s: %s", "powerOn", vAppResponse.getName(),
                        task));
               }
               logger.debug("<< on vApp(%s)", vAppResponse.getName());
            }
         }
      }
      if (portsToOpen.length > 0)
         createPublicAddressMappedToPorts(vAppResponse.getHref(), portsToOpen);
      return new ComputeServiceAdapter.NodeAndInitialCredentials<VApp>(vAppResponse, vAppResponse.getHref().toASCIIString(), password!= null?LoginCredentials.builder().password(password).build():null);
   }
   
   /**
    * Runs through all commands necessary to startup a vApp, opening at least
    * one ip address to the public network. These are the steps:
    * <p/>
    * instantiate -> deploy -> powerOn
    * <p/>
    * This command blocks until the vApp is in state {@code VAppStatus#ON}
    * 
    * @param VDC
    *           id of the virtual datacenter {@code VCloudClient#getDefaultVDC}
    * @param templateId
    *           id of the vAppTemplate you wish to instantiate
    * @param name
    *           name of the vApp
    * @param cores
    *           amount of virtual cpu cores
    * @param megs
    *           amount of ram in megabytes
    * @param options
    *           options for instantiating the vApp; null is ok
    * @param portsToOpen
    *           opens the following ports on the public ip address
    * @return map contains at least the following properties
    *         <ol>
    *         <li>id - vApp id</li> <li>username - console login user</li> <li>
    *         password - console login password</li>
    *         </ol>
    */
   public VApp start(@Nullable URI VDC, URI templateId, String name, InstantiateVAppTemplateOptions options,
            int... portsToOpen) {
      return startAndReturnCredentials(VDC, templateId, name, options, portsToOpen).getNode();
   }

   public String createPublicAddressMappedToPorts(URI vAppId, int... ports) {
      VApp vApp = client.getVApp(vAppId);
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

            Entry<InternetService, PublicIpAddress> entry = internetServiceAndPublicIpAddressSupplier
                  .getNewInternetServiceAndIp(vApp, port, protocol);
            is = entry.getKey();
            ip = entry.getValue();

         } else {
            logger.debug(">> adding InternetService %s:%s:%d", ip.getAddress(), protocol, port);
            is = client.addInternetServiceToExistingIp(
                  ip.getId(),
                  vApp.getName() + "-" + port,
                  protocol,
                  port,
                  withDescription(String.format("port %d access to serverId: %s name: %s", port, vApp.getName(),
                        vApp.getName())));
         }
         logger.debug("<< created InternetService(%s) %s:%s:%d", is.getName(), is.getPublicIpAddress().getAddress(),
               is.getProtocol(), is.getPort());
         logger.debug(">> adding Node %s:%d -> %s:%d", is.getPublicIpAddress().getAddress(), is.getPort(),
               privateAddress, port);
         Node node = client.addNode(is.getId(), privateAddress, vApp.getName() + "-" + port, port);
         logger.debug("<< added Node(%s)", node.getName());
      }
      return ip != null ? ip.getAddress() : null;
   }

   private Set<PublicIpAddress> deleteInternetServicesAndNodesAssociatedWithVApp(VApp vApp) {
      checkNotNull(vApp.getVDC(), "VDC reference missing for vApp(%s)", vApp.getName());
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
            } catch (Exception e) {
               logger.trace("cannot delete PublicIpAddress(%s) as it is unsupported", address.getId());
            }
            continue IPADDRESS;
         }
      }
   }

   /**
    * Destroys dependent resources, powers off and deletes the vApp, blocking
    * until the following state transition is complete:
    * <p/>
    * current -> {@code VAppStatus#OFF} -> deleted
    * <p/>
    * * deletes the internet service and nodes associated with the vapp. Deletes
    * the IP address, if there are no others using it. Finally, it powers off
    * and deletes the vapp. Note that we do not call undeploy, as terremark does
    * not support the command.
    * 
    * @param vAppId
    *           vApp to stop
    */
   public void stop(URI id) {
      VApp vApp = client.getVApp(id);
      if (vApp == null)
         return;
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

   private void powerOffAndWait(VApp vApp) {
      logger.debug(">> powering off vApp(%s), current status: %s", vApp.getName(), vApp.getStatus());
      Task task = client.powerOffVApp(vApp.getHref());
      if (!taskTester.apply(task.getHref()))
         throw new RuntimeException(String.format("failed to %s %s: %s", "powerOff", vApp.getName(), task));
   }

   void blockOnLastTask(VApp vApp) {
      TasksList list = client.findTasksListInOrgNamed(null, null);
      try {
         Task lastTask = getLast(filter(list.getTasks(), new Predicate<Task>() {

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
    * returns a set of addresses that are only visible to the private network.
    * 
    * @returns empty set if the node is not found
    */
   public Set<String> getPrivateAddresses(URI id) {
      VApp vApp = client.getVApp(id);
      if (vApp != null)
         return Sets.newHashSet(vApp.getNetworkToAddresses().values());
      else
         return ImmutableSet.<String> of();
   }

   /**
    * returns a set of addresses that are publically visible
    * 
    * @returns empty set if the node is not found
    */
   public Set<String> getPublicAddresses(URI id) {
      VApp vApp = client.getVApp(id);
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

   /**
    * reboots the vApp, blocking until the following state transition is
    * complete:
    * <p/>
    * current -> {@code VAppStatus#OFF} -> {@code VAppStatus#ON}
    * 
    * @param vAppId
    *           vApp to reboot
    */
   public void reset(URI id) {
      VApp vApp = refreshVApp(id);
      logger.debug(">> resetting vApp(%s)", vApp.getName());
      Task task = reset(vApp);
      if (!taskTester.apply(task.getHref())) {
         throw new RuntimeException(String.format("failed to %s %s: %s", "resetVApp", vApp.getName(), task));
      }
      logger.debug("<< on vApp(%s)", vApp.getName());
   }

   protected void deleteVApp(VApp vApp) {
      logger.debug(">> deleting vApp(%s)", vApp.getName());
      Task task = client.deleteVApp(vApp.getHref());
      if (task != null)
         if (!taskTester.apply(task.getHref()))
            throw new RuntimeException(String.format("failed to %s %s: %s", "delete", vApp.getName(), task));
   }

   protected VApp refreshVApp(URI id) {
      return client.getVApp(id);
   }

   protected Task powerOff(VApp vApp) {
      return client.powerOffVApp(vApp.getHref());
   }

   protected Task reset(VApp vApp) {
      return client.resetVApp(vApp.getHref());
   }

   protected Task undeploy(VApp vApp) {
      return client.undeployVApp(vApp.getHref());
   }
}
