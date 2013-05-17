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
package org.jclouds.openstack.nova.v2_0.extensions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;

import javax.ws.rs.core.MediaType;

import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.v2_0.domain.HostAggregate;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaApiExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * Tests HostAggregateApi guice wiring and parsing
 *
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "HostAggregateApiExpectTest")
public class HostAggregateApiExpectTest extends BaseNovaApiExpectTest {
   private DateService dateService = new SimpleDateFormatDateService();

   public void testList() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-aggregates");
      HostAggregateApi api = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/host_aggregate_list.json")).build())
            .getHostAggregateExtensionForZone("az-1.region-a.geo-1").get();

      HostAggregate result = Iterables.getOnlyElement(api.list());
      assertEquals(result, exampleHostAggregate());
   }

   public void testGet() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-aggregates/1");
      HostAggregateApi api = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/host_aggregate_with_host_details.json")).build())
            .getHostAggregateExtensionForZone("az-1.region-a.geo-1").get();

      assertEquals(api.get("1"), exampleHostAggregateWithHost());
   }

   public void testGetFailNotFound() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-aggregates/1");
      HostAggregateApi api = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(404).build()).getHostAggregateExtensionForZone("az-1.region-a.geo-1").get();

      assertNull(api.get("1"));
   }

   public void testCreateAggregate() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-aggregates");
      HostAggregateApi api = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            authenticatedGET().endpoint(endpoint).method("POST")
                  .payload(payloadFromStringWithContentType("{\"aggregate\":{\"name\":\"ubuntu1\",\"availability_zone\":\"nova\"}}", MediaType.APPLICATION_JSON))
                  .endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/host_aggregate_details.json")).build())
            .getHostAggregateExtensionForZone("az-1.region-a.geo-1").get();

      assertEquals(api.createInAvailabilityZone("ubuntu1", "nova"), exampleHostAggregate());
   }

   public void testDeleteAggregate() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-aggregates/1");
      HostAggregateApi api = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            authenticatedGET().endpoint(endpoint).method("DELETE").build(),
            HttpResponse.builder().statusCode(200).build()).getHostAggregateExtensionForZone("az-1.region-a.geo-1").get();

      assertTrue(api.delete("1"));
   }

   public void testDeleteAggregateFailNotFound() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-aggregates/1");
      HostAggregateApi api = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            authenticatedGET().endpoint(endpoint).method("DELETE").build(),
            HttpResponse.builder().statusCode(404).build()).getHostAggregateExtensionForZone("az-1.region-a.geo-1").get();

      assertFalse(api.delete("1"));
   }

   public void testUpdateName() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-aggregates/1");
      HostAggregateApi api = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            authenticatedGET().endpoint(endpoint).method("POST")
                  .payload(payloadFromStringWithContentType("{\"aggregate\":{\"name\":\"newaggregatename\"}}", MediaType.APPLICATION_JSON)).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/host_aggregate_details.json")).build()).getHostAggregateExtensionForZone("az-1.region-a.geo-1").get();

      assertEquals(api.updateName("1", "newaggregatename"), exampleHostAggregate());
   }

   public void testUpdateAvailabilityZone() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-aggregates/1");
      HostAggregateApi api = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            authenticatedGET().endpoint(endpoint).method("POST")
                  .payload(payloadFromStringWithContentType("{\"aggregate\":{\"availability_zone\":\"zone1\"}}", MediaType.APPLICATION_JSON)).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/host_aggregate_details.json")).build()).getHostAggregateExtensionForZone("az-1.region-a.geo-1").get();

      assertEquals(api.updateAvailabilityZone("1", "zone1"), exampleHostAggregate());
   }

   public void testAddHost() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-aggregates/1/action");
      HostAggregateApi api = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            authenticatedGET().endpoint(endpoint).method("POST")
                  .payload(payloadFromStringWithContentType("{\"add_host\":{\"host\":\"ubuntu\"}}", MediaType.APPLICATION_JSON)).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/host_aggregate_details.json")).build()).getHostAggregateExtensionForZone("az-1.region-a.geo-1").get();

      assertEquals(api.addHost("1", "ubuntu"), exampleHostAggregate());
   }

   public void testRemoveHost() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-aggregates/1/action");
      HostAggregateApi api = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            authenticatedGET().endpoint(endpoint).method("POST")
                  .payload(payloadFromStringWithContentType("{\"remove_host\":{\"host\":\"ubuntu\"}}", MediaType.APPLICATION_JSON)).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/host_aggregate_details.json")).build()).getHostAggregateExtensionForZone("az-1.region-a.geo-1").get();

      assertEquals(api.removeHost("1", "ubuntu"), exampleHostAggregate());
   }


   public void testSetMetadata() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-aggregates/1/action");
      HostAggregateApi api = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            authenticatedGET().endpoint(endpoint).method("POST")
                  .payload(payloadFromStringWithContentType("{\"set_metadata\":{\"metadata\":{\"mykey\":\"some value or other\"}}}", MediaType.APPLICATION_JSON)).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/host_aggregate_details.json")).build()).getHostAggregateExtensionForZone("az-1.region-a.geo-1").get();

      assertEquals(api.setMetadata("1", ImmutableMap.of("mykey", "some value or other")), exampleHostAggregate());
   }

   public HostAggregate exampleHostAggregate() {
      return HostAggregate.builder().name("jclouds-test-a").availabilityZone("nova")
            .created(dateService.iso8601SecondsDateParse("2012-05-11 11:40:17"))
            .updated(dateService.iso8601SecondsDateParse("2012-05-11 11:46:44"))
            .state("created").id("1").metadata(ImmutableMap.of("somekey", "somevalue", "anotherkey", "another val")).build();
   }

   public HostAggregate exampleHostAggregateWithHost() {
      return exampleHostAggregate().toBuilder().hosts("ubuntu").build();
   }
}
