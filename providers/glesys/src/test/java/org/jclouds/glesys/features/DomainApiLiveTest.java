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

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.glesys.domain.Domain;
import org.jclouds.glesys.domain.DomainRecord;
import org.jclouds.glesys.internal.BaseGleSYSApiLiveTest;
import org.jclouds.glesys.options.DomainOptions;
import org.jclouds.glesys.options.UpdateRecordOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;

/**
 * Tests behavior of {@code DomainApi}
 *
 * @author Adam Lowe
 */
@Test(groups = "live", testName = "DomainApiLiveTest", singleThreaded = true)
public class DomainApiLiveTest extends BaseGleSYSApiLiveTest {
   public String testDomain;

   @BeforeClass(groups = { "integration", "live" })
   public void setupContext() {
      super.setupContext();
      testDomain =  identity.toLowerCase() + "-domain.jclouds.org";
      api = gleContext.getApi().getDomainApi();
      domainCounter = retry(new Predicate<Integer>() {
         public boolean apply(Integer value) {
            return api.list().size() == value;
         }
      }, 30, 1, SECONDS);
      recordCounter = retry(new Predicate<Integer>() {
         public boolean apply(Integer value) {
            return api.listRecords(testDomain).size() == value;
         }
      }, 30, 1, SECONDS);

      try {
         api.delete(testDomain);
      } catch (Exception ex) {
      }
      
      createDomain(testDomain);
   }

   @AfterClass(groups = { "integration", "live" })
   public void tearDownContext() {
      int before = api.list().size();
      api.delete(testDomain);
      assertTrue(domainCounter.apply(before - 1));
   
      super.tearDownContext();
   }

   private DomainApi api;
   private Predicate<Integer> domainCounter;
   private Predicate<Integer> recordCounter;

   @Test
   public void testGetDomain() throws Exception {
      Domain domain = api.get(testDomain);
      assertNotNull(domain);
      assertEquals(domain.getName(), testDomain);
      assertNotNull(domain.getCreateTime());
   }
   
   @Test
   public void testUpdateDomain() throws Exception {
      api.update(testDomain, DomainOptions.Builder.responsiblePerson("another-tester.jclouds.org."));
      Domain domain = api.get(testDomain);
      assertEquals(domain.getResponsiblePerson(), "another-tester.jclouds.org.");
   }

   @Test
   public void testCreateRecord() throws Exception {
      int before = api.listRecords(testDomain).size();

      api.createRecord(testDomain, "test", "A", "127.0.0.1");

      assertTrue(recordCounter.apply(before + 1));

      for(DomainRecord record : api.listRecords(testDomain)) {
         if ("test".equals(record.getHost())) {
            assertEquals(record.getType(), "A");
            assertEquals(record.getData(), "127.0.0.1");
         }
      }
   }

   @Test
   public void testUpdateRecord() throws Exception {
      int before = api.listRecords(testDomain).size();

      api.createRecord(testDomain, "testeditbefore", "A", "127.0.0.1");

      assertTrue(recordCounter.apply(before + 1));

      String recordId = null;
      for(DomainRecord record : api.listRecords(testDomain)) {
         if ("testeditbefore".equals(record.getHost())) {
            assertEquals(record.getType(), "A");
            assertEquals(record.getData(), "127.0.0.1");
            recordId = record.getId();
         }
      }

      assertNotNull(recordId);

      api.updateRecord(recordId, UpdateRecordOptions.Builder.host("testeditafter"));

      boolean found = false;
      for(DomainRecord record : api.listRecords(testDomain)) {
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
      Set<DomainRecord> domainRecords = api.listRecords(testDomain);

      int before = domainRecords.size();
      
      api.deleteRecord(domainRecords.iterator().next().getId());

      assertTrue(recordCounter.apply(before - 1));
   }
}
