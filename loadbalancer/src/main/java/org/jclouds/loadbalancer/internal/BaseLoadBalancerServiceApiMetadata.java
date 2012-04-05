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
package org.jclouds.loadbalancer.internal;

import org.jclouds.apis.ApiType;
import org.jclouds.apis.internal.BaseApiMetadata;
import org.jclouds.loadbalancer.LoadBalancerServiceApiMetadata;
import org.jclouds.loadbalancer.LoadBalancerServiceContext;

import com.google.common.annotations.Beta;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

/**
 * 
 * @author Adrian Cole
 * @since 1.5
 */
@Beta
public abstract class BaseLoadBalancerServiceApiMetadata<S, A, C extends LoadBalancerServiceContext<S, A>, M extends LoadBalancerServiceApiMetadata<S, A, C, M>>
      extends BaseApiMetadata<S, A, C, M> implements LoadBalancerServiceApiMetadata<S, A, C, M> {

   public static class Builder<S, A, C extends LoadBalancerServiceContext<S, A>, M extends LoadBalancerServiceApiMetadata<S, A, C, M>>
         extends BaseApiMetadata.Builder<S, A, C, M> implements LoadBalancerServiceApiMetadata.Builder<S, A, C, M> {
      public Builder() {
         type(ApiType.LOADBALANCER);
      }
      
      /**
       * {@inheritDoc}
       */
      @Override
      @SuppressWarnings("rawtypes")
      protected TypeToken contextToken(TypeToken<S> clientToken, TypeToken<A> asyncClientToken) {
         return new TypeToken<LoadBalancerServiceContext<S, A>>() {
            private static final long serialVersionUID = 1L;
         }.where(new TypeParameter<S>() {
         }, clientToken).where(new TypeParameter<A>() {
         }, asyncClientToken);
      }
   }

   protected BaseLoadBalancerServiceApiMetadata(BaseLoadBalancerServiceApiMetadata.Builder<?, ?, ?, ?> builder) {
      super(builder);
   }

}