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
package org.jclouds.deltacloud;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.deltacloud.compute.config.DeltacloudComputeServiceContextModule;
import org.jclouds.deltacloud.config.DeltacloudRestClientModule;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.BaseRestApiMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for Apache Deltacloud API
 * 
 * @author Adrian Cole
 */
public class DeltacloudApiMetadata extends BaseRestApiMetadata {
   
   /** The serialVersionUID */
   private static final long serialVersionUID = 6725672099385580694L;

   public static final TypeToken<RestContext<DeltacloudClient, DeltacloudAsyncClient>> CONTEXT_TOKEN = new TypeToken<RestContext<DeltacloudClient, DeltacloudAsyncClient>>() {
      private static final long serialVersionUID = -5070937833892503232L;
   };

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public DeltacloudApiMetadata() {
      this(new Builder());
   }

   protected DeltacloudApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      return BaseRestApiMetadata.defaultProperties();
   }

   public static class Builder extends BaseRestApiMetadata.Builder {

      protected Builder() {
         super(DeltacloudClient.class, DeltacloudAsyncClient.class);
            id("deltacloud")
            .name("Apache Deltacloud API")
            .identityName("Username")
            .credentialName("Password")
            .documentation(URI.create("http://deltacloud.apache.org/api.html"))
            .version("0.3.0")
            .defaultEndpoint("http://localhost:3001/api")
            .defaultProperties(DeltacloudApiMetadata.defaultProperties())
            .view(TypeToken.of(ComputeServiceContext.class))
            .defaultModules(ImmutableSet.<Class<? extends Module>>of(DeltacloudRestClientModule.class, DeltacloudComputeServiceContextModule.class));
      }

      @Override
      public DeltacloudApiMetadata build() {
         return new DeltacloudApiMetadata(this);
      }

      @Override
      public Builder fromApiMetadata(ApiMetadata in) {
         super.fromApiMetadata(in);
         return this;
      }

   }

}
