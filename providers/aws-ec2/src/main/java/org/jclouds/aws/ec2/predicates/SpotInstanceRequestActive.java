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
package org.jclouds.aws.ec2.predicates;

import java.util.NoSuchElementException;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.aws.ec2.AWSEC2Client;
import org.jclouds.aws.ec2.domain.SpotInstanceRequest;
import org.jclouds.logging.Logger;
import org.jclouds.rest.ResourceNotFoundException;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

/**
 * 
 * 
 * @author Adrian Cole
 */
@Singleton
public class SpotInstanceRequestActive implements Predicate<SpotInstanceRequest> {

   private final AWSEC2Client client;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public SpotInstanceRequestActive(AWSEC2Client client) {
      this.client = client;
   }

   public boolean apply(SpotInstanceRequest spot) {
      logger.trace("looking for state on spot %s", spot);
      try {
         spot = refresh(spot);
         logger.trace("%s: looking for spot state %s: currently: %s", spot.getId(), SpotInstanceRequest.State.ACTIVE,
                  spot.getState());
         if (spot.getState() == SpotInstanceRequest.State.CANCELLED
                  || spot.getState() == SpotInstanceRequest.State.CLOSED)
            throw new IllegalStateException(String.format("spot request %s %s", spot.getId(), spot.getState()));
         if (spot.getFaultCode() != null)
            throw new IllegalStateException(String.format("spot request %s fault code(%s) message(%s)", spot.getId(),
                     spot.getFaultCode(), spot.getFaultMessage()));
         return spot.getState() == SpotInstanceRequest.State.ACTIVE;
      } catch (ResourceNotFoundException e) {
         return false;
      } catch (NoSuchElementException e) {
         return false;
      }
   }

   private SpotInstanceRequest refresh(SpotInstanceRequest spot) {
      return Iterables.getOnlyElement(client.getSpotInstanceServices().describeSpotInstanceRequestsInRegion(
               spot.getRegion(), spot.getId()));
   }
}
