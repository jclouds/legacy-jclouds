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
package org.jclouds.gogrid;

import static org.jclouds.reflect.Reflection2.typeToken;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.gogrid.compute.config.GoGridComputeServiceContextModule;
import org.jclouds.gogrid.config.GoGridRestClientModule;
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

   /**
    * @deprecated please use {@code org.jclouds.ContextBuilder#buildApi(GoGridClient.class)} as
    *             {@link GoGridAsyncClient} interface will be removed in jclouds 1.7.
    */
   @Deprecated
   public static final TypeToken<org.jclouds.rest.RestContext<GoGridClient, GoGridAsyncClient>> CONTEXT_TOKEN = new TypeToken<org.jclouds.rest.RestContext<GoGridClient, GoGridAsyncClient>>() {
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
      properties.setProperty("jclouds.ssh.max-retries", "5");
      properties.setProperty("jclouds.ssh.retry-auth", "true");
      return properties;
   }

   public static class Builder extends BaseRestApiMetadata.Builder<Builder> {

      @SuppressWarnings("deprecation")
      protected Builder() {
         super(GoGridClient.class, GoGridAsyncClient.class);
         id("gogrid")
         .name("GoGrid API")
         .identityName("API Key")
         .credentialName("Shared Secret")
         .documentation(URI.create("https://wiki.gogrid.com/wiki/index.php/API"))
         .version("1.5")
         .defaultEndpoint("https://api.gogrid.com/api")
         .defaultProperties(GoGridApiMetadata.defaultProperties())
         .view(typeToken(ComputeServiceContext.class))
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
