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
package org.jclouds.iam;

import static org.jclouds.aws.reference.AWSConstants.PROPERTY_AUTH_TAG;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_HEADER_TAG;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.iam.config.IAMRestClientModule;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.BaseRestApiMetadata;

import com.google.common.reflect.TypeToken;

/**
 * Implementation of {@link ApiMetadata} for Amazon's IAM api.
 * 
 * @author Adrian Cole
 */
public class IAMApiMetadata extends BaseRestApiMetadata {

   public static final TypeToken<RestContext<? extends IAMApi, ? extends IAMAsyncApi>> CONTEXT_TOKEN = new TypeToken<RestContext<? extends IAMApi, ? extends IAMAsyncApi>>() {
      private static final long serialVersionUID = 1L;
   };

   @Override
   public Builder toBuilder() {
      return new Builder(getApi(), getAsyncApi()).fromApiMetadata(this);
   }

   public IAMApiMetadata() {
      this(new Builder(IAMApi.class, IAMAsyncApi.class));
   }

   protected IAMApiMetadata(Builder builder) {
      super(Builder.class.cast(builder));
   }

   public static Properties defaultProperties() {
      Properties properties = BaseRestApiMetadata.defaultProperties();
      properties.setProperty(PROPERTY_AUTH_TAG, "AWS");
      properties.setProperty(PROPERTY_HEADER_TAG, "amz");
      return properties;
   }

   public static class Builder extends BaseRestApiMetadata.Builder<Builder> {

      protected Builder(Class<?> api, Class<?> asyncApi) {
         super(api, asyncApi);
         id("iam")
         .name("Amazon IAM Api")
         .identityName("Access Key ID")
         .credentialName("Secret Access Key")
         .version("2010-05-08")
         .documentation(URI.create("http://docs.amazonwebservices.com/IAM/latest/APIReference/"))
         .defaultEndpoint("https://iam.amazonaws.com")
         .defaultProperties(IAMApiMetadata.defaultProperties())
         .defaultModule(IAMRestClientModule.class);
      }

      @Override
      public IAMApiMetadata build() {
         return new IAMApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
}
