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
package org.jclouds.rest.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.apis.internal.BaseApiMetadata;
import org.jclouds.rest.RestApiMetadata;
import org.jclouds.rest.RestContext;

import com.google.common.annotations.Beta;

/**
 * Useful in creating rest clients.
 * 
 * @author Adrian Cole
 */
@Beta
public class BaseRestApiMetadata<S, A, C extends RestContext<S, A>, M extends RestApiMetadata<S, A, C, M>> extends BaseApiMetadata<S, A, C, M>
      implements RestApiMetadata<S, A, C, M> {

   @SuppressWarnings("unchecked")
   @Override
   public Builder<S, A, C, M> toBuilder() {
      return new Builder<S, A, C, M>(getApi(), getAsyncApi()).fromApiMetadata((M) this);
   }

   public BaseRestApiMetadata(Class<S> client, Class<A> asyncClient) {
      super(new Builder<S, A, C, M>(client, asyncClient));
   }

   protected BaseRestApiMetadata(Builder<S, A, C, M> builder) {
      super(builder);
   }

   public static class Builder<S, A, C extends RestContext<S, A>, M extends RestApiMetadata<S, A, C, M> > extends
         BaseApiMetadata.Builder<S, A, C, M> implements RestApiMetadata.Builder<S, A, C, M> {

      public Builder(Class<S> client, Class<A> asyncClient) {
         checkNotNull(client, "client");
         checkNotNull(asyncClient, "asyncClient");
         javaApi(client, asyncClient);
      }

      @SuppressWarnings("unchecked")
      @Override
      public M build() {
         return (M) new BaseRestApiMetadata<S, A, C, M>(this);
      }
      
      @Override
      public Builder<S, A, C, M> fromApiMetadata(M in) {
         super.fromApiMetadata(in);
         return this;
      }
   }

}
