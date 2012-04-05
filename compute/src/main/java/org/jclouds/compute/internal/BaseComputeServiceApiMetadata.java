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
package org.jclouds.compute.internal;

import org.jclouds.apis.ApiType;
import org.jclouds.apis.internal.BaseApiMetadata;
import org.jclouds.compute.ComputeServiceApiMetadata;
import org.jclouds.compute.ComputeServiceContext;

import com.google.common.annotations.Beta;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

/**
 * 
 * @author Adrian Cole
 * @since 1.5
 */
@Beta
public abstract class BaseComputeServiceApiMetadata<S, A, C extends ComputeServiceContext<S, A>, M extends ComputeServiceApiMetadata<S, A, C, M>>
      extends BaseApiMetadata<S, A, C, M> implements ComputeServiceApiMetadata<S, A, C, M> {

   public static class Builder<S, A, C extends ComputeServiceContext<S, A>, M extends ComputeServiceApiMetadata<S, A, C, M>>
         extends BaseApiMetadata.Builder<S, A, C, M> implements ComputeServiceApiMetadata.Builder<S, A, C, M> {
      public Builder() {
         type(ApiType.COMPUTE);
      }
      
      /**
       * {@inheritDoc}
       */
      @Override
      @SuppressWarnings("rawtypes")
      protected TypeToken contextToken(TypeToken<S> clientToken, TypeToken<A> asyncClientToken) {
         return new TypeToken<ComputeServiceContext<S, A>>() {
            private static final long serialVersionUID = 1L;
         }.where(new TypeParameter<S>() {
         }, clientToken).where(new TypeParameter<A>() {
         }, asyncClientToken);
      }
   }

   protected BaseComputeServiceApiMetadata(BaseComputeServiceApiMetadata.Builder<?, ?, ?, ?> builder) {
      super(builder);
   }

}