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
package org.jclouds.apis;

import java.net.URI;

import org.jclouds.http.IntegrationTestAsyncClient;
import org.jclouds.http.IntegrationTestClient;
import org.jclouds.rest.internal.BaseRestApiMetadata;

/**
 * Implementation of @ link org.jclouds.types.ApiMetadata} for testing.
 * 
 * @author Jeremy Whitlock <jwhitlock@apache.org>, Adrian Cole
 */
public class JcloudsTestComputeApiMetadata extends BaseRestApiMetadata {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return Builder.class.cast(builder().fromApiMetadata(this));
   }

   public JcloudsTestComputeApiMetadata() {
      super(builder());
   }

   protected JcloudsTestComputeApiMetadata(Builder builder) {
      super(builder);
   }

   public static class Builder extends BaseRestApiMetadata.Builder<Builder> {

      protected Builder(){
         super(IntegrationTestClient.class, IntegrationTestAsyncClient.class);
         id("test-compute-api")
         .view(Compute.class)
         .name("Test Compute Api")
         .identityName("user")
         .credentialName("password")
         .documentation(URI.create("http://jclouds.org/documentation"));
      }

      @Override
      public JcloudsTestComputeApiMetadata build() {
         return new JcloudsTestComputeApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
}
