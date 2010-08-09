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
package org.jclouds.slicehost.compute.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.domain.OsFamily.UBUNTU;

import java.util.Map;
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
import org.jclouds.compute.internal.BaseComputeService;
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
import org.jclouds.rest.RestContext;
import org.jclouds.rest.annotations.Provider;
import org.jclouds.rest.internal.RestContextImpl;
import org.jclouds.slicehost.SlicehostAsyncClient;
import org.jclouds.slicehost.SlicehostClient;
import org.jclouds.slicehost.compute.functions.SliceToNodeMetadata;
import org.jclouds.slicehost.domain.Flavor;
import org.jclouds.slicehost.domain.Slice;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
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
 * Configures the {@link SlicehostComputeServiceContext}; requires
 * {@link BaseComputeService} bound.
 * 
 * @author Adrian Cole
 */
public class SlicehostComputeServiceContextModule extends AbstractModule {

   @Override
   protected void configure() {
      install(new ComputeServiceTimeoutsModule());
      bind(new TypeLiteral<Function<Slice, NodeMetadata>>() {
      }).to(SliceToNodeMetadata.class);
      bind(LoadBalancerService.class).toProvider(Providers.<LoadBalancerService> of(null));
      bind(new TypeLiteral<ComputeServiceContext>() {
      }).to(new TypeLiteral<ComputeServiceContextImpl<SlicehostClient, SlicehostAsyncClient>>() {
      }).in(Scopes.SINGLETON);
      bind(new TypeLiteral<RestContext<SlicehostClient, SlicehostAsyncClient>>() {
      }).to(new TypeLiteral<RestContextImpl<SlicehostClient, SlicehostAsyncClient>>() {
      }).in(Scopes.SINGLETON);
      bind(AddNodeWithTagStrategy.class).to(SlicehostAddNodeWithTagStrategy.class);
      bind(ListNodesStrategy.class).to(SlicehostListNodesStrategy.class);
      bind(GetNodeMetadataStrategy.class).to(SlicehostGetNodeMetadataStrategy.class);
      bind(RebootNodeStrategy.class).to(SlicehostRebootNodeStrategy.class);
      bind(DestroyNodeStrategy.class).to(SlicehostDestroyNodeStrategy.class);
   }

   @Provides
   @Named("DEFAULT")
   protected TemplateBuilder provideTemplate(TemplateBuilder template) {
      return template.osFamily(UBUNTU).imageNameMatches(".*10\\.?04.*");
   }

   @Provides
   @Named("NAMING_CONVENTION")
   @Singleton
   String provideNamingConvention() {
      return "%s-%s";
   }

   @Singleton
   public static class SlicehostRebootNodeStrategy implements RebootNodeStrategy {
      private final SlicehostClient client;
      private final GetNodeMetadataStrategy getNode;

      @Inject
      protected SlicehostRebootNodeStrategy(SlicehostClient client, GetNodeMetadataStrategy getNode) {
         this.client = client;
         this.getNode = getNode;
      }

      @Override
      public NodeMetadata execute(String id) {
         int sliceId = Integer.parseInt(id);
         client.hardRebootSlice(sliceId);
         return getNode.execute(id);
      }

   }

   @Singleton
   public static class SlicehostDestroyNodeStrategy implements DestroyNodeStrategy {
      private final SlicehostClient client;
      private final GetNodeMetadataStrategy getNode;

      @Inject
      protected SlicehostDestroyNodeStrategy(SlicehostClient client, GetNodeMetadataStrategy getNode) {
         this.client = client;
         this.getNode = getNode;
      }

      @Override
      public NodeMetadata execute(String id) {
         int sliceId = Integer.parseInt(id);
         // if false slice wasn't around in the first place
         client.destroySlice(sliceId);
         return getNode.execute(id);
      }

   }

   @Singleton
   public static class SlicehostAddNodeWithTagStrategy implements AddNodeWithTagStrategy {
      private final SlicehostClient client;

      @Inject
      protected SlicehostAddNodeWithTagStrategy(SlicehostClient client) {
         this.client = checkNotNull(client, "client");
      }

      @Override
      public NodeMetadata execute(String tag, String name, Template template) {
         Slice slice = client.createSlice(name, Integer.parseInt(template.getImage().getProviderId()), Integer
               .parseInt(template.getSize().getProviderId()));
         return new NodeMetadataImpl(slice.getId() + "", name, slice.getId() + "", template.getLocation(), null,
               ImmutableMap.<String, String> of(), tag, template.getImage(), NodeState.PENDING, Iterables.filter(slice
                     .getAddresses(), new Predicate<String>() {

                  @Override
                  public boolean apply(String input) {
                     return !input.startsWith("10.");
                  }

               }), Iterables.filter(slice.getAddresses(), new Predicate<String>() {

                  @Override
                  public boolean apply(String input) {
                     return input.startsWith("10.");
                  }

               }), ImmutableMap.<String, String> of(), new Credentials("root", slice.getRootPassword()));
      }

   }

   @Singleton
   public static class SlicehostListNodesStrategy implements ListNodesStrategy {
      private final SlicehostClient client;
      private final Function<Slice, NodeMetadata> sliceToNodeMetadata;

      @Inject
      protected SlicehostListNodesStrategy(SlicehostClient client, Function<Slice, NodeMetadata> sliceToNodeMetadata) {
         this.client = client;
         this.sliceToNodeMetadata = sliceToNodeMetadata;
      }

      @Override
      public Iterable<? extends ComputeMetadata> list() {
         return listDetailsOnNodesMatching(NodePredicates.all());
      }

      @Override
      public Iterable<? extends NodeMetadata> listDetailsOnNodesMatching(Predicate<ComputeMetadata> filter) {
         return Iterables.filter(Iterables.transform(client.listSlices(), sliceToNodeMetadata), filter);
      }
   }

   @Singleton
   public static class SlicehostGetNodeMetadataStrategy implements GetNodeMetadataStrategy {

      private final SlicehostClient client;
      private final Function<Slice, NodeMetadata> sliceToNodeMetadata;

      @Inject
      protected SlicehostGetNodeMetadataStrategy(SlicehostClient client,
            Function<Slice, NodeMetadata> sliceToNodeMetadata) {
         this.client = client;
         this.sliceToNodeMetadata = sliceToNodeMetadata;
      }

      @Override
      public NodeMetadata execute(String id) {
         int sliceId = Integer.parseInt(id);
         Slice slice = client.getSlice(sliceId);
         return slice == null ? null : sliceToNodeMetadata.apply(slice);
      }
   }

   @VisibleForTesting
   static final Map<Slice.Status, NodeState> sliceStatusToNodeState = ImmutableMap.<Slice.Status, NodeState> builder()
         .put(Slice.Status.ACTIVE, NodeState.RUNNING)//
         .put(Slice.Status.BUILD, NodeState.PENDING)//
         .put(Slice.Status.REBOOT, NodeState.PENDING)//
         .put(Slice.Status.HARD_REBOOT, NodeState.PENDING)//
         .put(Slice.Status.TERMINATED, NodeState.TERMINATED)//
         .build();

   @Singleton
   @Provides
   Map<Slice.Status, NodeState> provideSliceToNodeState() {
      return sliceStatusToNodeState;
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
   protected Set<? extends Size> provideSizes(SlicehostClient sync, Set<? extends Image> images, Location location,
         LogHolder holder, @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor,
         Function<ComputeMetadata, String> indexer) throws InterruptedException, TimeoutException, ExecutionException {
      final Set<Size> sizes = Sets.newHashSet();
      holder.logger.debug(">> providing sizes");
      for (final Flavor from : sync.listFlavors()) {
         sizes.add(new SizeImpl(from.getId() + "", from.getName(), from.getId() + "", location, null, ImmutableMap
               .<String, String> of(), from.getRam() / 1024.0, from.getRam(), (from.getRam() * 4) / 1024,
               ImagePredicates.any()));
      }
      holder.logger.debug("<< sizes(%d)", sizes.size());
      return sizes;
   }

   private static class LogHolder {
      @Resource
      @Named(ComputeServiceConstants.COMPUTE_LOGGER)
      protected Logger logger = Logger.NULL;
   }

   public static final Pattern SLICEHOST_PATTERN = Pattern.compile("(([^ ]*) .*)");

   @Provides
   @Singleton
   Location getLocation(@Provider String name) {
      return new LocationImpl(LocationScope.PROVIDER, name, name, null);
   }

   @Provides
   @Singleton
   Set<? extends Location> provideLocations(Location location) {
      return ImmutableSet.of(location);
   }

   @Provides
   @Singleton
   protected Set<? extends Image> provideImages(final SlicehostClient sync, Location location, LogHolder holder,
         Function<ComputeMetadata, String> indexer) throws InterruptedException, ExecutionException, TimeoutException {
      final Set<Image> images = Sets.newHashSet();
      holder.logger.debug(">> providing images");
      for (final org.jclouds.slicehost.domain.Image from : sync.listImages()) {
         OsFamily os = null;
         Architecture arch = Architecture.X86_64;
         String osDescription = "";
         String version = "";
         Matcher matcher = SLICEHOST_PATTERN.matcher(from.getName());
         osDescription = from.getName();
         if (matcher.find()) {
            try {
               os = OsFamily.fromValue(matcher.group(2).toLowerCase());
            } catch (IllegalArgumentException e) {
               holder.logger.debug("<< didn't match os(%s)", matcher.group(2));
            }
         }
         images
               .add(new ImageImpl(from.getId() + "", from.getName(), from.getId() + "", location, null, ImmutableMap
                     .<String, String> of(), from.getName(), version, os, osDescription, arch, new Credentials("root",
                     null)));
      }
      holder.logger.debug("<< images(%d)", images.size());
      return images;
   }
}
