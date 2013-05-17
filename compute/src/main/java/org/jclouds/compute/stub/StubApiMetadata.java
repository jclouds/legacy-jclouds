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
package org.jclouds.compute.stub;

import java.net.URI;

import org.jclouds.apis.internal.BaseApiMetadata;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.stub.config.StubComputeServiceContextModule;

/**
 * Implementation of {@link ApiMetadata} for jclouds in-memory (Stub) API
 * 
 * @author Adrian Cole
 */
public class StubApiMetadata extends BaseApiMetadata {

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public StubApiMetadata() {
      super(new Builder());
   }

   protected StubApiMetadata(Builder builder) {
      super(builder);
   }

   public static class Builder extends BaseApiMetadata.Builder<Builder> {

      protected Builder(){
         id("stub")
         .name("in-memory (Stub) API")
         .identityName("Unused")
         .defaultIdentity("stub")
         .defaultCredential("stub")
         .defaultEndpoint("stub")
         .documentation(URI.create("http://www.jclouds.org/documentation/userguide/compute"))
         .view(ComputeServiceContext.class)
         .defaultModule(StubComputeServiceContextModule.class);
      }

      @Override
      public StubApiMetadata build() {
         return new StubApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
}
