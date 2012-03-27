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
package org.jclouds.cloudstack.predicates;

import static org.jclouds.cloudstack.predicates.ZonePredicates.supportsAdvancedNetworks;
import static org.jclouds.cloudstack.predicates.ZonePredicates.supportsSecurityGroups;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.jclouds.cloudstack.domain.NetworkType;
import org.jclouds.cloudstack.domain.Zone;
import org.testng.annotations.Test;

/**
 * @author Andrei Savu
 */
@Test(groups = "unit")
public class ZonePredicatesTest {

   @Test
   public void testSupportsAdvancedNetworks() {
      assertTrue(supportsAdvancedNetworks().apply(
         Zone.builder().networkType(NetworkType.ADVANCED).build()
      ));
      assertFalse(supportsAdvancedNetworks().apply(
         Zone.builder().networkType(NetworkType.BASIC).build()
      ));
   }

   @Test
   public void testSupportsSecurityGroups() {
      assertTrue(supportsSecurityGroups().apply(
         Zone.builder().securityGroupsEnabled(true).build()
      ));
      assertFalse(supportsSecurityGroups().apply(
         Zone.builder().securityGroupsEnabled(false).build()
      ));
   }

}
