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
package org.jclouds.azure.management.features;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Set;

import org.jclouds.azure.management.domain.Location;
import org.jclouds.azure.management.internal.BaseAzureManagementApiLiveTest;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "LocationApiLiveTest")
public class LocationApiLiveTest extends BaseAzureManagementApiLiveTest {

   @Test
   protected void testList() {
      Set<Location> response = api().list();

      for (Location location : response) {
         checkLocation(location);
      }

   }

   protected Predicate<String> knownServices = Predicates.in(ImmutableSet.of("Compute", "Storage", "PersistentVMRole"));

   private void checkLocation(Location location) {
      checkNotNull(location.getName(), "Name cannot be null for a Location.");
      checkNotNull(location.getDisplayName(), "DisplayName cannot be null for Location %s", location.getName());
      checkNotNull(location.getAvailableServices(), "AvailableServices cannot be null for Location %s",
               location.getName());
      checkState(Iterables.all(location.getAvailableServices(), knownServices),
               "AvailableServices in Location %s didn't match %s: %s", location.getName(), knownServices,
               location.getAvailableServices());
   }

   protected LocationApi api() {
      return context.getApi().getLocationApi();
   }
}
