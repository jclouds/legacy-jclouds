/**
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
package org.jclouds.compute;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentMap;

import org.jclouds.compute.config.ResolvesImages;
import org.jclouds.compute.stub.StubApiMetadata;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.rest.internal.ContextBuilder;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * @author Adrian Cole
 */
public abstract class ComputeServiceContextBuilder<S, A, C extends ComputeServiceContext<S, A>, M extends ComputeServiceApiMetadata<S, A, C, M>> extends
      ContextBuilder<S, A, C, M> {
   
   /**
    * looks up a provider or api with the given id
    * 
    * @param providerOrApi
    *           id of the provider or api
    * @return means to build a context to that provider
    * @throws NoSuchElementException
    *            if the id was not configured.
    * @throws IllegalArgumentException
    *            if the api or provider isn't assignable from ComputeServiceContext
    */
   public static ComputeServiceContextBuilder<?, ?, ?, ?> newBuilder(String providerOrApi) throws NoSuchElementException {
      ContextBuilder<?, ?, ?, ?> builder = ContextBuilder.newBuilder(providerOrApi);
      checkArgument(builder instanceof ComputeServiceContextBuilder,
            "type of providerOrApi[%s] is not ComputeServiceContextBuilder: %s", providerOrApi, builder);
      return ComputeServiceContextBuilder.class.cast(builder);
   }
   
   @SuppressWarnings("rawtypes")
   public static ContextBuilder<ConcurrentMap, ConcurrentMap, ComputeServiceContext<ConcurrentMap, ConcurrentMap>, StubApiMetadata> forTests() {
      return ContextBuilder.newBuilder(new StubApiMetadata());
   }

   public ComputeServiceContextBuilder(ProviderMetadata<S, A, C, M> providerMetadata) {
      super(providerMetadata);
   }

   public ComputeServiceContextBuilder(M apiMetadata) {
      super(apiMetadata);
   }

   protected void addImageResolutionModule() {
      // do nothing;
      // this is to be overridden when needed
   }
   
   @Override
   public Injector buildInjector() {
      addImageResolutionModuleIfNotPresent();
      return super.buildInjector();
   }
   
   protected void addImageResolutionModuleIfNotPresent() {
      if (!Iterables.any(modules, new Predicate<Module>() {
         public boolean apply(Module input) {
            return input.getClass().isAnnotationPresent(ResolvesImages.class);
         }

      })) {
         addImageResolutionModule();
      }
   }

}