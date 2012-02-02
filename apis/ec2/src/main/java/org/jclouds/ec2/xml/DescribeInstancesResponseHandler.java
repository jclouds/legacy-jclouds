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
package org.jclouds.ec2.xml;

import java.util.Set;

import javax.inject.Inject;

import org.jclouds.date.DateService;
import org.jclouds.ec2.domain.Reservation;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.location.Region;

import com.google.common.base.Supplier;
import com.google.common.collect.Sets;
import com.google.inject.Provider;

/**
 * Parses the following XML document:
 * <p/>
 * DescribeImagesResponse xmlns="http:
 * 
 * @author Adrian Cole
 * @see <a href="http: />
 */
public class DescribeInstancesResponseHandler extends
         BaseReservationHandler<Set<Reservation<? extends RunningInstance>>> {
   private Set<Reservation<? extends RunningInstance>> reservations = Sets.newLinkedHashSet();

   @Inject
   DescribeInstancesResponseHandler(DateService dateService, @Region Supplier<String> defaultRegion,
            Provider<RunningInstance.Builder> builderProvider) {
      super(dateService, defaultRegion, builderProvider);
   }

   @Override
   public Set<Reservation<? extends RunningInstance>> getResult() {
      return reservations;
   }

   protected boolean endOfReservationItem() {
      return itemDepth == 1;
   }

   @Override
   protected void inItem() {
      if (endOfReservationItem()) {
         reservations.add(super.newReservation());
      } else {
         super.inItem();
      }
   }

}
