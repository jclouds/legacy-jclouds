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
package org.jclouds.rackspace.clouddns.v1.features;

import static org.jclouds.rackspace.clouddns.v1.predicates.JobPredicates.awaitComplete;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import org.jclouds.rackspace.clouddns.v1.domain.CreateDomain;
import org.jclouds.rackspace.clouddns.v1.domain.CreateSubdomain;
import org.jclouds.rackspace.clouddns.v1.domain.Domain;
import org.jclouds.rackspace.clouddns.v1.domain.Record;
import org.jclouds.rackspace.clouddns.v1.domain.RecordDetail;
import org.jclouds.rackspace.clouddns.v1.domain.Subdomain;
import org.jclouds.rackspace.clouddns.v1.domain.UpdateDomain;
import org.jclouds.rackspace.clouddns.v1.functions.DomainFunctions;
import org.jclouds.rackspace.clouddns.v1.internal.BaseCloudDNSApiLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author Everett Toews
 */
@Test(groups = "live", singleThreaded = true, testName = "DomainApiLiveTest")
public class DomainApiLiveTest extends BaseCloudDNSApiLiveTest {

   // just in case the username has a '.' we replace it to avoid creating subdomains
   private static final String JCLOUDS_EXAMPLE = System.getProperty("user.name").replace('.', '-') + "-domaintest-jclouds.org";
   
   private Map<String, Domain> testDomains;

   @Test
   public void testCreateDomainsWithSubdomainsAndRecords() throws Exception {
      Record createMXRecord = Record.builder()
            .type("MX")
            .name(JCLOUDS_EXAMPLE)
            .data("mail." + JCLOUDS_EXAMPLE)
            .priority(11235)
            .comment("MX Record")
            .ttl(60000)
            .build();
      
      Record createARecord = Record.builder()
            .type("A")
            .name(JCLOUDS_EXAMPLE)
            .data("10.0.0.1")
            .comment("A Record")
            .ttl(60000)
            .build();
      
      List<Record> createRecords = ImmutableList.of(createMXRecord, createARecord);
      
      CreateSubdomain createSubdomain1 = CreateSubdomain.builder()
            .name("dev." + JCLOUDS_EXAMPLE)
            .email("jclouds@" + JCLOUDS_EXAMPLE)
            .comment("Hello dev subdomain")
            .build();
      
      CreateSubdomain createSubdomain2 = CreateSubdomain.builder()
            .name("test." + JCLOUDS_EXAMPLE)
            .email("jclouds@" + JCLOUDS_EXAMPLE)
            .comment("Hello test subdomain")
            .build();
      
      List<CreateSubdomain> createSubdomains = ImmutableList.of(createSubdomain1, createSubdomain2);

      CreateDomain createDomain1 = CreateDomain.builder()
            .name(JCLOUDS_EXAMPLE)
            .email("jclouds1@" + JCLOUDS_EXAMPLE)
            .ttl(600001)
            .comment("Hello Domain 1")
            .subdomains(createSubdomains)
            .records(createRecords)
            .build();

      CreateDomain createDomain2 = CreateDomain.builder()
            .name("alt-" + JCLOUDS_EXAMPLE)
            .email("jclouds2@" + JCLOUDS_EXAMPLE)
            .ttl(600002)
            .comment("Hello Domain 2")
            .build();

      Iterable<CreateDomain> createDomains = ImmutableList.of(createDomain1, createDomain2);      
      testDomains = DomainFunctions.toDomainMap(awaitComplete(api, api.getDomainApi().create(createDomains)));

      assertEquals(testDomains.size(), 2);

      Domain jclouds = testDomains.get(JCLOUDS_EXAMPLE);
      Domain altjclouds = testDomains.get("alt-" + JCLOUDS_EXAMPLE);
      
      Thread.sleep(1000);
      Date now = new Date();

      assertTrue(jclouds.getId() > 0);
      assertTrue(jclouds.getAccountId() > 0);
      assertEquals(jclouds.getName(), JCLOUDS_EXAMPLE);
      assertEquals(jclouds.getEmail(), "jclouds1@" + JCLOUDS_EXAMPLE);
      assertEquals(jclouds.getComment().get(), "Hello Domain 1");
      assertEquals(jclouds.getTTL(), 600001);
      assertTrue(jclouds.getCreated().before(now));
      assertTrue(jclouds.getUpdated().before(now));
      
      assertEquals(jclouds.getSubdomains().size(), 2);
      
      Subdomain devjclouds = null;
      Subdomain testjclouds = null;
      
      for (Subdomain subdomain: jclouds.getSubdomains()) {
         if (subdomain.getName().equals("dev." + JCLOUDS_EXAMPLE)) {
            devjclouds = subdomain;
         } else if (subdomain.getName().equals("test." + JCLOUDS_EXAMPLE)) {
            testjclouds = subdomain;
         }
      }
      
      assertTrue(devjclouds.getId() > 0);
      assertEquals(devjclouds.getName(), "dev." + JCLOUDS_EXAMPLE);
      assertEquals(devjclouds.getEmail(), "jclouds@" + JCLOUDS_EXAMPLE);
      assertEquals(devjclouds.getComment().get(), "Hello dev subdomain");
      assertTrue(devjclouds.getCreated().before(now));
      assertTrue(devjclouds.getUpdated().before(now));
      
      assertTrue(testjclouds.getId() > 0);
      assertEquals(testjclouds.getName(), "test." + JCLOUDS_EXAMPLE);
      assertEquals(testjclouds.getEmail(), "jclouds@" + JCLOUDS_EXAMPLE);
      assertEquals(testjclouds.getComment().get(), "Hello test subdomain");
      assertTrue(testjclouds.getCreated().before(now));
      assertTrue(testjclouds.getUpdated().before(now));
      
      assertEquals(jclouds.getRecords().size(), 2);
      
      RecordDetail mxRecord = null;
      RecordDetail aRecord = null;
      
      for (RecordDetail record: jclouds.getRecords()) {
         if (record.getType().equals("MX")) {
            mxRecord = record;
         } else if (record.getType().equals("A")) {
            aRecord = record;
         }
      }
      
      assertNotNull(mxRecord.getId());
      assertEquals(mxRecord.getType(), "MX");
      assertEquals(mxRecord.getName(), JCLOUDS_EXAMPLE);
      assertEquals(mxRecord.getPriority().intValue(), 11235);
      assertEquals(mxRecord.getComment(), "MX Record");
      assertEquals(mxRecord.getTTL(), 60000);
      assertTrue(mxRecord.getCreated().before(now));
      assertTrue(mxRecord.getUpdated().before(now));
      
      assertNotNull(aRecord.getId());
      assertEquals(aRecord.getType(), "A");
      assertEquals(aRecord.getName(), JCLOUDS_EXAMPLE);
      assertNull(aRecord.getPriority());
      assertEquals(aRecord.getComment(), "A Record");
      assertEquals(aRecord.getTTL(), 60000);
      assertTrue(aRecord.getCreated().before(now));
      assertTrue(aRecord.getUpdated().before(now));
      
      assertTrue(altjclouds.getId() > 0);
      assertTrue(altjclouds.getAccountId() > 0);
      assertEquals(altjclouds.getName(), "alt-" + JCLOUDS_EXAMPLE);
      assertEquals(altjclouds.getEmail(), "jclouds2@" + JCLOUDS_EXAMPLE);
      assertEquals(altjclouds.getComment().get(), "Hello Domain 2");
      assertEquals(altjclouds.getTTL(), 600002);
      assertTrue(altjclouds.getCreated().before(now));
      assertTrue(altjclouds.getUpdated().before(now));
   }   

   @Test(dependsOnMethods = "testCreateDomainsWithSubdomainsAndRecords")
   public void testCreateSimpleDomain() throws Exception {
      CreateDomain createDomain = CreateDomain.builder()
            .name("simple-" + JCLOUDS_EXAMPLE)
            .email("simple-jclouds@" + JCLOUDS_EXAMPLE)
            .build();

      Iterable<CreateDomain> createDomains = ImmutableList.of(createDomain);      
      Domain domain = awaitComplete(api, api.getDomainApi().create(createDomains)).iterator().next();
      
      assertEquals(domain.getName(), "simple-" + JCLOUDS_EXAMPLE);
      assertEquals(domain.getEmail(), "simple-jclouds@" + JCLOUDS_EXAMPLE);
      
      testDomains = Maps.newHashMap(testDomains);
      testDomains.put(domain.getName(), domain);
   }   

   @Test(dependsOnMethods = "testCreateSimpleDomain")
   public void testListDomains() throws Exception {
      Set<Domain> domains = api.getDomainApi().list().concat().toSet();
      assertEquals(domains.size(), 5);
   }

   @Test(dependsOnMethods = "testListDomains")
   public void testListDomainsWithFilter() throws Exception {
      Set<Domain> domains = api.getDomainApi().listWithFilterByNamesMatching("alt-" + JCLOUDS_EXAMPLE).concat().toSet();
      assertEquals(domains.size(), 1);
   }

   @Test(dependsOnMethods = "testListDomainsWithFilter")
   public void testListSubdomains() throws Exception {
      Domain domain = testDomains.get(JCLOUDS_EXAMPLE);
      Set<Subdomain> subdomains = api.getDomainApi().listSubdomains(domain.getId()).concat().toSet();
      assertEquals(subdomains.size(), 2);
   }

   @Test(dependsOnMethods = "testListSubdomains")
   public void testGetDomain() throws Exception {
      Domain domain = testDomains.get(JCLOUDS_EXAMPLE);
      Domain jclouds = api.getDomainApi().get(domain.getId());

      Thread.sleep(1000);
      Date now = new Date();

      assertTrue(jclouds.getId() > 0);
      assertTrue(jclouds.getAccountId() > 0);
      assertEquals(jclouds.getName(), JCLOUDS_EXAMPLE);
      assertEquals(jclouds.getEmail(), "jclouds1@" + JCLOUDS_EXAMPLE);
      assertEquals(jclouds.getComment().get(), "Hello Domain 1");
      assertEquals(jclouds.getTTL(), 600001);
      assertTrue(jclouds.getCreated().before(now));
      assertTrue(jclouds.getUpdated().before(now));
      
      assertEquals(jclouds.getSubdomains().size(), 2);
      
      Subdomain devjclouds = null;
      Subdomain testjclouds = null;
      
      for (Subdomain subdomain: jclouds.getSubdomains()) {
         if (subdomain.getName().equals("dev." + JCLOUDS_EXAMPLE)) {
            devjclouds = subdomain;
         } else if (subdomain.getName().equals("test." + JCLOUDS_EXAMPLE)) {
            testjclouds = subdomain;
         }
      }
      
      assertTrue(devjclouds.getId() > 0);
      assertEquals(devjclouds.getName(), "dev." + JCLOUDS_EXAMPLE);
      assertEquals(devjclouds.getEmail(), "jclouds@" + JCLOUDS_EXAMPLE);
      assertEquals(devjclouds.getComment().get(), "Hello dev subdomain");
      assertTrue(devjclouds.getCreated().before(now));
      assertTrue(devjclouds.getUpdated().before(now));
      
      assertTrue(testjclouds.getId() > 0);
      assertEquals(testjclouds.getName(), "test." + JCLOUDS_EXAMPLE);
      assertEquals(testjclouds.getEmail(), "jclouds@" + JCLOUDS_EXAMPLE);
      assertEquals(testjclouds.getComment().get(), "Hello test subdomain");
      assertTrue(testjclouds.getCreated().before(now));
      assertTrue(testjclouds.getUpdated().before(now));
      
      assertEquals(jclouds.getRecords().size(), 4); // 2 created above + 2 nameserver (NS) records
      
      RecordDetail mxRecord = null;
      RecordDetail aRecord = null;
      RecordDetail nsRecord = null;
      
      for (RecordDetail record: jclouds.getRecords()) {
         if (record.getType().equals("MX")) {
            mxRecord = record;
         } else if (record.getType().equals("A")) {
            aRecord = record;
         } else if (record.getType().equals("NS")) {
            nsRecord = record; // don't care which one we get
         }
      }
      
      assertNotNull(mxRecord.getId());
      assertEquals(mxRecord.getType(), "MX");
      assertEquals(mxRecord.getName(), JCLOUDS_EXAMPLE);
      assertEquals(mxRecord.getPriority().intValue(), 11235);
      assertEquals(mxRecord.getComment(), "MX Record");
      assertEquals(mxRecord.getTTL(), 60000);
      assertTrue(mxRecord.getCreated().before(now));
      assertTrue(mxRecord.getUpdated().before(now));
      
      assertNotNull(aRecord.getId());
      assertEquals(aRecord.getType(), "A");
      assertEquals(aRecord.getName(), JCLOUDS_EXAMPLE);
      assertNull(aRecord.getPriority());
      assertEquals(aRecord.getComment(), "A Record");
      assertEquals(aRecord.getTTL(), 60000);
      assertTrue(aRecord.getCreated().before(now));
      assertTrue(aRecord.getUpdated().before(now));

      assertNotNull(nsRecord.getId());
      assertEquals(nsRecord.getType(), "NS");
      assertEquals(nsRecord.getName(), JCLOUDS_EXAMPLE);
      assertEquals(nsRecord.getTTL(), 600001);
      assertTrue(nsRecord.getCreated().before(now));
      assertTrue(nsRecord.getUpdated().before(now));
   }
   
   @Test(dependsOnMethods = "testGetDomain")
   public void testUpdateDomain() throws Exception {
      UpdateDomain updateDomain = UpdateDomain.builder()
            .email("jclouds3@" + JCLOUDS_EXAMPLE)
            .ttl(600003)
            .comment("Hello Domain Update 3")
            .build();

      awaitComplete(api, api.getDomainApi().update(testDomains.get(JCLOUDS_EXAMPLE).getId(), updateDomain));
      Domain jclouds = api.getDomainApi().get(testDomains.get(JCLOUDS_EXAMPLE).getId());
      
      assertEquals(jclouds.getEmail(), "jclouds3@" + JCLOUDS_EXAMPLE);
      assertEquals(jclouds.getComment().get(), "Hello Domain Update 3");
      assertEquals(jclouds.getTTL(), 600003);
   }

   @Test(dependsOnMethods = "testUpdateDomain")
   public void testUpdateDomainsTTL() throws Exception {
      List<Integer> ids = ImmutableList.of(
            testDomains.get(JCLOUDS_EXAMPLE).getId(), testDomains.get("alt-" + JCLOUDS_EXAMPLE).getId());
      awaitComplete(api, api.getDomainApi().updateTTL(ids, 1234567));
      
      Domain jclouds = api.getDomainApi().get(testDomains.get(JCLOUDS_EXAMPLE).getId());
      Domain altjclouds = api.getDomainApi().get(testDomains.get("alt-" + JCLOUDS_EXAMPLE).getId());
      
      assertEquals(jclouds.getTTL(), 1234567);
      assertEquals(altjclouds.getTTL(), 1234567);
   }

   @Test(dependsOnMethods = "testUpdateDomainsTTL")
   public void testUpdateDomainsEmail() throws Exception {
      List<Integer> ids = ImmutableList.of(
            testDomains.get(JCLOUDS_EXAMPLE).getId(), testDomains.get("alt-" + JCLOUDS_EXAMPLE).getId());
      awaitComplete(api, api.getDomainApi().updateEmail(ids, "jclouds-up@" + JCLOUDS_EXAMPLE));

      Domain jclouds = api.getDomainApi().get(testDomains.get(JCLOUDS_EXAMPLE).getId());
      Domain altjclouds = api.getDomainApi().get(testDomains.get("alt-" + JCLOUDS_EXAMPLE).getId());

      assertEquals(jclouds.getEmail(), "jclouds-up@" + JCLOUDS_EXAMPLE);
      assertEquals(altjclouds.getEmail(), "jclouds-up@" + JCLOUDS_EXAMPLE);
   }

   @Test(dependsOnMethods = "testUpdateDomainsEmail")
   public void testExportDomain() throws Exception {
      Domain domain = testDomains.get(JCLOUDS_EXAMPLE);
      List<String> domainExport = awaitComplete(api, api.getDomainApi().exportFormat(domain.getId(), Domain.Format.BIND_9)); 
      
      assertTrue(domainExport.get(0).contains(JCLOUDS_EXAMPLE));
   }

   @Test(dependsOnMethods = "testExportDomain")
   public void testImportDomain() throws Exception {
      List<String> contents = ImmutableList.<String> of(
            "imp-" + JCLOUDS_EXAMPLE + ".      3600  IN SOA   ns.rackspace.com. jclouds.imp-" + JCLOUDS_EXAMPLE + ". 1363882703 3600 3600 3600 3600",
            "imp-" + JCLOUDS_EXAMPLE + ".      600   IN A  50.56.174.152"); 

      Domain domain = awaitComplete(api, api.getDomainApi().importFormat(contents, Domain.Format.BIND_9));
      RecordDetail record = domain.getRecords().iterator().next();
      
      assertEquals(domain.getName(), "imp-" + JCLOUDS_EXAMPLE);
      assertEquals(domain.getEmail(), "jclouds@imp-" + JCLOUDS_EXAMPLE);
      assertEquals(record.getType(), "A");
      assertEquals(record.getData(), "50.56.174.152");
      assertEquals(record.getTTL(), 600);
      
      testDomains = Maps.newHashMap(testDomains);
      testDomains.put(domain.getName(), domain);
   }

   @Override
   @AfterClass(groups = { "integration", "live" })
   protected void tearDown() {
      List<Integer> domainIds = Lists.newArrayList();
      
      for (Domain domain: testDomains.values()) {
         domainIds.add(domain.getId());
      }

      try {
         awaitComplete(api, api.getDomainApi().delete(domainIds, true));
      }
      catch (TimeoutException e) {
         e.printStackTrace();
      }
      super.tearDown();
   }
}
