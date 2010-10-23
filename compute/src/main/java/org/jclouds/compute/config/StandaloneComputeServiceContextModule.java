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

package org.jclouds.compute.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Set;

import javax.inject.Singleton;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.compute.strategy.AddNodeWithTagStrategy;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.compute.strategy.impl.AdaptingComputeServiceStrategies;
import org.jclouds.compute.suppliers.DefaultLocationSupplier;
import org.jclouds.domain.Location;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
public class StandaloneComputeServiceContextModule<N, H, I, L> extends BaseComputeServiceContextModule {

   @Override
   protected void configure() {
      super.configure();
      bindDefaultLocation();
      bind(new TypeLiteral<ComputeServiceContext>() {
      }).to(new TypeLiteral<ComputeServiceContextImpl<ComputeService, ComputeService>>() {
      }).in(Scopes.SINGLETON);
   }

   public class TransformingSetSupplier<F, T> implements Supplier<Set<? extends T>> {
      private final Supplier<Iterable<F>> backingSupplier;
      private final Function<F, T> converter;

      public TransformingSetSupplier(Supplier<Iterable<F>> backingSupplier, Function<F, T> converter) {
         this.backingSupplier = checkNotNull(backingSupplier, "backingSupplier");
         this.converter = checkNotNull(converter, "converter");
      }

      @Override
      public Set<? extends T> get() {
         return newHashSet(transform(backingSupplier.get(), converter));
      }

   }

   @Provides
   @Singleton
   protected Supplier<Set<? extends Hardware>> provideHardware(final ComputeServiceAdapter<N, H, I, L> adapter,
         Function<H, Hardware> transformer) {
      return new TransformingSetSupplier<H, Hardware>(new Supplier<Iterable<H>>() {

         @Override
         public Iterable<H> get() {
            return adapter.listHardware();
         }

      }, transformer);
   }

   @Provides
   @Singleton
   protected Supplier<Set<? extends Image>> provideImages(final ComputeServiceAdapter<N, H, I, L> adapter,
         Function<I, Image> transformer) {
      return new TransformingSetSupplier<I, Image>(new Supplier<Iterable<I>>() {

         @Override
         public Iterable<I> get() {
            return adapter.listImages();
         }

      }, transformer);
   }

   protected void bindDefaultLocation() {
      bind(new TypeLiteral<Supplier<Location>>() {
      }).to(DefaultLocationSupplier.class);
   }

   @Provides
   @Singleton
   protected Supplier<Set<? extends Location>> provideLocations(final ComputeServiceAdapter<N, H, I, L> adapter,
         Function<L, Location> transformer) {
      return new TransformingSetSupplier<L, Location>(new Supplier<Iterable<L>>() {

         @Override
         public Iterable<L> get() {
            return adapter.listLocations();
         }

      }, transformer);
   }

   @Provides
   @Singleton
   protected AddNodeWithTagStrategy defineAddNodeWithTagStrategy(AdaptingComputeServiceStrategies<N, H, I, L> in) {
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
   protected ListNodesStrategy defineListNodesStrategy(AdaptingComputeServiceStrategies<N, H, I, L> in) {
      return in;
   }

   @Provides
   @Singleton
   protected RebootNodeStrategy defineRebootNodeStrategy(AdaptingComputeServiceStrategies<N, H, I, L> in) {
      return in;
   }

   // enum singleton pattern
   public static enum IdentityFunction implements Function<Object, Object> {
      INSTANCE;

      public Object apply(Object o) {
         return o;
      }

      @Override
      public String toString() {
         return "identity";
      }
   }

}