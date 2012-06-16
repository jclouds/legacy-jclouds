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
package org.jclouds.joyent.sdc.v6_5;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.joyent.sdc.v6_5.compute.config.SDCComputeServiceContextModule;
import org.jclouds.joyent.sdc.v6_5.config.DatacentersAreZonesModule;
import org.jclouds.joyent.sdc.v6_5.config.SDCProperties;
import org.jclouds.joyent.sdc.v6_5.config.SDCRestClientModule;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.BaseRestApiMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for SDC ~6.5 API
 * 
 * @author Adrian Cole
 */
public class SDCApiMetadata extends BaseRestApiMetadata {

   /** The serialVersionUID */
   private static final long serialVersionUID = 6725672099385580694L;

   public static final TypeToken<RestContext<SDCClient, SDCAsyncClient>> CONTEXT_TOKEN = new TypeToken<RestContext<SDCClient, SDCAsyncClient>>() {
      private static final long serialVersionUID = -5070937833892503232L;
   };

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public SDCApiMetadata() {
      this(new Builder());
   }

   protected SDCApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseRestApiMetadata.defaultProperties();
      properties.setProperty(SDCProperties.AUTOGENERATE_KEYS, "true");
      return properties;
   }

   public static class Builder extends BaseRestApiMetadata.Builder {

      protected Builder() {
         super(SDCClient.class, SDCAsyncClient.class);
         id("joyent-sdc")
         .name("Joyent SDC API")
         .identityName("username")
         .credentialName("password")
         .documentation(URI.create("http://sdc.joyent.org/sdcapi.html"))
         .version("~6.5")
         .defaultEndpoint("https://api.joyentcloud.com")
         .defaultProperties(SDCApiMetadata.defaultProperties())
         .view(TypeToken.of(ComputeServiceContext.class))
         .defaultModules(ImmutableSet.<Class<? extends Module>> of(DatacentersAreZonesModule.class, SDCRestClientModule.class, SDCComputeServiceContextModule.class));
      }

      @Override
      public SDCApiMetadata build() {
         return new SDCApiMetadata(this);
      }

      @Override
      public Builder fromApiMetadata(ApiMetadata in) {
         super.fromApiMetadata(in);
         return this;
      }

   }

}
