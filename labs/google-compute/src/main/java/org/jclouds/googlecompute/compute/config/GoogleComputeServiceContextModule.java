/*
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.googlecompute.compute.config;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.strategy.PopulateDefaultLoginCredentialsForImageStrategy;
import org.jclouds.compute.strategy.PrioritizeCredentialsFromTemplate;
import org.jclouds.compute.strategy.impl.CreateNodesWithGroupEncodedIntoNameThenAddToSet;
import org.jclouds.domain.Location;
import org.jclouds.googlecompute.GoogleComputeApi;
import org.jclouds.googlecompute.compute.GoogleComputeService;
import org.jclouds.googlecompute.compute.GoogleComputeServiceAdapter;
import org.jclouds.googlecompute.compute.functions.BuildInstanceMetadata;
import org.jclouds.googlecompute.compute.functions.GoogleComputeImageToImage;
import org.jclouds.googlecompute.compute.functions.InstanceToNodeMetadata;
import org.jclouds.googlecompute.compute.functions.MachineTypeToHardware;
import org.jclouds.googlecompute.compute.functions.OrphanedGroupsFromDeadNodes;
import org.jclouds.googlecompute.compute.functions.ZoneToLocation;
import org.jclouds.googlecompute.compute.options.GoogleComputeTemplateOptions;
import org.jclouds.googlecompute.compute.predicates.AllNodesInGroupTerminated;
import org.jclouds.googlecompute.compute.strategy.ApplyTemplateOptionsCreateNodesWithGroupEncodedIntoNameThenAddToSet;
import org.jclouds.googlecompute.compute.strategy.GoogleComputePopulateDefaultLoginCredentialsForImageStrategy;
import org.jclouds.googlecompute.compute.strategy.UseNodeCredentialsButOverrideFromTemplate;
import org.jclouds.googlecompute.config.UserProject;
import org.jclouds.googlecompute.domain.Image;
import org.jclouds.googlecompute.domain.Instance;
import org.jclouds.googlecompute.domain.MachineType;
import org.jclouds.googlecompute.domain.Zone;

import javax.inject.Singleton;
import java.net.URI;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Maps.uniqueIndex;

/**
 * @author David Alves
 */
public class GoogleComputeServiceContextModule
        extends ComputeServiceAdapterContextModule<Instance, MachineType, Image, Zone> {

   @Override
   protected void configure() {
      super.configure();

      bind(ComputeService.class).to(GoogleComputeService.class);

      bind(new TypeLiteral<ComputeServiceAdapter<Instance, MachineType, Image, Zone>>() {})
              .to(GoogleComputeServiceAdapter.class);

      bind(new TypeLiteral<Function<Instance, NodeMetadata>>() {})
              .to(InstanceToNodeMetadata.class);

      bind(new TypeLiteral<Function<MachineType, Hardware>>() {})
              .to(MachineTypeToHardware.class);

      bind(new TypeLiteral<Function<Image, org.jclouds.compute.domain.Image>>() {})
              .to(GoogleComputeImageToImage.class);

      bind(new TypeLiteral<Function<Zone, Location>>() {})
              .to(ZoneToLocation.class);

      bind(new TypeLiteral<Function<TemplateOptions, ImmutableMap.Builder<String, String>>>() {})
              .to(BuildInstanceMetadata.class);

      bind(PopulateDefaultLoginCredentialsForImageStrategy.class)
              .to(GoogleComputePopulateDefaultLoginCredentialsForImageStrategy.class);

      bind(CreateNodesWithGroupEncodedIntoNameThenAddToSet.class).to(
              ApplyTemplateOptionsCreateNodesWithGroupEncodedIntoNameThenAddToSet.class);

      bind(TemplateOptions.class).to(GoogleComputeTemplateOptions.class);

      bind(new TypeLiteral<Function<Set<? extends NodeMetadata>, Set<String>>>() {})
              .to(OrphanedGroupsFromDeadNodes.class);

      bind(new TypeLiteral<Predicate<String>>() {}).to(AllNodesInGroupTerminated.class);

      bind(PrioritizeCredentialsFromTemplate.class).to(UseNodeCredentialsButOverrideFromTemplate.class);

      install(new LocationsFromComputeServiceAdapterModule<Instance, MachineType, Image, Zone>() {});

   }

   @Provides
   @Singleton
   @Memoized
   public Supplier<Map<URI, ? extends org.jclouds.compute.domain.Image>> provideImagesMap(
           final Supplier<Set<? extends org.jclouds.compute.domain.Image>> images) {
      return new Supplier<Map<URI, ? extends org.jclouds.compute.domain.Image>>() {
         @Override
         public Map<URI, ? extends org.jclouds.compute.domain.Image> get() {
            return uniqueIndex(images.get(), new Function<org.jclouds.compute.domain.Image, URI>() {
               @Override
               public URI apply(org.jclouds.compute.domain.Image input) {
                  return input.getUri();
               }
            });
         }
      };
   }

   @Provides
   @Singleton
   @Memoized
   public Supplier<Map<URI, ? extends Hardware>> provideHardwaresMap(
           final Supplier<Set<? extends Hardware>> hardwares) {
      return new Supplier<Map<URI, ? extends Hardware>>() {
         @Override
         public Map<URI, ? extends Hardware> get() {
            return uniqueIndex(hardwares.get(), new Function<Hardware, URI>() {
               @Override
               public URI apply(Hardware input) {
                  return input.getUri();
               }
            });
         }
      };
   }

   @Provides
   @Singleton
   @Memoized
   public Supplier<Map<URI, ? extends Location>> provideLocations(
           final GoogleComputeApi api, final Function<Zone, Location> zoneToLocation,
           final @UserProject Supplier<String> userProject) {
      return new Supplier<Map<URI, ? extends Location>>() {
         @Override
         public Map<URI, ? extends Location> get() {
            return uniqueIndex(transform(api.getZoneApiForProject(userProject.get()).list().concat(), zoneToLocation),
                    new Function<Location, URI>() {
                       @Override
                       public URI apply(Location input) {
                          return (URI) input.getMetadata().get("selfLink");
                       }
                    });
         }
      };
   }

   @Override
   protected Optional<ImageExtension> provideImageExtension(Injector i) {
      return Optional.absent();
   }

   @VisibleForTesting
   public static final Map<Instance.Status, NodeMetadata.Status> toPortableNodeStatus =
           ImmutableMap.<Instance.Status, NodeMetadata.Status>builder()
                   .put(Instance.Status.PROVISIONING, NodeMetadata.Status.PENDING)
                   .put(Instance.Status.STAGING, NodeMetadata.Status.PENDING)
                   .put(Instance.Status.RUNNING, NodeMetadata.Status.RUNNING)
                   .put(Instance.Status.STOPPING, NodeMetadata.Status.PENDING)
                   .put(Instance.Status.STOPPED, NodeMetadata.Status.SUSPENDED)
                   .put(Instance.Status.TERMINATED, NodeMetadata.Status.TERMINATED).build();

   @Singleton
   @Provides
   protected Map<Instance.Status, NodeMetadata.Status> toPortableNodeStatus() {
      return toPortableNodeStatus;
   }
}
