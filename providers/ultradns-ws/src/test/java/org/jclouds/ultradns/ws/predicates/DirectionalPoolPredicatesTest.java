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

import static org.jclouds.ultradns.ws.domain.IdAndName.create;
import static org.jclouds.ultradns.ws.predicates.DirectionalPoolPredicates.idEqualTo;
import static org.jclouds.ultradns.ws.predicates.DirectionalPoolPredicates.recordIdEqualTo;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.jclouds.ultradns.ws.domain.DirectionalPool;
import org.jclouds.ultradns.ws.domain.DirectionalPool.TieBreak;
import org.jclouds.ultradns.ws.domain.DirectionalPool.Type;
import org.jclouds.ultradns.ws.domain.DirectionalPoolRecord;
import org.jclouds.ultradns.ws.domain.DirectionalPoolRecordDetail;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "DirectionalPoolPredicatesTest")
public class DirectionalPoolPredicatesTest {
   DirectionalPool pool = DirectionalPool.builder()
                                         .zoneId("0000000000000001")
                                         .id("000000000000000A")
                                         .dname("mixy.jclouds.org.")
                                         .type(Type.MIXED)
                                         .tieBreak(TieBreak.GEOLOCATION)
                                         .name("mixy").build();

   @Test
   public void testIdEqualToWhenEqual() {
      assertTrue(idEqualTo("000000000000000A").apply(pool));
   }

   @Test
   public void testIdEqualToWhenNotEqual() {
      assertFalse(idEqualTo("000000000000000B").apply(pool));
   }

   DirectionalPoolRecordDetail record = DirectionalPoolRecordDetail.builder()
      .zoneName("geo.jclouds.org.")
      .name("www.geo.jclouds.org.")
      .id("A000000000000001")
      .geolocationGroup(create("C000000000000001", "southamerica"))
      .record(DirectionalPoolRecord.drBuilder()
                               .type("CNAME")
                               .ttl(300)
                               .noResponseRecord(false)
                               .rdata("southamerica.geo.jclouds.org.").build()).build();
   @Test
   public void testRecordIdEqualToWhenEqual() {
      assertTrue(recordIdEqualTo("A000000000000001").apply(record));
   }

   @Test
   public void testRecordIdEqualToWhenNotEqual() {
      assertFalse(recordIdEqualTo("A000000000000002").apply(record));
   }
}
