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
package org.jclouds.ultradns.ws;

import static java.util.logging.Logger.getAnonymousLogger;
import static org.jclouds.ultradns.ws.domain.ResourceRecord.rrBuilder;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import org.jclouds.ultradns.ws.domain.ResourceRecord;
import org.jclouds.ultradns.ws.domain.ZoneProperties;
import org.jclouds.ultradns.ws.internal.BaseUltraDNSWSApiLiveTest;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "ScopedTransactionLiveTest")
public class ScopedTransactionLiveTest extends BaseUltraDNSWSApiLiveTest {

   ResourceRecord mx = rrBuilder().name("mail." + zoneName)
         .type(15)
         .ttl(1800)
         .infoValue(10)
         .infoValue("maileast.jclouds.org.").build();

   @Test
   public void testMultiStepTransactionOnCommit() {
      String txId = api.getTransactionApi().start();
      assertNotNull(txId);
      getAnonymousLogger().info("starting transaction: " + txId);
      try {
         api.getZoneApi().createInAccount(zoneName, account.getId());
         api.getResourceRecordApiForZone(zoneName).create(mx);

         // can't read uncommitted stuff
         assertNull(api.getZoneApi().get(zoneName));
         
         // commit the tx
         api.getTransactionApi().commit(txId);

         // now we can read it
         ZoneProperties newZone = api.getZoneApi().get(zoneName);
         assertEquals(newZone.getName(), zoneName);
         assertEquals(newZone.getResourceRecordCount(), 6);
      } finally {
         // in case an assertion problem or otherwise occurred in the test.
         api.getTransactionApi().rollback(txId);
         api.getZoneApi().delete(zoneName);
      }
   }

   @Test
   public void testScopedTransactionOnRollback() {
      String txId = api.getTransactionApi().start();
      assertNotNull(txId);
      getAnonymousLogger().info("starting transaction: " + txId);
      try {
         api.getZoneApi().createInAccount(zoneName, account.getId());
         api.getTransactionApi().rollback(txId);
         assertNull(api.getZoneApi().get(zoneName));
      } finally {
         // in case an assertion problem or otherwise occurred in the test.
         api.getZoneApi().delete(zoneName);
      }
   }
}
