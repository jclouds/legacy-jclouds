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
package org.jclouds.fujitsu.fgcp;

import java.net.URI;
import java.util.Properties;

import org.jclouds.Constants;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.fujitsu.fgcp.compute.FGCPRestClientModule;
import org.jclouds.fujitsu.fgcp.compute.config.FGCPComputeServiceContextModule;
import org.jclouds.rest.internal.BaseRestApiMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for Fujitsu's Global Cloud Platform
 * (FGCP, FGCP/S5) provider in Australia.
 * 
 * @author Dies Koper
 */
public class FGCPApiMetadata extends BaseRestApiMetadata {

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public FGCPApiMetadata() {
      this(new Builder());
   }

   protected FGCPApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseRestApiMetadata.defaultProperties();
      // enables peer verification using the CAs bundled with the JRE (or
      // value of javax.net.ssl.trustStore if set)
      properties.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "false");
      // properties.setProperty("jclouds.ssh.max-retries", "5");
      // properties.setProperty("jclouds.ssh.retry-auth", "true");
      return properties;
   }

   public static class Builder extends BaseRestApiMetadata.Builder {

      protected Builder() {
         super(FGCPApi.class, FGCPAsyncApi.class);
         id("fgcp")
               .name("Fujitsu Global Cloud Platform (FGCP)")
               .identityName("User certificate (PEM file)")
               .credentialName("User certificate password")
               .documentation(
                     URI.create("https://globalcloud.fujitsu.com.au/portala/ctrl/aboutSopManual"))
               .version(FGCPAsyncApi.VERSION)
               .defaultEndpoint(
                     "https://api.globalcloud.fujitsu.com.au/ovissapi/endpoint")
               .defaultProperties(FGCPApiMetadata.defaultProperties())
               .view(TypeToken.of(ComputeServiceContext.class))
               .defaultModules(
                     ImmutableSet.<Class<? extends Module>> of(
                           FGCPComputeServiceContextModule.class,
                           FGCPRestClientModule.class));
      }

      @Override
      public FGCPApiMetadata build() {
         return new FGCPApiMetadata(this);
      }

      @Override
      public Builder fromApiMetadata(ApiMetadata in) {
         super.fromApiMetadata(in);
         return this;
      }

   }
}
