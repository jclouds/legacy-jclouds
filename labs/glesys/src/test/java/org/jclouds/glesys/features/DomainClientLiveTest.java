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
package org.jclouds.glesys.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.glesys.domain.Domain;
import org.jclouds.glesys.domain.DomainRecord;
import org.jclouds.glesys.internal.BaseGleSYSClientLiveTest;
import org.jclouds.glesys.options.DomainOptions;
import org.jclouds.glesys.options.EditRecordOptions;
import org.jclouds.predicates.RetryablePredicate;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;

/**
 * Tests behavior of {@code DomainClient}
 *
 * @author Adam Lowe
 */
@Test(groups = "live", testName = "DomainClientLiveTest", singleThreaded = true)
public class DomainClientLiveTest extends BaseGleSYSClientLiveTest {
   public final String testDomain = "glesystest.jclouds.org";

   @BeforeGroups(groups = {"live"})
   public void setupContext() {
      super.setupContext();

      client = gleContext.getApi().getDomainClient();
      domainCounter = new RetryablePredicate<Integer>(
            new Predicate<Integer>() {
               public boolean apply(Integer value) {
                  return client.listDomains().size() == value;
               }
            }, 30, 1, TimeUnit.SECONDS);
      recordCounter = new RetryablePredicate<Integer>(
            new Predicate<Integer>() {
               public boolean apply(Integer value) {
                  return client.listRecords(testDomain).size() == value;
               }
            }, 30, 1, TimeUnit.SECONDS);

      try {
         client.deleteDomain(testDomain);
      } catch (Exception ex) {
      }
      
      createDomain(testDomain);
   }

   @AfterGroups(groups = {"live"})
   public void tearDownContext() {
      int before = client.listDomains().size();
      client.deleteDomain(testDomain);
      assertTrue(domainCounter.apply(before - 1));
   
      super.tearDownContext();
   }

   private DomainClient client;
   private RetryablePredicate<Integer> domainCounter;
   private RetryablePredicate<Integer> recordCounter;

   @Test
   public void testEditDomain() throws Exception {
      client.editDomain(testDomain, DomainOptions.Builder.responsiblePerson("tester.jclouds.org"));
      assertTrue(client.listDomains().contains(Domain.builder().domainName(testDomain).build()));
   }

   @Test
   public void testCreateRecord() throws Exception {
      int before = client.listRecords(testDomain).size();

      client.addRecord(testDomain, "test", "A", "127.0.0.1");

      assertTrue(recordCounter.apply(before + 1));

      for(DomainRecord record : client.listRecords(testDomain)) {
         if ("test".equals(record.getHost())) {
            assertEquals(record.getType(), "A");
            assertEquals(record.getData(), "127.0.0.1");
         }
      }
   }

   @Test
   public void testEditRecord() throws Exception {
      int before = client.listRecords(testDomain).size();

      client.addRecord(testDomain, "testeditbefore", "A", "127.0.0.1");

      assertTrue(recordCounter.apply(before + 1));

      String recordId = null;
      for(DomainRecord record : client.listRecords(testDomain)) {
         if ("testeditbefore".equals(record.getHost())) {
            assertEquals(record.getType(), "A");
            assertEquals(record.getData(), "127.0.0.1");
            recordId = record.getId();
         }
      }

      assertNotNull(recordId);

      client.editRecord(recordId, EditRecordOptions.Builder.host("testeditafter"));

      boolean found = false;
      for(DomainRecord record : client.listRecords(testDomain)) {
         if (recordId.equals(record.getId())) {
            assertEquals(record.getHost(), "testeditafter");
            assertEquals(record.getType(), "A");
            assertEquals(record.getData(), "127.0.0.1");
            found = true;
         }
      }
      assertTrue(found);
   }

   @Test
   public void testDeleteRecord() throws Exception {
      Set<DomainRecord> domainRecords = client.listRecords(testDomain);

      int before = domainRecords.size();
      
      client.deleteRecord(domainRecords.iterator().next().getId());

      assertTrue(recordCounter.apply(before - 1));
   }
}