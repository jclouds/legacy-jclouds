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
package org.jclouds.openstack.nova.ec2.loaders;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.aws.AWSResponseException;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.compute.loaders.CreateSecurityGroupIfNeeded;

import com.google.common.base.Predicate;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class NovaCreateSecurityGroupIfNeeded extends CreateSecurityGroupIfNeeded {

   @Inject
   public NovaCreateSecurityGroupIfNeeded(EC2Client ec2Client,
            @Named("SECURITY") Predicate<RegionAndName> securityGroupEventualConsistencyDelay) {
      super(checkNotNull(ec2Client, "ec2Client").getSecurityGroupServices(), securityGroupEventualConsistencyDelay);
   }

   protected void authorizeGroupToItself(String region, String name) {
      try {
         super.authorizeGroupToItself(region, name);
      } catch (AWSResponseException e) {
         logger.warn(e, "<< error authorizing securityGroup(%s)", name);
      }
   }

}
