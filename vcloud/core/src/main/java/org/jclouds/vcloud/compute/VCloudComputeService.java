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
package org.jclouds.vcloud.compute;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.vcloud.options.InstantiateVAppTemplateOptions.Builder.processorCount;

import java.io.ByteArrayInputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.ComputeType;
import org.jclouds.compute.domain.CreateNodeResponse;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.LoginType;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.internal.CreateNodeResponseImpl;
import org.jclouds.compute.domain.internal.NodeMetadataImpl;
import org.jclouds.compute.options.RunNodeOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Credentials;
import org.jclouds.logging.Logger;
import org.jclouds.ssh.SshClient;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VAppStatus;
import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * @author Adrian Cole
 */
@Singleton
public class VCloudComputeService implements ComputeService, VCloudComputeClient {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   private final VCloudClient client;
   protected final Provider<Set<? extends Image>> images;
   protected final Provider<SortedSet<? extends Size>> sizes;
   private final Provider<Set<? extends VCloudTemplate>> templates;

   @Inject(optional = true)
   private SshClient.Factory sshFactory;
   private Predicate<InetSocketAddress> socketTester;

   protected static final Map<VAppStatus, NodeState> vAppStatusToNodeState = ImmutableMap
            .<VAppStatus, NodeState> builder().put(VAppStatus.OFF, NodeState.TERMINATED).put(
                     VAppStatus.ON, NodeState.RUNNING).put(VAppStatus.RESOLVED, NodeState.PENDING)
            .put(VAppStatus.SUSPENDED, NodeState.SUSPENDED).put(VAppStatus.UNRESOLVED,
                     NodeState.PENDING).build();

   @Inject
   public VCloudComputeService(VCloudClient client, Provider<Set<? extends Image>> images,
            Provider<SortedSet<? extends Size>> sizes,
            Provider<Set<? extends VCloudTemplate>> templates, Predicate<String> successTester,
            Predicate<InetSocketAddress> socketTester) {
      this.taskTester = successTester;
      this.client = client;
      this.images = images;
      this.sizes = sizes;
      this.templates = templates;
      this.socketTester = socketTester;
   }

   @Override
   public CreateNodeResponse runNode(String name, Template template) {
      return this.runNode(name, template, RunNodeOptions.NONE);
   }

   @Override
   public CreateNodeResponse runNode(String name, Template template, RunNodeOptions options) {
      checkNotNull(template.getImage().getLocation(), "location");
      Map<String, String> metaMap = start(template.getImage().getLocation(), name, template
               .getImage().getId(), template.getSize().getCores(), template.getSize().getRam(),
               template.getSize().getDisk() * 1024 * 1024l, ImmutableMap.<String, String> of(),
               options.getOpenPorts());
      VApp vApp = client.getVApp(metaMap.get("id"));
      CreateNodeResponse node = newCreateNodeResponse(template, metaMap, vApp);
      if (options.getRunScript() != null) {
         checkState(this.sshFactory != null, "runScript requested, but no SshModule configured");
         runScriptOnNode(node, options.getRunScript());
      }
      return node;
   }

   private void runScriptOnNode(CreateNodeResponse node, byte[] script) {
      InetSocketAddress socket = new InetSocketAddress(node.getPublicAddresses().last(), node
               .getLoginPort());
      socketTester.apply(socket);
      SshClient ssh = isKeyBasedAuth(node) ? sshFactory.create(socket,
               node.getCredentials().account, node.getCredentials().key.getBytes()) : sshFactory
               .create(socket, node.getCredentials().account, node.getCredentials().key);
      try {
         ssh.connect();
         String scriptName = node.getId() + ".sh";
         ssh.put(scriptName, new ByteArrayInputStream(script));
         ssh.exec("chmod 755 " + scriptName);
         if (node.getCredentials().account.equals("root")) {
            logger.debug(">> running %s as %s", scriptName, node.getCredentials().account);
            logger.debug("<< complete(%d)", ssh.exec("./" + scriptName).getExitCode());
         } else if (isKeyBasedAuth(node)) {
            logger.debug(">> running sudo %s as %s", scriptName, node.getCredentials().account);
            logger.debug("<< complete(%d)", ssh.exec("sudo ./" + scriptName).getExitCode());
         } else {
            logger.debug(">> running sudo -S %s as %s", scriptName, node.getCredentials().account);
            logger.debug("<< complete(%d)", ssh.exec(
                     String.format("echo %s|sudo -S ./%s", node.getCredentials().key, scriptName))
                     .getExitCode());
         }
      } finally {
         if (ssh != null)
            ssh.disconnect();
      }
   }

   private boolean isKeyBasedAuth(CreateNodeResponse node) {
      return node.getCredentials().key.startsWith("-----BEGIN RSA PRIVATE KEY-----");
   }

   protected CreateNodeResponse newCreateNodeResponse(Template template,
            Map<String, String> metaMap, VApp vApp) {
      return new CreateNodeResponseImpl(vApp.getId(), vApp.getName(), template.getImage()
               .getLocation(), vApp.getLocation(), ImmutableMap.<String, String> of(),
               vAppStatusToNodeState.get(vApp.getStatus()), getPublicAddresses(vApp.getId()),
               getPrivateAddresses(vApp.getId()), 22, LoginType.SSH, new Credentials(metaMap
                        .get("username"), metaMap.get("password")), ImmutableMap
                        .<String, String> of());
   }

   @Override
   public NodeMetadata getNodeMetadata(ComputeMetadata node) {
      checkArgument(node.getType() == ComputeType.NODE, "this is only valid for nodes, not "
               + node.getType());
      return getNodeMetadataByIdInVDC(checkNotNull(node.getLocation(), "location"), checkNotNull(
               node.getId(), "node.id"));
   }

   private NodeMetadata getNodeMetadataByIdInVDC(String vDCId, String id) {
      VApp vApp = client.getVApp(id);
      return new NodeMetadataImpl(vApp.getId(), vApp.getName(), vDCId, vApp.getLocation(),
               ImmutableMap.<String, String> of(), vAppStatusToNodeState.get(vApp.getStatus()),
               vApp.getNetworkToAddresses().values(), ImmutableSet.<InetAddress> of(), 22,
               LoginType.SSH, ImmutableMap.<String, String> of());
   }

   @Override
   public Set<ComputeMetadata> listNodes() {
      Set<ComputeMetadata> nodes = Sets.newHashSet();
      for (NamedResource vdc : client.getDefaultOrganization().getVDCs().values()) {
         for (NamedResource resource : client.getVDC(vdc.getId()).getResourceEntities().values()) {
            if (resource.getType().equals(VCloudMediaType.VAPP_XML)) {
               nodes.add(getNodeMetadataByIdInVDC(vdc.getId(), resource.getId()));
            }
         }
      }
      return nodes;
   }

   @Override
   public void destroyNode(ComputeMetadata node) {
      checkArgument(node.getType() == ComputeType.NODE, "this is only valid for nodes, not "
               + node.getType());
      stop(checkNotNull(node.getId(), "node.id"));
   }

   @Override
   public Template createTemplateInLocation(String location) {
      return new VCloudTemplate(client, images.get(), sizes.get(), location);
   }

   @Override
   public SortedSet<? extends Size> listSizes() {
      return sizes.get();
   }

   @Override
   public Set<? extends Template> listTemplates() {
      return templates.get();
   }

   private final Predicate<String> taskTester;

   public Map<String, String> start(String vDCId, String name, String templateId, int minCores,
            int minMegs, Long diskSize, Map<String, String> properties, int... portsToOpen) {
      logger
               .debug(
                        ">> instantiating vApp vDC(%s) name(%s) template(%s)  minCores(%d) minMegs(%d) diskSize(%d) properties(%s) ",
                        vDCId, name, templateId, minCores, minMegs, diskSize, properties);
      InstantiateVAppTemplateOptions options = processorCount(minCores).memory(minMegs)
               .productProperties(properties);
      if (diskSize != null)
         options.disk(diskSize);
      VApp vAppResponse = client.instantiateVAppTemplateInVDC(vDCId, name, templateId, options);
      logger.debug("<< instantiated VApp(%s)", vAppResponse.getId());

      logger.debug(">> deploying vApp(%s)", vAppResponse.getId());
      VApp vApp = blockUntilVAppStatusOrThrowException(vAppResponse, client.deployVApp(vAppResponse
               .getId()), "deploy", ImmutableSet.of(VAppStatus.OFF, VAppStatus.ON));
      logger.debug("<< deployed vApp(%s)", vApp.getId());

      logger.debug(">> powering vApp(%s)", vApp.getId());
      vApp = blockUntilVAppStatusOrThrowException(vApp, client.powerOnVApp(vApp.getId()),
               "powerOn", ImmutableSet.of(VAppStatus.ON));
      logger.debug("<< on vApp(%s)", vApp.getId());
      Map<String, String> response = parseResponse(vAppResponse);
      checkState(response.containsKey("id"), "bad configuration: [id] should be in response");
      checkState(response.containsKey("username"),
               "bad configuration: [username] should be in response");
      checkState(response.containsKey("password"),
               "bad configuration: [password] should be in response");
      return response;
   }

   protected Map<String, String> parseResponse(VApp vAppResponse) {
      return ImmutableMap.<String, String> of("id", vAppResponse.getId(), "username", "",
               "password", "");
   }

   public void reboot(String id) {
      VApp vApp = client.getVApp(id);
      logger.debug(">> rebooting vApp(%s)", vApp.getId());
      blockUntilVAppStatusOrThrowException(vApp, client.resetVApp(vApp.getId()), "reset",
               ImmutableSet.of(VAppStatus.ON));
      logger.debug("<< on vApp(%s)", vApp.getId());
   }

   public void stop(String id) {
      VApp vApp = client.getVApp(id);
      if (vApp.getStatus() != VAppStatus.OFF) {
         logger.debug(">> powering off vApp(%s), current status: %s", vApp.getId(), vApp
                  .getStatus());
         blockUntilVAppStatusOrThrowException(vApp, client.powerOffVApp(vApp.getId()), "powerOff",
                  ImmutableSet.of(VAppStatus.OFF));
         logger.debug("<< off vApp(%s)", vApp.getId());
      }
      logger.debug(">> deleting vApp(%s)", vApp.getId());
      client.deleteVApp(id);
      logger.debug("<< deleted vApp(%s)", vApp.getId());
   }

   private VApp blockUntilVAppStatusOrThrowException(VApp vApp, Task deployTask, String taskType,
            Set<VAppStatus> acceptableStatuses) {
      if (!taskTester.apply(deployTask.getId())) {
         throw new TaskException(taskType, vApp, deployTask);
      }

      vApp = client.getVApp(vApp.getId());
      if (!acceptableStatuses.contains(vApp.getStatus())) {
         throw new VAppException(String.format("vApp %s status %s should be %s after %s", vApp
                  .getId(), vApp.getStatus(), acceptableStatuses, taskType), vApp);
      }
      return vApp;
   }

   public static class TaskException extends VAppException {

      private final Task task;
      /** The serialVersionUID */
      private static final long serialVersionUID = 251801929573211256L;

      public TaskException(String type, VApp vApp, Task task) {
         super(String.format("failed to %s vApp %s status %s;task %s status %s", type,
                  vApp.getId(), vApp.getStatus(), task.getLocation(), task.getStatus()), vApp);
         this.task = task;
      }

      public Task getTask() {
         return task;
      }

   }

   public static class VAppException extends RuntimeException {

      private final VApp vApp;
      /** The serialVersionUID */
      private static final long serialVersionUID = 251801929573211256L;

      public VAppException(String message, VApp vApp) {
         super(message);
         this.vApp = vApp;
      }

      public VApp getvApp() {
         return vApp;
      }

   }

   @Override
   public Set<InetAddress> getPrivateAddresses(String id) {
      return ImmutableSet.of();
   }

   @Override
   public Set<InetAddress> getPublicAddresses(String id) {
      VApp vApp = client.getVApp(id);
      return Sets.newHashSet(vApp.getNetworkToAddresses().values());
   }

}