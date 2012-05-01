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
package org.jclouds.aws.ec2;

import static org.jclouds.aws.ec2.reference.AWSEC2Constants.PROPERTY_EC2_GENERATE_INSTANCE_NAMES;
import static org.jclouds.ec2.reference.EC2Constants.PROPERTY_EC2_AMI_OWNERS;

import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.aws.ec2.compute.AWSEC2ComputeServiceContext;
import org.jclouds.aws.ec2.compute.config.AWSEC2ComputeServiceContextModule;
import org.jclouds.aws.ec2.config.AWSEC2RestClientModule;
import org.jclouds.ec2.EC2ApiMetadata;
import org.jclouds.ec2.compute.config.EC2ResolveImagesModule;
import org.jclouds.rest.RestContext;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for the Amazon-specific EC2 API
 * 
 * @author Adrian Cole
 */
public class AWSEC2ApiMetadata extends EC2ApiMetadata {

   /** The serialVersionUID */
   private static final long serialVersionUID = -1492951757032303845L;
   
   public static final TypeToken<RestContext<AWSEC2Client, AWSEC2AsyncClient>> CONTEXT_TOKEN = new TypeToken<RestContext<AWSEC2Client, AWSEC2AsyncClient>>() {
      private static final long serialVersionUID = -5070937833892503232L;
   };
   
   private static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromApiMetadata(this);
   }

   public AWSEC2ApiMetadata() {
      this(builder());
   }

   protected AWSEC2ApiMetadata(Builder builder) {
      super(builder);
   }
   
   public static Properties defaultProperties() {
      Properties properties = EC2ApiMetadata.defaultProperties();
      properties.remove(PROPERTY_EC2_AMI_OWNERS);
      // auth fail sometimes happens in EC2, as the rc.local script that injects the
      // authorized key executes after ssh has started.  
      properties.setProperty("jclouds.ssh.max-retries", "7");
      properties.setProperty("jclouds.ssh.retry-auth", "true");
      properties.setProperty(PROPERTY_EC2_GENERATE_INSTANCE_NAMES, "true");
      return properties;
   }

   public static class Builder extends EC2ApiMetadata.Builder {
      protected Builder(){
         super(AWSEC2Client.class, AWSEC2AsyncClient.class);
         id("aws-ec2")
         .version(AWSEC2AsyncClient.VERSION)
         .name("Amazon-specific EC2 API")
         .view(AWSEC2ComputeServiceContext.class)
         .context(CONTEXT_TOKEN)
         .defaultProperties(AWSEC2ApiMetadata.defaultProperties())
         .defaultModules(ImmutableSet.<Class<? extends Module>>of(AWSEC2RestClientModule.class, EC2ResolveImagesModule.class, AWSEC2ComputeServiceContextModule.class));
      }
      
      @Override
      public AWSEC2ApiMetadata build() {
         return new AWSEC2ApiMetadata(this);
      }

      @Override
      public Builder fromApiMetadata(ApiMetadata in) {
         super.fromApiMetadata(in);
         return this;
      }
   }

}