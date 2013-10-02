/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.compute.config;

import static com.google.common.base.Functions.compose;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Predicates.notNull;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.strategy.CreateNodeWithGroupEncodedIntoName;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetImageStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.PopulateDefaultLoginCredentialsForImageStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.compute.strategy.ResumeNodeStrategy;
import org.jclouds.compute.strategy.SuspendNodeStrategy;
import org.jclouds.compute.strategy.impl.AdaptingComputeServiceStrategies;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.location.suppliers.LocationsSupplier;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * 
 * @author Adrian Cole
 */
public class ComputeServiceAdapterContextModule<N, H, I, L> extends BaseComputeServiceContextModule {

   /**
    * install this, if you want to use your computeservice adapter to handle locations. Note that if you do this, you'll
    * want to instantiate a subclass to prevent type erasure.
    * 
    * ex.
    * 
    * <pre>
    * install(new LocationsFromComputeServiceAdapterModule&lt;NodeMetadata, Hardware, Image, Location&gt;() {
    * });
    * </pre>
    * 
    * not
    * 
    * <pre>
    * install(new LocationsFromComputeServiceAdapterModule&lt;NodeMetadata, Hardware, Image, Location&gt;());
    * </pre>
    */
   public static class LocationsFromComputeServiceAdapterModule<N, H, I, L> extends AbstractModule {

      @Override
      protected void configure() {
      }

      @Provides
      @Singleton
      protected LocationsSupplier supplyLocationsFromComputeServiceAdapter(
            final ComputeServiceAdapter<N, H, I, L> adapter, final Function<L, Location> transformer) {
         return new LocationsSupplier() {
            @Override
            public Set<? extends Location> get() {
               return transformGuardingNull(adapter.listLocations(), transformer);
            }

            public String toString() {
               return toStringHelper(adapter).add("method", "listLocations").toString();
            }
         };
      }
   }

   @Provides
   @Singleton
   protected Supplier<Set<? extends Hardware>> provideHardware(final ComputeServiceAdapter<N, H, I, L> adapter,
         final Function<H, Hardware> transformer) {
      return new Supplier<Set<? extends Hardware>>() {
         @Override
         public Set<? extends Hardware> get() {
            return transformGuardingNull(adapter.listHardwareProfiles(), transformer);
         }

         public String toString() {
            return toStringHelper(adapter).add("method", "listHardwareProfiles").toString();
         }
      };
   }

   private static <F, T> Set<? extends T> transformGuardingNull(Iterable<F> from, Function<F, T> transformer) {
      return FluentIterable.from(from).filter(notNull()).transform(transformer).filter(notNull()).toSet();
   }

   @Provides
   @Singleton
   protected Supplier<Set<? extends Image>> provideImages(final ComputeServiceAdapter<N, H, I, L> adapter,
         final Function<I, Image> transformer, final AddDefaultCredentialsToImage addDefaultCredentialsToImage) {
      return new Supplier<Set<? extends Image>>() {
         @Override
         public Set<? extends Image> get() {
            return transformGuardingNull(adapter.listImages(), compose(addDefaultCredentialsToImage, transformer));
         }

         public String toString() {
            return toStringHelper(adapter).add("method", "listImages").toString();
         }
      };
   }

   @Singleton
   public static class AddDefaultCredentialsToImage implements Function<Image, Image> {
      private final PopulateDefaultLoginCredentialsForImageStrategy credsForImage;

      @Inject
      public AddDefaultCredentialsToImage(PopulateDefaultLoginCredentialsForImageStrategy credsForImage) {
         this.credsForImage = credsForImage;
      }

      @Override
      public Image apply(Image arg0) {
         if (arg0 == null)
            return null;
         LoginCredentials credentials = credsForImage.apply(arg0);
         return credentials != null ? ImageBuilder.fromImage(arg0).defaultCredentials(credentials).build() : arg0;
      }

      @Override
      public String toString() {
         return toStringHelper(this).add("credsForImage", credsForImage).toString();
      }
   }

   @Provides
   @Singleton
   protected CreateNodeWithGroupEncodedIntoName defineAddNodeWithTagStrategy(
         AdaptingComputeServiceStrategies<N, H, I, L> in) {
      return in;
   }

   @Provides
   @Singleton
   protected DestroyNodeStrategy defineDestroyNodeStrategy(AdaptingComputeServiceStrategies<N, H, I, L> in) {
      return in;
   }

   @Provides
   @Singleton
   protected GetNodeMetadataStrategy defineGetNodeMetadataStrategy(AdaptingComputeServiceStrategies<N, H, I, L> in) {
      return in;
   }

   @Provides
   @Singleton
   protected GetImageStrategy defineGetImageStrategy(AdaptingComputeServiceStrategies<N, H, I, L> in) {
      return in;
   }

   @Provides
   @Singleton
   protected ListNodesStrategy defineListNodesStrategy(AdaptingComputeServiceStrategies<N, H, I, L> in) {
      return in;
   }

   @Provides
   @Singleton
   protected RebootNodeStrategy defineRebootNodeStrategy(AdaptingComputeServiceStrategies<N, H, I, L> in) {
      return in;
   }

   @Provides
   @Singleton
   protected ResumeNodeStrategy defineStartNodeStrategy(AdaptingComputeServiceStrategies<N, H, I, L> in) {
      return in;
   }

   @Provides
   @Singleton
   protected SuspendNodeStrategy defineStopNodeStrategy(AdaptingComputeServiceStrategies<N, H, I, L> in) {
      return in;
   }
}
