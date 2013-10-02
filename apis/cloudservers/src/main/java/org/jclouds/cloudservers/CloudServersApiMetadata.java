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
package org.jclouds.cloudservers;

import static org.jclouds.reflect.Reflection2.typeToken;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.cloudservers.compute.config.CloudServersComputeServiceContextModule;
import org.jclouds.cloudservers.config.CloudServersRestClientModule;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.rest.internal.BaseRestApiMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for CloudServers 1.0 API
 * 
 * @author Adrian Cole
 */
public class CloudServersApiMetadata extends BaseRestApiMetadata {

   /**
    * @deprecated please use {@code org.jclouds.ContextBuilder#buildApi(CloudServersClient.class)} as
    *             {@link CloudServersAsyncClient} interface will be removed in jclouds 1.7.
    */
   @Deprecated
   public static final TypeToken<org.jclouds.rest.RestContext<CloudServersClient, CloudServersAsyncClient>> CONTEXT_TOKEN = new TypeToken<org.jclouds.rest.RestContext<CloudServersClient, CloudServersAsyncClient>>() {
      private static final long serialVersionUID = 1L;
   };

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

   public static Properties defaultProperties() {
      Properties properties = BaseRestApiMetadata.defaultProperties();
      return properties;
   }

   public static class Builder extends BaseRestApiMetadata.Builder<Builder> {

      @SuppressWarnings("deprecation")
      protected Builder() {
         super(CloudServersClient.class, CloudServersAsyncClient.class);
         id("cloudservers")
         .name("Rackspace Cloud Servers API")
         .identityName("Username")
         .credentialName("API Key")
         .documentation(URI.create("http://docs.rackspacecloud.com/servers/api/v1.0/cs-devguide/content/ch01.html"))
         .version("1.0")
         .defaultEndpoint("https://auth.api.rackspacecloud.com")
         .defaultProperties(CloudServersApiMetadata.defaultProperties())
         .view(typeToken(ComputeServiceContext.class))
         .defaultModules(ImmutableSet.<Class<? extends Module>>of(CloudServersRestClientModule.class, CloudServersComputeServiceContextModule.class));
      }

      @Override
      public CloudServersApiMetadata build() {
         return new CloudServersApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
}
