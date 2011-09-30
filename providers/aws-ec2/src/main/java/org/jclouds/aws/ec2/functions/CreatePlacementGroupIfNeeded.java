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
package org.jclouds.aws.ec2.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.aws.AWSResponseException;
import org.jclouds.aws.ec2.AWSEC2Client;
import org.jclouds.aws.ec2.domain.PlacementGroup;
import org.jclouds.aws.ec2.domain.PlacementGroup.State;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;
import com.google.common.cache.CacheLoader;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class CreatePlacementGroupIfNeeded extends CacheLoader<RegionAndName, String> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   protected final AWSEC2Client ec2Client;
   protected final Predicate<PlacementGroup> placementGroupAvailable;

   @Inject
   public CreatePlacementGroupIfNeeded(AWSEC2Client ec2Client,
            @Named("AVAILABLE") Predicate<PlacementGroup> placementGroupAvailable) {
      this.ec2Client = ec2Client;
      this.placementGroupAvailable = placementGroupAvailable;
   }

   @Override
   public String load(RegionAndName from) {
      createPlacementGroupInRegion(from.getRegion(), from.getName());
      return from.getName();
   }

   private void createPlacementGroupInRegion(String region, String name) {
      checkNotNull(region, "region");
      checkNotNull(name, "name");
      logger.debug(">> creating placementGroup region(%s) name(%s)", region, name);
      try {
         ec2Client.getPlacementGroupServices().createPlacementGroupInRegion(region, name);
         logger.debug("<< created placementGroup(%s)", name);
         checkState(placementGroupAvailable.apply(new PlacementGroup(region, name, "cluster", State.PENDING)), String
                  .format("placementGroup region(%s) name(%s) failed to become available", region, name));
      } catch (AWSResponseException e) {
         if (e.getError().getCode().equals("InvalidPlacementGroup.Duplicate")) {
            logger.debug("<< reused placementGroup(%s)", name);
         } else {
            throw e;
         }
      }
   }

}
