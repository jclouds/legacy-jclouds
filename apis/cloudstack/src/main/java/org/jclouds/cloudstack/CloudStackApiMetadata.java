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
package org.jclouds.cloudstack;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.cloudstack.compute.config.CloudStackComputeServiceContextModule;
import org.jclouds.cloudstack.config.CloudStackParserModule;
import org.jclouds.cloudstack.config.CloudStackRestClientModule;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.BaseRestApiMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for Citrix/Apache CloudStack api.
 * 
 * <h3>note</h3>
 * <p/>
 * This class allows overriding of types {@code S}(client) and {@code A}
 * (asyncClient), so that children can add additional methods not declared here,
 * such as new features from AWS.
 * <p/>
 * 
 * As this is a popular api, we also allow overrides for type {@code C}
 * (context). This allows subtypes to add in new feature groups or extensions,
 * not present in the base api. For example, you could make a subtype for
 * context, that exposes admin operations.
 * 
 * @author Adrian Cole
 */
public class CloudStackApiMetadata extends BaseRestApiMetadata {
   
   public static final TypeToken<RestContext<CloudStackClient, CloudStackAsyncClient>> CONTEXT_TOKEN = new TypeToken<RestContext<CloudStackClient, CloudStackAsyncClient>>() {
   };
   
   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public CloudStackApiMetadata() {
      this(new Builder());
   }

   protected CloudStackApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseRestApiMetadata.defaultProperties();
      properties.setProperty("jclouds.ssh.max-retries", "7");
      properties.setProperty("jclouds.ssh.retry-auth", "true");
      return properties;
   }

   public static class Builder
         extends BaseRestApiMetadata.Builder {

      protected Builder() {
         super(CloudStackClient.class, CloudStackAsyncClient.class);
         id("cloudstack")
         .name("Citrix CloudStack API")
         .identityName("API Key")
         .credentialName("Secret Key")
         .documentation(URI.create("http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_User.html"))
         .defaultEndpoint("http://localhost:8080/client/api")
         .version("2.2")
         .view(TypeToken.of(CloudStackContext.class))
         .defaultProperties(CloudStackApiMetadata.defaultProperties())
         .defaultModules(ImmutableSet.<Class<? extends Module>> builder()
                                     .add(CloudStackParserModule.class)
                                     .add(CloudStackRestClientModule.class)
                                     .add(CloudStackComputeServiceContextModule.class).build());
      }
      
      @Override
      public CloudStackApiMetadata build() {
         return new CloudStackApiMetadata(this);
      }
      
      @Override
      public Builder fromApiMetadata(ApiMetadata in) {
         super.fromApiMetadata(in);
         return this;
      }

   }

}
