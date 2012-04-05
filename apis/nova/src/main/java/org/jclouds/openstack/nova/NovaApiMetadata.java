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
package org.jclouds.openstack.nova;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.internal.BaseComputeServiceApiMetadata;

import com.google.common.reflect.TypeToken;

/**
 * Implementation of {@link ApiMetadata} for Nova 1.0 API
 * 
 * @author Adrian Cole
 */
public class NovaApiMetadata
      extends
      BaseComputeServiceApiMetadata<NovaClient, NovaAsyncClient, ComputeServiceContext<NovaClient, NovaAsyncClient>, NovaApiMetadata> {

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public NovaApiMetadata() {
      this(new Builder());
   }

   protected NovaApiMetadata(Builder builder) {
      super(builder);
   }

   protected static Properties defaultProperties() {
      Properties properties = BaseComputeServiceApiMetadata.Builder.defaultProperties();
      return properties;
   }

   public static class Builder
         extends
         BaseComputeServiceApiMetadata.Builder<NovaClient, NovaAsyncClient, ComputeServiceContext<NovaClient, NovaAsyncClient>, NovaApiMetadata> {

      protected Builder() {
          id("nova")
         .name("OpenStack Nova Pre-Diablo API")
         .identityName("accessKey")
         .credentialName("secretKey")
         .documentation(URI.create("http://api.openstack.org/"))
         .version("1.1")
         .defaultEndpoint("http://localhost:5000")
         .javaApi(NovaClient.class, NovaAsyncClient.class)
         .defaultProperties(NovaApiMetadata.defaultProperties())
         .contextBuilder(TypeToken.of(NovaContextBuilder.class));
      }

      @Override
      public NovaApiMetadata build() {
         return new NovaApiMetadata(this);
      }

      @Override
      public Builder fromApiMetadata(NovaApiMetadata in) {
         super.fromApiMetadata(in);
         return this;
      }

   }

}