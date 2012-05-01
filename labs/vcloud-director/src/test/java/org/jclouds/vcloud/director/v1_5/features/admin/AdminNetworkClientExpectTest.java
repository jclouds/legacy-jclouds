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
package org.jclouds.vcloud.director.v1_5.features.admin;

import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.admin.VCloudDirectorAdminClient;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.org.OrgNetwork;
import org.jclouds.vcloud.director.v1_5.features.NetworkClientExpectTest;
import org.jclouds.vcloud.director.v1_5.internal.VCloudDirectorAdminClientExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Test the {@link AdminNetworkClient} by observing its side effects.
 * 
 * @author danikov
 */
@Test(groups = { "unit", "admin" }, singleThreaded = true, testName = "AdminNetworkClientExpectTest")
public class AdminNetworkClientExpectTest extends VCloudDirectorAdminClientExpectTest {
   
   Reference networkRef = Reference.builder()
      .href(URI.create(endpoint+"/admin/network/b466c0c5-8a5c-4335-b703-a2e2e6b5f3e1"))
      .build();
   
   @Test
   public void testGetNetworkWithOrgNetwork() {
      VCloudDirectorAdminClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", "/admin/network/b466c0c5-8a5c-4335-b703-a2e2e6b5f3e1")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/network/admin/orgNetwork.xml", VCloudDirectorMediaType.ORG_NETWORK)
            .httpResponseBuilder().build());

      OrgNetwork expected = orgNetwork();

      assertEquals(client.getNetworkClient().getNetwork(networkRef.getHref()), expected);
   }
   
   @Test
   public void testUpdateNetwork() {
      VCloudDirectorAdminClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", "/admin/network/b466c0c5-8a5c-4335-b703-a2e2e6b5f3e1")
            .xmlFilePayload("/network/admin/updateNetworkSource.xml", VCloudDirectorMediaType.ORG_NETWORK)
            .acceptMedia(VCloudDirectorMediaType.TASK)
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/network/admin/updateNetworkTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

      Task expected = updateNetworkTask();

      assertEquals(client.getNetworkClient().updateNetwork(networkRef.getHref(), updateNetwork()), expected);
   }
   
   @Test(enabled = false)
   public void testResetNetwork() {
      VCloudDirectorAdminClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", "/admin/network/b466c0c5-8a5c-4335-b703-a2e2e6b5f3e1/action/reset")
            .acceptMedia(VCloudDirectorMediaType.TASK)
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/network/admin/resetNetworkTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

      Task expected = resetNetworkTask();

      assertEquals(client.getNetworkClient().resetNetwork(networkRef.getHref()), expected);
   }
   
   public final OrgNetwork orgNetwork() {
      return NetworkClientExpectTest.orgNetwork().toBuilder()
         .href(toAdminUri(NetworkClientExpectTest.orgNetwork().getHref()))
         .links(ImmutableSet.of(
            Link.builder()
               .rel("alternate")
               .type("application/vnd.vmware.vcloud.orgNetwork+xml")
               .href(URI.create("https://vcloudbeta.bluelock.com/api/network/f3ba8256-6f48-4512-aad6-600e85b4dc38"))
               .build(),
            Link.builder()
               .rel("edit")
               .type("application/vnd.vmware.admin.orgNetwork+xml")
               .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/network/f3ba8256-6f48-4512-aad6-600e85b4dc38"))
               .build(),
            Link.builder()
               .rel("up")
               .type("application/vnd.vmware.admin.organization+xml")
               .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0"))
               .build(),
            Link.builder()
               .rel("repair")
               .type("application/vnd.vmware.admin.orgNetwork+xml")
               .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/network/f3ba8256-6f48-4512-aad6-600e85b4dc38/action/reset"))
               .build(),
            Link.builder()
               .rel("down")
               .type("application/vnd.vmware.vcloud.metadata+xml")
               .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/network/f3ba8256-6f48-4512-aad6-600e85b4dc38/metadata"))
               .build()))
         .networkPool(Reference.builder()
            .type("application/vnd.vmware.admin.networkPool+xml")
            .name("vcdni01")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/extension/networkPool/e86bfdb5-b3e0-4ece-9125-e764ac64c95c"))
            .build())
         .build();
   }
   
   public final OrgNetwork updateNetwork() {
      return orgNetwork().toBuilder()
            
         .build();
   }
   
   public final Task resetNetworkTask() {
      return Task.builder()
            
         .build();
   }
   
   public final Task updateNetworkTask() {
      return Task.builder()
         .status("running")
         .startTime(dateService.iso8601DateParse("2012-03-14T12:39:23.720-04:00"))
         .operationName("networkUpdateNetwork")
         .operation("Updating Network ilsolation01-Jclouds(f3ba8256-6f48-4512-aad6-600e85b4dc38)")
         .expiryTime(dateService.iso8601DateParse("2012-06-12T12:39:23.720-04:00"))
         .name("task")
         .id("urn:vcloud:task:49d2e180-7921-4902-ac39-b4ff5406bb94")
         .type("application/vnd.vmware.vcloud.task+xml")
         .href(URI.create("https://vcloudbeta.bluelock.com/api/task/49d2e180-7921-4902-ac39-b4ff5406bb94"))
         .link(Link.builder()
            .rel("task:cancel")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/task/49d2e180-7921-4902-ac39-b4ff5406bb94/action/cancel"))
            .build())
         .owner(Reference.builder()
            .type("application/vnd.vmware.vcloud.network+xml")
            .name("ilsolation01-Jclouds")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/network/f3ba8256-6f48-4512-aad6-600e85b4dc38"))
            .build())
         .user(Reference.builder()
            .type("application/vnd.vmware.admin.user+xml")
            .name("dan@cloudsoftcorp.com")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/user/ae75edd2-12de-414c-8e85-e6ea10442c08"))
            .build())
         .org(Reference.builder()
            .type("application/vnd.vmware.vcloud.org+xml")
            .name("JClouds")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0"))
            .build())
         .build();
   }
}
