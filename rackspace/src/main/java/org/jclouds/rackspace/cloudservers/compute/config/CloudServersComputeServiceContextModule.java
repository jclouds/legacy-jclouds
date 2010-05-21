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
package org.jclouds.rackspace.cloudservers.compute.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.domain.OsFamily.UBUNTU;

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
import org.jclouds.compute.internal.BaseComputeService;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.compute.predicates.ScriptStatusReturnsZero;
import org.jclouds.compute.predicates.ScriptStatusReturnsZero.CommandUsingClient;
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
import org.jclouds.rackspace.cloudservers.CloudServersAsyncClient;
import org.jclouds.rackspace.cloudservers.CloudServersClient;
import org.jclouds.rackspace.cloudservers.compute.functions.ServerToNodeMetadata;
import org.jclouds.rackspace.cloudservers.config.CloudServersContextModule;
import org.jclouds.rackspace.cloudservers.domain.Flavor;
import org.jclouds.rackspace.cloudservers.domain.RebootType;
import org.jclouds.rackspace.cloudservers.domain.Server;
import org.jclouds.rackspace.cloudservers.domain.ServerStatus;
import org.jclouds.rackspace.cloudservers.options.ListOptions;
import org.jclouds.rackspace.config.RackspaceLocationsModule;
import org.jclouds.rackspace.reference.RackspaceConstants;
import org.jclouds.rest.RestContext;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * Configures the {@link CloudServersComputeServiceContext}; requires {@link BaseComputeService}
 * bound.
 * 
 * @author Adrian Cole
 */
public class CloudServersComputeServiceContextModule extends CloudServersContextModule {
   private final String providerName;

   public CloudServersComputeServiceContextModule(String providerName) {
      this.providerName = providerName;
   }

   @Override
   protected void configure() {
      super.configure();
      install(new RackspaceLocationsModule(providerName));
      bind(new TypeLiteral<Function<Server, NodeMetadata>>() {
      }).to(ServerToNodeMetadata.class);
      bind(AddNodeWithTagStrategy.class).to(CloudServersAddNodeWithTagStrategy.class);
      bind(ListNodesStrategy.class).to(CloudServersListNodesStrategy.class);
      bind(GetNodeMetadataStrategy.class).to(CloudServersGetNodeMetadataStrategy.class);
      bind(RebootNodeStrategy.class).to(CloudServersRebootNodeStrategy.class);
      bind(DestroyNodeStrategy.class).to(CloudServersDestroyNodeStrategy.class);
   }

   @Provides
   @Named("DEFAULT")
   protected TemplateBuilder provideTemplate(TemplateBuilder template) {
      return template.osFamily(UBUNTU);
   }

   @Provides
   @Named("NAMING_CONVENTION")
   @Singleton
   String provideNamingConvention(@Named(RackspaceConstants.PROPERTY_RACKSPACE_USER) String account) {
      return account + "-%s-%s";
   }

   @Singleton
   public static class CloudServersRebootNodeStrategy implements RebootNodeStrategy {
      private final CloudServersClient client;
      private final Predicate<Server> serverActive;

      @Inject
      protected CloudServersRebootNodeStrategy(CloudServersClient client,
               @Named("ACTIVE") Predicate<Server> serverActive) {
         this.client = client;
         this.serverActive = serverActive;
      }

      @Override
      public boolean execute(String id) {
         int serverId = Integer.parseInt(id);
         // if false server wasn't around in the first place
         client.rebootServer(serverId, RebootType.HARD);
         Server server = client.getServer(serverId);
         return server == null ? false : serverActive.apply(server);
      }

   }

   @Singleton
   public static class CloudServersDestroyNodeStrategy implements DestroyNodeStrategy {
      private final CloudServersClient client;
      private final Predicate<Server> serverDeleted;

      @Inject
      protected CloudServersDestroyNodeStrategy(CloudServersClient client,
               @Named("DELETED") Predicate<Server> serverDeleted) {
         this.client = client;
         this.serverDeleted = serverDeleted;
      }

      @Override
      public boolean execute(String id) {
         int serverId = Integer.parseInt(id);
         // if false server wasn't around in the first place
         if (!client.deleteServer(serverId))
            return false;
         Server server = client.getServer(serverId);
         return server == null ? false : serverDeleted.apply(server);
      }

   }

   @Singleton
   public static class CloudServersAddNodeWithTagStrategy implements AddNodeWithTagStrategy {
      private final CloudServersClient client;
      private final Predicate<Server> serverActive;

      @Inject
      protected CloudServersAddNodeWithTagStrategy(CloudServersClient client,
               @Named("ACTIVE") Predicate<Server> serverActive) {
         this.client = checkNotNull(client, "client");
         this.serverActive = checkNotNull(serverActive, "serverActive");
      }

      @Override
      public NodeMetadata execute(String tag, String name, Template template) {
         Server server = client.createServer(name, Integer.parseInt(template.getImage().getProviderId()),
                  Integer.parseInt(template.getSize().getProviderId()));
         serverActive.apply(server);
         return new NodeMetadataImpl(server.getId() + "", name, server.getId() + "",
                  new LocationImpl(LocationScope.HOST, server.getHostId(), server.getHostId(),
                           template.getLocation()), null, server.getMetadata(), tag, template
                           .getImage(), NodeState.RUNNING, server.getAddresses()
                           .getPublicAddresses(), server.getAddresses().getPrivateAddresses(),
                  ImmutableMap.<String, String> of(),
                  new Credentials("root", server.getAdminPass()));
      }

   }

   @Singleton
   public static class CloudServersListNodesStrategy implements ListNodesStrategy {
      private final CloudServersClient client;
      private final Function<Server, NodeMetadata> serverToNodeMetadata;

      @Inject
      protected CloudServersListNodesStrategy(CloudServersClient client,
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
         return Iterables.filter(Iterables.transform(client.listServers(ListOptions.Builder
                  .withDetails()), serverToNodeMetadata), filter);
      }
   }

   @Singleton
   public static class CloudServersGetNodeMetadataStrategy implements GetNodeMetadataStrategy {

      private final CloudServersClient client;
      private final Function<Server, NodeMetadata> serverToNodeMetadata;

      @Inject
      protected CloudServersGetNodeMetadataStrategy(CloudServersClient client,
               Function<Server, NodeMetadata> serverToNodeMetadata) {
         this.client = client;
         this.serverToNodeMetadata = serverToNodeMetadata;
      }

      @Override
      public NodeMetadata execute(String id) {
         int serverId = Integer.parseInt(id);
         Server server = client.getServer(serverId);
         return server == null ? null : serverToNodeMetadata.apply(server);
      }
   }

   @Singleton
   @Provides
   Map<ServerStatus, NodeState> provideServerToNodeState() {
      return ImmutableMap.<ServerStatus, NodeState> builder().put(ServerStatus.ACTIVE,
               NodeState.RUNNING)//
               .put(ServerStatus.SUSPENDED, NodeState.SUSPENDED)//
               .put(ServerStatus.DELETED, NodeState.TERMINATED)//
               .put(ServerStatus.QUEUE_RESIZE, NodeState.PENDING)//
               .put(ServerStatus.PREP_RESIZE, NodeState.PENDING)//
               .put(ServerStatus.RESIZE, NodeState.PENDING)//
               .put(ServerStatus.VERIFY_RESIZE, NodeState.PENDING)//
               .put(ServerStatus.QUEUE_MOVE, NodeState.PENDING)//
               .put(ServerStatus.PREP_MOVE, NodeState.PENDING)//
               .put(ServerStatus.MOVE, NodeState.PENDING)//
               .put(ServerStatus.VERIFY_MOVE, NodeState.PENDING)//
               .put(ServerStatus.RESCUE, NodeState.PENDING)//
               .put(ServerStatus.ERROR, NodeState.ERROR)//
               .put(ServerStatus.BUILD, NodeState.PENDING)//
               .put(ServerStatus.RESTORING, NodeState.PENDING)//
               .put(ServerStatus.PASSWORD, NodeState.PENDING)//
               .put(ServerStatus.REBUILD, NodeState.PENDING)//
               .put(ServerStatus.DELETE_IP, NodeState.PENDING)//
               .put(ServerStatus.SHARE_IP_NO_CONFIG, NodeState.PENDING)//
               .put(ServerStatus.SHARE_IP, NodeState.PENDING)//
               .put(ServerStatus.REBOOT, NodeState.PENDING)//
               .put(ServerStatus.HARD_REBOOT, NodeState.PENDING)//
               .put(ServerStatus.UNKNOWN, NodeState.UNKNOWN).build();
   }

   @Provides
   @Singleton
   ComputeServiceContext provideContext(ComputeService computeService,
            RestContext<CloudServersAsyncClient, CloudServersClient> context) {
      return new ComputeServiceContextImpl<CloudServersAsyncClient, CloudServersClient>(
               computeService, context);
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
   protected Set<? extends Size> provideSizes(CloudServersClient sync, Set<? extends Image> images,
            Location location, LogHolder holder,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor,
            Function<ComputeMetadata, String> indexer) throws InterruptedException,
            TimeoutException, ExecutionException {
      final Set<Size> sizes = Sets.newHashSet();
      holder.logger.debug(">> providing sizes");
      for (final Flavor from : sync.listFlavors(ListOptions.Builder.withDetails())) {
         sizes.add(new SizeImpl(from.getId() + "", from.getName(), from.getId() + "", location,
                  null, ImmutableMap.<String, String> of(), from.getDisk() / 10, from.getRam(),
                  from.getDisk(), ImmutableSet.<Architecture> of(Architecture.X86_32,
                           Architecture.X86_64)));
      }
      holder.logger.debug("<< sizes(%d)", sizes.size());
      return sizes;
   }

   private static class LogHolder {
      @Resource
      @Named(ComputeServiceConstants.COMPUTE_LOGGER)
      protected Logger logger = Logger.NULL;
   }

   public static final Pattern RACKSPACE_PATTERN = Pattern.compile("(([^ ]*) .*)");

   @Provides
   @Singleton
   protected Set<? extends Image> provideImages(final CloudServersClient sync, Location location,
            LogHolder holder, Function<ComputeMetadata, String> indexer)
            throws InterruptedException, ExecutionException, TimeoutException {
      final Set<Image> images = Sets.newHashSet();
      holder.logger.debug(">> providing images");
      for (final org.jclouds.rackspace.cloudservers.domain.Image from : sync
               .listImages(ListOptions.Builder.withDetails())) {
         OsFamily os = null;
         Architecture arch = Architecture.X86_64;
         String osDescription = "";
         String version = "";
         Matcher matcher = RACKSPACE_PATTERN.matcher(from.getName());
         osDescription = from.getName();
         if (from.getName().indexOf("Red Hat EL") != -1) {
            os = OsFamily.RHEL;
         } else if (from.getName().indexOf("Oracle EL") != -1) {
            os = OsFamily.OEL;
         } else if (matcher.find()) {
            try {
               os = OsFamily.fromValue(matcher.group(2).toLowerCase());
            } catch (IllegalArgumentException e) {
               holder.logger.debug("<< didn't match os(%s)", matcher.group(2));
            }
         }
         images.add(new ImageImpl(from.getId() + "", from.getName(), from.getId() + "", location,
                  null, ImmutableMap.<String, String> of(), from.getName(), version, os,
                  osDescription, arch, new Credentials("root", null)));
      }
      holder.logger.debug("<< images(%d)", images.size());
      return images;
   }
}
