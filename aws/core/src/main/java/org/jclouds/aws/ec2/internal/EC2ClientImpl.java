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
package org.jclouds.aws.ec2.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.aws.ec2.EC2Client;
import org.jclouds.aws.ec2.services.AMIClient;
import org.jclouds.aws.ec2.services.AvailabilityZoneAndRegionClient;
import org.jclouds.aws.ec2.services.ElasticBlockStoreClient;
import org.jclouds.aws.ec2.services.ElasticIPAddressClient;
import org.jclouds.aws.ec2.services.ElasticLoadBalancerClient;
import org.jclouds.aws.ec2.services.InstanceClient;
import org.jclouds.aws.ec2.services.KeyPairClient;
import org.jclouds.aws.ec2.services.MonitoringClient;
import org.jclouds.aws.ec2.services.SecurityGroupClient;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class EC2ClientImpl implements EC2Client {

   private final AMIClient AMIServices;
   private final ElasticIPAddressClient elasticIPAddressServices;
   private final InstanceClient instanceServices;
   private final KeyPairClient keyPairServices;
   private final SecurityGroupClient securityGroupServices;
   private final MonitoringClient monitoringServices;
   private final AvailabilityZoneAndRegionClient availabilityZoneAndRegionServices;
   private final ElasticBlockStoreClient elasticBlockStoreServices;
   private final ElasticLoadBalancerClient elasticLoadBalancerClient;

   @Inject
   public EC2ClientImpl(AMIClient AMIServices, ElasticIPAddressClient elasticIPAddressServices,
            InstanceClient instanceServices, KeyPairClient keyPairServices,
            SecurityGroupClient securityGroupServices, MonitoringClient monitoringServices,
            AvailabilityZoneAndRegionClient availabilityZoneAndRegionServices,
            ElasticBlockStoreClient elasticBlockStoreServices,
            ElasticLoadBalancerClient elasticLoadBalancerClient) {
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
      this.elasticLoadBalancerClient = checkNotNull(elasticLoadBalancerClient,
               "elasticLoadBalancerClient");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AMIClient getAMIServices() {
      return AMIServices;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ElasticIPAddressClient getElasticIPAddressServices() {
      return elasticIPAddressServices;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public InstanceClient getInstanceServices() {
      return instanceServices;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public KeyPairClient getKeyPairServices() {
      return keyPairServices;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public SecurityGroupClient getSecurityGroupServices() {
      return securityGroupServices;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public MonitoringClient getMonitoringServices() {
      return monitoringServices;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AvailabilityZoneAndRegionClient getAvailabilityZoneAndRegionServices() {
      return availabilityZoneAndRegionServices;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ElasticBlockStoreClient getElasticBlockStoreServices() {
      return elasticBlockStoreServices;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ElasticLoadBalancerClient getElasticLoadBalancerServices() {
      return elasticLoadBalancerClient;
   }

}
