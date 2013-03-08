/*
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

package org.jclouds.googlecompute.compute;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.Template;
import org.jclouds.domain.Location;
import org.jclouds.googlecompute.compute.options.GoogleComputeTemplateOptions;
import org.jclouds.googlecompute.domain.Instance;
import org.jclouds.googlecompute.features.InstanceApiExpectTest;
import org.jclouds.googlecompute.internal.BaseGoogleComputeServiceExpectTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.jclouds.googlecompute.GoogleComputeConstants.COMPUTE_READONLY_SCOPE;
import static org.jclouds.googlecompute.GoogleComputeConstants.COMPUTE_SCOPE;
import static org.jclouds.googlecompute.features.FirewallApiExpectTest.GET_FIREWALL_REQUEST;
import static org.jclouds.googlecompute.features.ImageApiExpectTest.LIST_PROJECT_IMAGES_REQUEST;
import static org.jclouds.googlecompute.features.ImageApiExpectTest.LIST_PROJECT_IMAGES_RESPONSE;
import static org.jclouds.googlecompute.features.InstanceApiExpectTest.LIST_INSTANCES_REQUEST;
import static org.jclouds.googlecompute.features.InstanceApiExpectTest.LIST_INSTANCES_RESPONSE;
import static org.jclouds.googlecompute.features.MachineTypeApiExpectTest.LIST_MACHINE_TYPES_REQUEST;
import static org.jclouds.googlecompute.features.MachineTypeApiExpectTest.LIST_MACHINE_TYPES_RESPONSE;
import static org.jclouds.googlecompute.features.NetworkApiExpectTest.GET_NETWORK_REQUEST;
import static org.jclouds.googlecompute.features.OperationApiExpectTest.GET_OPERATION_REQUEST;
import static org.jclouds.googlecompute.features.OperationApiExpectTest.GET_OPERATION_RESPONSE;
import static org.jclouds.googlecompute.features.ZoneApiExpectTest.LIST_ZONES_REQ;
import static org.jclouds.googlecompute.features.ZoneApiExpectTest.LIST_ZONES_RESPONSE;
import static org.jclouds.util.Strings2.toStringAndClose;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;


/**
 * @author David Alves
 */
@Test(groups = "unit")
public class GoogleComputeServiceExpectTest extends BaseGoogleComputeServiceExpectTest {

   public static final HttpRequest LIST_GOOGLE_IMAGES_REQUEST = HttpRequest
           .builder()
           .method("GET")
           .endpoint("https://www.googleapis.com/compute/v1beta13/projects/google/images")
           .addHeader("Accept", "application/json")
           .addHeader("Authorization", "Bearer " + TOKEN).build();

   public static final HttpResponse LIST_GOOGLE_IMAGES_RESPONSE = HttpResponse.builder().statusCode(200)
           .payload(staticPayloadFromResource("/image_list_single_page.json")).build();

   private HttpRequest INSERT_NETWORK_REQUEST = HttpRequest
           .builder()
           .method("POST")
           .endpoint("https://www.googleapis.com/compute/v1beta13/projects/myproject/networks")
           .addHeader("Accept", "application/json")
           .addHeader("Authorization", "Bearer " + TOKEN)
           .payload(payloadFromStringWithContentType("{\"name\":\"jclouds-test\",\"IPv4Range\":\"10.0.0.0/8\"}",
                   MediaType.APPLICATION_JSON))
           .build();

   private HttpRequest INSERT_FIREWALL_REQUEST = HttpRequest
           .builder()
           .method("POST")
           .endpoint("https://www.googleapis.com/compute/v1beta13/projects/myproject/firewalls")
           .addHeader("Accept", "application/json")
           .addHeader("Authorization", "Bearer " + TOKEN)
           .payload(payloadFromStringWithContentType("{\"name\":\"jclouds-test\",\"network\":\"https://www.googleapis" +
                   ".com/compute/v1beta13/projects/myproject/networks/jclouds-test\"," +
                   "\"sourceRanges\":[\"10.0.0.0/8\",\"0.0.0.0/0\"],\"allowed\":[{\"IPProtocol\":\"tcp\"," +
                   "\"ports\":[\"22\"]}," +
                   "{\"IPProtocol\":\"udp\",\"ports\":[\"22\"]}]}",
                   MediaType.APPLICATION_JSON))
           .build();

   private HttpResponse GET_NETWORK_RESPONSE = HttpResponse.builder().statusCode(200)
           .payload(payloadFromStringWithContentType("{\n" +
                   " \"kind\": \"compute#network\",\n" +
                   " \"id\": \"13024414170909937976\",\n" +
                   " \"creationTimestamp\": \"2012-10-24T20:13:19.967\",\n" +
                   " \"selfLink\": \"https://www.googleapis" +
                   ".com/compute/v1beta13/projects/myproject/networks/jclouds-test\",\n" +
                   " \"name\": \"jclouds-test\",\n" +
                   " \"description\": \"test network\",\n" +
                   " \"IPv4Range\": \"10.0.0.0/8\",\n" +
                   " \"gatewayIPv4\": \"10.0.0.1\"\n" +
                   "}", MediaType.APPLICATION_JSON)).build();

   private HttpResponse SUCESSFULL_OPERATION_RESPONSE = HttpResponse.builder().statusCode(200)
           .payload(payloadFromResource("/operation.json")).build();


   private HttpResponse getInstanceResponseForInstanceAndNetworkAndStatus(String instanceName, String networkName,
                                                                          String status) throws
           IOException {
      return HttpResponse.builder().statusCode(200)
              .payload(payloadFromStringWithContentType(
                      replaceInstanceNameNetworkAndStatusOnResource("/instance_get.json",
                              instanceName, networkName, status),
                      "application/json")).build();
   }

   private HttpResponse getListInstancesResponseForSingleInstanceAndNetworkAndStatus(String instanceName,
                                                                                     String networkName,
                                                                                     String status) {
      return HttpResponse.builder().statusCode(200)
              .payload(payloadFromStringWithContentType(
                      replaceInstanceNameNetworkAndStatusOnResource("/instance_list.json",
                              instanceName, networkName, status),
                      "application/json")).build();
   }

   private String replaceInstanceNameNetworkAndStatusOnResource(String resourceName, String instanceName,
                                                                String networkName, String status) {
      try {
         return Strings2.toStringAndClose(this.getClass().getResourceAsStream(resourceName)).replace("test-0",
                 instanceName).replace("default", networkName).replace("RUNNING", status);
      } catch (IOException e) {
         throw Throwables.propagate(e);
      }
   }

   private HttpRequest createInstanceRequestForInstance(String instanceName, String networkName, String publicKey) {
      return HttpRequest
              .builder()
              .method("POST")
              .endpoint("https://www.googleapis.com/compute/v1beta13/projects/myproject/instances")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN)
              .payload(payloadFromStringWithContentType("{\"name\":\"" + instanceName + "\"," +
                      "\"machineType\":\"https://www.googleapis" +
                      ".com/compute/v1beta13/projects/myproject/machineTypes/n1-standard-1\"," +
                      "\"zone\":\"https://www.googleapis" +
                      ".com/compute/v1beta13/projects/myproject/zones/us-central1-a\"," +
                      "\"image\":\"https://www.googleapis" +
                      ".com/compute/v1beta13/projects/google/images/gcel-12-04-v20121106\"," +
                      "\"tags\":[],\"serviceAccounts\":[]," +
                      "\"networkInterfaces\":[{\"network\":\"https://www.googleapis" +
                      ".com/compute/v1beta13/projects/myproject/networks/" + networkName + "\"," +
                      "\"accessConfigs\":[{\"type\":\"ONE_TO_ONE_NAT\"}]}]," +
                      "\"metadata\":{\"kind\":\"compute#metadata\",\"items\":[{\"key\":\"sshKeys\"," +
                      "\"value\":\"jclouds:" +
                      publicKey + " jclouds@localhost\"}]}}",
                      MediaType.APPLICATION_JSON)).build();
   }

   private HttpRequest getInstanceRequestForInstance(String instanceName) {
      return HttpRequest
              .builder()
              .method("GET")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1beta13/projects/myproject/instances/" + instanceName)
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();
   }


   @Override
   protected Properties setupProperties() {
      Properties overrides = super.setupProperties();
      overrides.put("google-compute.identity", "myproject");
      try {
         overrides.put("google-compute.credential", toStringAndClose(getClass().getResourceAsStream("/testpk.pem")));
      } catch (IOException e) {
         Throwables.propagate(e);
      }
      return overrides;
   }

   @Test(enabled = false)
   public void testThrowsAuthorizationException() throws Exception {

      Properties properties = new Properties();
      properties.setProperty("oauth.identity", "MOMMA");
      properties.setProperty("oauth.credential", "MiA");

      ComputeService client = requestsSendResponses(ImmutableMap.<HttpRequest, HttpResponse>of(), createModule(),
              properties);
      Template template = client.templateBuilder().build();
      Template toMatch = client.templateBuilder().imageId(template.getImage().getId()).build();
      assertEquals(toMatch.getImage(), template.getImage());
   }

   @Test
   public void testTemplateMatch() throws Exception {
      ImmutableMap<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.
              <HttpRequest, HttpResponse>builder()
              .put(requestForScopes(COMPUTE_READONLY_SCOPE), TOKEN_RESPONSE)
              .put(LIST_ZONES_REQ, LIST_ZONES_RESPONSE)
              .put(LIST_PROJECT_IMAGES_REQUEST, LIST_PROJECT_IMAGES_RESPONSE)
              .put(LIST_GOOGLE_IMAGES_REQUEST, LIST_GOOGLE_IMAGES_RESPONSE)
              .put(LIST_MACHINE_TYPES_REQUEST, LIST_MACHINE_TYPES_RESPONSE)
              .build();

      ComputeService client = requestsSendResponses(requestResponseMap);
      Template template = client.templateBuilder().build();
      Template toMatch = client.templateBuilder().imageId(template.getImage().getId()).build();
      assertEquals(toMatch.getImage(), template.getImage());
   }

   @Test
   public void testNetworksAndFirewallDeletedWhenAllGroupNodesAreTerminated() throws IOException {

      HttpRequest deleteNodeRequest = HttpRequest.builder()
              .method("DELETE")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1beta13/projects/myproject/instances/test-delete-networks")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpRequest deleteFirewallRequest = HttpRequest.builder()
              .method("DELETE")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1beta13/projects/myproject/firewalls/jclouds-test-delete")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpRequest deleteNetworkReqquest = HttpRequest.builder()
              .method("DELETE")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1beta13/projects/myproject/networks/jclouds-test-delete")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      List<HttpRequest> orderedRequests = ImmutableList.<HttpRequest>builder()
              .add(requestForScopes(COMPUTE_READONLY_SCOPE))
              .add(getInstanceRequestForInstance("test-delete-networks"))
              .add(LIST_PROJECT_IMAGES_REQUEST)
              .add(LIST_GOOGLE_IMAGES_REQUEST)
              .add(LIST_ZONES_REQ)
              .add(LIST_MACHINE_TYPES_REQUEST)
              .add(requestForScopes(COMPUTE_SCOPE))
              .add(deleteNodeRequest)
              .add(GET_OPERATION_REQUEST)
              .add(getInstanceRequestForInstance("test-delete-networks"))
              .add(LIST_PROJECT_IMAGES_REQUEST)
              .add(LIST_GOOGLE_IMAGES_REQUEST)
              .add(LIST_ZONES_REQ)
              .add(LIST_MACHINE_TYPES_REQUEST)
              .add(LIST_INSTANCES_REQUEST)
              .add(LIST_PROJECT_IMAGES_REQUEST)
              .add(LIST_GOOGLE_IMAGES_REQUEST)
              .add(LIST_ZONES_REQ)
              .add(LIST_MACHINE_TYPES_REQUEST)
              .add(deleteFirewallRequest)
              .add(GET_OPERATION_REQUEST)
              .add(deleteNetworkReqquest)
              .add(GET_OPERATION_REQUEST)
              .build();


      List<HttpResponse> orderedResponses = ImmutableList.<HttpResponse>builder()
              .add(TOKEN_RESPONSE)
              .add(getInstanceResponseForInstanceAndNetworkAndStatus("test-delete-networks", "test-network", Instance
                      .Status.RUNNING.name()))
              .add(LIST_PROJECT_IMAGES_RESPONSE)
              .add(LIST_GOOGLE_IMAGES_RESPONSE)
              .add(LIST_ZONES_RESPONSE)
              .add(LIST_MACHINE_TYPES_RESPONSE)
              .add(TOKEN_RESPONSE)
              .add(SUCESSFULL_OPERATION_RESPONSE)
              .add(GET_OPERATION_RESPONSE)
              .add(getInstanceResponseForInstanceAndNetworkAndStatus("test-delete-networks", "test-network", Instance
                      .Status.TERMINATED.name()))
              .add(LIST_PROJECT_IMAGES_RESPONSE)
              .add(LIST_GOOGLE_IMAGES_RESPONSE)
              .add(LIST_ZONES_RESPONSE)
              .add(LIST_MACHINE_TYPES_RESPONSE)
              .add(getListInstancesResponseForSingleInstanceAndNetworkAndStatus("test-delete-networks",
                      "test-network", Instance
                      .Status.TERMINATED.name()))
              .add(LIST_PROJECT_IMAGES_RESPONSE)
              .add(LIST_GOOGLE_IMAGES_RESPONSE)
              .add(LIST_ZONES_RESPONSE)
              .add(LIST_MACHINE_TYPES_RESPONSE)
              .add(SUCESSFULL_OPERATION_RESPONSE)
              .add(GET_OPERATION_RESPONSE)
              .add(SUCESSFULL_OPERATION_RESPONSE)
              .add(GET_OPERATION_RESPONSE)
              .build();

      ComputeService client = orderedRequestsSendResponses(orderedRequests, orderedResponses);
      client.destroyNode("test-delete-networks");

   }

   public void testListLocationsWhenResponseIs2xx() throws Exception {

      ImmutableMap<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.
              <HttpRequest, HttpResponse>builder()
              .put(requestForScopes(COMPUTE_READONLY_SCOPE), TOKEN_RESPONSE)
              .put(LIST_ZONES_REQ, LIST_ZONES_RESPONSE)
              .put(LIST_INSTANCES_REQUEST, LIST_INSTANCES_RESPONSE)
              .put(LIST_PROJECT_IMAGES_REQUEST, LIST_PROJECT_IMAGES_RESPONSE)
              .put(LIST_GOOGLE_IMAGES_REQUEST, LIST_GOOGLE_IMAGES_RESPONSE)
              .put(LIST_MACHINE_TYPES_REQUEST, LIST_MACHINE_TYPES_RESPONSE)
              .build();

      ComputeService apiWhenServersExist = requestsSendResponses(requestResponseMap);

      Set<? extends Location> locations = apiWhenServersExist.listAssignableLocations();

      assertNotNull(locations);
      assertEquals(locations.size(), 2);
      assertEquals(locations.iterator().next().getId(), "us-central1-a");

      assertNotNull(apiWhenServersExist.listNodes());
      assertEquals(apiWhenServersExist.listNodes().size(), 1);
      assertEquals(apiWhenServersExist.listNodes().iterator().next().getId(), "test-0");
      assertEquals(apiWhenServersExist.listNodes().iterator().next().getName(), "test-0");
   }

   @Test(dependsOnMethods = "testListLocationsWhenResponseIs2xx")
   public void testCreateNodeWhenNetworkNorFirewallExistDoesNotExist() throws RunNodesException, IOException {


      String payload = Strings2.toStringAndClose(InstanceApiExpectTest.class.getResourceAsStream("/instance_get.json"));
      payload = payload.replace("test-0", "test-1");

      HttpResponse getInstanceResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromStringWithContentType(payload, "application/json")).build();

      List<HttpRequest> orderedRequests = ImmutableList.<HttpRequest>builder()
              .add(requestForScopes(COMPUTE_READONLY_SCOPE))
              .add(LIST_ZONES_REQ)
              .add(LIST_PROJECT_IMAGES_REQUEST)
              .add(LIST_GOOGLE_IMAGES_REQUEST)
              .add(LIST_MACHINE_TYPES_REQUEST)
              .add(GET_NETWORK_REQUEST)
              .add(requestForScopes(COMPUTE_SCOPE))
              .add(INSERT_NETWORK_REQUEST)
              .add(GET_OPERATION_REQUEST)
              .add(GET_NETWORK_REQUEST)
              .add(GET_FIREWALL_REQUEST)
              .add(INSERT_FIREWALL_REQUEST)
              .add(GET_OPERATION_REQUEST)
              .add(LIST_INSTANCES_REQUEST)
              .add(LIST_PROJECT_IMAGES_REQUEST)
              .add(LIST_GOOGLE_IMAGES_REQUEST)
              .add(LIST_ZONES_REQ)
              .add(LIST_MACHINE_TYPES_REQUEST)
              .add(createInstanceRequestForInstance("test-1", "jclouds-test", openSshKey))
              .add(GET_OPERATION_REQUEST)
              .add(getInstanceRequestForInstance("test-1"))
              .add(LIST_PROJECT_IMAGES_REQUEST)
              .add(LIST_GOOGLE_IMAGES_REQUEST)
              .add(LIST_ZONES_REQ)
              .add(LIST_MACHINE_TYPES_REQUEST)
              .build();

      List<HttpResponse> orderedResponses = ImmutableList.<HttpResponse>builder()
              .add(TOKEN_RESPONSE)
              .add(LIST_ZONES_RESPONSE)
              .add(LIST_PROJECT_IMAGES_RESPONSE)
              .add(LIST_GOOGLE_IMAGES_RESPONSE)
              .add(LIST_MACHINE_TYPES_RESPONSE)
              .add(HttpResponse.builder().statusCode(404).build())
              .add(TOKEN_RESPONSE)
              .add(SUCESSFULL_OPERATION_RESPONSE)
              .add(GET_OPERATION_RESPONSE)
              .add(GET_NETWORK_RESPONSE)
              .add(HttpResponse.builder().statusCode(404).build())
              .add(SUCESSFULL_OPERATION_RESPONSE)
              .add(GET_OPERATION_RESPONSE)
              .add(LIST_INSTANCES_RESPONSE)
              .add(LIST_PROJECT_IMAGES_RESPONSE)
              .add(LIST_GOOGLE_IMAGES_RESPONSE)
              .add(LIST_ZONES_RESPONSE)
              .add(LIST_MACHINE_TYPES_RESPONSE)
              .add(SUCESSFULL_OPERATION_RESPONSE)
              .add(GET_OPERATION_RESPONSE)
              .add(getInstanceResponse)
              .add(LIST_PROJECT_IMAGES_RESPONSE)
              .add(LIST_GOOGLE_IMAGES_RESPONSE)
              .add(LIST_ZONES_RESPONSE)
              .add(LIST_MACHINE_TYPES_RESPONSE)
              .build();


      ComputeService computeService = orderedRequestsSendResponses(orderedRequests, orderedResponses);

      GoogleComputeTemplateOptions options = computeService.templateOptions().as(GoogleComputeTemplateOptions.class);

      getOnlyElement(computeService.createNodesInGroup("test", 1, options));
   }
}

