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
package org.jclouds.cloudwatch;

import static org.jclouds.aws.reference.AWSConstants.PROPERTY_AUTH_TAG;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_HEADER_TAG;

import java.net.URI;
import java.util.Properties;

import com.google.common.reflect.TypeToken;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.cloudwatch.config.CloudWatchRestClientModule;
import org.jclouds.rest.internal.BaseRestApiMetadata;

/**
 * Implementation of {@link ApiMetadata} for Amazon's CloudWatch api.
 * 
 * @author Adrian Cole
 */
public class CloudWatchApiMetadata extends BaseRestApiMetadata {

   /**
    * @deprecated please use {@code org.jclouds.ContextBuilder#buildApi(CloudWatchApi.class)} as
    *             {@link CloudWatchAsyncApi} interface will be removed in jclouds 1.7.
    */
   @Deprecated
   public static final TypeToken<org.jclouds.rest.RestContext<CloudWatchApi, CloudWatchAsyncApi>> CONTEXT_TOKEN = new TypeToken<org.jclouds.rest.RestContext<CloudWatchApi, CloudWatchAsyncApi>>() {
      private static final long serialVersionUID = 1L;
   };

   @Override
   public Builder toBuilder() {
      return new Builder(getApi(), getAsyncApi()).fromApiMetadata(this);
   }

   @SuppressWarnings("deprecation")
   public CloudWatchApiMetadata() {
      this(new Builder(CloudWatchApi.class, CloudWatchAsyncApi.class));
   }

   protected CloudWatchApiMetadata(Builder builder) {
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
         id("cloudwatch")
         .name("Amazon CloudWatch Api")
         .identityName("Access Key ID")
         .credentialName("Secret Access Key")
         .version("2010-08-01")
         .documentation(URI.create("http://docs.amazonwebservices.com/AmazonCloudWatch/latest/APIReference/"))
         .defaultEndpoint("https://monitoring.us-east-1.amazonaws.com")
         .defaultProperties(CloudWatchApiMetadata.defaultProperties())
         .defaultModule(CloudWatchRestClientModule.class);
      }

      @Override
      public CloudWatchApiMetadata build() {
         return new CloudWatchApiMetadata(this);
      }
      
      @Override
      protected Builder self() {
         return this;
      }
   }
}
