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
package org.jclouds.openstack.nova.v2_0.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.SecurityGroup;
import org.jclouds.compute.domain.SecurityGroupBuilder;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.SecurityGroupInZone;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.inject.Inject;


/**
 * A function for transforming a Nova-specific SecurityGroup into a generic
 * SecurityGroup object.
 * 
 * @author Andrew Bayer
 */
@Singleton
public class NovaSecurityGroupInZoneToSecurityGroup implements Function<SecurityGroupInZone, SecurityGroup> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   protected final Function<org.jclouds.openstack.nova.v2_0.domain.SecurityGroup, SecurityGroup> baseConverter;
   protected final Supplier<Map<String, Location>> locationIndex;

   @Inject
   public NovaSecurityGroupInZoneToSecurityGroup(Function<org.jclouds.openstack.nova.v2_0.domain.SecurityGroup, SecurityGroup> baseConverter,
                                                 Supplier<Map<String, Location>> locationIndex) {
      this.baseConverter = checkNotNull(baseConverter, "baseConverter");
      this.locationIndex = checkNotNull(locationIndex, "locationIndex");
   }

   @Override
   public SecurityGroup apply(SecurityGroupInZone group) {
      SecurityGroupBuilder builder = SecurityGroupBuilder.fromSecurityGroup(baseConverter.apply(group.getSecurityGroup()));

      Location zone = locationIndex.get().get(group.getZone());
      checkState(zone != null, "location %s not in locationIndex: %s", group.getZone(), locationIndex.get());

      builder.location(zone);

      return builder.build();
   }
}
