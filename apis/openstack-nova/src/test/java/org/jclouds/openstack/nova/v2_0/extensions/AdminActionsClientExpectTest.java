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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.net.URI;

import javax.ws.rs.core.MediaType;

import org.jclouds.http.HttpRequest;
import org.jclouds.openstack.nova.v2_0.domain.BackupType;
import org.jclouds.openstack.nova.v2_0.extensions.AdminActionsClient;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaClientExpectTest;
import org.jclouds.openstack.nova.v2_0.options.CreateBackupOfServerOptions;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;

/**
 * Tests parsing and guice wiring of AdminActionsClient
 *
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "AdminActionsClientExpectTest")
public class AdminActionsClientExpectTest extends BaseNovaClientExpectTest {

   public void testSuspend() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/1/action");
      AdminActionsClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardActionRequestBuilderVoidResponse(endpoint, "suspend").build(),
            standardResponseBuilder(202).build()
      ).getAdminActionsExtensionForZone("az-1.region-a.geo-1").get();

      assertTrue(client.suspendServer("1"));
   }

   public void testSuspendFailsNotFound() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/1/action");
      AdminActionsClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardActionRequestBuilderVoidResponse(endpoint, "suspend").build(),
            standardResponseBuilder(404).build()
      ).getAdminActionsExtensionForZone("az-1.region-a.geo-1").get();

      assertFalse(client.suspendServer("1"));
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testSuspendFailsNotAuthorized() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/1/action");
      AdminActionsClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardActionRequestBuilderVoidResponse(endpoint, "suspend").build(),
            standardResponseBuilder(403).build()
      ).getAdminActionsExtensionForZone("az-1.region-a.geo-1").get();

      client.suspendServer("1");
   }
   
   public void testResume() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/1/action");
      AdminActionsClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardActionRequestBuilderVoidResponse(endpoint, "resume").build(),
            standardResponseBuilder(202).build()
      ).getAdminActionsExtensionForZone("az-1.region-a.geo-1").get();

      assertTrue(client.resumeServer("1"));
   }

   public void testResumeFailsNotFound() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/1/action");
      AdminActionsClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardActionRequestBuilderVoidResponse(endpoint, "resume").build(),
            standardResponseBuilder(404).build()
      ).getAdminActionsExtensionForZone("az-1.region-a.geo-1").get();

      assertFalse(client.resumeServer("1"));
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testResumeFailsNotAuthorized() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/1/action");
      AdminActionsClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardActionRequestBuilderVoidResponse(endpoint, "resume").build(),
            standardResponseBuilder(403).build()
      ).getAdminActionsExtensionForZone("az-1.region-a.geo-1").get();

      client.resumeServer("1");
   }

   public void testLock() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/1/action");
      AdminActionsClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardActionRequestBuilderVoidResponse(endpoint, "lock").build(),
            standardResponseBuilder(202).build()
      ).getAdminActionsExtensionForZone("az-1.region-a.geo-1").get();

      assertTrue(client.lockServer("1"));
   }

   public void testLockFailsNotFound() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/1/action");
      AdminActionsClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardActionRequestBuilderVoidResponse(endpoint, "lock").build(),
            standardResponseBuilder(404).build()
      ).getAdminActionsExtensionForZone("az-1.region-a.geo-1").get();

      assertFalse(client.lockServer("1"));
   }

   public void testUnlock() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/1/action");
      AdminActionsClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardActionRequestBuilderVoidResponse(endpoint, "unlock").build(),
            standardResponseBuilder(202).build()
      ).getAdminActionsExtensionForZone("az-1.region-a.geo-1").get();

      assertTrue(client.unlockServer("1"));
   }

   public void testUnlockFailsNotFound() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/1/action");
      AdminActionsClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardActionRequestBuilderVoidResponse(endpoint, "unlock").build(),
            standardResponseBuilder(404).build()
      ).getAdminActionsExtensionForZone("az-1.region-a.geo-1").get();

      assertFalse(client.unlockServer("1"));
   }

   public void testPause() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/1/action");
      AdminActionsClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardActionRequestBuilderVoidResponse(endpoint, "pause").build(),
            standardResponseBuilder(202).build()
      ).getAdminActionsExtensionForZone("az-1.region-a.geo-1").get();

      assertTrue(client.pauseServer("1"));
   }

   public void testPauseFailsNotFound() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/1/action");
      AdminActionsClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardActionRequestBuilderVoidResponse(endpoint, "pause").build(),
            standardResponseBuilder(404).build()
      ).getAdminActionsExtensionForZone("az-1.region-a.geo-1").get();

      assertFalse(client.pauseServer("1"));
   }
   
   public void testUnpause() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/1/action");
      AdminActionsClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardActionRequestBuilderVoidResponse(endpoint, "unpause").build(),
            standardResponseBuilder(202).build()
      ).getAdminActionsExtensionForZone("az-1.region-a.geo-1").get();

      assertTrue(client.unpauseServer("1"));
   }

   public void testUnpauseFailsNotFound() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/1/action");
      AdminActionsClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardActionRequestBuilderVoidResponse(endpoint, "unpause").build(),
            standardResponseBuilder(404).build()
      ).getAdminActionsExtensionForZone("az-1.region-a.geo-1").get();

      assertFalse(client.unpauseServer("1"));
   }
   
   public void testMigrateServer() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/1/action");
      AdminActionsClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardActionRequestBuilderVoidResponse(endpoint, "migrate").build(),
            standardResponseBuilder(202).build()
      ).getAdminActionsExtensionForZone("az-1.region-a.geo-1").get();

      assertTrue(client.migrateServer("1"));
   }


   public void testMigrateServerFailsNotFound() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/1/action");
      AdminActionsClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardActionRequestBuilderVoidResponse(endpoint, "migrate").build(),
            standardResponseBuilder(404).build()
      ).getAdminActionsExtensionForZone("az-1.region-a.geo-1").get();

      assertFalse(client.migrateServer("1"));
   }

   public void testResetNetworkOfServer() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/1/action");
      AdminActionsClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardActionRequestBuilderVoidResponse(endpoint, "resetNetwork").build(),
            standardResponseBuilder(202).build()
      ).getAdminActionsExtensionForZone("az-1.region-a.geo-1").get();

      assertTrue(client.resetNetworkOfServer("1"));
   }

   public void testResetNetworkOfServerFailsNotFound() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/1/action");
      AdminActionsClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardActionRequestBuilderVoidResponse(endpoint, "resetNetwork").build(),
            standardResponseBuilder(404).build()
      ).getAdminActionsExtensionForZone("az-1.region-a.geo-1").get();

      assertFalse(client.resetNetworkOfServer("1"));
   }
   
   public void testInjectNetworkInfoIntoServer() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/1/action");
      AdminActionsClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardActionRequestBuilderVoidResponse(endpoint, "injectNetworkInfo").build(),
            standardResponseBuilder(202).build()
      ).getAdminActionsExtensionForZone("az-1.region-a.geo-1").get();

      assertTrue(client.injectNetworkInfoIntoServer("1"));
   }

   public void testInjectNetworkInfoIntoServerFailsNotFound() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/1/action");
      AdminActionsClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardActionRequestBuilderVoidResponse(endpoint, "injectNetworkInfo").build(),
            standardResponseBuilder(404).build()
      ).getAdminActionsExtensionForZone("az-1.region-a.geo-1").get();

      assertFalse(client.injectNetworkInfoIntoServer("1"));
   }
   
   public void testBackupServer() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/1/action");
      AdminActionsClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint).method("POST")
                  .payload(payloadFromStringWithContentType("{\"createBackup\":{\"backup_type\":\"weekly\",\"rotation\":3,\"name\":\"mybackup\",\"metadata\":{\"some\":\"data or other\"}}}", MediaType.APPLICATION_JSON)).build(),
            standardResponseBuilder(202).headers(ImmutableMultimap.of("Location", "http://172.16.89.149:8774/v2/images/1976b3b3-409a-468d-b16c-a9172c341b46")).build()
      ).getAdminActionsExtensionForZone("az-1.region-a.geo-1").get();

      String imageId = client.createBackupOfServer("1", "mybackup", BackupType.WEEKLY, 3, CreateBackupOfServerOptions.Builder.metadata(ImmutableMap.of("some", "data or other")));
      assertEquals(imageId, "1976b3b3-409a-468d-b16c-a9172c341b46");
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testBackupServerFailNotFound() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/1/action");
      AdminActionsClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint).method("POST")
                  .payload(payloadFromStringWithContentType("{\"createBackup\":{\"backup_type\":\"weekly\",\"rotation\":3,\"name\":\"mybackup\",\"metadata\":{\"some\":\"data or other\"}}}", MediaType.APPLICATION_JSON)).build(),
            standardResponseBuilder(404).build()
      ).getAdminActionsExtensionForZone("az-1.region-a.geo-1").get();

      client.createBackupOfServer("1", "mybackup", BackupType.WEEKLY, 3, CreateBackupOfServerOptions.Builder.metadata(ImmutableMap.of("some", "data or other")));
   }

   public void testLiveMigrateServer() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/1/action");
      AdminActionsClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardActionRequestBuilderVoidResponse(endpoint, "GONNAOVERWRITE")
                  .payload(payloadFromStringWithContentType("{\"os-migrateLive\":{\"host\":\"bighost\",\"block_migration\":true,\"disk_over_commit\":false}}", MediaType.APPLICATION_JSON)).build(),
            standardResponseBuilder(202).build()
      ).getAdminActionsExtensionForZone("az-1.region-a.geo-1").get();

      assertTrue(client.liveMigrateServer("1", "bighost", true, false));
   }

   public void testLiveMigrateServerFailsNotFound() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/1/action");
      AdminActionsClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardActionRequestBuilderVoidResponse(endpoint, "GONNAOVERWRITE")
                  .payload(payloadFromStringWithContentType("{\"os-migrateLive\":{\"host\":\"bighost\",\"block_migration\":true,\"disk_over_commit\":false}}", MediaType.APPLICATION_JSON)).build(),
            standardResponseBuilder(404).build()
      ).getAdminActionsExtensionForZone("az-1.region-a.geo-1").get();

      assertFalse(client.liveMigrateServer("1", "bighost", true, false));
   }
   
   protected HttpRequest.Builder standardActionRequestBuilderVoidResponse(URI endpoint, String actionName) {
      return HttpRequest.builder().method("POST")
            .headers(ImmutableMultimap.of("X-Auth-Token", authToken))
            .payload(payloadFromStringWithContentType("{\"" + actionName + "\":null}", MediaType.APPLICATION_JSON))
            .endpoint(endpoint);
   }

}
