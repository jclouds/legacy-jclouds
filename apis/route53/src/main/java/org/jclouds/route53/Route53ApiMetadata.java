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
package org.jclouds.route53;

import static org.jclouds.aws.reference.AWSConstants.PROPERTY_AUTH_TAG;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_HEADER_TAG;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.rest.internal.BaseHttpApiMetadata;
import org.jclouds.route53.config.Route53HttpApiModule;

/**
 * Implementation of {@link ApiMetadata} for Amazon's Route53 api.
 * 
 * @author Adrian Cole
 */
public class Route53ApiMetadata extends BaseHttpApiMetadata<Route53Api> {

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public Route53ApiMetadata() {
      this(new Builder());
   }

   protected Route53ApiMetadata(Builder builder) {
      super(Builder.class.cast(builder));
   }

   public static Properties defaultProperties() {
      Properties properties = BaseHttpApiMetadata.defaultProperties();
      properties.setProperty(PROPERTY_AUTH_TAG, "AWS");
      properties.setProperty(PROPERTY_HEADER_TAG, "amz");
      return properties;
   }

   public static class Builder extends BaseHttpApiMetadata.Builder<Route53Api, Builder> {

      protected Builder() {
         id("route53")
         .name("Amazon Route 53 Api")
         .identityName("Access Key ID")
         .credentialName("Secret Access Key")
         .version("2012-02-29")
         .documentation(URI.create("http://docs.aws.amazon.com/Route53/latest/APIReference/"))
         .defaultEndpoint("https://route53.amazonaws.com")
         .defaultProperties(Route53ApiMetadata.defaultProperties())
         .defaultModule(Route53HttpApiModule.class);
      }

      @Override
      public Route53ApiMetadata build() {
         return new Route53ApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
}
