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
package org.jclouds.elasticstack;

import static org.jclouds.elasticstack.reference.ElasticStackConstants.PROPERTY_VNC_PASSWORD;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.internal.BaseComputeServiceApiMetadata;

import com.google.common.reflect.TypeToken;

/**
 * Implementation of {@link ApiMetadata} for the ElasticStack API
 * 
 * @author Adrian Cole
 */
public class ElasticStackApiMetadata
      extends
      BaseComputeServiceApiMetadata<ElasticStackClient, ElasticStackAsyncClient, ComputeServiceContext<ElasticStackClient, ElasticStackAsyncClient>, ElasticStackApiMetadata> {

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public ElasticStackApiMetadata() {
      this(new Builder());
   }

   protected ElasticStackApiMetadata(Builder builder) {
      super(builder);
   }

   protected static Properties defaultProperties() {
      Properties properties = BaseComputeServiceApiMetadata.Builder.defaultProperties();
      properties.setProperty(PROPERTY_VNC_PASSWORD, "IL9vs34d");
      // passwords are set post-boot, so auth failures are possible
      // from a race condition applying the password set script
      properties.setProperty("jclouds.ssh.max-retries", "5");
      properties.setProperty("jclouds.ssh.retry-auth", "true");
      return properties;
   }

   public static class Builder
         extends
         BaseComputeServiceApiMetadata.Builder<ElasticStackClient, ElasticStackAsyncClient, ComputeServiceContext<ElasticStackClient, ElasticStackAsyncClient>, ElasticStackApiMetadata> {

      protected Builder() {
         id("elasticstack")
         .name("ElasticStack API")
         .identityName("UUID")
         .credentialName("Secret API key")
         .documentation(URI.create("http://www.elasticstack.com/cloud-platform/api"))
         .version("1.0")
         .defaultEndpoint("https://api.lon-p.elastichosts.com")
         .defaultProperties(ElasticStackApiMetadata.defaultProperties())
         .javaApi(ElasticStackClient.class, ElasticStackAsyncClient.class)
         .contextBuilder(TypeToken.of(ElasticStackContextBuilder.class));
      }

      @Override
      public ElasticStackApiMetadata build() {
         return new ElasticStackApiMetadata(this);
      }

      @Override
      public Builder fromApiMetadata(ElasticStackApiMetadata in) {
         super.fromApiMetadata(in);
         return this;
      }

   }

}
