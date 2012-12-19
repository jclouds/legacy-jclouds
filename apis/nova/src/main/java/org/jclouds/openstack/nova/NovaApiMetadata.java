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
package org.jclouds.openstack.nova;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.openstack.nova.compute.config.NovaComputeServiceContextModule;
import org.jclouds.openstack.nova.config.NovaRestClientModule;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.BaseRestApiMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for Nova 1.0 API
 * 
 * @author Adrian Cole
 * 
 * @deprecated Deprecated in jclouds 1.6, to be removed in jclouds 1.7. See {@link org.jclouds.openstack.nova.v2_0.NovaApiMetadata} in openstack-nova.
 */
@Deprecated
public class NovaApiMetadata extends BaseRestApiMetadata {

   @Deprecated
   public static final TypeToken<RestContext<NovaClient, NovaAsyncClient>> CONTEXT_TOKEN = new TypeToken<RestContext<NovaClient, NovaAsyncClient>>() {
   };

   @Override
   @Deprecated
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   @Deprecated
   public NovaApiMetadata() {
      this(new Builder());
   }

   @Deprecated
   protected NovaApiMetadata(Builder builder) {
      super(builder);
   }

   @Deprecated
   public static Properties defaultProperties() {
      Properties properties = BaseRestApiMetadata.defaultProperties();
      return properties;
   }

   @Deprecated
   public static class Builder extends BaseRestApiMetadata.Builder {

      protected Builder() {
         super(NovaClient.class, NovaAsyncClient.class);
         id("nova")
         .name("OpenStack Nova Pre-Diablo API")
         .identityName("accessKey")
         .credentialName("secretKey")
         .documentation(URI.create("http://api.openstack.org/"))
         .version("1.1")
         .defaultEndpoint("http://localhost:5000")
         .defaultProperties(NovaApiMetadata.defaultProperties())
         .view(TypeToken.of(ComputeServiceContext.class))
         .defaultModules(ImmutableSet.<Class<? extends Module>>of(NovaRestClientModule.class, NovaComputeServiceContextModule.class));
      }

      @Override
      public NovaApiMetadata build() {
         return new NovaApiMetadata(this);
      }

      @Override
      public Builder fromApiMetadata(ApiMetadata in) {
         super.fromApiMetadata(in);
         return this;
      }

   }

}
