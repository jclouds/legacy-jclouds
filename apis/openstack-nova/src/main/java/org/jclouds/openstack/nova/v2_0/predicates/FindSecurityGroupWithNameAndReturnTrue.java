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
package org.jclouds.openstack.nova.v2_0.predicates;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.logging.Logger;
import org.jclouds.openstack.nova.v2_0.NovaClient;
import org.jclouds.openstack.nova.v2_0.domain.SecurityGroup;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.SecurityGroupInZone;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.ZoneAndName;
import org.jclouds.openstack.nova.v2_0.extensions.SecurityGroupClient;
import org.jclouds.rest.ResourceNotFoundException;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

/**
 * AtomicReference is so that we can return the securityGroup that matched.
 * 
 * @author Adrian Cole
 */
@Singleton
public class FindSecurityGroupWithNameAndReturnTrue implements Predicate<AtomicReference<ZoneAndName>> {

   private final NovaClient novaClient;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public FindSecurityGroupWithNameAndReturnTrue(NovaClient novaClient) {
      this.novaClient = checkNotNull(novaClient, "novaClient");
   }

   public boolean apply(AtomicReference<ZoneAndName> securityGroupInZoneRef) {
      checkNotNull(securityGroupInZoneRef, "securityGroupRef");
      final ZoneAndName securityGroupInZone = checkNotNull(securityGroupInZoneRef.get(), "securityGroupInZone");

      Optional<SecurityGroupClient> client = novaClient.getSecurityGroupExtensionForZone(securityGroupInZone.getZone());
      checkArgument(client.isPresent(), "Security groups are required, but the extension is not available!");

      logger.trace("looking for security group %s", securityGroupInZone.slashEncode());
      try {
         SecurityGroup returnVal = Iterables.find(client.get().listSecurityGroups(), new Predicate<SecurityGroup>() {

            @Override
            public boolean apply(SecurityGroup input) {
               return input.getName().equals(securityGroupInZone.getName());
            }

         });
         securityGroupInZoneRef.set(new SecurityGroupInZone(returnVal, securityGroupInZone.getZone()));
         return true;
      } catch (ResourceNotFoundException e) {
         return false;
      } catch (NoSuchElementException e) {
         return false;
      }
   }
}
