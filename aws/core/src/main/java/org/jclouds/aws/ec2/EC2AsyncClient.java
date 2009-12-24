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

import org.jclouds.aws.ec2.internal.EC2AsyncClientImpl;
import org.jclouds.aws.ec2.services.AMIAsyncClient;
import org.jclouds.aws.ec2.services.ElasticIPAddressAsyncClient;
import org.jclouds.aws.ec2.services.InstanceAsyncClient;
import org.jclouds.aws.ec2.services.KeyPairAsyncClient;
import org.jclouds.aws.ec2.services.SecurityGroupAsyncClient;

import com.google.inject.ImplementedBy;

/**
 * Provides asynchronous access to EC2 services.
 * 
 * @author Adrian Cole
 */
@ImplementedBy(EC2AsyncClientImpl.class)
public interface EC2AsyncClient {
   /**
    * Provides asynchronous access to AMI services.
    */
   AMIAsyncClient getAMIServices();

   /**
    * Provides asynchronous access to Elastic IP Address services.
    */
   ElasticIPAddressAsyncClient getElasticIPAddressServices();

   /**
    * Provides asynchronous access to Instance services.
    */
   InstanceAsyncClient getInstanceServices();

   /**
    * Provides asynchronous access to KeyPair services.
    */
   KeyPairAsyncClient getKeyPairServices();

   /**
    * Provides asynchronous access to SecurityGroup services.
    */
   SecurityGroupAsyncClient getSecurityGroupServices();

}
