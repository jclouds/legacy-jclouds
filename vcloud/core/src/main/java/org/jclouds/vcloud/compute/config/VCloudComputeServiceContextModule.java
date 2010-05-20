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

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.domain.OsFamily.UBUNTU;
import static org.jclouds.vcloud.options.InstantiateVAppTemplateOptions.Builder.processorCount;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
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
import org.jclouds.compute.domain.internal.ComputeMetadataImpl;
import org.jclouds.compute.domain.internal.ImageImpl;
import org.jclouds.compute.domain.internal.NodeMetadataImpl;
import org.jclouds.compute.domain.internal.SizeImpl;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.compute.predicates.ScriptStatusReturnsZero;
import org.jclouds.compute.predicates.ScriptStatusReturnsZero.CommandUsingClient;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.AddNodeWithTagStrategy;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.PopulateDefaultLoginCredentialsForImageStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.compute.strategy.RunNodesAndAddToSetStrategy;
import org.jclouds.concurrent.ConcurrentUtils;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.internal.LocationImpl;
import org.jclouds.logging.Logger;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.rest.RestContext;
import org.jclouds.vcloud.VCloudAsyncClient;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.compute.BaseVCloudComputeClient;
import org.jclouds.vcloud.compute.VCloudComputeClient;
import org.jclouds.vcloud.compute.functions.GetExtra;
import org.jclouds.vcloud.compute.functions.VCloudGetNodeMetadata;
import org.jclouds.vcloud.compute.strategy.EncodeTemplateIdIntoNameRunNodesAndAddToSetStrategy;
import org.jclouds.vcloud.config.VCloudContextModule;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VAppStatus;
import org.jclouds.vcloud.domain.VAppTemplate;
import org.jclouds.vcloud.internal.VCloudLoginAsyncClient.VCloudSession;
import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
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

   private final String providerName;

   public VCloudComputeServiceContextModule(String providerName) {
      this.providerName = providerName;
   }

   @Singleton
   @Provides
   Map<VAppStatus, NodeState> provideVAppStatusToNodeState() {
      return ImmutableMap.<VAppStatus, NodeState> builder().put(VAppStatus.OFF,
               NodeState.TERMINATED).put(VAppStatus.ON, NodeState.RUNNING).put(VAppStatus.RESOLVED,
               NodeState.PENDING).put(VAppStatus.SUSPENDED, NodeState.SUSPENDED).put(
               VAppStatus.UNRESOLVED, NodeState.PENDING).build();
   }

   @Provides
   @Named("DEFAULT")
   protected TemplateBuilder provideTemplate(TemplateBuilder template) {
      return template.osFamily(UBUNTU);
   }

   @Override
   protected void configure() {
      super.configure();
      bind(AddNodeWithTagStrategy.class).to(VCloudAddNodeWithTagStrategy.class);
      bind(RunNodesAndAddToSetStrategy.class).to(
               EncodeTemplateIdIntoNameRunNodesAndAddToSetStrategy.class);
      bind(ListNodesStrategy.class).to(VCloudListNodesStrategy.class);
      bind(GetNodeMetadataStrategy.class).to(VCloudGetNodeMetadataStrategy.class);
      bind(RebootNodeStrategy.class).to(VCloudRebootNodeStrategy.class);
      bind(DestroyNodeStrategy.class).to(VCloudDestroyNodeStrategy.class);
   }

   @Provides
   @Named("NAMING_CONVENTION")
   @Singleton
   protected String provideNamingConvention() {
      return "%s-%s%s";
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
      public boolean execute(String id) {
         Task task = client.resetVApp(checkNotNull(id, "node.id"));
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
      public boolean execute(String id) {
         computeClient.stop(checkNotNull(id, "node.id"));
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
         this.client = client;
         this.computeClient = computeClient;
         this.vAppStatusToNodeState = vAppStatusToNodeState;
      }

      @Override
      public NodeMetadata execute(String tag, String name, Template template) {

         InstantiateVAppTemplateOptions options = processorCount(
                  Double.valueOf(template.getSize().getCores()).intValue()).memory(
                  template.getSize().getRam()).disk(template.getSize().getDisk() * 1024 * 1024l);
         Map<String, String> metaMap = computeClient.start(template.getLocation().getId(), name,
                  template.getImage().getId(), options, template.getOptions().getInboundPorts());
         VApp vApp = client.getVApp(metaMap.get("id"));
         return newCreateNodeResponse(tag, template, metaMap, vApp);
      }

      protected NodeMetadata newCreateNodeResponse(String tag, Template template,
               Map<String, String> metaMap, VApp vApp) {
         return new NodeMetadataImpl(vApp.getId(), vApp.getName(), vApp.getId(), template
                  .getLocation(), vApp.getLocation(), ImmutableMap.<String, String> of(), tag,
                  template.getImage(), vAppStatusToNodeState.get(vApp.getStatus()), computeClient
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
               Map<VAppStatus, NodeState> vAppStatusToNodeState, GetExtra getExtra,
               FindLocationForResourceInVDC findLocationForResourceInVDC,
               Set<? extends Image> images) {
         super(client, computeClient, vAppStatusToNodeState, getExtra,
                  findLocationForResourceInVDC, images);
      }

      @Override
      public Iterable<ComputeMetadata> list() {
         Set<ComputeMetadata> nodes = Sets.newHashSet();
         for (NamedResource vdc : client.getDefaultOrganization().getVDCs().values()) {
            for (NamedResource resource : client.getVDC(vdc.getId()).getResourceEntities().values()) {
               if (resource.getType().equals(VCloudMediaType.VAPP_XML)) {
                  nodes.add(convertVAppToComputeMetadata(vdc, resource));
               }
            }
         }
         return nodes;
      }

      private ComputeMetadata convertVAppToComputeMetadata(NamedResource vdc, NamedResource resource) {
         Location location = findLocationForResourceInVDC.apply(resource, vdc.getId());
         return new ComputeMetadataImpl(ComputeType.NODE, resource.getId(), resource.getName(),
                  resource.getId(), location, null, ImmutableMap.<String, String> of());
      }

      @Override
      public Iterable<NodeMetadata> listDetailsOnNodesMatching(Predicate<ComputeMetadata> filter) {
         Set<NodeMetadata> nodes = Sets.newHashSet();
         for (NamedResource vdc : client.getDefaultOrganization().getVDCs().values()) {
            for (NamedResource resource : client.getVDC(vdc.getId()).getResourceEntities().values()) {
               if (resource.getType().equals(VCloudMediaType.VAPP_XML)
                        && filter.apply(convertVAppToComputeMetadata(vdc, resource))) {
                  addVAppToSetRetryingIfNotYetPresent(nodes, vdc, resource);
               }
            }
         }
         return nodes;
      }

      @VisibleForTesting
      void addVAppToSetRetryingIfNotYetPresent(Set<NodeMetadata> nodes, NamedResource vdc,
               NamedResource resource) {
         NodeMetadata node = null;
         int i = 0;
         while (node == null && i++ < 3) {
            try {
               node = getNodeMetadataByIdInVDC(resource.getId());
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
               VCloudComputeClient computeClient, Map<VAppStatus, NodeState> vAppStatusToNodeState,
               GetExtra getExtra, FindLocationForResourceInVDC findLocationForResourceInVDC,
               Set<? extends Image> images) {
         super(client, computeClient, vAppStatusToNodeState, getExtra,
                  findLocationForResourceInVDC, images);
      }

      @Override
      public NodeMetadata execute(String id) {
         return getNodeMetadataByIdInVDC(checkNotNull(id, "node.id"));
      }

   }

   @Provides
   @Singleton
   @Named("NOT_RUNNING")
   protected Predicate<CommandUsingClient> runScriptRunning(ScriptStatusReturnsZero stateRunning) {
      return new RetryablePredicate<CommandUsingClient>(Predicates.not(stateRunning), 600, 3,
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
   protected Set<? extends Image> provideImages(final VCloudClient client,
            final PopulateDefaultLoginCredentialsForImageStrategy credentialsProvider,
            LogHolder holder, final FindLocationForResourceInVDC findLocationForResourceInVDC,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor,
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

                              Location location = findLocationForResourceInVDC.apply(resource, vDC
                                       .getId());

                              images.add(new ImageImpl(resource.getId(), template.getName(),
                                       resource.getId(), location, template.getLocation(),
                                       ImmutableMap.<String, String> of(), template
                                                .getDescription(), "", myOs, template.getName(),
                                       arch, new Credentials("root", null)));
                              return null;
                           }
                        }), executor));

            }
         }
         ConcurrentUtils.awaitCompletion(responses, executor, null, holder.logger,
                  "vAppTemplates in " + vDC);
      }
      return images;
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
   Set<? extends Location> provideLocations(Supplier<VCloudSession> cache, VCloudClient client) {
      Location provider = new LocationImpl(LocationScope.PROVIDER, providerName, providerName, null);
      Set<Location> locations = Sets.newLinkedHashSet();

      for (NamedResource org : cache.get().getOrgs().values()) {
         Location orgL = new LocationImpl(LocationScope.REGION, org.getId(), org.getName(),
                  provider);
         for (NamedResource vdc : client.getOrganization(org.getId()).getVDCs().values()) {
            locations.add(new LocationImpl(LocationScope.ZONE, vdc.getId(), vdc.getName(), orgL));
         }
      }
      return locations;
   }

   @Provides
   @Singleton
   Location getVDC(VCloudClient client, Set<? extends Location> locations) {
      final String vdc = client.getDefaultVDC().getId();
      return Iterables.find(locations, new Predicate<Location>() {

         @Override
         public boolean apply(Location input) {
            return input.getId().equals(vdc);
         }

      });
   }

   @Provides
   @Singleton
   protected Set<? extends Size> provideSizes(Function<ComputeMetadata, String> indexer,
            VCloudClient client, Set<? extends Image> images, LogHolder holder,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor)
            throws InterruptedException, TimeoutException, ExecutionException {
      Set<Size> sizes = Sets.newHashSet();
      for (int cpus : new int[] { 1, 2, 4 })
         for (int ram : new int[] { 512, 1024, 2048, 4096, 8192, 16384 }) {
            String id = String.format("cpu=%d,ram=%s,disk=%d", cpus, ram, 10);
            sizes.add(new SizeImpl(id, null, id, null, null, ImmutableMap.<String, String> of(),
                     cpus, ram, 10, ImmutableSet.<Architecture> of(Architecture.X86_32,
                              Architecture.X86_64)));
         }
      return sizes;
   }

   public static class FindLocationForResourceInVDC {

      @Resource
      protected Logger logger = Logger.NULL;

      final Set<? extends Location> locations;
      final Location defaultLocation;

      @Inject
      public FindLocationForResourceInVDC(Set<? extends Location> locations,
               Location defaultLocation) {
         this.locations = locations;
         this.defaultLocation = defaultLocation;
      }

      public Location apply(final NamedResource resource, final String vdcId) {
         Location location = null;
         try {
            location = Iterables.find(locations, new Predicate<Location>() {

               @Override
               public boolean apply(Location input) {
                  return input.getId().equals(vdcId);
               }

            });
         } catch (NoSuchElementException e) {
            logger.error("unknown vdc %s for %s %s; not in %s", vdcId, resource.getType(), resource
                     .getId(), locations);
            location = new LocationImpl(LocationScope.ZONE, vdcId, vdcId, defaultLocation
                     .getParent());
         }
         return location;
      }
   }
}
