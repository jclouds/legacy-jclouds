/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.aws.ec2;

import org.jclouds.aws.ec2.internal.EC2ClientImpl;
import org.jclouds.aws.ec2.services.AMIClient;
import org.jclouds.aws.ec2.services.AvailabilityZoneAndRegionClient;
import org.jclouds.aws.ec2.services.ElasticIPAddressClient;
import org.jclouds.aws.ec2.services.InstanceClient;
import org.jclouds.aws.ec2.services.KeyPairClient;
import org.jclouds.aws.ec2.services.MonitoringClient;
import org.jclouds.aws.ec2.services.SecurityGroupClient;

import com.google.inject.ImplementedBy;

/**
 * Provides synchronous access to EC2 services.
 * 
 * @author Adrian Cole
 */
@ImplementedBy(EC2ClientImpl.class)
public interface EC2Client {
   /**
    * Provides synchronous access to AMI services.
    */
   AMIClient getAMIServices();

   /**
    * Provides synchronous access to Elastic IP Address services.
    */
   ElasticIPAddressClient getElasticIPAddressServices();

   /**
    * Provides synchronous access to Instance services.
    */
   InstanceClient getInstanceServices();

   /**
    * Provides synchronous access to KeyPair services.
    */
   KeyPairClient getKeyPairServices();

   /**
    * Provides synchronous access to SecurityGroup services.
    */
   SecurityGroupClient getSecurityGroupServices();

   /**
    * Provides synchronous access to Monitoring services.
    */
   MonitoringClient getMonitoringServices();

   /**
    * Provides synchronous access to Availability Zones and Regions services.
    */
   AvailabilityZoneAndRegionClient getAvailabilityZoneAndRegionServices();
}
