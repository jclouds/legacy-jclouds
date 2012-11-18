#set( $lcaseProviderName = ${providerName.toLowerCase()} )
#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
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
package ${package};

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import ${package}.config.${providerName}RestClientModule;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.BaseRestApiMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for ${providerName} ${providerApiVersion} API
 * 
 * @author ${author}
 */
public class ${providerName}ApiMetadata extends BaseRestApiMetadata {

   public static final TypeToken<RestContext<${providerName}Api, ${providerName}AsyncApi>> CONTEXT_TOKEN = new TypeToken<RestContext<${providerName}Api, ${providerName}AsyncApi>>() {
   };

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public ${providerName}ApiMetadata() {
      this(new Builder());
   }

   protected ${providerName}ApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseRestApiMetadata.defaultProperties();
      // TODO: add any custom properties here
      return properties;
   }

   public static class Builder extends BaseRestApiMetadata.Builder {

      protected Builder() {
         super(${providerName}Api.class, ${providerName}AsyncApi.class);
         id("${lcaseProviderName}")
         .name("${providerName} API")
         .identityName("${providerIdentity}")
         .credentialName("${providerCredential}")
         .documentation(URI.create("TODO"))
         .version("${providerApiVersion}")
         .defaultEndpoint("${providerEndpoint}")
         .defaultProperties(${providerName}ApiMetadata.defaultProperties())
         .defaultModules(ImmutableSet.<Class<? extends Module>> of(${providerName}RestClientModule.class));
      }

      @Override
      public ${providerName}ApiMetadata build() {
         return new ${providerName}ApiMetadata(this);
      }

      @Override
      public Builder fromApiMetadata(ApiMetadata in) {
         super.fromApiMetadata(in);
         return this;
      }

   }

}
