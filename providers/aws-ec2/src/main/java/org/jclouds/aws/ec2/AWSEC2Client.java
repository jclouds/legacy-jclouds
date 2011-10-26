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

import java.util.concurrent.TimeUnit;

import org.jclouds.aws.ec2.services.AWSAMIClient;
import org.jclouds.aws.ec2.services.AWSInstanceClient;
import org.jclouds.aws.ec2.services.AWSKeyPairClient;
import org.jclouds.aws.ec2.services.AWSSecurityGroupClient;
import org.jclouds.aws.ec2.services.MonitoringClient;
import org.jclouds.aws.ec2.services.PlacementGroupClient;
import org.jclouds.aws.ec2.services.SpotInstanceClient;
import org.jclouds.aws.ec2.services.TagClient;
import org.jclouds.concurrent.Timeout;
import org.jclouds.ec2.EC2Client;
import org.jclouds.rest.annotations.Delegate;

/**
 * Provides synchronous access to EC2 services.
 * 
 * @author Adrian Cole
 */
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface AWSEC2Client extends EC2Client {

   /**
    * {@inheritDoc}
    */
   @Delegate
   @Override
   AWSInstanceClient getInstanceServices();

   /**
    * {@inheritDoc}
    */
   @Delegate
   @Override
   AWSSecurityGroupClient getSecurityGroupServices();
   
   /**
    * {@inheritDoc}
    */
   @Delegate
   @Override
   AWSAMIClient getAMIServices();

   /**
    * Provides synchronous access to PlacementGroup services.
    */
   @Delegate
   PlacementGroupClient getPlacementGroupServices();

   /**
    * Provides synchronous access to Monitoring services.
    */
   @Delegate
   MonitoringClient getMonitoringServices();

   /**
    * {@inheritDoc}
    */
   @Delegate
   @Override
   AWSKeyPairClient getKeyPairServices();
   
   /**
    * Provides synchronous access to SpotInstance services.
    */
   @Delegate
   SpotInstanceClient getSpotInstanceServices();
   
   /**
    * Provides synchronous access to Tag services.
    */
   @Delegate
   TagClient getTagServices();
}
