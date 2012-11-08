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
package org.jclouds.cloudstack.ec2.config;

import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.cloudstack.ec2.CloudStackEC2AsyncClient;
import org.jclouds.cloudstack.ec2.CloudStackEC2Client;
import org.jclouds.cloudstack.ec2.services.CloudStackAMIAsyncClient;
import org.jclouds.cloudstack.ec2.services.CloudStackAMIClient;
import org.jclouds.ec2.EC2AsyncClient;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.config.EC2RestClientModule;
import org.jclouds.ec2.features.WindowsApi;
import org.jclouds.ec2.features.WindowsAsyncApi;
import org.jclouds.ec2.services.AvailabilityZoneAndRegionAsyncClient;
import org.jclouds.ec2.services.AvailabilityZoneAndRegionClient;
import org.jclouds.ec2.services.ElasticBlockStoreAsyncClient;
import org.jclouds.ec2.services.ElasticBlockStoreClient;
import org.jclouds.ec2.services.ElasticIPAddressAsyncClient;
import org.jclouds.ec2.services.ElasticIPAddressClient;
import org.jclouds.ec2.services.InstanceAsyncClient;
import org.jclouds.ec2.services.InstanceClient;
import org.jclouds.ec2.services.KeyPairAsyncClient;
import org.jclouds.ec2.services.KeyPairClient;
import org.jclouds.ec2.services.SecurityGroupAsyncClient;
import org.jclouds.ec2.services.SecurityGroupClient;
import org.jclouds.ec2.services.WindowsAsyncClient;
import org.jclouds.ec2.services.WindowsClient;
import org.jclouds.rest.ConfiguresRestClient;

import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import com.google.inject.Provides;

/**
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
public class CloudStackEC2RestClientModule extends EC2RestClientModule<CloudStackEC2Client, CloudStackEC2AsyncClient> {
   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap.<Class<?>, Class<?>> builder()//
         .put(CloudStackAMIClient.class, CloudStackAMIAsyncClient.class)//
         .put(ElasticIPAddressClient.class, ElasticIPAddressAsyncClient.class)//
         .put(InstanceClient.class, InstanceAsyncClient.class)//
         .put(KeyPairClient.class, KeyPairAsyncClient.class)//
         .put(SecurityGroupClient.class, SecurityGroupAsyncClient.class)//
         .put(WindowsClient.class, WindowsAsyncClient.class)//
         .put(AvailabilityZoneAndRegionClient.class, AvailabilityZoneAndRegionAsyncClient.class)//
         .put(ElasticBlockStoreClient.class, ElasticBlockStoreAsyncClient.class)//
         .put(WindowsApi.class, WindowsAsyncApi.class)//
         .build();

   public CloudStackEC2RestClientModule() {
      super(TypeToken.of(CloudStackEC2Client.class), TypeToken.of(CloudStackEC2AsyncClient.class), DELEGATE_MAP);
   }

   @Override
   protected void configure() {
      super.configure();
      // override parsers, etc. here
      // ex.
      // bind(DescribeImagesResponseHandler.class).to(CloudStackDescribeImagesResponseHandler.class);
   }

   @Singleton
   @Provides
   EC2Client provide(CloudStackEC2Client in) {
      return in;
   }

   @Singleton
   @Provides
   EC2AsyncClient provide(CloudStackEC2AsyncClient in) {
      return in;
   }
}
