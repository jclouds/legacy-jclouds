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
package org.jclouds.cloudsigma;

import static org.jclouds.cloudsigma.reference.CloudSigmaConstants.PROPERTY_VNC_PASSWORD;
import static org.jclouds.compute.config.ComputeServiceProperties.TEMPLATE;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.cloudsigma.compute.config.CloudSigmaComputeServiceContextModule;
import org.jclouds.cloudsigma.config.CloudSigmaRestClientModule;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.BaseRestApiMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for the Cloud Sigma API
 * 
 * @author Adrian Cole
 */
public class CloudSigmaApiMetadata extends BaseRestApiMetadata {
   /** The serialVersionUID */
   private static final long serialVersionUID = 6725672099385580694L;

   public static final TypeToken<RestContext<CloudSigmaClient, CloudSigmaAsyncClient>> CONTEXT_TOKEN = new TypeToken<RestContext<CloudSigmaClient, CloudSigmaAsyncClient>>() {
      private static final long serialVersionUID = -5070937833892503232L;
   };
   
   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public CloudSigmaApiMetadata() {
      this(new Builder());
   }

   protected CloudSigmaApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseRestApiMetadata.defaultProperties();
      properties.setProperty(PROPERTY_VNC_PASSWORD, "IL9vs34d");
      // passwords are set post-boot, so auth failures are possible
      // from a race condition applying the password set script
      properties.setProperty("jclouds.ssh.max-retries", "7");
      properties.setProperty("jclouds.ssh.retry-auth", "true");
      properties.setProperty(TEMPLATE, "osFamily=UBUNTU,imageNameMatches=.*[Aa]utomated SSH Access.*,os64Bit=true");
      return properties;
   }

   public static class Builder extends BaseRestApiMetadata.Builder {

      protected Builder() {
         super(CloudSigmaClient.class, CloudSigmaAsyncClient.class);
         id("cloudsigma")
         .name("CloudSigma API")
         .identityName("Email")
         .credentialName("Password")
         .documentation(URI.create("http://cloudsigma.com/en/platform-details/the-api"))
         .version("1.0")
         .defaultEndpoint("https://api.cloudsigma.com")
         .defaultProperties(CloudSigmaApiMetadata.defaultProperties())
         .view(TypeToken.of(ComputeServiceContext.class))
         .defaultModules(ImmutableSet.<Class<? extends Module>>of(CloudSigmaRestClientModule.class, CloudSigmaComputeServiceContextModule.class));
      }

      @Override
      public CloudSigmaApiMetadata build() {
         return new CloudSigmaApiMetadata(this);
      }

      @Override
      public Builder fromApiMetadata(ApiMetadata in) {
         super.fromApiMetadata(in);
         return this;
      }

   }

}
