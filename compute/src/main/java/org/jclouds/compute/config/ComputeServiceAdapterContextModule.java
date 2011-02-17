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

import java.util.Set;

import javax.inject.Singleton;

import org.jclouds.collect.TransformingSetSupplier;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.compute.strategy.CreateNodeWithGroupEncodedIntoName;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.compute.strategy.ResumeNodeStrategy;
import org.jclouds.compute.strategy.SuspendNodeStrategy;
import org.jclouds.compute.strategy.impl.AdaptingComputeServiceStrategies;
import org.jclouds.domain.Location;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;

/**
 * 
 * @author Adrian Cole
 */
public class ComputeServiceAdapterContextModule<S, A, N, H, I, L> extends BaseComputeServiceContextModule {

   private Class<A> asyncClientType;
   private Class<S> syncClientType;

   public ComputeServiceAdapterContextModule(Class<S> syncClientType, Class<A> asyncClientType) {
      this.syncClientType = syncClientType;
      this.asyncClientType = asyncClientType;
   }

   @SuppressWarnings( { "unchecked", "rawtypes" })
   @Override
   protected void configure() {
      super.configure();
      bind(new TypeLiteral<ComputeServiceContext>() {
      }).to(
               (TypeLiteral) TypeLiteral.get(Types.newParameterizedType(ComputeServiceContextImpl.class,
                        syncClientType, asyncClientType))).in(Scopes.SINGLETON);
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
   protected Supplier<Set<? extends Hardware>> provideHardware(final ComputeServiceAdapter<N, H, I, L> adapter,
            Function<H, Hardware> transformer) {
      return new TransformingSetSupplier<H, Hardware>(new Supplier<Iterable<H>>() {

         @Override
         public Iterable<H> get() {
            return adapter.listHardwareProfiles();
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

   @Provides
   @Singleton
   protected CreateNodeWithGroupEncodedIntoName defineAddNodeWithTagStrategy(AdaptingComputeServiceStrategies<N, H, I, L> in) {
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