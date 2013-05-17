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
package org.jclouds.glesys;

import static org.jclouds.reflect.Reflection2.typeToken;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.glesys.compute.config.GleSYSComputeServiceContextModule;
import org.jclouds.glesys.config.GleSYSRestClientModule;
import org.jclouds.rest.internal.BaseRestApiMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for  API
 * 
 * @author Adrian Cole
 */
public class GleSYSApiMetadata extends BaseRestApiMetadata {

   /**
    * @deprecated please use {@code org.jclouds.ContextBuilder#buildApi(GleSYSApi.class)} as
    *             {@link GleSYSAsyncApi} interface will be removed in jclouds 1.7.
    */
   @Deprecated
   public static final TypeToken<org.jclouds.rest.RestContext<GleSYSApi, GleSYSAsyncApi>> CONTEXT_TOKEN = new TypeToken<org.jclouds.rest.RestContext<GleSYSApi, GleSYSAsyncApi>>() {
      private static final long serialVersionUID = 1L;
   };
   
   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public GleSYSApiMetadata() {
      this(new Builder());
   }

   protected GleSYSApiMetadata(Builder builder) {
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
         super(GleSYSApi.class, GleSYSAsyncApi.class);
         id("glesys")
         .name("GleSYS API")
         .identityName("Username")
         .credentialName("API Key")
         .documentation(URI.create("https://customer.glesys.com/api.php"))
         .version("1")
         .buildVersion("3.5.0")
         .defaultEndpoint("https://api.glesys.com")
         .defaultProperties(GleSYSApiMetadata.defaultProperties())
         .view(typeToken(ComputeServiceContext.class))
         .defaultModules(ImmutableSet.<Class<? extends Module>>of(GleSYSComputeServiceContextModule.class, GleSYSRestClientModule.class));
      }

      @Override
      public GleSYSApiMetadata build() {
         return new GleSYSApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
}
