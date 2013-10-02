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
package org.jclouds.ec2.compute.predicates;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.NoSuchElementException;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.ec2.EC2Api;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.domain.SecurityGroup;
import org.jclouds.logging.Logger;
import org.jclouds.rest.ResourceNotFoundException;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class SecurityGroupPresent implements Predicate<RegionAndName> {

   private final EC2Api client;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public SecurityGroupPresent(EC2Api client) {
      this.client = checkNotNull(client, "client");
   }

   public boolean apply(RegionAndName securityGroup) {
      logger.trace("looking for security group %s/%s", securityGroup.getRegion(), securityGroup.getName());
      try {
         return refresh(securityGroup) != null;
      } catch (ResourceNotFoundException e) {
         return false;
      } catch (NoSuchElementException e) {
         return false;
      }
   }

   protected SecurityGroup refresh(RegionAndName securityGroup) {
      return Iterables.getOnlyElement(client.getSecurityGroupApi().get().describeSecurityGroupsInRegion(
            securityGroup.getRegion(), securityGroup.getName()));
   }
}
