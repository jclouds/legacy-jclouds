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
   public void setup() {
      super.setup();
      testDomain =  identity.toLowerCase() + "-domain.jclouds.org";
      domainApi = api.getDomainApi();
      domainCounter = retry(new Predicate<Integer>() {
         public boolean apply(Integer value) {
            return domainApi.list().size() == value.intValue();
         }
      }, 30, 1, SECONDS);
      recordCounter = retry(new Predicate<Integer>() {
         public boolean apply(Integer value) {
            return domainApi.listRecords(testDomain).size() == value.intValue();
         }
      }, 30, 1, SECONDS);

      try {
         domainApi.delete(testDomain);
      } catch (Exception ex) {
      }
      
      createDomain(testDomain);
   }

   @AfterClass(groups = { "integration", "live" })
   public void tearDown() {
      int before = domainApi.list().size();
      domainApi.delete(testDomain);
      assertTrue(domainCounter.apply(before - 1));
   
      super.tearDown();
   }

   private DomainApi domainApi;
   private Predicate<Integer> domainCounter;
   private Predicate<Integer> recordCounter;

   @Test
   public void testGetDomain() throws Exception {
      Domain domain = domainApi.get(testDomain);
      assertNotNull(domain);
      assertEquals(domain.getName(), testDomain);
      assertNotNull(domain.getCreateTime());
   }
   
   @Test
   public void testUpdateDomain() throws Exception {
      domainApi.update(testDomain, DomainOptions.Builder.responsiblePerson("another-tester.jclouds.org."));
      Domain domain = domainApi.get(testDomain);
      assertEquals(domain.getResponsiblePerson(), "another-tester.jclouds.org.");
   }

   @Test
   public void testCreateRecord() throws Exception {
      int before = domainApi.listRecords(testDomain).size();

      domainApi.createRecord(testDomain, "test", "A", "127.0.0.1");

      assertTrue(recordCounter.apply(before + 1));

      for(DomainRecord record : domainApi.listRecords(testDomain)) {
         if ("test".equals(record.getHost())) {
            assertEquals(record.getType(), "A");
            assertEquals(record.getData(), "127.0.0.1");
         }
      }
   }

   @Test
   public void testUpdateRecord() throws Exception {
      int before = domainApi.listRecords(testDomain).size();

      domainApi.createRecord(testDomain, "testeditbefore", "A", "127.0.0.1");

      assertTrue(recordCounter.apply(before + 1));

      String recordId = null;
      for(DomainRecord record : domainApi.listRecords(testDomain)) {
         if ("testeditbefore".equals(record.getHost())) {
            assertEquals(record.getType(), "A");
            assertEquals(record.getData(), "127.0.0.1");
            recordId = record.getId();
         }
      }

      assertNotNull(recordId);

      domainApi.updateRecord(recordId, UpdateRecordOptions.Builder.host("testeditafter"));

      boolean found = false;
      for(DomainRecord record : domainApi.listRecords(testDomain)) {
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
      Set<DomainRecord> domainRecords = domainApi.listRecords(testDomain);

      int before = domainRecords.size();
      
      domainApi.deleteRecord(domainRecords.iterator().next().getId());

      assertTrue(recordCounter.apply(before - 1));
   }
}
