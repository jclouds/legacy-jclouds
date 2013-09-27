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
package org.jclouds.ec2.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.NoSuchElementException;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.SecurityGroup;
import org.jclouds.compute.domain.SecurityGroupBuilder;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;


/**
 * A function for transforming an EC2-specific SecurityGroup into a generic
 * SecurityGroup object.
 * 
 * @author Andrew Bayer
 */
@Singleton
public class EC2SecurityGroupToSecurityGroup implements Function<org.jclouds.ec2.domain.SecurityGroup, SecurityGroup> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   
   protected final Supplier<Set<? extends Location>> locations;

   @Inject
   public EC2SecurityGroupToSecurityGroup(@Memoized Supplier<Set<? extends Location>> locations) {
      this.locations = checkNotNull(locations, "locations");
   }

   @Override
   public SecurityGroup apply(org.jclouds.ec2.domain.SecurityGroup group) {
      SecurityGroupBuilder builder = new SecurityGroupBuilder();
      Location location = findLocationWithId(group.getRegion());
      builder.location(location);
      builder.id(group.getRegion() + "/" + idOrName(group));
      builder.providerId(group.getId());
      builder.name(group.getName());
      builder.ipPermissions(group);
      builder.ownerId(group.getOwnerId());
      
      return builder.build();
   }

   protected String idOrName(org.jclouds.ec2.domain.SecurityGroup group) {
      return group.getName();
   }

   private Location findLocationWithId(final String locationId) {
      if (locationId == null)
         return null;
      try {
         Location location = Iterables.find(locations.get(), new Predicate<Location>() {

            @Override
            public boolean apply(Location input) {
               return input.getId().equals(locationId);
            }

         });
         return location;

      } catch (NoSuchElementException e) {
         logger.debug("couldn't match instance location %s in: %s", locationId, locations.get());
         return null;
      }
   }
}
