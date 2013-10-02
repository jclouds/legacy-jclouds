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
import org.jclouds.cloudwatch.config.CloudWatchHttpApiModule;
import org.jclouds.rest.internal.BaseHttpApiMetadata;

/**
 * Implementation of {@link ApiMetadata} for Amazon's CloudWatch api.
 * 
 * @author Adrian Cole
 */
public class CloudWatchApiMetadata extends BaseHttpApiMetadata {


   @Override
   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromApiMetadata(this);
   }

   public CloudWatchApiMetadata() {
      this(new ConcreteBuilder());
   }

   protected CloudWatchApiMetadata(Builder<?> builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseHttpApiMetadata.defaultProperties();
      properties.setProperty(PROPERTY_AUTH_TAG, "AWS");
      properties.setProperty(PROPERTY_HEADER_TAG, "amz");
      return properties;
   }

   public abstract static class Builder<T extends Builder<T>> extends BaseHttpApiMetadata.Builder<CloudWatchApi, T> {

      protected Builder() {
         id("cloudwatch")
         .name("Amazon CloudWatch Api")
         .identityName("Access Key ID")
         .credentialName("Secret Access Key")
         .version("2010-08-01")
         .documentation(URI.create("http://docs.amazonwebservices.com/AmazonCloudWatch/latest/APIReference/"))
         .defaultEndpoint("https://monitoring.us-east-1.amazonaws.com")
         .defaultProperties(CloudWatchApiMetadata.defaultProperties())
         .defaultModule(CloudWatchHttpApiModule.class);
      }

      @Override
      public CloudWatchApiMetadata build() {
         return new CloudWatchApiMetadata(this);
      }
      
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }
}
