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
package org.jclouds.trmk.vcloudexpress;

import static org.jclouds.trmk.vcloud_0_8.reference.TerremarkConstants.PROPERTY_TERREMARK_EXTENSION_NAME;
import static org.jclouds.trmk.vcloud_0_8.reference.TerremarkConstants.PROPERTY_TERREMARK_EXTENSION_VERSION;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.trmk.vcloud_0_8.internal.TerremarkVCloudApiMetadata;
import org.jclouds.trmk.vcloudexpress.compute.TerremarkVCloudExpressComputeServiceContextModule;
import org.jclouds.trmk.vcloudexpress.config.TerremarkVCloudExpressRestClientModule;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for the Terremark vCloud Express API
 * 
 * @author Adrian Cole
 */
public class TerremarkVCloudExpressApiMetadata extends TerremarkVCloudApiMetadata {

   /**
    * @deprecated please use {@code org.jclouds.ContextBuilder#buildApi(TerremarkVCloudExpressClient.class)} as
    *             {@link TerremarkVCloudExpressAsyncClient} interface will be removed in jclouds 1.7.
    */
   @Deprecated
   public static final TypeToken<org.jclouds.rest.RestContext<TerremarkVCloudExpressClient, TerremarkVCloudExpressAsyncClient>> CONTEXT_TOKEN = new TypeToken<org.jclouds.rest.RestContext<TerremarkVCloudExpressClient, TerremarkVCloudExpressAsyncClient>>() {
      private static final long serialVersionUID = 1L;
   };
   
   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public TerremarkVCloudExpressApiMetadata() {
      this(new Builder());
   }

   protected TerremarkVCloudExpressApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = TerremarkVCloudApiMetadata.defaultProperties();
      properties.setProperty(PROPERTY_TERREMARK_EXTENSION_NAME, "vCloudExpressExtensions");
      properties.setProperty(PROPERTY_TERREMARK_EXTENSION_VERSION, "1.6");
      return properties;
   }

   public static class Builder extends TerremarkVCloudApiMetadata.Builder<Builder> {

      @SuppressWarnings("deprecation")
      protected Builder() {
         super(TerremarkVCloudExpressClient.class, TerremarkVCloudExpressAsyncClient.class);
         id("trmk-vcloudexpress")
         .name("Terremark vCloud Express API")
         .identityName("Email")
         .credentialName("Password")
         .version("0.8a-ext1.6")
         .defaultEndpoint("https://services.vcloudexpress.terremark.com/api")
         .documentation(URI.create("https://community.vcloudexpress.terremark.com/en-us/product_docs/m/vcefiles/2342.aspx"))
         .defaultProperties(TerremarkVCloudExpressApiMetadata.defaultProperties())
         .defaultModules(ImmutableSet.<Class<? extends Module>>of(TerremarkVCloudExpressRestClientModule.class, TerremarkVCloudExpressComputeServiceContextModule.class));
      }

      @Override
      public TerremarkVCloudExpressApiMetadata build() {
         return new TerremarkVCloudExpressApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
}
