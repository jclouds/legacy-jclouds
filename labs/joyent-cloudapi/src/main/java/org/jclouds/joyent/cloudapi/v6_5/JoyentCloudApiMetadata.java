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
package org.jclouds.joyent.cloudapi.v6_5;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.joyent.cloudapi.v6_5.compute.config.JoyentCloudComputeServiceContextModule;
import org.jclouds.joyent.cloudapi.v6_5.config.DatacentersAreZonesModule;
import org.jclouds.joyent.cloudapi.v6_5.config.JoyentCloudProperties;
import org.jclouds.joyent.cloudapi.v6_5.config.JoyentCloudRestClientModule;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.BaseRestApiMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for JoyentCloud ~6.5 API
 * 
 * @author Adrian Cole
 */
public class JoyentCloudApiMetadata extends BaseRestApiMetadata {

   public static final TypeToken<RestContext<JoyentCloudApi, JoyentCloudAsyncApi>> CONTEXT_TOKEN = new TypeToken<RestContext<JoyentCloudApi, JoyentCloudAsyncApi>>() {
   };

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public JoyentCloudApiMetadata() {
      this(new Builder());
   }

   protected JoyentCloudApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseRestApiMetadata.defaultProperties();
      // auth fail sometimes happens, as the rc.local script that injects the
      // authorized key executes after ssh has started.  
      properties.setProperty("jclouds.ssh.max-retries", "7");
      properties.setProperty("jclouds.ssh.retry-auth", "true");
      properties.setProperty(JoyentCloudProperties.AUTOGENERATE_KEYS, "true");
      return properties;
   }

   public static class Builder extends BaseRestApiMetadata.Builder {

      protected Builder() {
         super(JoyentCloudApi.class, JoyentCloudAsyncApi.class);
         id("joyent-cloudapi")
         .name("Joyent Cloud API")
         .identityName("username")
         .credentialName("password")
         .documentation(URI.create("http://cloudApi.joyent.org/cloudApiapi.html"))
         .version("~6.5")
         .defaultEndpoint("https://api.joyentcloud.com")
         .defaultProperties(JoyentCloudApiMetadata.defaultProperties())
         .view(TypeToken.of(ComputeServiceContext.class))
         .defaultModules(ImmutableSet.<Class<? extends Module>> of(DatacentersAreZonesModule.class, JoyentCloudRestClientModule.class, JoyentCloudComputeServiceContextModule.class));
      }

      @Override
      public JoyentCloudApiMetadata build() {
         return new JoyentCloudApiMetadata(this);
      }

      @Override
      public Builder fromApiMetadata(ApiMetadata in) {
         super.fromApiMetadata(in);
         return this;
      }

   }

}
