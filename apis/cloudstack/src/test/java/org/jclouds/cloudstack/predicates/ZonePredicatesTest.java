/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
         Zone.builder().id("41").networkType(NetworkType.ADVANCED).build()
      ));
      assertFalse(supportsAdvancedNetworks().apply(
         Zone.builder().id("42").networkType(NetworkType.BASIC).build()
      ));
   }

   @Test
   public void testSupportsSecurityGroups() {
      assertTrue(supportsSecurityGroups().apply(
         Zone.builder().id("43").securityGroupsEnabled(true).build()
      ));
      assertFalse(supportsSecurityGroups().apply(
         Zone.builder().id("44").securityGroupsEnabled(false).build()
      ));
   }

}
