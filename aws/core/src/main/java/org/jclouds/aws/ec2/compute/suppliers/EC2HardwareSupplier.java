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
import static com.google.common.collect.Sets.newLinkedHashSet;
import static org.jclouds.aws.ec2.compute.domain.EC2HardwareBuilder.c1_medium;
import static org.jclouds.aws.ec2.compute.domain.EC2HardwareBuilder.c1_xlarge;
import static org.jclouds.aws.ec2.compute.domain.EC2HardwareBuilder.cc1_4xlarge;
import static org.jclouds.aws.ec2.compute.domain.EC2HardwareBuilder.m1_large;
import static org.jclouds.aws.ec2.compute.domain.EC2HardwareBuilder.m1_small;
import static org.jclouds.aws.ec2.compute.domain.EC2HardwareBuilder.m1_xlarge;
import static org.jclouds.aws.ec2.compute.domain.EC2HardwareBuilder.m2_2xlarge;
import static org.jclouds.aws.ec2.compute.domain.EC2HardwareBuilder.m2_4xlarge;
import static org.jclouds.aws.ec2.compute.domain.EC2HardwareBuilder.m2_xlarge;
import static org.jclouds.aws.ec2.compute.domain.EC2HardwareBuilder.t1_micro;
import static org.jclouds.aws.ec2.reference.EC2Constants.PROPERTY_EC2_CC_AMIs;
import static org.jclouds.compute.predicates.ImagePredicates.any;

import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.logging.Logger;
import org.jclouds.rest.annotations.Provider;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
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
   private final String providerName;

   @Inject
   EC2HardwareSupplier(Supplier<Set<? extends Location>> locations, @Provider String providerName,
         @Named(PROPERTY_EC2_CC_AMIs) String[] ccAmis) {
      this.locations = locations;
      this.ccAmis = ccAmis;
      this.providerName = providerName;
   }

   @Override
   public Set<? extends Hardware> get() {
      Set<Hardware> sizes = newLinkedHashSet();
      for (String ccAmi : ccAmis) {
         final String region = ccAmi.split("/")[0];
         Location location = find(locations.get(), new Predicate<Location>() {

            @Override
            public boolean apply(Location input) {
               return input.getScope() == LocationScope.REGION && input.getId().equals(region);
            }

         });
         sizes.add(cc1_4xlarge().location(location).supportsImageIds(ccAmi).build());
      }
      sizes.addAll(ImmutableSet.<Hardware> of(t1_micro().build(), c1_medium().build(), c1_xlarge().build(), m1_large()
            .build(), "nova".equals(providerName) ? m1_small().supportsImage(any()).build() : m1_small().build(),
            m1_xlarge().build(), m2_xlarge().build(), m2_2xlarge().build(), m2_4xlarge().build()));
      return sizes;
   }
}