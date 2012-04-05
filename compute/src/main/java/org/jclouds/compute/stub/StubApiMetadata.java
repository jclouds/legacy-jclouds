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
package org.jclouds.compute.stub;

import java.net.URI;
import java.util.concurrent.ConcurrentMap;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.internal.BaseComputeServiceApiMetadata;
import org.jclouds.compute.stub.internal.StubComputeServiceContextBuilder;

import com.google.common.reflect.TypeToken;

/**
 * Implementation of {@link ApiMetadata} for jclouds in-memory (Stub) API
 * 
 * @author Adrian Cole
 */
@SuppressWarnings("rawtypes")
public class StubApiMetadata extends BaseComputeServiceApiMetadata<ConcurrentMap, ConcurrentMap, ComputeServiceContext<ConcurrentMap, ConcurrentMap>, StubApiMetadata> {
   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return Builder.class.cast(builder().fromApiMetadata(this));
   }

   public StubApiMetadata() {
      super(builder());
   }

   protected StubApiMetadata(Builder builder) {
      super(builder);
   }

   public static class Builder extends BaseComputeServiceApiMetadata.Builder<ConcurrentMap, ConcurrentMap, ComputeServiceContext<ConcurrentMap, ConcurrentMap>, StubApiMetadata> {

      protected Builder(){
         id("stub")
         .name("in-memory (Stub) API")
         .identityName("Unused")
         .defaultIdentity("stub")
         .defaultCredential("stub")
         .defaultEndpoint("stub")
         .context(new TypeToken<ComputeServiceContext<ConcurrentMap, ConcurrentMap>>(getClass()){
            private static final long serialVersionUID = 1L;
            })
         .javaApi(ConcurrentMap.class, ConcurrentMap.class)
         .documentation(URI.create("http://www.jclouds.org/documentation/userguide/compute"))
         .contextBuilder(TypeToken.of(StubComputeServiceContextBuilder.class));
      }

      @Override
      public StubApiMetadata build() {
         return new StubApiMetadata(this);
      }

   }
}