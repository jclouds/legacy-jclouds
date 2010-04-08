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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
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
import org.jclouds.compute.internal.TemplateBuilderImpl;
import org.jclouds.compute.predicates.RunScriptRunning;
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
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.rest.RestContext;
import org.jclouds.rimuhosting.miro.RimuHostingAsyncClient;
import org.jclouds.rimuhosting.miro.RimuHostingClient;
import org.jclouds.rimuhosting.miro.config.RimuHostingContextModule;
import org.jclouds.rimuhosting.miro.domain.NewServerResponse;
import org.jclouds.rimuhosting.miro.domain.PricingPlan;
import org.jclouds.rimuhosting.miro.domain.Server;
import org.jclouds.rimuhosting.miro.domain.internal.RunningState;
import org.jclouds.ssh.SshClient;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * Configures the {@link RimuHostingComputeServiceContext}; requires
 * {@link RimuHostingComputeService} bound.
 * 
 * @author Adrian Cole
 */
public class RimuHostingComputeServiceContextModule extends RimuHostingContextModule {

   @Override
   protected void configure() {
      super.configure();
      bind(new TypeLiteral<Function<Server, NodeMetadata>>() {
      }).to(ServerToNodeMetadata.class);
      bind(new TypeLiteral<Function<Server, Iterable<InetAddress>>>() {
      }).to(ServerToPublicAddresses.class);
      bind(AddNodeWithTagStrategy.class).to(RimuHostingAddNodeWithTagStrategy.class);
      bind(ListNodesStrategy.class).to(RimuHostingListNodesStrategy.class);
      bind(GetNodeMetadataStrategy.class).to(RimuHostingGetNodeMetadataStrategy.class);
      bind(RebootNodeStrategy.class).to(RimuHostingRebootNodeStrategy.class);
      bind(DestroyNodeStrategy.class).to(RimuHostingDestroyNodeStrategy.class);
   }

   @Provides
   TemplateBuilder provideTemplate(Map<String, ? extends Location> locations,
            Map<String, ? extends Image> images, Map<String, ? extends Size> sizes,
            Location defaultLocation) {
      return new TemplateBuilderImpl(locations, images, sizes, defaultLocation).sizeId("MIRO1B")
               .osFamily(UBUNTU);
   }

   @Provides
   @Named("NAMING_CONVENTION")
   @Singleton
   String provideNamingConvention() {
      return "%s-%d";
   }

   @Singleton
   public static class RimuHostingRebootNodeStrategy implements RebootNodeStrategy {
      private final RimuHostingClient client;

      @Inject
      protected RimuHostingRebootNodeStrategy(RimuHostingClient client) {
         this.client = client;
      }

      @Override
      public boolean execute(ComputeMetadata node) {
         Long serverId = Long.parseLong(node.getId());
         // if false server wasn't around in the first place
         return client.restartServer(serverId).getState() == RunningState.RUNNING;
      }

   }

   @Singleton
   public static class RimuHostingDestroyNodeStrategy implements DestroyNodeStrategy {
      private final RimuHostingClient client;
      private final Predicate<Server> serverDestroyed;

      @Inject
      protected RimuHostingDestroyNodeStrategy(RimuHostingClient client,
               @Named("DESTROYED") Predicate<Server> serverDestroyed) {
         this.client = client;
         this.serverDestroyed = serverDestroyed;
      }

      @Override
      public boolean execute(ComputeMetadata node) {
         long serverId = Long.parseLong(node.getId());
         client.destroyServer(serverId);
         return serverDestroyed.apply(client.getServer(serverId));
      }

   }

   @Singleton
   public static class RimuHostingAddNodeWithTagStrategy implements AddNodeWithTagStrategy {
      private final RimuHostingClient client;
      private final Predicate<Server> serverRunning;
      private final Function<Server, Iterable<InetAddress>> getPublicAddresses;
      private final Map<RunningState, NodeState> runningStateToNodeState;

      @Inject
      protected RimuHostingAddNodeWithTagStrategy(RimuHostingClient client,
               @Named("RUNNING") Predicate<Server> serverRunning,
               Function<Server, Iterable<InetAddress>> getPublicAddresses,
               Map<RunningState, NodeState> runningStateToNodeState) {
         this.client = client;
         this.serverRunning = serverRunning;
         this.getPublicAddresses = getPublicAddresses;
         this.runningStateToNodeState = runningStateToNodeState;
      }

      @Override
      public NodeMetadata execute(String tag, String name, Template template) {
         NewServerResponse serverResponse = client.createServer(name, checkNotNull(template
                  .getImage().getId(), "imageId"), checkNotNull(template.getSize().getId(),
                  "sizeId"));
         serverRunning.apply(serverResponse.getServer());
         Server server = client.getServer(serverResponse.getServer().getId());
         // we have to lookup the new details in order to retrieve the currently assigned ip
         // address.
         NodeMetadata node = new NodeMetadataImpl(server.getId().toString(), name, template
                  .getLocation().getId(), null, ImmutableMap.<String, String> of(), tag,
                  runningStateToNodeState.get(server.getState()), getPublicAddresses.apply(server),
                  ImmutableList.<InetAddress> of(), ImmutableMap.<String, String> of(),
                  new Credentials("root", serverResponse.getNewInstanceRequest().getCreateOptions()
                           .getPassword()));
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
      public Iterable<? extends ComputeMetadata> execute() {
         return Iterables.transform(client.getServerList(), serverToNodeMetadata);
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
      public NodeMetadata execute(ComputeMetadata node) {
         long serverId = Long.parseLong(node.getId());
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
      private final Function<Server, Iterable<InetAddress>> getPublicAddresses;
      private final Map<RunningState, NodeState> runningStateToNodeState;

      @SuppressWarnings("unused")
      @Inject
      ServerToNodeMetadata(Function<Server, Iterable<InetAddress>> getPublicAddresses,
               Map<RunningState, NodeState> runningStateToNodeState) {
         this.getPublicAddresses = getPublicAddresses;
         this.runningStateToNodeState = runningStateToNodeState;
      }

      @Override
      public NodeMetadata apply(Server from) {
         String locationId = "//TODO";
         String tag = from.getName().replaceAll("-[0-9]+", "");
         Credentials creds = null;
         NodeState state = runningStateToNodeState.get(from.getState());
         return new NodeMetadataImpl(from.getId() + "", from.getName(), locationId, null,
                  ImmutableMap.<String, String> of(), tag, state, getPublicAddresses.apply(from),
                  ImmutableList.<InetAddress> of(), ImmutableMap.<String, String> of(), creds);

      }
   }

   @Singleton
   private static class ServerToPublicAddresses implements Function<Server, Iterable<InetAddress>> {
      @Override
      public Iterable<InetAddress> apply(Server server) {
         Iterable<String> addresses = server.getIpAddresses() == null ? ImmutableSet.<String> of()
                  : Iterables.concat(ImmutableList.of(server.getIpAddresses().getPrimaryIp()),
                           server.getIpAddresses().getSecondaryIps());
         return Iterables.transform(addresses, new Function<String, InetAddress>() {

            @Override
            public InetAddress apply(String from) {
               try {
                  return InetAddress.getByName(from);
               } catch (UnknownHostException e) {
                  // TODO: log the failure.
                  return null;
               }
            }
         });
      }
   }

   @Provides
   @Singleton
   ComputeServiceContext provideContext(ComputeService computeService,
            RestContext<RimuHostingAsyncClient, RimuHostingClient> context) {
      return new ComputeServiceContextImpl<RimuHostingAsyncClient, RimuHostingClient>(
               computeService, context);
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
   Location getDefaultLocation(Map<String, ? extends Location> locations) {
      return locations.get("DCDALLAS");
   }

   @Provides
   @Singleton
   Map<String, ? extends Location> getDefaultLocations(RimuHostingClient sync, LogHolder holder,
            Function<ComputeMetadata, String> indexer) {
      final Set<Location> locations = Sets.newHashSet();
      holder.logger.debug(">> providing locations");
      for (final PricingPlan from : sync.getPricingPlanList()) {
         try {
            locations.add(new LocationImpl(LocationScope.ZONE, from.getDataCenter().getId(), from
                     .getDataCenter().getName(), null, true));
         } catch (NullPointerException e) {
            holder.logger.warn("datacenter not present in " + from.getId());
         }
      }
      holder.logger.debug("<< locations(%d)", locations.size());
      return Maps.uniqueIndex(locations, new Function<Location, String>() {

         @Override
         public String apply(Location from) {
            return from.getId();
         }
      });
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
   protected Map<String, ? extends Size> provideSizes(RimuHostingClient sync,
            Map<String, ? extends Image> images, LogHolder holder,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService userExecutor,
            Function<ComputeMetadata, String> indexer) throws InterruptedException,
            TimeoutException, ExecutionException {
      final Set<Size> sizes = Sets.newHashSet();
      holder.logger.debug(">> providing sizes");
      for (final PricingPlan from : sync.getPricingPlanList()) {
         try {
            sizes.add(new SizeImpl(from.getId(), from.getId(), from.getDataCenter().getId(), null,
                     ImmutableMap.<String, String> of(), from.getDiskSize(), from.getRam(), from
                              .getDiskSize(), ImmutableSet.<Architecture> of(Architecture.X86_32,
                              Architecture.X86_64)));
         } catch (NullPointerException e) {
            holder.logger.warn("datacenter not present in " + from.getId());
         }
      }
      holder.logger.debug("<< sizes(%d)", sizes.size());
      return Maps.uniqueIndex(sizes, indexer);
   }

   private static class LogHolder {
      @Resource
      @Named(ComputeServiceConstants.COMPUTE_LOGGER)
      protected Logger logger = Logger.NULL;
   }

   public static final Pattern RIMU_PATTERN = Pattern.compile("([^0-9]*)(.*)");

   @Provides
   @Singleton
   protected Map<String, ? extends Image> provideImages(final RimuHostingClient sync,
            LogHolder holder, Function<ComputeMetadata, String> indexer)
            throws InterruptedException, ExecutionException, TimeoutException {
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

         images.add(new ImageImpl(from.getId(), from.getDescription(), null, null, ImmutableMap
                  .<String, String> of(), from.getDescription(), version, os, osDescription, arch,
                 new Credentials("root", null)));
      }
      holder.logger.debug("<< images(%d)", images.size());
      return Maps.uniqueIndex(images, indexer);
   }

}
