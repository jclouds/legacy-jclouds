/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 1.1 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-1.1
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.openstack.quantum.v1_0.features;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.quantum.v1_0.domain.Attachment;
import org.jclouds.openstack.quantum.v1_0.domain.Port;
import org.jclouds.openstack.quantum.v1_0.domain.PortDetails;
import org.jclouds.openstack.quantum.v1_0.domain.Reference;
import org.jclouds.openstack.quantum.v1_0.internal.BaseQuantumClientExpectTest;
import org.jclouds.openstack.quantum.v1_0.parse.ParsePortDetailsTest;
import org.jclouds.openstack.quantum.v1_0.parse.ParsePortTest;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests parsing and Guice wiring of PortClient
 *
 * @author Adam Lowe
 */
@Test(groups="unit", testName = "PortClientExpectTest")
public class PortClientExpectTest extends BaseQuantumClientExpectTest {

   public void testListReferencesReturns2xx() {
      PortClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint + "/tenants/3456/networks/1a104cf5-cb18-4d35-9407-2fd2646d9d0b/ports").build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromStringWithContentType("{\"ports\": [{\"id\": \"a6058a59-fa8c-46cc-bac8-08904e6ff0a5\"}]}", APPLICATION_JSON)).build())
            .getPortClientForRegionAndNetwork("region-a.geo-1", "1a104cf5-cb18-4d35-9407-2fd2646d9d0b");

      Set<Reference> nets = client.listReferences();
      assertEquals(nets, ImmutableSet.of(Reference.builder().id("a6058a59-fa8c-46cc-bac8-08904e6ff0a5").build()));
   }

   public void testListReferencesReturns4xx() {
      PortClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint + "/tenants/3456/networks/1a104cf5-cb18-4d35-9407-2fd2646d9d0b/ports").build(),
            HttpResponse.builder().statusCode(404).build())
            .getPortClientForRegionAndNetwork("region-a.geo-1", "1a104cf5-cb18-4d35-9407-2fd2646d9d0b");

      assertTrue(client.listReferences().isEmpty());
   }

   public void testListReturns2xx() {
      PortClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint + "/tenants/3456/networks/1a104cf5-cb18-4d35-9407-2fd2646d9d0b/ports/detail").build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromStringWithContentType("{\"ports\": [{\"state\": \"DOWN\", \"id\": \"814ae4bb-33d9-425f-8ee2-13a5c90b1465\"}]}", APPLICATION_JSON)).build())
            .getPortClientForRegionAndNetwork("region-a.geo-1", "1a104cf5-cb18-4d35-9407-2fd2646d9d0b");

      Set<Port> nets = client.list();
      assertEquals(nets, ImmutableSet.of(Port.builder().state(Port.State.DOWN).id("814ae4bb-33d9-425f-8ee2-13a5c90b1465").build()));
   }

   public void testListReturns4xx() {
      PortClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint + "/tenants/3456/networks/1a104cf5-cb18-4d35-9407-2fd2646d9d0b/ports/detail").build(),
            HttpResponse.builder().statusCode(404).build())
            .getPortClientForRegionAndNetwork("region-a.geo-1", "1a104cf5-cb18-4d35-9407-2fd2646d9d0b");

      assertTrue(client.list().isEmpty());
   }

   public void testShowReturns2xx() {
      PortClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint + "/tenants/3456/networks/16dba3bc-f3fa-4775-afdc-237e12c72f6a/ports/646c123b-871a-4124-9fa2-a94f04a582df").build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResourceWithContentType("/port.json", APPLICATION_JSON)).build())
            .getPortClientForRegionAndNetwork("region-a.geo-1", "16dba3bc-f3fa-4775-afdc-237e12c72f6a");

      Port port = client.get("646c123b-871a-4124-9fa2-a94f04a582df");
      assertEquals(port, new ParsePortTest().expected());
   }

   public void testShowReturns4xx() {
      PortClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint + "/tenants/3456/networks/16dba3bc-f3fa-4775-afdc-237e12c72f6a/ports/646c123b-871a-4124-9fa2-a94f04a582df").build(),
            HttpResponse.builder().statusCode(404).build())
            .getPortClientForRegionAndNetwork("region-a.geo-1", "16dba3bc-f3fa-4775-afdc-237e12c72f6a");

      assertNull(client.get("646c123b-871a-4124-9fa2-a94f04a582df"));
   }

   public void testShowDetailsReturns2xx() {
      PortClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint + "/tenants/3456/networks/16dba3bc-f3fa-4775-afdc-237e12c72f6a/ports/646c123b-871a-4124-9fa2-a94f04a582df/detail").build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResourceWithContentType("/port_details.json", APPLICATION_JSON)).build())
            .getPortClientForRegionAndNetwork("region-a.geo-1", "16dba3bc-f3fa-4775-afdc-237e12c72f6a");

      PortDetails net = client.getDetails("646c123b-871a-4124-9fa2-a94f04a582df");
      assertEquals(net, new ParsePortDetailsTest().expected());
   }

   public void testShowDetailsReturns4xx() {
      PortClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint + "/tenants/3456/networks/16dba3bc-f3fa-4775-afdc-237e12c72f6a/ports/646c123b-871a-4124-9fa2-a94f04a582df/detail").build(),
            HttpResponse.builder().statusCode(404).build())
            .getPortClientForRegionAndNetwork("region-a.geo-1", "16dba3bc-f3fa-4775-afdc-237e12c72f6a");

      assertNull(client.getDetails("646c123b-871a-4124-9fa2-a94f04a582df"));
   }

   public void testCreateReturns2xx() {
      PortClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint + "/tenants/3456/networks/16dba3bc-f3fa-4775-afdc-237e12c72f6a/ports").method("POST").build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromStringWithContentType("{\"port\":{\"id\":\"12345\"}}", APPLICATION_JSON)).build())
            .getPortClientForRegionAndNetwork("region-a.geo-1", "16dba3bc-f3fa-4775-afdc-237e12c72f6a");

      Reference port = client.create();
      assertEquals(port, Reference.builder().id("12345").build());
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testCreateReturns4xx() {
      PortClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint + "/tenants/3456/networks/16dba3bc-f3fa-4775-afdc-237e12c72f6a/ports").method("POST").build(),
            HttpResponse.builder().statusCode(404).build())
            .getPortClientForRegionAndNetwork("region-a.geo-1", "16dba3bc-f3fa-4775-afdc-237e12c72f6a");

      client.create();
   }

   public void testUpdateReturns2xx() {
      PortClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint + "/tenants/3456/networks/16dba3bc-f3fa-4775-afdc-237e12c72f6a/ports/77777").method("PUT")
                  .payload(payloadFromStringWithContentType("{\"port\":{\"state\":\"ACTIVE\"}}", MediaType.APPLICATION_JSON)).build(),
            HttpResponse.builder().statusCode(200).build())
            .getPortClientForRegionAndNetwork("region-a.geo-1", "16dba3bc-f3fa-4775-afdc-237e12c72f6a");

      assertTrue(client.updateState("77777", Port.State.ACTIVE));
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testUpdateReturns4xx() {
      PortClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint + "/tenants/3456/networks/16dba3bc-f3fa-4775-afdc-237e12c72f6a/ports/77777").method("PUT")
                  .payload(payloadFromStringWithContentType("{\"port\":{\"state\":\"ACTIVE\"}}", MediaType.APPLICATION_JSON)).build(),
            HttpResponse.builder().statusCode(401).build())
            .getPortClientForRegionAndNetwork("region-a.geo-1", "16dba3bc-f3fa-4775-afdc-237e12c72f6a");

      client.updateState("77777", Port.State.ACTIVE);
   }

   public void testShowAttachment() {
      PortClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint + "/tenants/3456/networks/16dba3bc-f3fa-4775-afdc-237e12c72f6a/ports/77777/attachment").build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResourceWithContentType("/attachment.json", APPLICATION_JSON)).build())
            .getPortClientForRegionAndNetwork("region-a.geo-1", "16dba3bc-f3fa-4775-afdc-237e12c72f6a");

      Attachment attachment = client.showAttachment("77777");
      assertEquals(attachment, Attachment.builder().id("jclouds-live-test").build());
   }

   public void testShowAttachmentReturns4xx() {
      PortClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint + "/tenants/3456/networks/16dba3bc-f3fa-4775-afdc-237e12c72f6a/ports/77777/attachment").build(),
            HttpResponse.builder().statusCode(404).build())
            .getPortClientForRegionAndNetwork("region-a.geo-1", "16dba3bc-f3fa-4775-afdc-237e12c72f6a");

      assertNull(client.showAttachment("77777"));
   }

   public void testPlugAttachment() {
      PortClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint + "/tenants/3456/networks/16dba3bc-f3fa-4775-afdc-237e12c72f6a/ports/77777/attachment")
            .payload(payloadFromStringWithContentType("{\"attachment\":{\"id\":\"jclouds-live-test\"}}", MediaType.APPLICATION_JSON))
                  .method("PUT").build(),
            HttpResponse.builder().statusCode(200).build())
            .getPortClientForRegionAndNetwork("region-a.geo-1", "16dba3bc-f3fa-4775-afdc-237e12c72f6a");

      assertTrue(client.plugAttachment("77777", "jclouds-live-test"));
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testPlugAttachmentReturns4xx() {
      PortClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint + "/tenants/3456/networks/16dba3bc-f3fa-4775-afdc-237e12c72f6a/ports/77777/attachment")
                  .payload(payloadFromStringWithContentType("{\"attachment\":{\"id\":\"jclouds-live-test\"}}", MediaType.APPLICATION_JSON))
                  .method("PUT").build(),
            HttpResponse.builder().statusCode(403).build())
            .getPortClientForRegionAndNetwork("region-a.geo-1", "16dba3bc-f3fa-4775-afdc-237e12c72f6a");

      client.plugAttachment("77777", "jclouds-live-test");
   }
   public void testUnplugAttachment() {
      PortClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint + "/tenants/3456/networks/16dba3bc-f3fa-4775-afdc-237e12c72f6a/ports/77777/attachment").method("DELETE").build(),
            HttpResponse.builder().statusCode(200).build())
            .getPortClientForRegionAndNetwork("region-a.geo-1", "16dba3bc-f3fa-4775-afdc-237e12c72f6a");

      assertTrue(client.unplugAttachment("77777"));
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testUnplugAttachmentReturns4xx() {
      PortClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint + "/tenants/3456/networks/16dba3bc-f3fa-4775-afdc-237e12c72f6a/ports/77777/attachment").method("DELETE").build(),
            HttpResponse.builder().statusCode(404).build())
            .getPortClientForRegionAndNetwork("region-a.geo-1", "16dba3bc-f3fa-4775-afdc-237e12c72f6a");

      client.unplugAttachment("77777");
   }
   
}
