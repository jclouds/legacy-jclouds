/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo;
import static org.jclouds.Constants.PROPERTY_MAX_REDIRECTS;
import static org.jclouds.abiquo.config.AbiquoProperties.ASYNC_TASK_MONITOR_DELAY;
import static org.jclouds.abiquo.config.AbiquoProperties.CREDENTIAL_IS_TOKEN;
import static org.jclouds.reflect.Reflection2.typeToken;

import java.net.URI;
import java.util.Properties;

import org.jclouds.abiquo.compute.config.AbiquoComputeServiceContextModule;
import org.jclouds.abiquo.config.AbiquoRestClientModule;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.concurrent.config.ScheduledExecutorServiceModule;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.BaseRestApiMetadata;

import com.google.common.base.CharMatcher;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for Abiquo API.
 * 
 * @author Ignasi Barrera
 */
public class AbiquoApiMetadata extends BaseRestApiMetadata {

   /** The token describing the rest api context. */
   public static TypeToken<RestContext<AbiquoApi, AbiquoAsyncApi>> CONTEXT_TOKEN = new TypeToken<RestContext<AbiquoApi, AbiquoAsyncApi>>() {
      private static final long serialVersionUID = -2098594161943130770L;
   };

   public AbiquoApiMetadata() {
      this(new Builder());
   }

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }
   
   protected AbiquoApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseRestApiMetadata.defaultProperties();
      // By default redirects will be handled in the domain objects
      properties.setProperty(PROPERTY_MAX_REDIRECTS, "0");
      // The default polling delay between AsyncTask monitor requests
      properties.setProperty(ASYNC_TASK_MONITOR_DELAY, "5000");
      // By default the provided credential is not a token
      properties.setProperty(CREDENTIAL_IS_TOKEN, "false");
      return properties;
   }

   public static class Builder extends BaseRestApiMetadata.Builder<Builder> {
      private static final String DOCUMENTATION_ROOT = "http://community.abiquo.com/display/ABI"
            + CharMatcher.DIGIT.retainFrom(AbiquoAsyncApi.API_VERSION);

      protected Builder() {
         super(AbiquoApi.class, AbiquoAsyncApi.class);
         id("abiquo")
               .name("Abiquo API")
               .identityName("API Username")
               .credentialName("API Password")
               .documentation(URI.create(DOCUMENTATION_ROOT + "/API+Reference"))
               .defaultEndpoint("http://localhost/api")
               .version(AbiquoAsyncApi.API_VERSION)
               .buildVersion(AbiquoAsyncApi.BUILD_VERSION)
               .view(typeToken(AbiquoContext.class))
               .defaultProperties(AbiquoApiMetadata.defaultProperties())
               .defaultModules(
                     ImmutableSet.<Class<? extends Module>> of(AbiquoRestClientModule.class,
                           AbiquoComputeServiceContextModule.class, ScheduledExecutorServiceModule.class));
      }

      @Override
      public AbiquoApiMetadata build() {
         return new AbiquoApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }

   }

}
