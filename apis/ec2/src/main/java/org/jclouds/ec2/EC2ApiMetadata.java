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
package org.jclouds.ec2;

import static org.jclouds.aws.reference.AWSConstants.PROPERTY_AUTH_TAG;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_HEADER_TAG;
import static org.jclouds.compute.config.ComputeServiceProperties.RESOURCENAME_DELIMITER;
import static org.jclouds.ec2.reference.EC2Constants.PROPERTY_EC2_AMI_OWNERS;
import static org.jclouds.ec2.reference.EC2Constants.PROPERTY_EC2_AUTO_ALLOCATE_ELASTIC_IPS;
import static org.jclouds.ec2.reference.EC2Constants.PROPERTY_EC2_GENERATE_INSTANCE_NAMES;
import static org.jclouds.ec2.reference.EC2Constants.PROPERTY_EC2_TIMEOUT_SECURITYGROUP_PRESENT;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.ec2.compute.EC2ComputeServiceContext;
import org.jclouds.ec2.compute.config.EC2ComputeServiceContextModule;
import org.jclouds.ec2.compute.config.EC2ResolveImagesModule;
import org.jclouds.ec2.config.EC2HttpApiModule;
import org.jclouds.rest.internal.BaseHttpApiMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

public final class EC2ApiMetadata extends BaseHttpApiMetadata<EC2Api> {

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public EC2ApiMetadata() {
      super(new Builder());
   }

   protected EC2ApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseHttpApiMetadata.defaultProperties();
      properties.setProperty(PROPERTY_AUTH_TAG, "AWS");
      properties.setProperty(PROPERTY_HEADER_TAG, "amz");
      properties.setProperty(PROPERTY_EC2_AMI_OWNERS, "*");
      properties.setProperty(PROPERTY_EC2_TIMEOUT_SECURITYGROUP_PRESENT, "500");
      properties.setProperty(PROPERTY_EC2_AUTO_ALLOCATE_ELASTIC_IPS, "false");
      properties.setProperty(RESOURCENAME_DELIMITER, "#");
      properties.setProperty(PROPERTY_EC2_GENERATE_INSTANCE_NAMES, "true");
      return properties;
   }

   public static final class Builder extends BaseHttpApiMetadata.Builder<EC2Api, Builder> {
      public Builder() {
         id("ec2")
         .name("Amazon Elastic Compute Cloud (EC2) API")
         .identityName("Access Key ID")
         .credentialName("Secret Access Key")
         .defaultEndpoint("https://ec2.us-east-1.amazonaws.com")
         .documentation(URI.create("http://docs.amazonwebservices.com/AWSEC2/latest/APIReference"))
         .version("2010-06-15")
         .defaultProperties(EC2ApiMetadata.defaultProperties())
         .view(EC2ComputeServiceContext.class)
         .defaultModules(ImmutableSet.<Class<? extends Module>>of(EC2HttpApiModule.class, EC2ResolveImagesModule.class, EC2ComputeServiceContextModule.class));
      }

      @Override
      public ApiMetadata build() {
         return new EC2ApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
}
