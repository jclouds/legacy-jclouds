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
package org.jclouds.googlecompute;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.googlecompute.config.GoogleComputeRestClientModule;
import org.jclouds.oauth.v2.config.OAuthAuthenticationModule;
import org.jclouds.oauth.v2.config.OAuthModule;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.BaseRestApiMetadata;

import java.net.URI;
import java.util.Properties;

import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.oauth.v2.config.OAuthProperties.AUDIENCE;
import static org.jclouds.oauth.v2.config.OAuthProperties.SIGNATURE_OR_MAC_ALGORITHM;

/**
 * Implementation of {@link ApiMetadata} for GoogleCompute v1beta13 API
 *
 * @author David Alves
 */
public class GoogleComputeApiMetadata extends BaseRestApiMetadata {

   public static final TypeToken<RestContext<GoogleComputeApi, GoogleComputeAsyncApi>> CONTEXT_TOKEN = new
           TypeToken<RestContext<GoogleComputeApi, GoogleComputeAsyncApi>>() {};

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public GoogleComputeApiMetadata() {
      this(new Builder());
   }

   protected GoogleComputeApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseRestApiMetadata.defaultProperties();
      properties.put("oauth.endpoint", "https://accounts.google.com/o/oauth2/token");
      properties.put(AUDIENCE, "https://accounts.google.com/o/oauth2/token");
      properties.put(SIGNATURE_OR_MAC_ALGORITHM, "RS256");
      properties.put(PROPERTY_SESSION_INTERVAL, 3600);
      return properties;
   }

   public static class Builder extends BaseRestApiMetadata.Builder {

      protected Builder() {
         super(GoogleComputeApi.class, GoogleComputeAsyncApi.class);
         id("google-compute")
                 .name("Google Compute Engine Api")
                 .identityName("Email associated with the Goole API client_id")
                 .credentialName("Private key (PKCS12 file) associated with the Google API client_id")
                 .documentation(URI.create("https://developers.google.com/compute/docs"))
                 .version("v1beta13")
                 .defaultEndpoint("https://www.googleapis.com/compute/v1beta13")
                 .defaultProperties(GoogleComputeApiMetadata.defaultProperties())
                 .defaultModules(ImmutableSet.<Class<? extends Module>>of(GoogleComputeRestClientModule.class,
                         OAuthAuthenticationModule.class, OAuthModule.class));
      }

      @Override
      public GoogleComputeApiMetadata build() {
         return new GoogleComputeApiMetadata(this);
      }

      @Override
      public Builder fromApiMetadata(ApiMetadata in) {
         super.fromApiMetadata(in);
         return this;
      }

   }

}
