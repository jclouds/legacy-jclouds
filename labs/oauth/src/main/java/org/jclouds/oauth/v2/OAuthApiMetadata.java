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
package org.jclouds.oauth.v2;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.oauth.v2.config.OAuthRestClientModule;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.BaseRestApiMetadata;

import java.net.URI;
import java.util.Properties;

import static org.jclouds.oauth.v2.OAuthConstants.PKCS_CERITIFICATE_KEY_PASSWORD;
import static org.jclouds.oauth.v2.OAuthConstants.PKCS_CERTIFICATE_KEY_NAME;
import static org.jclouds.oauth.v2.OAuthConstants.SIGNATURE_ALGORITHM;
import static org.jclouds.oauth.v2.OAuthConstants.SIGNATURE_KEY_FORMAT;

/**
 * Implementation of {@link ApiMetadata} for OAuth 2 API
 *
 * @author David Alves
 */
public class OAuthApiMetadata extends BaseRestApiMetadata {

   public static final TypeToken<RestContext<OAuthClient, OAuthAsyncClient>> CONTEXT_TOKEN = new
           TypeToken<RestContext<OAuthClient, OAuthAsyncClient>>() {};

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public OAuthApiMetadata() {
      this(new Builder());
   }

   protected OAuthApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseRestApiMetadata.defaultProperties();
      properties.put(SIGNATURE_ALGORITHM, "RS256");
      properties.put(SIGNATURE_KEY_FORMAT, "PKCS12");
      properties.put(PKCS_CERTIFICATE_KEY_NAME, "privatekey");
      properties.put(PKCS_CERITIFICATE_KEY_PASSWORD, "notasecret");
      return properties;
   }

   public static class Builder extends BaseRestApiMetadata.Builder {

      protected Builder() {
         super(OAuthClient.class, OAuthAsyncClient.class);
         id("oauth").name("OAuth API")
                 .identityName("service_account")
                 .credentialName("service_key")
                 .documentation(URI.create("TODO"))
                 .version("2")
                 .defaultProperties(OAuthApiMetadata.defaultProperties())
                 .defaultModules(ImmutableSet.<Class<? extends Module>>of(OAuthRestClientModule.class));
      }

      @Override
      public OAuthApiMetadata build() {
         return new OAuthApiMetadata(this);
      }

      @Override
      public Builder fromApiMetadata(ApiMetadata in) {
         super.fromApiMetadata(in);
         return this;
      }

   }

}
