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
package org.jclouds.openstack.nova.v2_0.extensions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.v2_0.domain.SimpleServerUsage;
import org.jclouds.openstack.nova.v2_0.domain.SimpleTenantUsage;
import org.jclouds.openstack.nova.v2_0.extensions.SimpleTenantUsageClient;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaClientExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Tests SimpleTenantUsageClient guice wiring and parsing
 * 
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "SimpleTenantUsageClientExpectTest")
public class SimpleTenantUsageClientExpectTest extends BaseNovaClientExpectTest {
   private DateService dateService = new SimpleDateFormatDateService();

   public void testList() throws Exception {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-simple-tenant-usage");
      SimpleTenantUsageClient client = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            HttpRequest.builder().method("GET").headers(ImmutableMultimap.of("Accept", MediaType.APPLICATION_JSON, "X-Auth-Token", authToken))
            .endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200)
                  .payload(payloadFromResource("/simple_tenant_usages.json")).build())
            .getSimpleTenantUsageExtensionForZone("az-1.region-a.geo-1").get();
      
      Set<SimpleTenantUsage> results = client.listTenantUsages();
      
      SimpleTenantUsage usage = Iterables.getOnlyElement(results);
      assertEquals(usage.getTenantId(), "f8535069c3fb404cb61c873b1a0b4921");
      assertEquals(usage.getTotalHours(), 4.888888888888889e-07);
      assertEquals(usage.getTotalLocalGbUsage(), 1.9555555555555557e-05);
      assertEquals(usage.getTotalMemoryMbUsage(), 0.0015018666666666667);
      assertEquals(usage.getTotalVcpusUsage(), 7.333333333333333e-07);
      assertEquals(usage.getStart(), dateService.iso8601DateParse("2012-04-18 12:18:39.702411"));
      assertEquals(usage.getStop(), dateService.iso8601DateParse("2012-04-18 12:18:39.702499"));
      assertNotNull(usage.getServerUsages());
      assertTrue(usage.getServerUsages().isEmpty());
   }

   public void testGet() throws Exception {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-simple-tenant-usage/test-1234");
      SimpleTenantUsageClient client = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            HttpRequest.builder().method("GET").headers(ImmutableMultimap.of("Accept", MediaType.APPLICATION_JSON, "X-Auth-Token", authToken))
                  .endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200)
                  .payload(payloadFromResource("/simple_tenant_usage.json")).build())
            .getSimpleTenantUsageExtensionForZone("az-1.region-a.geo-1").get();

      SimpleTenantUsage usage = client.getTenantUsage("test-1234");
      assertEquals(usage.getTenantId(), "f8535069c3fb404cb61c873b1a0b4921");
      
      SimpleTenantUsage expected = SimpleTenantUsage.builder().tenantId("f8535069c3fb404cb61c873b1a0b4921").totalHours(4.833333333333333E-7).totalLocalGbUsage(1.933333333333333E-05)
            .start(dateService.iso8601DateParse("2012-04-18 13:32:07.255743")).stop(dateService.iso8601DateParse("2012-04-18 13:32:07.255743"))
            .totalMemoryMbUsage(0.0014847999999999999).totalVcpusUsage(7.249999999999999E-07).serverUsages(
            ImmutableSet.of(
                  SimpleServerUsage.builder().hours(2.4166666666666665e-07).uptime(91149).flavorLocalGb(50).instanceName("test1").tenantId("f8535069c3fb404cb61c873b1a0b4921").flavorVcpus(2).flavorMemoryMb(4096).instanceStatus(SimpleServerUsage.Status.ACTIVE).flavorName("m1.medium").instanceCreated(this.dateService.iso8601SecondsDateParse("2012-04-17T12:12:58")).build(),
                  SimpleServerUsage.builder().hours(2.4166666666666665e-07).uptime(84710).flavorLocalGb(30).instanceName("mish_test").tenantId("f8535069c3fb404cb61c873b1a0b4921").flavorVcpus(1).flavorMemoryMb(2048).instanceStatus(SimpleServerUsage.Status.ACTIVE).flavorName("m1.small").instanceCreated(this.dateService.iso8601SecondsDateParse("2012-04-17T14:00:17")).build()
            )).build();
      
      assertEquals(usage, expected);
   }

}
