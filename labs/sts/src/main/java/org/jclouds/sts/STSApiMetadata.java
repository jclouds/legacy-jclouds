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
package org.jclouds.sts;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.Constants.PROPERTY_TIMEOUTS_PREFIX;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_AUTH_TAG;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_HEADER_TAG;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.sts.config.STSRestClientModule;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.BaseRestApiMetadata;

import com.google.common.reflect.TypeToken;

/**
 * Implementation of {@link ApiMetadata} for Amazon's STS api.
 * 
 * @author Adrian Cole
 */
public class STSApiMetadata extends BaseRestApiMetadata {

   public static final TypeToken<RestContext<? extends STSApi, ? extends STSAsyncApi>> CONTEXT_TOKEN = new TypeToken<RestContext<? extends STSApi, ? extends STSAsyncApi>>() {
      private static final long serialVersionUID = 1L;
   };

   @Override
   public Builder toBuilder() {
      return new Builder(getApi(), getAsyncApi()).fromApiMetadata(this);
   }

   public STSApiMetadata() {
      this(new Builder(STSApi.class, STSAsyncApi.class));
   }

   protected STSApiMetadata(Builder builder) {
      super(Builder.class.cast(builder));
   }

   public static Properties defaultProperties() {
      Properties properties = BaseRestApiMetadata.defaultProperties();
      properties.setProperty(PROPERTY_TIMEOUTS_PREFIX + "default", SECONDS.toMillis(30) + "");
      properties.setProperty(PROPERTY_AUTH_TAG, "AWS");
      properties.setProperty(PROPERTY_HEADER_TAG, "amz");
      return properties;
   }

   public static class Builder extends BaseRestApiMetadata.Builder<Builder> {

      protected Builder(Class<?> api, Class<?> asyncApi) {
         super(api, asyncApi);
         id("sts")
         .name("Amazon STS Api")
         .identityName("Access Key ID")
         .credentialName("Secret Access Key")
         .version("2011-06-15")
         .documentation(URI.create("http://docs.amazonwebservices.com/STS/latest/APIReference/"))
         .defaultEndpoint("https://sts.amazonaws.com")
         .defaultProperties(STSApiMetadata.defaultProperties())
         .defaultModule(STSRestClientModule.class);
      }

      @Override
      public STSApiMetadata build() {
         return new STSApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
}
