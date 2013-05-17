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
package org.jclouds.ultradns.ws.predicates;

import static org.jclouds.ultradns.ws.predicates.ZonePredicates.typeEqualTo;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.jclouds.ultradns.ws.domain.Zone;
import org.jclouds.ultradns.ws.domain.Zone.DNSSECStatus;
import org.jclouds.ultradns.ws.domain.Zone.Type;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ZonePredicatesTest")
public class ZonePredicatesTest {
   Zone zone = Zone.builder()
                   .name("jclouds.org.")
                   .typeCode(1)
                   .accountId("AAAAAAAAAAAAAAAA")
                   .ownerId("EEEEEEEEEEEEEEEE")
                   .id("0000000000000001")
                   .dnssecStatus(DNSSECStatus.UNSIGNED).build();

   @Test
   public void testTypeEqualsWhenEqual() {
      assertTrue(typeEqualTo(Type.PRIMARY).apply(zone));
   }

   @Test
   public void testTypeEqualsWhenNotEqual() {
      assertFalse(typeEqualTo(Type.SECONDARY).apply(zone));
   }
}
