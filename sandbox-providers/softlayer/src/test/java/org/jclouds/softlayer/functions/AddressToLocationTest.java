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
package org.jclouds.softlayer.functions;

import org.jclouds.domain.Location;
import org.jclouds.softlayer.domain.Address;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Set;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

/**
 * Tests {@code AddressToLocation}
 *
 * @author Jason King
 */
@Test(sequential = true,groups = "unit")
public class AddressToLocationTest {

   private AccountToLocation function;

   @BeforeMethod
   public void setup() {
        function = new AccountToLocation();
   }

   @Test
   public void testAddressToLocation() {
      Address address = Address.builder().id(1)
                                         .country("US")
                                         .state("TX")
                                         .description("This is Texas!").build();
      Location location = function.apply(address);

      assertEquals(location.getId(), Long.toString(address.getId()));
      Set<String> iso3166Codes = location.getIso3166Codes();
      assertEquals(iso3166Codes.size(),1);
      assertTrue(iso3166Codes.contains("US-TX"));
   }

   @Test
   public void testGetIso3166CodeNoCountryAndState() {
      Address address = Address.builder().id(1)
                                         .description("Nowhere").build();
      Location location = function.apply(address);

      assertEquals(location.getId(), Long.toString(address.getId()));
      Set<String> iso3166Codes = location.getIso3166Codes();
      assertEquals(iso3166Codes.size(),1);
      assertTrue(iso3166Codes.contains("null-null"));
   }
}
