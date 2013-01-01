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
package org.jclouds.gogrid;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.Constants.PROPERTY_TIMEOUTS_PREFIX;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.gogrid.compute.config.GoGridComputeServiceContextModule;
import org.jclouds.gogrid.config.GoGridRestClientModule;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.BaseRestApiMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for  API
 * 
 * @author Adrian Cole
 */
public class GoGridApiMetadata extends BaseRestApiMetadata {

   public static final TypeToken<RestContext<GoGridClient, GoGridAsyncClient>> CONTEXT_TOKEN = new TypeToken<RestContext<GoGridClient, GoGridAsyncClient>>() {
      private static final long serialVersionUID = 1L;
   };
   
   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public GoGridApiMetadata() {
      this(new Builder());
   }

   protected GoGridApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseRestApiMetadata.defaultProperties();
      properties.setProperty(PROPERTY_TIMEOUTS_PREFIX + "default", SECONDS.toMillis(90) + "");
      properties.setProperty("jclouds.ssh.max-retries", "5");
      properties.setProperty("jclouds.ssh.retry-auth", "true");
      return properties;
   }

   public static class Builder extends BaseRestApiMetadata.Builder<Builder> {

      protected Builder() {
         super(GoGridClient.class, GoGridAsyncClient.class);
         id("gogrid")
         .name("GoGrid API")
         .identityName("API Key")
         .credentialName("Shared Secret")
         .documentation(URI.create("https://wiki.gogrid.com/wiki/index.php/API"))
         .version(GoGridAsyncClient.VERSION)
         .defaultEndpoint("https://api.gogrid.com/api")
         .defaultProperties(GoGridApiMetadata.defaultProperties())
         .view(TypeToken.of(ComputeServiceContext.class))
         .defaultModules(ImmutableSet.<Class<? extends Module>>of(GoGridRestClientModule.class, GoGridComputeServiceContextModule.class));
      }

      @Override
      public GoGridApiMetadata build() {
         return new GoGridApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
}
