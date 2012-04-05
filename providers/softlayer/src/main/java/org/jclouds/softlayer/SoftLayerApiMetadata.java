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
package org.jclouds.softlayer;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.apis.ApiType;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.internal.BaseComputeServiceApiMetadata;

import com.google.common.reflect.TypeToken;

/**
 * Implementation of {@link ApiMetadata} for  API
 * 
 * @author Adrian Cole
 */
public class SoftLayerApiMetadata
      extends
      BaseComputeServiceApiMetadata<SoftLayerClient, SoftLayerAsyncClient, ComputeServiceContext<SoftLayerClient, SoftLayerAsyncClient>, SoftLayerApiMetadata> {

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public SoftLayerApiMetadata() {
      this(new Builder());
   }

   protected SoftLayerApiMetadata(Builder builder) {
      super(builder);
   }

   protected static Properties defaultProperties() {
      Properties properties = BaseComputeServiceApiMetadata.Builder.defaultProperties();
      properties.setProperty("jclouds.ssh.max-retries", "5");
      properties.setProperty("jclouds.ssh.retry-auth", "true");
      return properties;
   }

   public static class Builder
         extends
         BaseComputeServiceApiMetadata.Builder<SoftLayerClient, SoftLayerAsyncClient, ComputeServiceContext<SoftLayerClient, SoftLayerAsyncClient>, SoftLayerApiMetadata> {

      protected Builder() {
         id("softlayer")
         .type(ApiType.COMPUTE)
         .name("SoftLayer API")
         .identityName("API Username")
         .credentialName("API Key")
         .documentation(URI.create("http://sldn.softlayer.com/article/REST"))
         .version("3")
         .defaultEndpoint("https://api.softlayer.com/rest")
         .defaultProperties(SoftLayerApiMetadata.defaultProperties())
         .javaApi(SoftLayerClient.class, SoftLayerAsyncClient.class)
         .contextBuilder(TypeToken.of(SoftLayerContextBuilder.class));
      }

      @Override
      public SoftLayerApiMetadata build() {
         return new SoftLayerApiMetadata(this);
      }

      @Override
      public Builder fromApiMetadata(SoftLayerApiMetadata in) {
         super.fromApiMetadata(in);
         return this;
      }

   }

}
