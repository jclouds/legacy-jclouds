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
package org.jclouds.loadbalancer;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.NoSuchElementException;

import org.jclouds.providers.ProviderMetadata;
import org.jclouds.rest.internal.ContextBuilder;

/**
 * @author Adrian Cole
 */
public abstract class LoadBalancerServiceContextBuilder<S, A, C extends LoadBalancerServiceContext<S, A>, M extends LoadBalancerServiceApiMetadata<S, A, C, M>> extends
      ContextBuilder<S, A, C, M> {
   
//   TODO:
//   public static ContextBuilder<?, ?, LoadBalancerServiceContext, ?> forTests() {
//      return ContextBuilder.newBuilder(new StubApiMetadata());
//   }
   
   /**
    * looks up a provider or api with the given id
    * 
    * @param providerOrApi
    *           id of the provider or api
    * @return means to build a context to that provider
    * @throws NoSuchElementException
    *            if the id was not configured.
    * @throws IllegalArgumentException
    *            if the api or provider isn't assignable from LoadBalancerServiceContext
    */
   public static LoadBalancerServiceContextBuilder<?, ?, ?, ?> newBuilder(String providerOrApi) throws NoSuchElementException {
      ContextBuilder<?, ?, ?, ?> builder = ContextBuilder.newBuilder(providerOrApi);
      checkArgument(builder instanceof LoadBalancerServiceContextBuilder,
            "type of providerOrApi[%s] is not LoadBalancerServiceContextBuilder: %s", providerOrApi, builder);
      return LoadBalancerServiceContextBuilder.class.cast(builder);
   }
   
   public LoadBalancerServiceContextBuilder(ProviderMetadata<S, A, C, M> providerMetadata) {
      super(providerMetadata);
   }

   public LoadBalancerServiceContextBuilder(M apiMetadata) {
      super(apiMetadata);
   }

}