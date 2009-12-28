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
package org.jclouds.aws.ec2.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.aws.ec2.EC2AsyncClient;
import org.jclouds.aws.ec2.services.AMIAsyncClient;
import org.jclouds.aws.ec2.services.AvailabilityZoneAndRegionAsyncClient;
import org.jclouds.aws.ec2.services.ElasticBlockStoreAsyncClient;
import org.jclouds.aws.ec2.services.ElasticIPAddressAsyncClient;
import org.jclouds.aws.ec2.services.InstanceAsyncClient;
import org.jclouds.aws.ec2.services.KeyPairAsyncClient;
import org.jclouds.aws.ec2.services.MonitoringAsyncClient;
import org.jclouds.aws.ec2.services.SecurityGroupAsyncClient;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class EC2AsyncClientImpl implements EC2AsyncClient {

   private final AMIAsyncClient AMIServices;
   private final ElasticIPAddressAsyncClient elasticIPAddressServices;
   private final InstanceAsyncClient instanceServices;
   private final KeyPairAsyncClient keyPairServices;
   private final SecurityGroupAsyncClient securityGroupServices;
   private final MonitoringAsyncClient monitoringServices;
   private final AvailabilityZoneAndRegionAsyncClient availabilityZoneAndRegionServices;
   private final ElasticBlockStoreAsyncClient elasticBlockStoreServices;

   @Inject
   public EC2AsyncClientImpl(AMIAsyncClient AMIServices,
            ElasticIPAddressAsyncClient elasticIPAddressServices,
            InstanceAsyncClient instanceServices, KeyPairAsyncClient keyPairServices,
            SecurityGroupAsyncClient securityGroupServices,
            MonitoringAsyncClient monitoringServices,
            AvailabilityZoneAndRegionAsyncClient availabilityZoneAndRegionServices,
            ElasticBlockStoreAsyncClient elasticBlockStoreServices) {
      this.AMIServices = checkNotNull(AMIServices, "AMIServices");
      this.elasticIPAddressServices = checkNotNull(elasticIPAddressServices,
               "elasticIPAddressServices");
      this.instanceServices = checkNotNull(instanceServices, "instanceServices");
      this.keyPairServices = checkNotNull(keyPairServices, "keyPairServices");
      this.securityGroupServices = checkNotNull(securityGroupServices, "securityGroupServices");
      this.monitoringServices = checkNotNull(monitoringServices, "monitoringServices");
      this.availabilityZoneAndRegionServices = checkNotNull(availabilityZoneAndRegionServices,
               "availabilityZoneAndRegionServices");
      this.elasticBlockStoreServices = checkNotNull(elasticBlockStoreServices,
               "elasticBlockStoreServices");
   }

   /**
    * {@inheritDoc}
    */
   public AMIAsyncClient getAMIServices() {
      return AMIServices;
   }

   /**
    * {@inheritDoc}
    */
   public ElasticIPAddressAsyncClient getElasticIPAddressServices() {
      return elasticIPAddressServices;
   }

   /**
    * {@inheritDoc}
    */
   public InstanceAsyncClient getInstanceServices() {
      return instanceServices;
   }

   /**
    * {@inheritDoc}
    */
   public KeyPairAsyncClient getKeyPairServices() {
      return keyPairServices;
   }

   /**
    * {@inheritDoc}
    */
   public SecurityGroupAsyncClient getSecurityGroupServices() {
      return securityGroupServices;
   }

   /**
    * {@inheritDoc}
    */
   public MonitoringAsyncClient getMonitoringServices() {
      return monitoringServices;
   }

   /**
    * {@inheritDoc}
    */
   public AvailabilityZoneAndRegionAsyncClient getAvailabilityZoneAndRegionServices() {
      return availabilityZoneAndRegionServices;
   }

   /**
    * {@inheritDoc}
    */
   public ElasticBlockStoreAsyncClient getElasticBlockStoreServices() {
      return elasticBlockStoreServices;
   }

}
