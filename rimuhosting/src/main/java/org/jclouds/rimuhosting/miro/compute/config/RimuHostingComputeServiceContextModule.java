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
package org.jclouds.rimuhosting.miro.compute.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.domain.OsFamily.UBUNTU;
import static org.jclouds.rimuhosting.miro.reference.RimuHostingConstants.PROPERTY_RIMUHOSTING_DEFAULT_DC;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.LoadBalancerService;
import org.jclouds.compute.config.ComputeServiceTimeoutsModule;
import org.jclouds.compute.domain.Architecture;
import org.jclouds.compute.domain.ComputeMetadata;
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
import org.jclouds.compute.predicates.ImagePredicates;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.AddNodeWithTagStrategy;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.internal.LocationImpl;
import org.jclouds.logging.Logger;
import org.jclouds.rest.annotations.Provider;
import org.jclouds.rimuhosting.miro.RimuHostingAsyncClient;
import org.jclouds.rimuhosting.miro.RimuHostingClient;
import org.jclouds.rimuhosting.miro.domain.NewServerResponse;
import org.jclouds.rimuhosting.miro.domain.PricingPlan;
import org.jclouds.rimuhosting.miro.domain.Server;
import org.jclouds.rimuhosting.miro.domain.internal.RunningState;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Providers;

/**
 * Configures the {@link RimuHostingComputeServiceContext}; requires
 * {@link RimuHostingComputeService} bound.
 * 
 * @author Adrian Cole
 */
public class RimuHostingComputeServiceContextModule extends AbstractModule {

   @Override
   protected void configure() {
      install(new ComputeServiceTimeoutsModule());
      bind(new TypeLiteral<Function<Server, NodeMetadata>>() {
      }).to(ServerToNodeMetadata.class);
      bind(LoadBalancerService.class).toProvider(Providers.<LoadBalancerService> of(null));
      bind(new TypeLiteral<ComputeServiceContext>() {
      })
               .to(
                        new TypeLiteral<ComputeServiceContextImpl<RimuHostingClient, RimuHostingAsyncClient>>() {
                        }).in(Scopes.SINGLETON);
      bind(new TypeLiteral<Function<Server, Iterable<String>>>() {
      }).to(ServerToPublicAddresses.class);
      bind(AddNodeWithTagStrategy.class).to(RimuHostingAddNodeWithTagStrategy.class);
      bind(ListNodesStrategy.class).to(RimuHostingListNodesStrategy.class);
      bind(GetNodeMetadataStrategy.class).to(RimuHostingGetNodeMetadataStrategy.class);
      bind(RebootNodeStrategy.class).to(RimuHostingRebootNodeStrategy.class);
      bind(DestroyNodeStrategy.class).to(RimuHostingDestroyNodeStrategy.class);
   }

   @Provides
   @Named("DEFAULT")
   protected TemplateBuilder provideTemplate(TemplateBuilder template) {
      return template.sizeId("MIRO1B").osFamily(UBUNTU).architecture(Architecture.X86_32)
               .imageNameMatches(".*10\\.?04.*");
   }

   @Provides
   @Named("NAMING_CONVENTION")
   @Singleton
   String provideNamingConvention() {
      return "%s-%s";
   }

   @Singleton
   public static class RimuHostingRebootNodeStrategy implements RebootNodeStrategy {
      private final RimuHostingClient client;
      private final GetNodeMetadataStrategy getNode;

      @Inject
      protected RimuHostingRebootNodeStrategy(RimuHostingClient client,
               GetNodeMetadataStrategy getNode) {
         this.client = client;
         this.getNode = getNode;
      }

      @Override
      public NodeMetadata execute(String id) {
         Long serverId = Long.parseLong(id);
         // if false server wasn't around in the first place
         client.restartServer(serverId).getState();
         return getNode.execute(id);
      }

   }

   @Singleton
   public static class RimuHostingDestroyNodeStrategy implements DestroyNodeStrategy {
      private final RimuHostingClient client;
      private final GetNodeMetadataStrategy getNode;

      @Inject
      protected RimuHostingDestroyNodeStrategy(RimuHostingClient client,
               GetNodeMetadataStrategy getNode) {
         this.client = client;
         this.getNode = getNode;
      }

      @Override
      public NodeMetadata execute(String id) {
         Long serverId = Long.parseLong(id);
         client.destroyServer(serverId);
         return getNode.execute(id);
      }

   }

   @Singleton
   public static class RimuHostingAddNodeWithTagStrategy implements AddNodeWithTagStrategy {
      private final RimuHostingClient client;
      private final Function<Server, Iterable<String>> getPublicAddresses;
      private final Map<RunningState, NodeState> runningStateToNodeState;

      @Inject
      protected RimuHostingAddNodeWithTagStrategy(RimuHostingClient client,
               Function<Server, Iterable<String>> getPublicAddresses,
               Map<RunningState, NodeState> runningStateToNodeState) {
         this.client = client;
         this.getPublicAddresses = getPublicAddresses;
         this.runningStateToNodeState = runningStateToNodeState;
      }

      @Override
      public NodeMetadata execute(String tag, String name, Template template) {
         NewServerResponse serverResponse = client.createServer(name, checkNotNull(template
                  .getImage().getProviderId(), "imageId"), checkNotNull(template.getSize()
                  .getProviderId(), "sizeId"));
         Server server = client.getServer(serverResponse.getServer().getId());
         NodeMetadata node = new NodeMetadataImpl(server.getId().toString(), name, server.getId()
                  .toString(), template.getLocation(), null, ImmutableMap.<String, String> of(),
                  tag, template.getImage(), runningStateToNodeState.get(server.getState()),
                  getPublicAddresses.apply(server), ImmutableList.<String> of(), ImmutableMap
                           .<String, String> of(), new Credentials("root", serverResponse
                           .getNewInstanceRequest().getCreateOptions().getPassword()));
         return node;
      }

   }

   @Singleton
   public static class RimuHostingListNodesStrategy implements ListNodesStrategy {
      private final RimuHostingClient client;
      private final Function<Server, NodeMetadata> serverToNodeMetadata;

      @Inject
      protected RimuHostingListNodesStrategy(RimuHostingClient client,
               Function<Server, NodeMetadata> serverToNodeMetadata) {
         this.client = client;
         this.serverToNodeMetadata = serverToNodeMetadata;
      }

      @Override
      public Iterable<? extends ComputeMetadata> list() {
         return listDetailsOnNodesMatching(NodePredicates.all());
      }

      @Override
      public Iterable<? extends NodeMetadata> listDetailsOnNodesMatching(
               Predicate<ComputeMetadata> filter) {
         return Iterables.filter(Iterables.transform(client.getServerList(), serverToNodeMetadata),
                  filter);
      }

   }

   @Singleton
   public static class RimuHostingGetNodeMetadataStrategy implements GetNodeMetadataStrategy {

      private final RimuHostingClient client;
      private final Function<Server, NodeMetadata> serverToNodeMetadata;

      @Inject
      protected RimuHostingGetNodeMetadataStrategy(RimuHostingClient client,
               Function<Server, NodeMetadata> serverToNodeMetadata) {
         this.client = client;
         this.serverToNodeMetadata = serverToNodeMetadata;
      }

      @Override
      public NodeMetadata execute(String id) {
         long serverId = Long.parseLong(id);
         Server server = client.getServer(serverId);
         return server == null ? null : serverToNodeMetadata.apply(server);
      }
   }

   @Singleton
   @Provides
   Map<RunningState, NodeState> provideServerToNodeState() {
      return ImmutableMap.<RunningState, NodeState> builder().put(RunningState.RUNNING,
               NodeState.RUNNING)//
               .put(RunningState.NOTRUNNING, NodeState.SUSPENDED)//
               .put(RunningState.POWERCYCLING, NodeState.PENDING)//
               .put(RunningState.RESTARTING, NodeState.PENDING)//
               .build();
   }

   @Singleton
   private static class ServerToNodeMetadata implements Function<Server, NodeMetadata> {

      @Resource
      protected Logger logger = Logger.NULL;
      private final Function<Server, Iterable<String>> getPublicAddresses;
      private final Map<RunningState, NodeState> runningStateToNodeState;
      private final Set<? extends Image> images;
      @SuppressWarnings("unused")
      private final Set<? extends Location> locations;

      private static class FindImageForServer implements Predicate<Image> {
         private final Location location;
         private final Server instance;

         private FindImageForServer(Location location, Server instance) {
            this.location = location;
            this.instance = instance;
         }

         @Override
         public boolean apply(Image input) {
            return input.getProviderId().equals(instance.getImageId())
                     && (input.getLocation() == null || input.getLocation().equals(location) || input
                              .getLocation().equals(location.getParent()));
         }
      }

      @SuppressWarnings("unused")
      @Inject
      ServerToNodeMetadata(Function<Server, Iterable<String>> getPublicAddresses,
               Map<RunningState, NodeState> runningStateToNodeState, Set<? extends Image> images,
               Set<? extends Location> locations) {
         this.getPublicAddresses = checkNotNull(getPublicAddresses, "serverStateToNodeState");
         this.runningStateToNodeState = checkNotNull(runningStateToNodeState,
                  "serverStateToNodeState");
         this.images = checkNotNull(images, "images");
         this.locations = checkNotNull(locations, "locations");
      }

      @Override
      public NodeMetadata apply(Server from) {

         Location location = new LocationImpl(LocationScope.ZONE, from.getLocation().getId(), from
                  .getLocation().getName(), null);
         String tag = from.getName().replaceAll("-[0-9]+", "");
         Credentials creds = null;

         Image image = null;
         try {
            image = Iterables.find(images, new FindImageForServer(location, from));
         } catch (NoSuchElementException e) {
            logger.warn("could not find a matching image for server %s in location %s", from,
                     location);
         }
         NodeState state = runningStateToNodeState.get(from.getState());
         return new NodeMetadataImpl(from.getId() + "", from.getName(), from.getId() + "",
                  location, null, ImmutableMap.<String, String> of(), tag, image, state,
                  getPublicAddresses.apply(from), ImmutableList.<String> of(), ImmutableMap
                           .<String, String> of(), creds);

      }
   }

   @Singleton
   private static class ServerToPublicAddresses implements Function<Server, Iterable<String>> {
      @Override
      public Iterable<String> apply(Server server) {
         return server.getIpAddresses() == null ? ImmutableSet.<String> of() : Iterables.concat(
                  ImmutableList.of(server.getIpAddresses().getPrimaryIp()), server.getIpAddresses()
                           .getSecondaryIps());
      }
   }

   @Provides
   @Singleton
   Location getDefaultLocation(@Named(PROPERTY_RIMUHOSTING_DEFAULT_DC) final String defaultDC,
            Set<? extends Location> locations) {
      return Iterables.find(locations, new Predicate<Location>() {

         @Override
         public boolean apply(Location input) {
            return input.getId().equals(defaultDC);
         }

      });
   }

   @Provides
   @Singleton
   Set<? extends Location> getDefaultLocations(RimuHostingClient sync, LogHolder holder,
            Function<ComputeMetadata, String> indexer, @Provider String providerName) {
      final Set<Location> locations = Sets.newHashSet();
      holder.logger.debug(">> providing locations");
      Location provider = new LocationImpl(LocationScope.PROVIDER, providerName, providerName, null);
      for (final PricingPlan from : sync.getPricingPlanList()) {
         try {
            locations.add(new LocationImpl(LocationScope.ZONE, from.getDataCenter().getId(), from
                     .getDataCenter().getName(), provider));
         } catch (NullPointerException e) {
            holder.logger.warn("datacenter not present in " + from.getId());
         }
      }
      holder.logger.debug("<< locations(%d)", locations.size());
      return locations;
   }

   @Provides
   @Singleton
   protected Function<ComputeMetadata, String> indexer() {
      return new Function<ComputeMetadata, String>() {
         @Override
         public String apply(ComputeMetadata from) {
            return from.getProviderId();
         }
      };
   }

   @Provides
   @Singleton
   protected Set<? extends Size> provideSizes(RimuHostingClient sync, Set<? extends Image> images,
            Set<? extends Location> locations, LogHolder holder,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService userExecutor,
            Function<ComputeMetadata, String> indexer) throws InterruptedException,
            TimeoutException, ExecutionException {
      final Set<Size> sizes = Sets.newHashSet();
      holder.logger.debug(">> providing sizes");
      for (final PricingPlan from : sync.getPricingPlanList()) {
         try {

            final Location location = Iterables.find(locations, new Predicate<Location>() {

               @Override
               public boolean apply(Location input) {
                  return input.getId().equals(from.getDataCenter().getId());
               }

            });
            sizes.add(new SizeImpl(from.getId(), from.getId(), from.getId(), location, null,
                     ImmutableMap.<String, String> of(), 1, from.getRam(), from.getDiskSize(),
                     ImagePredicates.any()));
         } catch (NullPointerException e) {
            holder.logger.warn("datacenter not present in " + from.getId());
         }
      }
      holder.logger.debug("<< sizes(%d)", sizes.size());
      return sizes;
   }

   private static class LogHolder {
      @Resource
      @Named(ComputeServiceConstants.COMPUTE_LOGGER)
      protected Logger logger = Logger.NULL;
   }

   public static final Pattern RIMU_PATTERN = Pattern.compile("([^0-9]*)(.*)");

   @Provides
   @Singleton
   protected Set<? extends Image> provideImages(final RimuHostingClient sync, LogHolder holder,
            Function<ComputeMetadata, String> indexer) throws InterruptedException,
            ExecutionException, TimeoutException {
      final Set<Image> images = Sets.newHashSet();
      holder.logger.debug(">> providing images");
      for (final org.jclouds.rimuhosting.miro.domain.Image from : sync.getImageList()) {
         OsFamily os = null;
         Architecture arch = from.getId().indexOf("64") == -1 ? Architecture.X86_32
                  : Architecture.X86_64;
         String osDescription = "";
         String version = "";

         osDescription = from.getId();

         Matcher matcher = RIMU_PATTERN.matcher(from.getId());
         if (matcher.find()) {
            try {
               os = OsFamily.fromValue(matcher.group(1).toLowerCase());
            } catch (IllegalArgumentException e) {
               holder.logger.debug("<< didn't match os(%s)", matcher.group(2));
            }
         }

         images.add(new ImageImpl(from.getId(), from.getDescription(), from.getId(), null, null,
                  ImmutableMap.<String, String> of(), from.getDescription(), version, os,
                  osDescription, arch, new Credentials("root", null)));
      }
      holder.logger.debug("<< images(%d)", images.size());
      return images;
   }

}
