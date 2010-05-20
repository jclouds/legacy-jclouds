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
package org.jclouds.aws.ec2.compute.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.aws.ec2.options.DescribeImagesOptions.Builder.ownedBy;
import static org.jclouds.aws.ec2.reference.EC2Constants.PROPERTY_EC2_AMI_OWNERS;
import static org.jclouds.compute.domain.OsFamily.UBUNTU;
import static org.jclouds.concurrent.ConcurrentUtils.awaitCompletion;

import java.net.URI;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.aws.ec2.EC2;
import org.jclouds.aws.ec2.EC2AsyncClient;
import org.jclouds.aws.ec2.EC2Client;
import org.jclouds.aws.ec2.compute.EC2ComputeService;
import org.jclouds.aws.ec2.compute.domain.EC2Size;
import org.jclouds.aws.ec2.compute.domain.RegionAndName;
import org.jclouds.aws.ec2.compute.functions.CreateSecurityGroupIfNeeded;
import org.jclouds.aws.ec2.compute.functions.CreateUniqueKeyPair;
import org.jclouds.aws.ec2.compute.functions.ImageParser;
import org.jclouds.aws.ec2.compute.functions.RegionAndIdToImage;
import org.jclouds.aws.ec2.compute.functions.RunningInstanceToNodeMetadata;
import org.jclouds.aws.ec2.compute.internal.EC2TemplateBuilderImpl;
import org.jclouds.aws.ec2.compute.options.EC2TemplateOptions;
import org.jclouds.aws.ec2.compute.strategy.EC2DestroyNodeStrategy;
import org.jclouds.aws.ec2.compute.strategy.EC2LoadBalancerStrategy;
import org.jclouds.aws.ec2.compute.strategy.EC2RunNodesAndAddToSetStrategy;
import org.jclouds.aws.ec2.config.EC2ContextModule;
import org.jclouds.aws.ec2.domain.KeyPair;
import org.jclouds.aws.ec2.domain.RunningInstance;
import org.jclouds.aws.ec2.domain.Image.ImageType;
import org.jclouds.aws.ec2.functions.RunningInstanceToStorageMappingUnix;
import org.jclouds.aws.ec2.services.InstanceClient;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.Architecture;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.compute.predicates.ScriptStatusReturnsZero;
import org.jclouds.compute.predicates.ScriptStatusReturnsZero.CommandUsingClient;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.LoadBalancerStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.compute.strategy.RunNodesAndAddToSetStrategy;
import org.jclouds.concurrent.ConcurrentUtils;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.internal.LocationImpl;
import org.jclouds.logging.Logger;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.rest.RestContext;
import org.jclouds.util.Jsr330;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

/**
 * Configures the {@link ComputeServiceContext}; requires {@link EC2ComputeService} bound.
 * 
 * @author Adrian Cole
 */
public class EC2ComputeServiceContextModule extends EC2ContextModule {

   private final String providerName;

   public EC2ComputeServiceContextModule(String providerName) {
      this.providerName = providerName;
   }

   @Override
   protected void configure() {
      super.configure();
      bind(TemplateBuilder.class).to(EC2TemplateBuilderImpl.class);
      bind(TemplateOptions.class).to(EC2TemplateOptions.class);
      bind(ComputeService.class).to(EC2ComputeService.class);
      bind(RunNodesAndAddToSetStrategy.class).to(EC2RunNodesAndAddToSetStrategy.class);
      bind(ListNodesStrategy.class).to(EC2ListNodesStrategy.class);
      bind(GetNodeMetadataStrategy.class).to(EC2GetNodeMetadataStrategy.class);
      bind(RebootNodeStrategy.class).to(EC2RebootNodeStrategy.class);
      bind(DestroyNodeStrategy.class).to(EC2DestroyNodeStrategy.class);
      bind(LoadBalancerStrategy.class).to(EC2LoadBalancerStrategy.class);
      bind(new TypeLiteral<Function<RunningInstance, Map<String, String>>>() {
      }).annotatedWith(Jsr330.named("volumeMapping")).to(RunningInstanceToStorageMappingUnix.class)
               .in(Scopes.SINGLETON);
   }

   @Provides
   @Singleton
   Supplier<String> provideSuffix() {
      return new Supplier<String>() {
         final SecureRandom random = new SecureRandom();

         @Override
         public String get() {
            return random.nextInt(100) + "";
         }
      };

   }

   @Provides
   @Named("DEFAULT")
   protected TemplateBuilder provideTemplate(TemplateBuilder template) {
      return template.architecture(Architecture.X86_32).osFamily(UBUNTU);
   }

   // TODO make this more efficient for listNodes(); currently RunningInstanceToNodeMetadata is slow
   // due to image parsing; consider using MapMaker. computing map
   @Singleton
   public static class EC2ListNodesStrategy implements ListNodesStrategy {
      @Resource
      @Named(ComputeServiceConstants.COMPUTE_LOGGER)
      protected Logger logger = Logger.NULL;
      
      private final InstanceClient client;
      private final Map<String, URI> regionMap;
      private final RunningInstanceToNodeMetadata runningInstanceToNodeMetadata;
      private final ExecutorService executor;

      @Inject
      protected EC2ListNodesStrategy(InstanceClient client, @EC2 Map<String, URI> regionMap,
               RunningInstanceToNodeMetadata runningInstanceToNodeMetadata,
               @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor) {
         this.client = client;
         this.regionMap = regionMap;
         this.runningInstanceToNodeMetadata = runningInstanceToNodeMetadata;
         this.executor = executor;
      }

      @Override
      public Iterable<? extends ComputeMetadata> list() {
         return listDetailsOnNodesMatching(NodePredicates.all());
      }

      @Override
      public Iterable<? extends NodeMetadata> listDetailsOnNodesMatching(
               Predicate<ComputeMetadata> filter) {
         final Set<NodeMetadata> nodes = Sets.newHashSet();

         Map<String, ListenableFuture<?>> parallelResponses = Maps.newHashMap();

         for (final String region : regionMap.keySet()) {
            parallelResponses.put(region, ConcurrentUtils.makeListenable(executor
                     .submit(new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                           Iterables.addAll(nodes, Iterables.transform(Iterables.concat(client
                                    .describeInstancesInRegion(region)),
                                    runningInstanceToNodeMetadata));
                           return null;
                        }
                     }), executor));
         }
         Map<String, Exception> exceptions = awaitCompletion(parallelResponses, executor, null,
                  logger, "nodes");

         if (exceptions.size() > 0)
            throw new RuntimeException(String.format("error parsing nodes in regions: %s",
                     exceptions));
         return Iterables.filter(nodes, filter);
      }
   }

   @Singleton
   public static class GetRegionFromLocation implements Function<Location, String> {
      public String apply(Location location) {
         String region = location.getScope() == LocationScope.REGION ? location.getId() : location
                  .getParent().getId();
         return region;
      }
   }

   @Singleton
   public static class EC2GetNodeMetadataStrategy implements GetNodeMetadataStrategy {

      private final InstanceClient client;
      private final RunningInstanceToNodeMetadata runningInstanceToNodeMetadata;
      private final GetRegionFromLocation getRegionFromLocation;

      @Inject
      protected EC2GetNodeMetadataStrategy(InstanceClient client,
               GetRegionFromLocation getRegionFromLocation,
               RunningInstanceToNodeMetadata runningInstanceToNodeMetadata) {
         this.client = client;
         this.getRegionFromLocation = getRegionFromLocation;
         this.runningInstanceToNodeMetadata = runningInstanceToNodeMetadata;
      }

      @Override
      public NodeMetadata execute(Location location, String id) {
         String region = getRegionFromLocation.apply(checkNotNull(location, "location"));
         RunningInstance runningInstance = Iterables.getOnlyElement(getAllRunningInstancesInRegion(
                  client, region, checkNotNull(id, "id")));
         return runningInstanceToNodeMetadata.apply(runningInstance);
      }

   }

   public static Iterable<RunningInstance> getAllRunningInstancesInRegion(InstanceClient client,
            String region, String id) {
      return Iterables.concat(client.describeInstancesInRegion(region, id));
   }

   @Singleton
   public static class EC2RebootNodeStrategy implements RebootNodeStrategy {
      private final InstanceClient client;
      private final GetRegionFromLocation getRegionFromLocation;

      @Inject
      protected EC2RebootNodeStrategy(InstanceClient client,
               GetRegionFromLocation getRegionFromLocation) {
         this.client = client;
         this.getRegionFromLocation = getRegionFromLocation;
      }

      @Override
      public boolean execute(Location location, String id) {
         String region = getRegionFromLocation.apply(location);
         client.rebootInstancesInRegion(region, id);
         return true;
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
   protected final Map<RegionAndName, KeyPair> credentialsMap(CreateUniqueKeyPair in) {
      // doesn't seem to clear when someone issues remove(key)
      // return new MapMaker().makeComputingMap(in);
      return Maps.newLinkedHashMap();
   }

   @Provides
   @Singleton
   protected final Map<RegionAndName, String> securityGroupMap(CreateSecurityGroupIfNeeded in) {
      // doesn't seem to clear when someone issues remove(key)
      // return new MapMaker().makeComputingMap(in);
      return Maps.newLinkedHashMap();
   }

   @Provides
   @Singleton
   ComputeServiceContext provideContext(ComputeService computeService,
            RestContext<EC2AsyncClient, EC2Client> context) {
      return new ComputeServiceContextImpl<EC2AsyncClient, EC2Client>(computeService, context);
   }

   @Provides
   @Singleton
   Function<ComputeMetadata, String> indexer() {
      return new Function<ComputeMetadata, String>() {
         @Override
         public String apply(ComputeMetadata from) {
            return from.getId();
         }
      };
   }

   @Provides
   @Singleton
   Set<? extends Size> provideSizes() {
      return ImmutableSet.of(EC2Size.C1_MEDIUM, EC2Size.C1_XLARGE, EC2Size.M1_LARGE,
               EC2Size.M1_SMALL, EC2Size.M1_XLARGE, EC2Size.M2_XLARGE, EC2Size.M2_2XLARGE,
               EC2Size.M2_4XLARGE);
   }

   @Provides
   @Singleton
   Set<? extends Location> provideLocations(Map<String, String> availabilityZoneToRegionMap) {
      Location ec2 = new LocationImpl(LocationScope.PROVIDER, providerName, providerName, null);
      Set<Location> locations = Sets.newLinkedHashSet();
      for (String zone : availabilityZoneToRegionMap.keySet()) {
         Location region = new LocationImpl(LocationScope.REGION, availabilityZoneToRegionMap
                  .get(zone), availabilityZoneToRegionMap.get(zone), ec2);
         locations.add(region);
         locations.add(new LocationImpl(LocationScope.ZONE, zone, zone, region));
      }
      return locations;
   }

   @Provides
   @Singleton
   Location getDefaultLocation(@EC2 final String region, Set<? extends Location> set) {
      return Iterables.find(set, new Predicate<Location>() {

         @Override
         public boolean apply(Location input) {
            return input.getId().equals(region);
         }

      });
   }

   private static class LogHolder {
      @Resource
      @Named(ComputeServiceConstants.COMPUTE_LOGGER)
      protected Logger logger = Logger.NULL;
   }

   @Provides
   @Singleton
   @Named(PROPERTY_EC2_AMI_OWNERS)
   String[] amiOwners(@Named(PROPERTY_EC2_AMI_OWNERS) String amiOwners) {
      if (amiOwners.trim().equals(""))
         return new String[] {};
      return Iterables.toArray(Splitter.on(',').split(amiOwners), String.class);
   }

   @Provides
   protected Set<? extends Image> provideImages(Map<RegionAndName, ? extends Image> map) {
      return ImmutableSet.copyOf(map.values());
   }

   @Provides
   @Singleton
   protected ConcurrentMap<RegionAndName, Image> provideImageMap(
            RegionAndIdToImage regionAndIdToImage) {
      return new MapMaker().makeComputingMap(regionAndIdToImage);
   }

   @Provides
   @Singleton
   protected Map<RegionAndName, ? extends Image> provideImages(final EC2Client sync,
            @EC2 Map<String, URI> regionMap, final LogHolder holder,
            Function<ComputeMetadata, String> indexer,
            @Named(PROPERTY_EC2_AMI_OWNERS) final String[] amiOwners, final ImageParser parser,
            final ConcurrentMap<RegionAndName, Image> images,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor)
            throws InterruptedException, ExecutionException, TimeoutException {
      if (amiOwners.length == 0) {
         holder.logger.debug(">> no owners specified, skipping image parsing");
      } else {
         holder.logger.debug(">> providing images");

         Map<String, ListenableFuture<?>> parallelResponses = Maps.newHashMap();

         for (final String region : regionMap.keySet()) {
            parallelResponses.put(region, ConcurrentUtils.makeListenable(executor
                     .submit(new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                           for (final org.jclouds.aws.ec2.domain.Image from : sync.getAMIServices()
                                    .describeImagesInRegion(region, ownedBy(amiOwners))) {
                              Image image = parser.apply(from);
                              if (image != null)
                                 images.put(new RegionAndName(region, image.getId()), image);
                              else if (from.getImageType() == ImageType.MACHINE)
                                 holder.logger.trace("<< image(%s) didn't parse", from.getId());
                           }
                           return null;
                        }
                     }), executor));
         }
         Map<String, Exception> exceptions = awaitCompletion(parallelResponses, executor, null,
                  holder.logger, "images");

         if (exceptions.size() > 0)
            throw new RuntimeException(String.format("error parsing images in regions: %s",
                     exceptions));

         holder.logger.debug("<< images(%d)", images.size());
      }
      return images;
   }
}
