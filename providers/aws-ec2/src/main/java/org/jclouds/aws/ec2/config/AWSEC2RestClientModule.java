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
package org.jclouds.aws.ec2.config;

import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.aws.ec2.AWSEC2AsyncClient;
import org.jclouds.aws.ec2.AWSEC2Client;
import org.jclouds.aws.ec2.domain.AWSRunningInstance;
import org.jclouds.aws.ec2.options.AWSRunInstancesOptions;
import org.jclouds.aws.ec2.services.AWSAMIAsyncClient;
import org.jclouds.aws.ec2.services.AWSAMIClient;
import org.jclouds.aws.ec2.services.AWSInstanceAsyncClient;
import org.jclouds.aws.ec2.services.AWSInstanceClient;
import org.jclouds.aws.ec2.services.AWSKeyPairAsyncClient;
import org.jclouds.aws.ec2.services.AWSKeyPairClient;
import org.jclouds.aws.ec2.services.AWSSecurityGroupAsyncClient;
import org.jclouds.aws.ec2.services.AWSSecurityGroupClient;
import org.jclouds.aws.ec2.services.MonitoringAsyncClient;
import org.jclouds.aws.ec2.services.MonitoringClient;
import org.jclouds.aws.ec2.services.PlacementGroupAsyncClient;
import org.jclouds.aws.ec2.services.PlacementGroupClient;
import org.jclouds.aws.ec2.services.SpotInstanceAsyncClient;
import org.jclouds.aws.ec2.services.SpotInstanceClient;
import org.jclouds.aws.ec2.services.TagAsyncClient;
import org.jclouds.aws.ec2.services.TagClient;
import org.jclouds.ec2.EC2AsyncClient;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.config.EC2RestClientModule;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.ec2.options.RunInstancesOptions;
import org.jclouds.ec2.services.AMIAsyncClient;
import org.jclouds.ec2.services.AMIClient;
import org.jclouds.ec2.services.AvailabilityZoneAndRegionAsyncClient;
import org.jclouds.ec2.services.AvailabilityZoneAndRegionClient;
import org.jclouds.ec2.services.ElasticBlockStoreAsyncClient;
import org.jclouds.ec2.services.ElasticBlockStoreClient;
import org.jclouds.ec2.services.ElasticIPAddressAsyncClient;
import org.jclouds.ec2.services.ElasticIPAddressClient;
import org.jclouds.ec2.services.InstanceAsyncClient;
import org.jclouds.ec2.services.InstanceClient;
import org.jclouds.ec2.services.SecurityGroupAsyncClient;
import org.jclouds.ec2.services.SecurityGroupClient;
import org.jclouds.ec2.services.WindowsAsyncClient;
import org.jclouds.ec2.services.WindowsClient;
import org.jclouds.rest.ConfiguresRestClient;

import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import com.google.inject.Provides;

/**
 * Configures the EC2 connection.
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
public class AWSEC2RestClientModule extends EC2RestClientModule<AWSEC2Client, AWSEC2AsyncClient> {

   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap.<Class<?>, Class<?>> builder()//
         .put(AWSAMIClient.class, AWSAMIAsyncClient.class)//
         .put(ElasticIPAddressClient.class, ElasticIPAddressAsyncClient.class)//
         .put(AWSInstanceClient.class, AWSInstanceAsyncClient.class)//
         .put(AWSKeyPairClient.class, AWSKeyPairAsyncClient.class)//
         .put(AWSSecurityGroupClient.class, AWSSecurityGroupAsyncClient.class)//
         .put(PlacementGroupClient.class, PlacementGroupAsyncClient.class)//
         .put(MonitoringClient.class, MonitoringAsyncClient.class)//
         .put(WindowsClient.class, WindowsAsyncClient.class)//
         .put(AvailabilityZoneAndRegionClient.class, AvailabilityZoneAndRegionAsyncClient.class)//
         .put(ElasticBlockStoreClient.class, ElasticBlockStoreAsyncClient.class)//
         .put(SpotInstanceClient.class, SpotInstanceAsyncClient.class)//
         .put(TagClient.class, TagAsyncClient.class)//
         .build();

   public AWSEC2RestClientModule() {
      super(TypeToken.of(AWSEC2Client.class), TypeToken.of(AWSEC2AsyncClient.class), DELEGATE_MAP);
   }

   @Singleton
   @Provides
   EC2Client provide(AWSEC2Client in) {
      return in;
   }

   @Singleton
   @Provides
   EC2AsyncClient provide(AWSEC2AsyncClient in) {
      return in;
   }

   @Singleton
   @Provides
   InstanceClient getInstanceServices(AWSEC2Client in) {
      return in.getInstanceServices();
   }

   @Singleton
   @Provides
   InstanceAsyncClient getInstanceServices(AWSEC2AsyncClient in) {
      return in.getInstanceServices();
   }

   @Singleton
   @Provides
   SecurityGroupClient getSecurityGroupServices(AWSEC2Client in) {
      return in.getSecurityGroupServices();
   }

   @Singleton
   @Provides
   SecurityGroupAsyncClient getSecurityGroupServices(AWSEC2AsyncClient in) {
      return in.getSecurityGroupServices();
   }

   @Singleton
   @Provides
   AMIClient getAMIServices(AWSEC2Client in) {
      return in.getAMIServices();
   }

   @Singleton
   @Provides
   AMIAsyncClient getAMIServices(AWSEC2AsyncClient in) {
      return in.getAMIServices();
   }

   @Singleton
   @Provides
   TagClient getTagServices(AWSEC2Client in) {
      return in.getTagServices();
   }

   @Singleton
   @Provides
   TagAsyncClient getTagServices(AWSEC2AsyncClient in) {
      return in.getTagServices();
   }

   @Override
   protected void configure() {
      bind(RunningInstance.Builder.class).to(AWSRunningInstance.Builder.class);
      bind(RunInstancesOptions.class).to(AWSRunInstancesOptions.class);
      super.configure();
   }
}
