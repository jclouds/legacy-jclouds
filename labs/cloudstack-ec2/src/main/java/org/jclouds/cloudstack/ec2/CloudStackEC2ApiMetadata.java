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
package org.jclouds.cloudstack.ec2;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.cloudstack.ec2.config.CloudStackEC2RestClientModule;
import org.jclouds.ec2.EC2ApiMetadata;
import org.jclouds.ec2.compute.config.EC2ComputeServiceContextModule;
import org.jclouds.ec2.compute.config.EC2ResolveImagesModule;
import org.jclouds.rest.RestContext;

import java.net.URI;
import java.util.Properties;

import static org.jclouds.Constants.PROPERTY_CONNECTION_TIMEOUT;
import static org.jclouds.Constants.PROPERTY_SO_TIMEOUT;
import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * Implementation of {@link ApiMetadata} for the CloudStack's EC2-clone API
 * 
 * @author Adrian Cole
 */
public class CloudStackEC2ApiMetadata extends EC2ApiMetadata {

   public static final TypeToken<RestContext<CloudStackEC2Client, CloudStackEC2AsyncClient>> CONTEXT_TOKEN = new TypeToken<RestContext<CloudStackEC2Client, CloudStackEC2AsyncClient>>() {
   };

   private static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromApiMetadata(this);
   }

   public CloudStackEC2ApiMetadata() {
      this(builder());
   }

   protected CloudStackEC2ApiMetadata(Builder builder) {
      super(builder);
   }
   
   public static Properties defaultProperties() {
      Properties properties = EC2ApiMetadata.defaultProperties();
       properties.setProperty(PROPERTY_CONNECTION_TIMEOUT, MINUTES.toMillis(10) + "");
       properties.setProperty(PROPERTY_SO_TIMEOUT, MINUTES.toMillis(10) + "");

      return properties;
   }

   public static class Builder extends EC2ApiMetadata.Builder {
      protected Builder(){
         super(CloudStackEC2Client.class, CloudStackEC2AsyncClient.class);
         id("cloudstack-ec2")
         .name("CloudBridge (EC2 clone) API")
         .version("2012-08-15")
         .defaultEndpoint("http://localhost:7080/awsapi")
         .documentation(URI.create("http://docs.cloudstack.org/CloudBridge_Documentation"))
         .defaultProperties(CloudStackEC2ApiMetadata.defaultProperties())
         .context(CONTEXT_TOKEN)
         .defaultModules(ImmutableSet.<Class<? extends Module>>builder()
                                     .add(CloudStackEC2RestClientModule.class)
                                     .add(EC2ResolveImagesModule.class)
                                     .add(EC2ComputeServiceContextModule.class).build());
      }
      
      @Override
      public CloudStackEC2ApiMetadata build() {
         return new CloudStackEC2ApiMetadata(this);
      }

      @Override
      public Builder fromApiMetadata(ApiMetadata in) {
         super.fromApiMetadata(in);
         return this;
      }
   }

}
