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
package org.jclouds.vcloud.compute.config;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.domain.OsFamily.UBUNTU;
import static org.jclouds.vcloud.options.InstantiateVAppTemplateOptions.Builder.processorCount;

import java.net.InetAddress;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.Architecture;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.ComputeType;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.domain.internal.ImageImpl;
import org.jclouds.compute.domain.internal.NodeMetadataImpl;
import org.jclouds.compute.domain.internal.SizeImpl;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.compute.internal.TemplateBuilderImpl;
import org.jclouds.compute.predicates.RunScriptRunning;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.AddNodeWithTagStrategy;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.concurrent.ConcurrentUtils;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.internal.LocationImpl;
import org.jclouds.logging.Logger;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.rest.RestContext;
import org.jclouds.ssh.SshClient;
import org.jclouds.vcloud.VCloudAsyncClient;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.compute.BaseVCloudComputeClient;
import org.jclouds.vcloud.compute.VCloudComputeClient;
import org.jclouds.vcloud.config.VCloudContextModule;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.ResourceAllocation;
import org.jclouds.vcloud.domain.ResourceType;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VAppStatus;
import org.jclouds.vcloud.domain.VAppTemplate;
import org.jclouds.vcloud.domain.VDC;
import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Provides;

/**
 * Configures the {@link VCloudComputeServiceContext}; requires {@link BaseVCloudComputeClient}
 * bound.
 * 
 * @author Adrian Cole
 */
public class VCloudComputeServiceContextModule extends VCloudContextModule {

   @Singleton
   @Provides
   Map<VAppStatus, NodeState> provideVAppStatusToNodeState() {
      return ImmutableMap.<VAppStatus, NodeState> builder().put(VAppStatus.OFF,
               NodeState.TERMINATED).put(VAppStatus.ON, NodeState.RUNNING).put(VAppStatus.RESOLVED,
               NodeState.PENDING).put(VAppStatus.SUSPENDED, NodeState.SUSPENDED).put(
               VAppStatus.UNRESOLVED, NodeState.PENDING).build();
   }

   @Provides
   protected TemplateBuilder provideTemplate(TemplateBuilderImpl template) {
      return template.osFamily(UBUNTU);
   }

   @Override
   protected void configure() {
      super.configure();
      bind(AddNodeWithTagStrategy.class).to(VCloudAddNodeWithTagStrategy.class);
      bind(ListNodesStrategy.class).to(VCloudListNodesStrategy.class);
      bind(GetNodeMetadataStrategy.class).to(VCloudGetNodeMetadataStrategy.class);
      bind(RebootNodeStrategy.class).to(VCloudRebootNodeStrategy.class);
      bind(DestroyNodeStrategy.class).to(VCloudDestroyNodeStrategy.class);
   }

   @Provides
   @Named("NAMING_CONVENTION")
   @Singleton
   String provideNamingConvention() {
      return "%s-%d";
   }

   @Singleton
   public static class VCloudRebootNodeStrategy implements RebootNodeStrategy {
      private final VCloudClient client;
      protected final Predicate<String> taskTester;

      @Inject
      protected VCloudRebootNodeStrategy(VCloudClient client, Predicate<String> taskTester) {
         this.client = client;
         this.taskTester = taskTester;
      }

      @Override
      public boolean execute(ComputeMetadata node) {
         Task task = client.resetVApp(node.getId());
         return taskTester.apply(task.getId());
      }

   }

   @Singleton
   public static class VCloudDestroyNodeStrategy implements DestroyNodeStrategy {
      protected final VCloudComputeClient computeClient;

      @Inject
      protected VCloudDestroyNodeStrategy(VCloudComputeClient computeClient) {
         this.computeClient = computeClient;
      }

      @Override
      public boolean execute(ComputeMetadata node) {
         computeClient.stop(checkNotNull(node.getId(), "node.id"));
         return true;
      }

   }

   @Singleton
   public static class VCloudAddNodeWithTagStrategy implements AddNodeWithTagStrategy {
      protected final VCloudClient client;
      protected final VCloudComputeClient computeClient;
      protected final Map<VAppStatus, NodeState> vAppStatusToNodeState;

      @Inject
      protected VCloudAddNodeWithTagStrategy(VCloudClient client,
               VCloudComputeClient computeClient, Map<VAppStatus, NodeState> vAppStatusToNodeState) {
         super();
         this.client = client;
         this.computeClient = computeClient;
         this.vAppStatusToNodeState = vAppStatusToNodeState;
      }

      @Override
      public NodeMetadata execute(String tag, String name, Template template) {

         InstantiateVAppTemplateOptions options = processorCount(
                  Double.valueOf(template.getSize().getCores()).intValue()).memory(
                  template.getSize().getRam()).disk(template.getSize().getDisk() * 1024 * 1024l);

         options.networkName("templateId=" + template.getImage().getId());

         Map<String, String> metaMap = computeClient.start(template.getLocation().getId(), name,
                  template.getImage().getId(), options, template.getOptions().getInboundPorts());
         VApp vApp = client.getVApp(metaMap.get("id"));
         return newCreateNodeResponse(tag, template, metaMap, vApp);
      }

      protected NodeMetadata newCreateNodeResponse(String tag, Template template,
               Map<String, String> metaMap, VApp vApp) {
         return new NodeMetadataImpl(vApp.getId(), vApp.getName(), template.getLocation().getId(),
                  vApp.getLocation(), ImmutableMap.<String, String> of(), tag,
                  vAppStatusToNodeState.get(vApp.getStatus()), computeClient
                           .getPublicAddresses(vApp.getId()), computeClient
                           .getPrivateAddresses(vApp.getId()), ImmutableMap.<String, String> of(),
                  new Credentials(metaMap.get("username"), metaMap.get("password")));
      }

   }

   @Singleton
   public static class VCloudListNodesStrategy extends VCloudGetNodeMetadata implements
            ListNodesStrategy {
      @Resource
      protected Logger logger = Logger.NULL;

      @Inject
      protected VCloudListNodesStrategy(VCloudClient client, VCloudComputeClient computeClient,
               Map<VAppStatus, NodeState> vAppStatusToNodeState) {
         super(client, computeClient, vAppStatusToNodeState);
      }

      @Override
      public Iterable<? extends ComputeMetadata> execute() {
         Set<ComputeMetadata> nodes = Sets.newHashSet();
         for (NamedResource vdc : client.getDefaultOrganization().getVDCs().values()) {
            for (NamedResource resource : client.getVDC(vdc.getId()).getResourceEntities().values()) {
               if (resource.getType().equals(VCloudMediaType.VAPP_XML)) {
                  addVAppToSetRetryingIfNotYetPresent(nodes, vdc, resource);
               }
            }
         }
         return nodes;
      }

      @VisibleForTesting
      void addVAppToSetRetryingIfNotYetPresent(Set<ComputeMetadata> nodes, NamedResource vdc,
               NamedResource resource) {
         NodeMetadata node = null;
         int i = 0;
         while (node == null && i++ < 3) {
            try {
               node = getNodeMetadataByIdInVDC(vdc.getId(), resource.getId());
               nodes.add(node);
            } catch (NullPointerException e) {
               logger.warn("vApp %s not yet present in vdc %s", resource.getId(), vdc.getId());
            }
         }
      }

   }

   @Singleton
   public static class VCloudGetNodeMetadataStrategy extends VCloudGetNodeMetadata implements
            GetNodeMetadataStrategy {

      @Inject
      protected VCloudGetNodeMetadataStrategy(VCloudClient client,
               VCloudComputeClient computeClient, Map<VAppStatus, NodeState> vAppStatusToNodeState) {
         super(client, computeClient, vAppStatusToNodeState);
      }

      @Override
      public NodeMetadata execute(ComputeMetadata node) {
         checkArgument(node.getType() == ComputeType.NODE, "this is only valid for nodes, not "
                  + node.getType());
         return getNodeMetadataByIdInVDC(checkNotNull(node.getLocationId(), "location"),
                  checkNotNull(node.getId(), "node.id"));
      }

   }

   @Singleton
   public static class VCloudGetNodeMetadata {

      protected final VCloudClient client;
      protected final VCloudComputeClient computeClient;

      protected final Map<VAppStatus, NodeState> vAppStatusToNodeState;

      @Inject
      protected VCloudGetNodeMetadata(VCloudClient client, VCloudComputeClient computeClient,
               Map<VAppStatus, NodeState> vAppStatusToNodeState) {
         this.client = client;
         this.computeClient = computeClient;
         this.vAppStatusToNodeState = vAppStatusToNodeState;
      }

      protected NodeMetadata getNodeMetadataByIdInVDC(String vDCId, String id) {
         VApp vApp = client.getVApp(id);
         String tag = vApp.getName().replaceAll("-[0-9]+", "");
         return new NodeMetadataImpl(vApp.getId(), vApp.getName(), vDCId, vApp.getLocation(),
                  ImmutableMap.<String, String> of(), tag, vAppStatusToNodeState.get(vApp
                           .getStatus()), computeClient.getPublicAddresses(id), computeClient
                           .getPrivateAddresses(id), getExtra(vApp), null);
      }
   }

   private static Map<String, String> getExtra(VApp vApp) {
      Map<String, String> extra = Maps.newHashMap();
      extra.put("memory/mb", Iterables.getOnlyElement(
               vApp.getResourceAllocationByType().get(ResourceType.MEMORY)).getVirtualQuantity()
               + "");
      extra.put("processor/count", Iterables.getOnlyElement(
               vApp.getResourceAllocationByType().get(ResourceType.PROCESSOR)).getVirtualQuantity()
               + "");
      for (ResourceAllocation disk : vApp.getResourceAllocationByType().get(ResourceType.PROCESSOR)) {
         extra.put(String.format("disk_drive/%s/kb", disk.getId()), disk.getVirtualQuantity() + "");
      }

      for (Entry<String, InetAddress> net : vApp.getNetworkToAddresses().entries()) {
         extra.put(String.format("network/%s/ip", net.getKey()), net.getValue().getHostAddress());
      }
      return extra;
   }

   @Provides
   @Singleton
   @Named("NOT_RUNNING")
   protected Predicate<SshClient> runScriptRunning(RunScriptRunning stateRunning) {
      return new RetryablePredicate<SshClient>(Predicates.not(stateRunning), 600, 3,
               TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   protected ComputeServiceContext provideContext(ComputeService computeService,
            RestContext<VCloudAsyncClient, VCloudClient> context) {
      return new ComputeServiceContextImpl<VCloudAsyncClient, VCloudClient>(computeService, context);
   }

   protected static class LogHolder {
      @Resource
      @Named(ComputeServiceConstants.COMPUTE_LOGGER)
      public Logger logger = Logger.NULL;
   }

   @Provides
   @Singleton
   protected Map<String, ? extends Image> provideImages(final VCloudClient client,
            LogHolder holder, @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor,
            Function<ComputeMetadata, String> indexer) throws InterruptedException,
            ExecutionException, TimeoutException {
      final Set<Image> images = Sets.newHashSet();
      holder.logger.debug(">> providing vAppTemplates");
      for (final NamedResource vDC : client.getDefaultOrganization().getVDCs().values()) {
         Map<String, NamedResource> resources = client.getVDC(vDC.getId()).getResourceEntities();
         Map<String, ListenableFuture<Void>> responses = Maps.newHashMap();

         for (final NamedResource resource : resources.values()) {
            if (resource.getType().equals(VCloudMediaType.VAPPTEMPLATE_XML)) {
               responses.put(resource.getName(), ConcurrentUtils.makeListenable(executor
                        .submit(new Callable<Void>() {
                           @Override
                           public Void call() throws Exception {
                              OsFamily myOs = null;
                              for (OsFamily os : OsFamily.values()) {
                                 if (resource.getName().toLowerCase().replaceAll("\\s", "")
                                          .indexOf(os.toString()) != -1) {
                                    myOs = os;
                                 }
                              }
                              Architecture arch = resource.getName().indexOf("64") == -1 ? Architecture.X86_32
                                       : Architecture.X86_64;
                              VAppTemplate template = client.getVAppTemplate(resource.getId());
                              images.add(new ImageImpl(resource.getId(), template.getName(), vDC
                                       .getId(), template.getLocation(), ImmutableMap
                                       .<String, String> of(), template.getDescription(), "", myOs,
                                       template.getName(), arch, new Credentials("root", null)));
                              return null;
                           }
                        }), executor));

            }
         }
         ConcurrentUtils.awaitCompletion(responses, executor, null, holder.logger,
                  "vAppTemplates in " + vDC);
      }
      return Maps.uniqueIndex(images, indexer);
   }

   @Provides
   @Singleton
   protected Function<ComputeMetadata, String> indexer() {
      return new Function<ComputeMetadata, String>() {
         @Override
         public String apply(ComputeMetadata from) {
            return from.getId();
         }
      };
   }

   @Provides
   @Singleton
   Map<String, ? extends Location> provideLocations(final VCloudClient client) {
      return Maps.uniqueIndex(Iterables.transform(client.getDefaultOrganization().getVDCs()
               .values(), new Function<NamedResource, Location>() {

         @Override
         public Location apply(NamedResource from) {
            VDC vdc = client.getVDC(from.getId());
            return new LocationImpl(LocationScope.ZONE, vdc.getId(), vdc.getName(), null, true);
         }

      }), new Function<Location, String>() {

         @Override
         public String apply(Location from) {
            return from.getId();
         }

      });
   }

   @Provides
   @Singleton
   Location getVDC(VCloudClient client, Map<String, ? extends Location> locations) {
      return locations.get(client.getDefaultVDC().getId());
   }

   @Provides
   @Singleton
   protected Map<String, ? extends Size> provideSizes(Function<ComputeMetadata, String> indexer,
            VCloudClient client, Map<String, ? extends Image> images, LogHolder holder,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor)
            throws InterruptedException, TimeoutException, ExecutionException {
      Set<Size> sizes = Sets.newHashSet();
      for (int cpus : new int[] { 1, 2, 4 })
         for (int ram : new int[] { 512, 1024, 2048, 4096, 8192, 16384 })
            sizes.add(new SizeImpl(String.format("cpu=%d,ram=%s,disk=%d", cpus, ram, 10), null,
                     null, null, ImmutableMap.<String, String> of(), cpus, ram, 10, ImmutableSet
                              .<Architecture> of(Architecture.X86_32, Architecture.X86_64)));
      return Maps.uniqueIndex(sizes, indexer);
   }
}
