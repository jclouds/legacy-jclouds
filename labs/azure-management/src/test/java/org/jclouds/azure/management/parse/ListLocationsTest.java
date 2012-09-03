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
package org.jclouds.azure.management.parse;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.util.Set;

import org.jclouds.azure.management.domain.Location;
import org.jclouds.azure.management.xml.ListLocationsHandler;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "LocationsTest")
public class ListLocationsTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/locations.xml");

      Set<Location> expected = expected();

      ListLocationsHandler handler = injector.getInstance(ListLocationsHandler.class);
      Set<Location> result = factory.create(handler).parse(is);

      assertEquals(result.toString(), expected.toString());

   }

   public Set<Location> expected() {
      Set<String> availableServices = ImmutableSet.of("Compute", "Storage","PersistentVMRole");
      return ImmutableSet.<Location>builder()
                         .add(Location.builder()
                                      .name("West US")
                                      .displayName("West US")
                                      .availableServices(availableServices)
                                      .build())
                         .add(Location.builder()
                                      .name("East US")
                                      .displayName("East US")
                                      .availableServices(availableServices)
                                      .build())
                         .add(Location.builder()
                                      .name("East Asia")
                                      .displayName("East Asia")
                                      .availableServices(availableServices)
                                      .build())
                         .add(Location.builder()
                                      .name("Southeast Asia")
                                      .displayName("Southeast Asia")
                                      .availableServices(availableServices)
                                      .build())
                         .add(Location.builder()
                                      .name("North Europe")
                                      .displayName("North Europe")
                                      .availableServices(availableServices)
                                      .build())
                         .add(Location.builder()
                                      .name("West Europe")
                                      .displayName("West Europe")
                                      .availableServices(availableServices)
                                      .build()).build();
   }

}
