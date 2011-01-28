package org.jclouds.aws.ec2;

/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

import org.jclouds.aws.ec2.services.AWSAMIAsyncClient;
import org.jclouds.aws.ec2.services.AWSInstanceAsyncClient;
import org.jclouds.aws.ec2.services.MonitoringAsyncClient;
import org.jclouds.aws.ec2.services.PlacementGroupAsyncClient;
import org.jclouds.ec2.EC2AsyncClient;
import org.jclouds.rest.annotations.Delegate;

/**
 * Provides asynchronous access to EC2 services.
 * 
 * @author Adrian Cole
 */
public interface AWSEC2AsyncClient extends EC2AsyncClient {
   public final static String VERSION = "2010-06-15";

   /**
    * {@inheritDoc}
    */
   @Delegate
   @Override
   AWSInstanceAsyncClient getInstanceServices();

   /**
    * {@inheritDoc}
    */
   @Delegate
   @Override
   AWSAMIAsyncClient getAMIServices();

   /**
    * Provides asynchronous access to PlacementGroup services.
    */
   @Delegate
   PlacementGroupAsyncClient getPlacementGroupServices();

   /**
    * Provides asynchronous access to Monitoring services.
    */
   @Delegate
   MonitoringAsyncClient getMonitoringServices();

}
