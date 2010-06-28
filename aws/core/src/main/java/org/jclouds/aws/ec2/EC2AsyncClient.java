/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.aws.ec2;

import org.jclouds.aws.ec2.services.AMIAsyncClient;
import org.jclouds.aws.ec2.services.AvailabilityZoneAndRegionAsyncClient;
import org.jclouds.aws.ec2.services.ElasticBlockStoreAsyncClient;
import org.jclouds.aws.ec2.services.ElasticIPAddressAsyncClient;
import org.jclouds.aws.ec2.services.InstanceAsyncClient;
import org.jclouds.aws.ec2.services.KeyPairAsyncClient;
import org.jclouds.aws.ec2.services.MonitoringAsyncClient;
import org.jclouds.aws.ec2.services.SecurityGroupAsyncClient;
import org.jclouds.rest.annotations.Delegate;

/**
 * Provides asynchronous access to EC2 services.
 * 
 * @author Adrian Cole
 */
public interface EC2AsyncClient {
   public final static String VERSION = "2009-11-30";

   /**
    * Provides asynchronous access to AMI services.
    */
   @Delegate
   AMIAsyncClient getAMIServices();

   /**
    * Provides asynchronous access to Elastic IP Address services.
    */
   @Delegate
   ElasticIPAddressAsyncClient getElasticIPAddressServices();

   /**
    * Provides asynchronous access to Instance services.
    */
   @Delegate
   InstanceAsyncClient getInstanceServices();

   /**
    * Provides asynchronous access to KeyPair services.
    */
   @Delegate
   KeyPairAsyncClient getKeyPairServices();

   /**
    * Provides asynchronous access to SecurityGroup services.
    */
   @Delegate
   SecurityGroupAsyncClient getSecurityGroupServices();

   /**
    * Provides asynchronous access to Monitoring services.
    */
   @Delegate
   MonitoringAsyncClient getMonitoringServices();

   /**
    * Provides asynchronous access to Availability Zones and Regions services.
    */
   @Delegate
   AvailabilityZoneAndRegionAsyncClient getAvailabilityZoneAndRegionServices();

   /**
    * Provides asynchronous access to Elastic Block Store services.
    */
   @Delegate
   ElasticBlockStoreAsyncClient getElasticBlockStoreServices();

}
