/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.elasticstack;

import static org.jclouds.elasticstack.reference.ElasticStackConstants.PROPERTY_VNC_PASSWORD;
import static org.jclouds.reflect.Reflection2.typeToken;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.elasticstack.compute.config.ElasticStackComputeServiceContextModule;
import org.jclouds.elasticstack.config.ElasticStackHttpApiModule;
import org.jclouds.rest.internal.BaseHttpApiMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for the ElasticStack API
 * 
 * @author Adrian Cole
 */
public class ElasticStackApiMetadata extends BaseHttpApiMetadata<ElasticStackApi> {

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public ElasticStackApiMetadata() {
      this(new Builder());
   }

   protected ElasticStackApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseHttpApiMetadata.defaultProperties();
      properties.setProperty(PROPERTY_VNC_PASSWORD, "IL9vs34d");
      // passwords are set post-boot, so auth failures are possible
      // from a race condition applying the password set script
      properties.setProperty("jclouds.ssh.max-retries", "5");
      properties.setProperty("jclouds.ssh.retry-auth", "true");
      return properties;
   }

   public static class Builder extends BaseHttpApiMetadata.Builder<ElasticStackApi, Builder> {

      @SuppressWarnings("deprecation")
      protected Builder() {
         id("elasticstack")
         .name("ElasticStack API")
         .identityName("UUID")
         .credentialName("Secret API key")
         .documentation(URI.create("http://www.elasticstack.com/cloud-platform/api"))
         .version("1.0")
         .defaultEndpoint("https://api-lon-p.elastichosts.com")
         .defaultProperties(ElasticStackApiMetadata.defaultProperties())
         .view(typeToken(ComputeServiceContext.class))
         .defaultModules(ImmutableSet.<Class<? extends Module>>of(ElasticStackHttpApiModule.class, ElasticStackComputeServiceContextModule.class));
      }

      @Override
      public ElasticStackApiMetadata build() {
         return new ElasticStackApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
}
