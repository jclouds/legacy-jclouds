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
import static org.jclouds.compute.util.ComputeUtils.METADATA_TO_ID;
import static org.jclouds.concurrent.ConcurrentUtils.awaitCompletion;
import static org.jclouds.concurrent.ConcurrentUtils.makeListenable;
import static org.jclouds.vcloud.options.InstantiateVAppTemplateOptions.Builder.processorCount;

import java.net.InetAddress;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.ComputeType;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeSet;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.domain.internal.NodeMetadataImpl;
import org.jclouds.compute.domain.internal.NodeSetImpl;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.util.ComputeUtils;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VAppStatus;
import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;

/**
 * @author Adrian Cole
 */
@Singleton
public class VCloudComputeService implements ComputeService, VCloudComputeClient {

   private static class NodeMatchesTag implements Predicate<NodeMetadata> {
      private final String tag;

      @Override
      public boolean apply(NodeMetadata from) {
         return from.getTag().equals(tag);
      }

      public NodeMatchesTag(String tag) {
         super();
         this.tag = tag;
      }
   };

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   private final VCloudClient client;
   protected final Provider<Map<String, ? extends Image>> images;
   protected final Provider<Map<String, ? extends Size>> sizes;
   protected final Provider<Map<String, ? extends Location>> locations;
   protected final Provider<TemplateBuilder> templateBuilderProvider;
   protected final ComputeUtils utils;
   protected final Predicate<String> taskTester;
   protected final Predicate<VApp> notFoundTester;
   protected final ExecutorService executor;

   protected static final Map<VAppStatus, NodeState> vAppStatusToNodeState = ImmutableMap
            .<VAppStatus, NodeState> builder().put(VAppStatus.OFF, NodeState.TERMINATED).put(
                     VAppStatus.ON, NodeState.RUNNING).put(VAppStatus.RESOLVED, NodeState.PENDING)
            .put(VAppStatus.SUSPENDED, NodeState.SUSPENDED).put(VAppStatus.UNRESOLVED,
                     NodeState.PENDING).build();

   @Inject
   public VCloudComputeService(VCloudClient client,
            Provider<TemplateBuilder> templateBuilderProvider,
            Provider<Map<String, ? extends Image>> images,
            Provider<Map<String, ? extends Size>> sizes,
            Provider<Map<String, ? extends Location>> locations, ComputeUtils utils,
            Predicate<String> successTester, @Named("NOT_FOUND") Predicate<VApp> notFoundTester,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor) {
      this.taskTester = successTester;
      this.client = client;
      this.images = images;
      this.sizes = sizes;
      this.locations = locations;
      this.templateBuilderProvider = templateBuilderProvider;
      this.utils = utils;
      this.notFoundTester = notFoundTester;
      this.executor = executor;
   }

   @Override
   public NodeSet runNodesWithTag(final String tag, int max, final Template template) {
      checkArgument(tag.indexOf('-') == -1, "tag cannot contain hyphens");
      checkNotNull(template.getLocation(), "location");
      // TODO: find next id
      final Set<NodeMetadata> nodes = Sets.newHashSet();
      Set<ListenableFuture<Void>> responses = Sets.newHashSet();
      for (int i = 0; i < max; i++) {
         final String name = String.format("%s-%d", tag, i + 1);
         responses.add(makeListenable(executor.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
               runVAppAndAddToSet(name, tag, template, nodes);
               return null;
            }
         }), executor));
      }
      awaitCompletion(responses, executor, null, logger, "nodes");
      return new NodeSetImpl(nodes);
   }

   private void runVAppAndAddToSet(String name, String tag, Template template,
            Set<NodeMetadata> nodes) {
      Map<String, String> metaMap = start(template.getLocation().getId(), name, template.getImage()
               .getId(), template.getSize().getCores(), template.getSize().getRam(), template
               .getSize().getDisk() * 1024 * 1024l, ImmutableMap.<String, String> of(), template
               .getOptions().getInboundPorts());
      VApp vApp = client.getVApp(metaMap.get("id"));
      NodeMetadata node = newCreateNodeResponse(tag, template, metaMap, vApp);
      nodes.add(node);
      if (template.getOptions().getRunScript() != null) {
         utils.runScriptOnNode(node, template.getOptions().getRunScript());
      }
   }

   protected NodeMetadata newCreateNodeResponse(String tag, Template template,
            Map<String, String> metaMap, VApp vApp) {
      return new NodeMetadataImpl(vApp.getId(), vApp.getName(), template.getLocation().getId(),
               vApp.getLocation(), ImmutableMap.<String, String> of(), tag, vAppStatusToNodeState
                        .get(vApp.getStatus()), getPublicAddresses(vApp.getId()),
               getPrivateAddresses(vApp.getId()), ImmutableMap.<String, String> of(),
               new Credentials(metaMap.get("username"), metaMap.get("password")));
   }

   @Override
   public NodeMetadata getNodeMetadata(ComputeMetadata node) {
      checkArgument(node.getType() == ComputeType.NODE, "this is only valid for nodes, not "
               + node.getType());
      return getNodeMetadataByIdInVDC(checkNotNull(node.getLocationId(), "location"), checkNotNull(
               node.getId(), "node.id"));
   }

   protected NodeMetadata getNodeMetadataByIdInVDC(String vDCId, String id) {
      VApp vApp = client.getVApp(id);
      String tag = vApp.getName().replaceAll("-[0-9]+", "");
      return new NodeMetadataImpl(vApp.getId(), vApp.getName(), vDCId, vApp.getLocation(),
               ImmutableMap.<String, String> of(), tag,
               vAppStatusToNodeState.get(vApp.getStatus()), vApp.getNetworkToAddresses().values(),
               ImmutableSet.<InetAddress> of(), ImmutableMap.<String, String> of(), null);
   }

   @Override
   public Map<String, ? extends ComputeMetadata> getNodes() {
      logger.debug(">> listing vApps");
      Map<String, ? extends ComputeMetadata> nodes = doGetNodes();
      logger.debug("<< list(%d)", nodes.size());
      return nodes;
   }

   private Map<String, ? extends ComputeMetadata> doGetNodes() {
      Set<ComputeMetadata> nodes = Sets.newHashSet();
      for (NamedResource vdc : client.getDefaultOrganization().getVDCs().values()) {
         for (NamedResource resource : client.getVDC(vdc.getId()).getResourceEntities().values()) {
            if (resource.getType().equals(VCloudMediaType.VAPP_XML)) {
               nodes.add(getNodeMetadataByIdInVDC(vdc.getId(), resource.getId()));
            }
         }
      }
      return Maps.uniqueIndex(nodes, METADATA_TO_ID);
   }

   @Override
   public void destroyNode(ComputeMetadata node) {
      checkArgument(node.getType() == ComputeType.NODE, "this is only valid for nodes, not "
               + node.getType());
      stop(checkNotNull(node.getId(), "node.id"));
   }

   @Override
   public TemplateBuilder templateBuilder() {
      return templateBuilderProvider.get();
   }

   @Override
   public Map<String, ? extends Size> getSizes() {
      return sizes.get();
   }

   @Override
   public Map<String, ? extends Image> getImages() {
      return images.get();
   }

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

      Task task = client.deployVApp(vAppResponse.getId());
      if (!taskTester.apply(task.getId())) {
         throw new TaskException("deploy", vAppResponse, task);
      }

      logger.debug("<< deployed vApp(%s)", vAppResponse.getId());

      logger.debug(">> powering vApp(%s)", vAppResponse.getId());
      task = client.powerOnVApp(vAppResponse.getId());
      if (!taskTester.apply(task.getId())) {
         throw new TaskException("powerOn", vAppResponse, task);
      }
      logger.debug("<< on vApp(%s)", vAppResponse.getId());

      Map<String, String> response = parseResponse(vAppResponse);
      checkState(response.containsKey("id"), "bad configuration: [id] should be in response");
      checkState(response.containsKey("username"),
               "bad configuration: [username] should be in response");
      checkState(response.containsKey("password"),
               "bad configuration: [password] should be in response");
      return response;
   }

   protected Map<String, String> parseResponse(VApp vAppResponse) {
      Map<String, String> config = Maps.newLinkedHashMap();// Allows nulls
      config.put("id", vAppResponse.getId());
      config.put("username", null);
      config.put("password", null);
      return config;
   }

   public void reboot(String id) {
      VApp vApp = client.getVApp(id);
      logger.debug(">> resetting vApp(%s)", vApp.getId());
      Task task = client.resetVApp(vApp.getId());
      if (!taskTester.apply(task.getId())) {
         throw new TaskException("resetVApp", vApp, task);
      }
      logger.debug("<< on vApp(%s)", vApp.getId());
   }

   public void stop(String id) {
      VApp vApp = client.getVApp(id);
      if (vApp.getStatus() != VAppStatus.OFF) {
         logger.debug(">> powering off vApp(%s), current status: %s", vApp.getId(), vApp
                  .getStatus());
         Task task = client.powerOffVApp(vApp.getId());
         if (!taskTester.apply(task.getId())) {
            throw new TaskException("powerOff", vApp, task);
         }
         logger.debug("<< off vApp(%s)", vApp.getId());
      }
      logger.debug(">> deleting vApp(%s)", vApp.getId());
      client.deleteVApp(id);
      boolean successful = notFoundTester.apply(vApp);
      logger.debug("<< deleted vApp(%s) completed(%s)", vApp.getId(), successful);
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

   @Override
   public void destroyNodesWithTag(String tag) { // TODO parallel
      logger.debug(">> terminating servers by tag(%s)", tag);
      Set<ListenableFuture<Void>> responses = Sets.newHashSet();
      for (final NodeMetadata node : doGetNodes(tag)) {
         responses.add(makeListenable(executor.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
               destroyNode(node);
               return null;
            }
         }), executor));
      }
      awaitCompletion(responses, executor, null, logger, "nodes");
      logger.debug("<< destroyed");
   }

   @Override
   public Map<String, ? extends Location> getLocations() {
      return locations.get();
   }

   @Override
   public NodeSet getNodesWithTag(String tag) {
      logger.debug(">> listing servers by tag(%s)", tag);
      NodeSet nodes = doGetNodes(tag);
      logger.debug("<< list(%d)", nodes.size());
      return nodes;
   }

   protected NodeSet doGetNodes(final String tag) {
      Iterable<NodeMetadata> nodes = Iterables.filter(Iterables.transform(doGetNodes().values(),
               new Function<ComputeMetadata, NodeMetadata>() {

                  @Override
                  public NodeMetadata apply(ComputeMetadata from) {
                     return getNodeMetadata(from);
                  }

               }), new Predicate<NodeMetadata>() {

         @Override
         public boolean apply(NodeMetadata input) {
            return tag.equals(input.getTag());
         }

      });
      return new NodeSetImpl(Iterables.filter(nodes, new NodeMatchesTag(tag)));
   }
}