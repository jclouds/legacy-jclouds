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
package org.jclouds.openstack.nova.v1_1.compute.loaders;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.openstack.nova.v1_1.compute.domain.SecurityGroupInZone;
import org.jclouds.openstack.nova.v1_1.compute.domain.ZoneAndName;
import org.jclouds.openstack.nova.v1_1.compute.domain.ZoneSecurityGroupNameAndPorts;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.cache.CacheLoader;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class CreateOrUpdateSecurityGroupAsNeeded extends
         CacheLoader<ZoneSecurityGroupNameAndPorts, SecurityGroupInZone> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   protected final Predicate<AtomicReference<ZoneAndName>> securityGroupEventualConsistencyDelay;
   protected final Function<ZoneSecurityGroupNameAndPorts, SecurityGroupInZone> groupCreator;

   @Inject
   public CreateOrUpdateSecurityGroupAsNeeded(
            @Named("SECURITY") Predicate<AtomicReference<ZoneAndName>> securityGroupEventualConsistencyDelay,
            Function<ZoneSecurityGroupNameAndPorts, SecurityGroupInZone> groupCreator) {
      this.securityGroupEventualConsistencyDelay = checkNotNull(securityGroupEventualConsistencyDelay,
               "securityGroupEventualConsistencyDelay");
      this.groupCreator = checkNotNull(groupCreator, "groupCreator");
   }

   @Override
   public SecurityGroupInZone load(ZoneSecurityGroupNameAndPorts zoneSecurityGroupNameAndPorts) {
      checkNotNull(zoneSecurityGroupNameAndPorts, "zoneSecurityGroupNameAndPorts");

      AtomicReference<ZoneAndName> securityGroupInZoneRef = new AtomicReference<ZoneAndName>(
               zoneSecurityGroupNameAndPorts);

      if (securityGroupEventualConsistencyDelay.apply(securityGroupInZoneRef)) {
         ZoneAndName securityGroupInZone = securityGroupInZoneRef.get();
         checkState(
                  securityGroupInZone instanceof SecurityGroupInZone,
                  "programming error: predicate %s should update the atomic reference to the actual security group found",
                  securityGroupEventualConsistencyDelay);
         // TODO: check ports are actually present!
         return SecurityGroupInZone.class.cast(securityGroupInZone);
      } else {
         return groupCreator.apply(zoneSecurityGroupNameAndPorts);
      }
   }

}
