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
package org.jclouds.aws.ec2;

import static org.jclouds.ec2.reference.EC2Constants.PROPERTY_EC2_AMI_OWNERS;

import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.aws.ec2.compute.AWSEC2ComputeServiceContext;
import org.jclouds.aws.ec2.compute.config.AWSEC2ComputeServiceContextModule;
import org.jclouds.aws.ec2.config.AWSEC2RestClientModule;
import org.jclouds.ec2.EC2ApiMetadata;
import org.jclouds.ec2.compute.config.EC2ResolveImagesModule;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for the Amazon-specific EC2 API
 * 
 * @author Adrian Cole
 */
public class AWSEC2ApiMetadata extends EC2ApiMetadata {

   /**
    * @deprecated please use {@code org.jclouds.ContextBuilder#buildApi(AWSEC2Client.class)} as
    *             {@link AWSEC2AsyncClient} interface will be removed in jclouds 1.7.
    */
   @Deprecated
   public static final TypeToken<org.jclouds.rest.RestContext<AWSEC2Client, AWSEC2AsyncClient>> CONTEXT_TOKEN = new TypeToken<org.jclouds.rest.RestContext<AWSEC2Client, AWSEC2AsyncClient>>() {
      private static final long serialVersionUID = 1L;
   };

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public AWSEC2ApiMetadata() {
      this(new Builder());
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
      return properties;
   }

   public static class Builder extends EC2ApiMetadata.Builder<Builder> {
      @SuppressWarnings("deprecation")
      protected Builder(){
         super(AWSEC2Client.class, AWSEC2AsyncClient.class);
         id("aws-ec2")
         .version("2012-06-01")
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
      protected Builder self() {
         return this;
      }
   }
}
