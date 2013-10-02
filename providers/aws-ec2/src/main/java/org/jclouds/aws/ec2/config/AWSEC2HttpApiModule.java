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
package org.jclouds.aws.ec2.config;
import static org.jclouds.reflect.Reflection2.typeToken;

import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.aws.ec2.AWSEC2Api;
import org.jclouds.aws.ec2.domain.AWSRunningInstance;
import org.jclouds.aws.ec2.domain.SpotInstanceRequest;
import org.jclouds.aws.ec2.functions.SpotInstanceRequestToAWSRunningInstance;
import org.jclouds.aws.ec2.options.AWSRunInstancesOptions;
import org.jclouds.aws.ec2.features.AWSAMIApi;
import org.jclouds.aws.ec2.features.AWSInstanceApi;
import org.jclouds.aws.ec2.features.AWSKeyPairApi;
import org.jclouds.aws.ec2.features.AWSSecurityGroupApi;
import org.jclouds.aws.ec2.features.MonitoringApi;
import org.jclouds.aws.ec2.features.PlacementGroupApi;
import org.jclouds.aws.ec2.features.SpotInstanceApi;
import org.jclouds.ec2.EC2Api;
import org.jclouds.ec2.config.BaseEC2HttpApiModule;
import org.jclouds.ec2.features.SubnetApi;
import org.jclouds.ec2.features.TagApi;
import org.jclouds.ec2.features.WindowsApi;
import org.jclouds.ec2.options.RunInstancesOptions;
import org.jclouds.ec2.features.AMIApi;
import org.jclouds.ec2.features.AvailabilityZoneAndRegionApi;
import org.jclouds.ec2.features.ElasticBlockStoreApi;
import org.jclouds.ec2.features.ElasticIPAddressApi;
import org.jclouds.ec2.features.InstanceApi;
import org.jclouds.ec2.features.SecurityGroupApi;
import org.jclouds.rest.ConfiguresHttpApi;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * Configures the EC2 connection.
 * 
 * @author Adrian Cole
 */
@ConfiguresHttpApi
public class AWSEC2HttpApiModule extends BaseEC2HttpApiModule<AWSEC2Api> {

   public AWSEC2HttpApiModule() {
      super(AWSEC2Api.class);
   }

   @Singleton
   @Provides
   EC2Api provide(AWSEC2Api in) {
      return in;
   }

   @Singleton
   @Provides
   InstanceApi getInstanceApi(AWSEC2Api in) {
      return in.getInstanceApi().get();
   }

   @Singleton
   @Provides
   SecurityGroupApi getSecurityGroupApi(AWSEC2Api in) {
      return in.getSecurityGroupApi().get();
   }

   @Singleton
   @Provides
   AMIApi getAMIApi(AWSEC2Api in) {
      return in.getAMIApi().get();
   }

   @Override
   protected void configure() {
      bind(RunInstancesOptions.class).to(AWSRunInstancesOptions.class);
      bind(new TypeLiteral<Function<SpotInstanceRequest, AWSRunningInstance>>() {
      }).to(SpotInstanceRequestToAWSRunningInstance.class);
      super.configure();
   }
}
