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

import static javax.ws.rs.HttpMethod.POST;
import static javax.ws.rs.HttpMethod.PUT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SimpleTimeZone;

import javax.ws.rs.core.MediaType;

import org.jclouds.http.HttpResponse;
import org.jclouds.rackspace.clouddns.v1.CloudDNSApi;
import org.jclouds.rackspace.clouddns.v1.domain.CreateDomain;
import org.jclouds.rackspace.clouddns.v1.domain.CreateSubdomain;
import org.jclouds.rackspace.clouddns.v1.domain.Domain;
import org.jclouds.rackspace.clouddns.v1.domain.DomainChange;
import org.jclouds.rackspace.clouddns.v1.domain.Job;
import org.jclouds.rackspace.clouddns.v1.domain.Record;
import org.jclouds.rackspace.clouddns.v1.domain.Subdomain;
import org.jclouds.rackspace.clouddns.v1.domain.UpdateDomain;
import org.jclouds.rackspace.clouddns.v1.functions.DomainFunctions;
import org.jclouds.rackspace.clouddns.v1.internal.BaseCloudDNSApiExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * @author Everett Toews
 */
@Test(groups = "unit")
public class DomainApiExpectTest extends BaseCloudDNSApiExpectTest<CloudDNSApi> {
   private static final String JCLOUDS_EXAMPLE = "jclouds-example.com";

   public void testCreateDomain() {
      URI endpoint = URI.create("https://dns.api.rackspacecloud.com/v1.0/123123/domains");
      DomainApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey, 
            responseWithAccess,
            authenticatedGET()
               .method(POST)
               .payload(payloadFromResource("/domain-create.json"))
               .endpoint(endpoint)
               .build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/domain-create-response.json")).build())
            .getDomainApi();

      Record createMXRecord = Record.builder()
            .type("MX")
            .name(JCLOUDS_EXAMPLE)
            .data("mail." + JCLOUDS_EXAMPLE)
            .priority(11235)
            .build();
      
      Record createARecord = Record.builder()
            .type("A")
            .name(JCLOUDS_EXAMPLE)
            .data("10.0.0.1")
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
            .email("jclouds@" + JCLOUDS_EXAMPLE)
            .ttl(600000)
            .comment("Hello Domain")
            .subdomains(createSubdomains)
            .records(createRecords)
            .build();

      CreateDomain createDomain2 = CreateDomain.builder()
            .name("x" + JCLOUDS_EXAMPLE)
            .email("jclouds@" + JCLOUDS_EXAMPLE)
            .ttl(600000)
            .comment("Hello Domain")
            .build();

      Iterable<CreateDomain> createDomains = ImmutableList.of(createDomain1, createDomain2);
      Job<Set<Domain>> job = api.create(createDomains);
      
      assertEquals(job.getStatus(), Job.Status.COMPLETED);
      assertTrue(job.getResource().isPresent());
      
      Map<String, Domain> domains = DomainFunctions.toDomainMap(job.getResource().get());
      
      assertEquals(domains.get(JCLOUDS_EXAMPLE).getId(), 3650906);
      assertEquals(domains.get(JCLOUDS_EXAMPLE).getEmail(), "jclouds@jclouds-example.com");
      assertEquals(domains.get(JCLOUDS_EXAMPLE).getSubdomains().size(), 2);
      assertEquals(domains.get(JCLOUDS_EXAMPLE).getRecords().size(), 2);
      assertEquals(domains.get("x" + JCLOUDS_EXAMPLE).getId(), 3650909);
   }

   public void testListDomains() {
      URI endpoint = URI.create("https://dns.api.rackspacecloud.com/v1.0/123123/domains");
      DomainApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey, 
            responseWithAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/domain-list.json")).build())
            .getDomainApi();

      ImmutableList<Domain> domains = api.list().concat().toList();
      assertEquals(domains.size(), 4);
      
      for (Domain domain: domains) {
         assertTrue(domain.getName().contains(JCLOUDS_EXAMPLE));
      }
   }

   public void testListDomainsPaginated() {
      URI endpointPage1 = URI.create("https://dns.api.rackspacecloud.com/v1.0/123123/domains");
      URI endpointPage2 = URI.create("https://dns.api.rackspacecloud.com/v1.0/123123/domains?limit=4&offset=4");
      DomainApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey, 
            responseWithAccess,
            authenticatedGET().endpoint(endpointPage1).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/domain-list-page1.json")).build(),
            authenticatedGET().endpoint(endpointPage2).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/domain-list-page2.json")).build())
            .getDomainApi();

      ImmutableList<Domain> domains = api.list().concat().toList();
      assertEquals(domains.size(), 8);
      
      for (Domain domain: domains) {
         assertTrue(domain.getName().contains(JCLOUDS_EXAMPLE));
      }
   }

   public void testListSubdomains() {
      URI endpoint = URI.create("https://dns.api.rackspacecloud.com/v1.0/123123/domains/3650908/subdomains");
      DomainApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey, 
            responseWithAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/subdomain-list.json")).build())
            .getDomainApi();

      ImmutableList<Subdomain> subdomains = api.listSubdomains(3650908).concat().toList();
      assertEquals(subdomains.size(), 4);
      
      for (Subdomain subdomain: subdomains) {
         assertTrue(subdomain.getName().contains(JCLOUDS_EXAMPLE));
      }
   }

   public void testListSubdomainsPaginated() {
      URI endpointPage1 = URI.create("https://dns.api.rackspacecloud.com/v1.0/123123/domains/3650908/subdomains");
      URI endpointPage2 = URI.create("https://dns.api.rackspacecloud.com/v1.0/123123/domains/3650908/subdomains?limit=4&offset=4");
      DomainApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey, 
            responseWithAccess,
            authenticatedGET().endpoint(endpointPage1).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/subdomain-list-page1.json")).build(),
            authenticatedGET().endpoint(endpointPage2).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/subdomain-list-page2.json")).build())
            .getDomainApi();

      ImmutableList<Subdomain> subdomains = api.listSubdomains(3650908).concat().toList();
      assertEquals(subdomains.size(), 8);
      
      for (Subdomain subdomain: subdomains) {
         assertTrue(subdomain.getName().contains(JCLOUDS_EXAMPLE));
      }
   }

   public void testListWithFilterByNamesMatching() {
      URI endpoint = URI.create("https://dns.api.rackspacecloud.com/v1.0/123123/domains?name=test.jclouds-example.com");
      DomainApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey, 
            responseWithAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/domain-list-with-filter.json")).build())
            .getDomainApi();

      ImmutableList<Domain> domains = api.listWithFilterByNamesMatching("test." + JCLOUDS_EXAMPLE).concat().toList();
      assertEquals(domains.size(), 1);
      assertEquals(domains.get(0).getId(), 3650908);
      assertEquals(domains.get(0).getName(), "test." + JCLOUDS_EXAMPLE);
   }

   public void testListDomainChanges() {
      URI endpoint = URI.create("https://dns.api.rackspacecloud.com/v1.0/123123/domains/3650908/changes?changes=2013-03-22T03%3A39%3A31Z");
      DomainApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey, 
            responseWithAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/domain-list-changes.json")).build())
            .getDomainApi();

      Calendar cal = Calendar.getInstance(new SimpleTimeZone(0, "GMT"));
      cal.set(2013, 2, 22, 3, 39, 31);
      DomainChange domainChange = api.listChanges(3650908, cal.getTime());
      
      assertEquals(domainChange.getChanges().size(), 25);
   }

   public void testGetDomain() {
      URI endpoint = URI.create("https://dns.api.rackspacecloud.com/v1.0/123123/domains/3650908?showRecords=true&showSubdomains=true");
      DomainApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey, 
            responseWithAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/domain-get.json")).build())
            .getDomainApi();

      Domain domain = api.get(3650908);
      assertEquals(domain.getName(), "test." + JCLOUDS_EXAMPLE);
      assertEquals(domain.getRecords().size(), 2);
      assertEquals(domain.getComment().get(), "Hello test subdomain");
      assertEquals(domain.getTTL(), 3600);
   }
   
   @SuppressWarnings("rawtypes")
   public void testUpdateDomain() {
      URI endpoint = URI.create("https://dns.api.rackspacecloud.com/v1.0/123123/domains/3650908");
      DomainApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey, 
            responseWithAccess,
            authenticatedGET()
               .method(PUT)
               .payload(payloadFromResourceWithContentType("/domain-update.json", MediaType.APPLICATION_JSON))
               .endpoint(endpoint)
               .build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/domain-update-response.json")).build())
            .getDomainApi();

      UpdateDomain updateDomain = UpdateDomain.builder()
            .email("everett@" + JCLOUDS_EXAMPLE)
            .ttl(600001)
            .comment("Hello Domain Update")
            .build();

      Job job = api.update(3650908, updateDomain);
      
      assertEquals(job.getStatus(), Job.Status.COMPLETED);
   }
   
   @SuppressWarnings("rawtypes")
   public void testUpdateDomainsTTL() {
      URI endpoint = URI.create("https://dns.api.rackspacecloud.com/v1.0/123123/domains");
      DomainApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey, 
            responseWithAccess,
            authenticatedGET()
               .method(PUT)
               .payload(payloadFromResource("/domain-update-ttl.json"))
               .endpoint(endpoint)
               .build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/domain-update-response.json")).build())
            .getDomainApi();

      List<Integer> ids = ImmutableList.of(3650906, 3650908);
      Job job = api.updateTTL(ids, 1234567);
      
      assertEquals(job.getStatus(), Job.Status.COMPLETED);
   }
   
   @SuppressWarnings("rawtypes")
   public void testUpdateDomainsEmail() {
      URI endpoint = URI.create("https://dns.api.rackspacecloud.com/v1.0/123123/domains");
      DomainApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey, 
            responseWithAccess,
            authenticatedGET()
               .method(PUT)
               .payload(payloadFromResource("/domain-update-email.json"))
               .endpoint(endpoint)
               .build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/domain-update-response.json")).build())
            .getDomainApi();

      List<Integer> ids = ImmutableList.of(3650906, 3650908);
      Job job = api.updateEmail(ids, "everett@" + JCLOUDS_EXAMPLE);
      
      assertEquals(job.getStatus(), Job.Status.COMPLETED);
   }

   @SuppressWarnings("rawtypes")
   public void testDeleteDomains() {
      URI endpoint = URI.create("https://dns.api.rackspacecloud.com/v1.0/123123/domains?id=3650907&id=3650906&id=3650908&id=3650909&deleteSubdomains=true");
      DomainApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey, 
            responseWithAccess,
            authenticatedGET().method("DELETE").replaceHeader("Accept", MediaType.WILDCARD).endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/domain-delete.json")).build())
            .getDomainApi();

      List<Integer> domainIds = ImmutableList.<Integer> of(3650907, 3650906, 3650908, 3650909);      
      Job job = api.delete(domainIds, true);

      assertEquals(job.getStatus(), Job.Status.COMPLETED);
   }

   public void testExportDomain() {
      URI endpoint = URI.create("https://dns.api.rackspacecloud.com/v1.0/123123/domains/3651323/export");
      DomainApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey, 
            responseWithAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/domain-export.json")).build())
            .getDomainApi();

      Job<List<String>> job = api.exportFormat(3651323, Domain.Format.BIND_9);
      assertEquals(job.getStatus(), Job.Status.COMPLETED);
      assertEquals(job.getResource().get().size(), 5);
   }

   public void testImportDomain() {
      URI endpoint = URI.create("https://dns.api.rackspacecloud.com/v1.0/123123/domains/import");
      DomainApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey, 
            responseWithAccess,
            authenticatedGET()
               .method(POST)
               .payload(payloadFromResource("/domain-import.json"))
               .endpoint(endpoint)
               .build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/domain-import-response.json")).build())
            .getDomainApi();

      List<String> contents = ImmutableList.<String> of(
            "jclouds-example.com.      3600  IN SOA   ns.rackspace.com. jclouds.jclouds-example.com. 1363882703 3600 3600 3600 3600",
            "jclouds-example.com.      600   IN A  50.56.174.152"); 

      Job<Domain> job = api.importFormat(contents, Domain.Format.BIND_9);
      assertEquals(job.getStatus(), Job.Status.COMPLETED);
      assertEquals(job.getResource().get().getName(), JCLOUDS_EXAMPLE);
   }
}
