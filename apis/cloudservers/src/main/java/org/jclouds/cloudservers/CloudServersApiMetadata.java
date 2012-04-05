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
package org.jclouds.cloudservers;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.internal.BaseComputeServiceApiMetadata;
import org.jclouds.openstack.OpenStackAuthAsyncClient;

import com.google.common.reflect.TypeToken;

/**
 * Implementation of {@link ApiMetadata} for CloudServers 1.0 API
 * 
 * @author Adrian Cole
 */
public class CloudServersApiMetadata
      extends
      BaseComputeServiceApiMetadata<CloudServersClient, CloudServersAsyncClient, ComputeServiceContext<CloudServersClient, CloudServersAsyncClient>, CloudServersApiMetadata> {

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public CloudServersApiMetadata() {
      this(new Builder());
   }

   protected CloudServersApiMetadata(Builder builder) {
      super(builder);
   }

   protected static Properties defaultProperties() {
      Properties properties = BaseComputeServiceApiMetadata.Builder.defaultProperties();
      return properties;
   }

   public static class Builder
         extends
         BaseComputeServiceApiMetadata.Builder<CloudServersClient, CloudServersAsyncClient, ComputeServiceContext<CloudServersClient, CloudServersAsyncClient>, CloudServersApiMetadata> {

      protected Builder() {
         id("cloudservers")
         .name("Rackspace Cloud Servers API")
         .identityName("Username")
         .credentialName("API Key")
         .documentation(URI.create("http://docs.rackspacecloud.com/servers/api/v1.0/cs-devguide/content/ch01.html"))
         .version(OpenStackAuthAsyncClient.VERSION)
         .defaultEndpoint("https://auth.api.rackspacecloud.com")
         .javaApi(CloudServersClient.class, CloudServersAsyncClient.class)
         .defaultProperties(CloudServersApiMetadata.defaultProperties())
         .contextBuilder(TypeToken.of(CloudServersContextBuilder.class));
      }

      @Override
      public CloudServersApiMetadata build() {
         return new CloudServersApiMetadata(this);
      }

      @Override
      public Builder fromApiMetadata(CloudServersApiMetadata in) {
         super.fromApiMetadata(in);
         return this;
      }

   }

}