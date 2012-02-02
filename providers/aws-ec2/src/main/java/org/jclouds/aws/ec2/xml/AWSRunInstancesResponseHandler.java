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
package org.jclouds.aws.ec2.xml;

import javax.inject.Inject;

import org.jclouds.aws.ec2.domain.AWSRunningInstance;
import org.jclouds.date.DateService;
import org.jclouds.ec2.domain.Reservation;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.location.Region;

import com.google.common.base.Supplier;
import com.google.inject.Provider;

/**
 * Parses the following XML document:
 * <p/>
 * RunInstancesResponse xmlns="http:
 * 
 * @author Adrian Cole
 * @see <a href="http: />
 */
public class AWSRunInstancesResponseHandler extends BaseAWSReservationHandler<Reservation<? extends RunningInstance>> {

   @Inject
   AWSRunInstancesResponseHandler(DateService dateService, @Region Supplier<String> defaultRegion,
         Provider<AWSRunningInstance.Builder> builderProvider) {
      super(dateService, defaultRegion, builderProvider);
   }

   @Override
   public Reservation<? extends RunningInstance> getResult() {
      return newReservation();
   }

   protected boolean endOfInstanceItem() {
      return itemDepth == 1 && inInstancesSet;
   }
}
