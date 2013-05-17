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
package org.jclouds.sqs;

import static org.jclouds.aws.reference.AWSConstants.PROPERTY_AUTH_TAG;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_HEADER_TAG;
import static org.jclouds.sqs.config.SQSProperties.CREATE_QUEUE_MAX_RETRIES;
import static org.jclouds.sqs.config.SQSProperties.CREATE_QUEUE_RETRY_INTERVAL;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.rest.internal.BaseRestApiMetadata;
import org.jclouds.sqs.config.SQSRestClientModule;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for Amazon's Simple Queue Service api.
 * 
 * @author Adrian Cole
 */
public class SQSApiMetadata extends BaseRestApiMetadata {
   
   /**
    * @deprecated please use {@code org.jclouds.ContextBuilder#buildApi(SQSApi.class)} as
    *             {@link SQSAsyncApi} interface will be removed in jclouds 1.7.
    */
   @Deprecated
   public static final TypeToken<org.jclouds.rest.RestContext<SQSApi, SQSAsyncApi>> CONTEXT_TOKEN = new TypeToken<org.jclouds.rest.RestContext<SQSApi, SQSAsyncApi>>() {
      private static final long serialVersionUID = 1L;
   };

   @Override
   public Builder toBuilder() {
      return new Builder(getApi(), getAsyncApi()).fromApiMetadata(this);
   }

   @SuppressWarnings("deprecation")
   public SQSApiMetadata() {
      this(new Builder(SQSApi.class, SQSAsyncApi.class));
   }

   protected SQSApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseRestApiMetadata.defaultProperties();
      properties.setProperty(CREATE_QUEUE_MAX_RETRIES, "60");
      properties.setProperty(CREATE_QUEUE_RETRY_INTERVAL, "1000");
      properties.setProperty(PROPERTY_AUTH_TAG, "AWS");
      properties.setProperty(PROPERTY_HEADER_TAG, "amz");
      return properties;
   }
   
   public static class Builder extends BaseRestApiMetadata.Builder<Builder> {

      protected Builder(Class<?> api, Class<?> asyncApi) {
         super(api, asyncApi);
         id("sqs")
         .name("Amazon Simple Queue Service API")
         .identityName("Access Key ID")
         .credentialName("Secret Access Key")
         .version("2011-10-01")
         .defaultProperties(SQSApiMetadata.defaultProperties())
         .defaultEndpoint("https://sqs.us-east-1.amazonaws.com")
         .documentation(URI.create("http://docs.amazonwebservices.com/AWSSimpleQueueService/latest/APIReference"))
         .defaultModules(ImmutableSet.<Class<? extends Module>>of(SQSRestClientModule.class));
      }

      @Override
      public SQSApiMetadata build() {
         return new SQSApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
}
