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
package org.jclouds.openstack.nova.v2_0.compute.loaders;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.openstack.nova.v2_0.config.NovaProperties.TIMEOUT_SECURITYGROUP_PRESENT;

import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.openstack.nova.v2_0.domain.zonescoped.SecurityGroupInZone;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.ZoneAndName;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.ZoneSecurityGroupNameAndPorts;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.cache.CacheLoader;

/**
 * 
 * @author Adrian Cole
 */
public class FindSecurityGroupOrCreate extends CacheLoader<ZoneAndName, SecurityGroupInZone> {

   protected final Predicate<AtomicReference<ZoneAndName>> returnSecurityGroupExistsInZone;
   protected final Function<ZoneSecurityGroupNameAndPorts, SecurityGroupInZone> groupCreator;

   @Inject
   public FindSecurityGroupOrCreate(
            @Named(TIMEOUT_SECURITYGROUP_PRESENT) Predicate<AtomicReference<ZoneAndName>> returnSecurityGroupExistsInZone,
            Function<ZoneSecurityGroupNameAndPorts, SecurityGroupInZone> groupCreator) {
      this.returnSecurityGroupExistsInZone = checkNotNull(returnSecurityGroupExistsInZone,
               "returnSecurityGroupExistsInZone");
      this.groupCreator = checkNotNull(groupCreator, "groupCreator");
   }

   @Override
   public SecurityGroupInZone load(ZoneAndName in) {
      AtomicReference<ZoneAndName> securityGroupInZoneRef = new AtomicReference<ZoneAndName>(checkNotNull(in,
               "zoneSecurityGroupNameAndPorts"));
      if (returnSecurityGroupExistsInZone.apply(securityGroupInZoneRef)) {
         return returnExistingSecurityGroup(securityGroupInZoneRef);
      } else {
         return createNewSecurityGroup(in);
      }
   }

   private SecurityGroupInZone returnExistingSecurityGroup(AtomicReference<ZoneAndName> securityGroupInZoneRef) {
      ZoneAndName securityGroupInZone = securityGroupInZoneRef.get();
      checkState(securityGroupInZone instanceof SecurityGroupInZone,
               "programming error: predicate %s should update the atomic reference to the actual security group found",
               returnSecurityGroupExistsInZone);
      return SecurityGroupInZone.class.cast(securityGroupInZone);
   }

   private SecurityGroupInZone createNewSecurityGroup(ZoneAndName in) {
      checkState(
               checkNotNull(in, "zoneSecurityGroupNameAndPorts") instanceof ZoneSecurityGroupNameAndPorts,
               "programming error: when issuing get to this cacheloader, you need to pass an instance of ZoneSecurityGroupNameAndPorts, not %s",
               in);
      ZoneSecurityGroupNameAndPorts zoneSecurityGroupNameAndPorts = ZoneSecurityGroupNameAndPorts.class.cast(in);
      return groupCreator.apply(zoneSecurityGroupNameAndPorts);
   }

   @Override
   public String toString() {
      return "returnExistingSecurityGroupInZoneOrCreateAsNeeded()";
   }

}
