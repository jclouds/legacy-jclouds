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

import static org.jclouds.ultradns.ws.domain.TrafficControllerPoolRecord.createCNAME;
import static org.jclouds.ultradns.ws.predicates.TrafficControllerPoolPredicates.idEqualTo;
import static org.jclouds.ultradns.ws.predicates.TrafficControllerPoolPredicates.recordIdEqualTo;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.jclouds.ultradns.ws.domain.TrafficControllerPool;
import org.jclouds.ultradns.ws.domain.TrafficControllerPoolRecordDetail;
import org.jclouds.ultradns.ws.domain.TrafficControllerPoolRecordDetail.Status;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "TrafficControllerPoolPredicatesTest")
public class TrafficControllerPoolPredicatesTest {
   TrafficControllerPool pool = TrafficControllerPool.builder()
                                                     .zoneId("0000000000000001")
                                                     .id("000000000000002")
                                                     .name("us-west-1c.discovery.jclouds.org.")
                                                     .dname("us-west-1c.discovery.jclouds.org.")
                                                     .statusCode(1)
                                                     .failOverEnabled(true)
                                                     .probingEnabled(true).build();

   @Test
   public void testIdEqualToWhenEqual() {
      assertTrue(idEqualTo("000000000000002").apply(pool));
   }

   @Test
   public void testIdEqualToWhenNotEqual() {
      assertFalse(idEqualTo("000000000000003").apply(pool));
   }

   TrafficControllerPoolRecordDetail record = TrafficControllerPoolRecordDetail.builder()
                                                                   .id("0000000000000001")
                                                                   .poolId("0000000000000001")
                                                                   .record(createCNAME("canary.jclouds.org."))
                                                                   .weight(2)
                                                                   .priority(2)
                                                                   .forceAnswer("Normal")
                                                                   .probingEnabled(true)
                                                                   .status(Status.OK)
                                                                   .serving(true)
                                                                   .description("canary app").build();

   @Test
   public void testRecordIdEqualToWhenEqual() {
      assertTrue(recordIdEqualTo("0000000000000001").apply(record));
   }

   @Test
   public void testRecordIdEqualToWhenNotEqual() {
      assertFalse(recordIdEqualTo("0000000000000002").apply(record));
   }
}
