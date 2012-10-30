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
package org.jclouds.slicehost;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.BaseRestApiMetadata;
import org.jclouds.slicehost.compute.config.SlicehostComputeServiceContextModule;
import org.jclouds.slicehost.config.SlicehostRestClientModule;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for Slicehost 1.0 API
 * 
 * @author Adrian Cole
 */
public class SlicehostApiMetadata extends BaseRestApiMetadata {
   /** The serialVersionUID */
   private static final long serialVersionUID = 6725672099385580694L;

   public static final TypeToken<RestContext<SlicehostClient, SlicehostAsyncClient>> CONTEXT_TOKEN = new TypeToken<RestContext<SlicehostClient, SlicehostAsyncClient>>() {
      private static final long serialVersionUID = -5070937833892503232L;
   };
   
   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public SlicehostApiMetadata() {
      this(new Builder());
   }

   protected SlicehostApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseRestApiMetadata.defaultProperties();
      properties.setProperty("jclouds.ssh.max-retries", "8");
      return properties;
   }

   public static class Builder
         extends
         BaseRestApiMetadata.Builder {

      protected Builder() {
         super(SlicehostClient.class, SlicehostAsyncClient.class);
         id("slicehost")
         .name("Slicehost API")
         .identityName("API password")
         .documentation(URI.create("http://articles.slicehost.com/api"))
         .version("https://api.slicehost.com")
         .defaultEndpoint("https://api.slicehost.com")
         .defaultProperties(SlicehostApiMetadata.defaultProperties())
         .view(TypeToken.of(ComputeServiceContext.class))
         .defaultModules(ImmutableSet.<Class<? extends Module>>of(SlicehostRestClientModule.class, SlicehostComputeServiceContextModule.class));
      }

      @Override
      public SlicehostApiMetadata build() {
         return new SlicehostApiMetadata(this);
      }

      @Override
      public Builder fromApiMetadata(ApiMetadata in) {
         super.fromApiMetadata(in);
         return this;
      }

   }

}
