/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.aws.ec2.compute.suppliers;

import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Sets.newHashSet;
import static org.jclouds.aws.ec2.reference.EC2Constants.PROPERTY_EC2_CC_AMIs;

import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.aws.ec2.compute.domain.EC2Hardware;
import org.jclouds.aws.ec2.domain.InstanceType;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class EC2HardwareSupplier implements Supplier<Set<? extends Hardware>> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   private final Supplier<Set<? extends Location>> locations;
   private final String[] ccAmis;

   @Inject
   EC2HardwareSupplier(Supplier<Set<? extends Location>> locations, @Named(PROPERTY_EC2_CC_AMIs) String[] ccAmis) {
      this.locations = locations;
      this.ccAmis = ccAmis;
   }

   @Override
   public Set<? extends Hardware> get() {
      Set<Hardware> sizes = newHashSet();
      for (String ccAmi : ccAmis) {
         final String region = ccAmi.split("/")[0];
         Location location = find(locations.get(), new Predicate<Location>() {

            @Override
            public boolean apply(Location input) {
               return input.getScope() == LocationScope.REGION && input.getId().equals(region);
            }

         });
         sizes.add(new EC2Hardware(location, InstanceType.CC1_4XLARGE, ImmutableList.of(new Processor(4.0, 4.0),
               new Processor(4.0, 4.0)), 23 * 1024, 1690, ccAmis));
      }
      sizes.addAll(ImmutableSet.<Hardware> of(EC2Hardware.T1_MICRO, EC2Hardware.C1_MEDIUM, EC2Hardware.C1_XLARGE,
            EC2Hardware.M1_LARGE, EC2Hardware.M1_SMALL, EC2Hardware.M1_XLARGE, EC2Hardware.M2_XLARGE,
            EC2Hardware.M2_2XLARGE, EC2Hardware.M2_4XLARGE));
      return sizes;
   }
}