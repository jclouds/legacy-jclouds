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

import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.ADMIN_NETWORK;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.ENTITY;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.NETWORK;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.ORG;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.ORG_NETWORK;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.TASK;
import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.vcloud.director.v1_5.admin.VCloudDirectorAdminApi;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.network.DhcpService;
import org.jclouds.vcloud.director.v1_5.domain.network.IpAddresses;
import org.jclouds.vcloud.director.v1_5.domain.network.IpRange;
import org.jclouds.vcloud.director.v1_5.domain.network.IpRanges;
import org.jclouds.vcloud.director.v1_5.domain.network.IpScope;
import org.jclouds.vcloud.director.v1_5.domain.network.Network.FenceMode;
import org.jclouds.vcloud.director.v1_5.domain.network.NetworkConfiguration;
import org.jclouds.vcloud.director.v1_5.domain.network.NetworkFeatures;
import org.jclouds.vcloud.director.v1_5.domain.network.SyslogServerSettings;
import org.jclouds.vcloud.director.v1_5.domain.org.OrgNetwork;
import org.jclouds.vcloud.director.v1_5.internal.VCloudDirectorAdminApiExpectTest;
import org.testng.annotations.Test;

import com.google.common.net.HttpHeaders;

/**
 * Test the {@link AdminNetworkApi} by observing its side effects.
 * 
 * @author danikov, Adrian Cole
 */
@Test(groups = { "unit", "admin" }, singleThreaded = true, testName = "AdminNetworkApiExpectTest")
public class AdminNetworkApiExpectTest extends VCloudDirectorAdminApiExpectTest {

   static String network = "55a677cf-ab3f-48ae-b880-fab90421980c";
   static String networkUrn = "urn:vcloud:network:" + network;
   static URI networkHref = URI.create(endpoint + "/network/" + network);
   static URI networkAdminHref = URI.create(endpoint + "/admin/network/" + network);
   
   HttpRequest get = HttpRequest.builder()
            .method("GET")
            .endpoint(networkAdminHref)
            .addHeader("Accept", "*/*")
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token)
            .build();

    HttpResponse getResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResourceWithContentType("/network/network.xml", ORG + ";version=1.5"))
            .build();
    
   @Test
   public void testGetNetworkHref() {
      VCloudDirectorAdminApi api = requestsSendResponses(loginRequest, sessionResponse, get, getResponse);
      assertEquals(api.getNetworkApi().get(networkAdminHref), network());
   }
   
   HttpRequest resolveNetwork = HttpRequest.builder()
            .method("GET")
            .endpoint(endpoint + "/entity/" + networkUrn)
            .addHeader("Accept", "*/*")
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token)
            .build();
   
   String networkEntity = asString(createXMLBuilder("Entity").a("xmlns", "http://www.vmware.com/vcloud/v1.5")
                                                             .a("name", networkUrn)
                                                             .a("id", networkUrn)
                                                             .a("type", ENTITY)
                                                             .a("href", endpoint + "/entity/" + networkUrn)
                                  .e("Link").a("rel", "alternate").a("type", NETWORK).a("href", networkHref.toString()).up()
                                  .e("Link").a("rel", "alternate").a("type", ADMIN_NETWORK).a("href", networkAdminHref.toString()).up());
   
   HttpResponse resolveNetworkResponse = HttpResponse.builder()
           .statusCode(200)
           .payload(payloadFromStringWithContentType(networkEntity, ENTITY + ";version=1.5"))
           .build();
   
   @Test
   public void testGetNetworkUrn() {
      VCloudDirectorAdminApi api = requestsSendResponses(loginRequest, sessionResponse, resolveNetwork, resolveNetworkResponse, get, getResponse);
      assertEquals(api.getNetworkApi().get(networkUrn), network());
   }
   
   HttpRequest edit = HttpRequest.builder()
            .method("PUT")
            .endpoint(networkAdminHref )
            .addHeader("Accept", TASK)
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token)
            .payload(payloadFromResourceWithContentType("/network/admin/editNetworkSource.xml", ORG_NETWORK))
            .build();

   HttpResponse editResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResourceWithContentType("/network/admin/editNetworkTask.xml", TASK))
            .build();

   @Test
   public void testEditNetworkHref() {
      VCloudDirectorAdminApi api = requestsSendResponses(loginRequest, sessionResponse, edit, editResponse);
      assertEquals(api.getNetworkApi().edit(networkAdminHref, editNetwork()), editNetworkTask());
   }
   
   @Test
   public void testEditNetworkUrn() {
      VCloudDirectorAdminApi api = requestsSendResponses(loginRequest, sessionResponse, resolveNetwork, resolveNetworkResponse, edit, editResponse);
      assertEquals(api.getNetworkApi().edit(networkUrn, editNetwork()), editNetworkTask());
   }
   
   HttpRequest reset = HttpRequest.builder()
            .method("POST")
            .endpoint(networkAdminHref + "/action/reset")
            .addHeader("Accept", "*/*")
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token)
            .build();

   HttpResponse resetResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResourceWithContentType("/network/admin/resetNetworkTask.xml", TASK))
            .build();

   @Test
   public void testResetNetworkHref() {
      VCloudDirectorAdminApi api = requestsSendResponses(loginRequest, sessionResponse, reset, resetResponse);
      assertEquals(api.getNetworkApi().reset(networkAdminHref), resetNetworkTask());
   }
   
   @Test
   public void testResetNetworkUrn() {
      VCloudDirectorAdminApi api = requestsSendResponses(loginRequest, sessionResponse, resolveNetwork, resolveNetworkResponse, reset, resetResponse);
      assertEquals(api.getNetworkApi().reset(networkUrn), resetNetworkTask());
   }
   
   public static OrgNetwork network() {
      return OrgNetwork.builder()
         .name("ilsolation01-Jclouds")
         .id("urn:vcloud:network:f3ba8256-6f48-4512-aad6-600e85b4dc38")
         .type("application/vnd.vmware.vcloud.orgNetwork+xml")
         .href(URI.create("https://vcloudbeta.bluelock.com/api/network/f3ba8256-6f48-4512-aad6-600e85b4dc38"))
         .link(Link.builder()
            .rel("up")
            .type("application/vnd.vmware.vcloud.org+xml")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0"))
            .build())
         .link(Link.builder()
            .rel("down")
            .type("application/vnd.vmware.vcloud.metadata+xml")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/network/f3ba8256-6f48-4512-aad6-600e85b4dc38/metadata"))
            .build())
         .description("")
         .configuration(NetworkConfiguration.builder()
            .ipScope(IpScope.builder()
               .isInherited(false)
               .gateway("192.168.1.1")
               .netmask("255.255.255.0")
               .dns1("173.240.111.52")
               .dns2("173.240.111.53")
               .ipRanges(IpRanges.builder()
                     .ipRange(IpRange.builder()
                           .startAddress("192.168.1.100")
                           .endAddress("192.168.1.199")
                           .build())
                     .build())
               .build())
            .fenceMode(FenceMode.ISOLATED)
            .retainNetInfoAcrossDeployments(false)
            .features(NetworkFeatures.builder()
               .service(DhcpService.builder()
                  .enabled(false)
                  .defaultLeaseTime(3600)
                  .maxLeaseTime(7200)
                  .ipRange(IpRange.builder()
                     .startAddress("192.168.1.2")
                     .endAddress("192.168.1.99")
                     .build())
                  .build())
               .build())
            .syslogServerSettings(SyslogServerSettings.builder().build())
            .build())
         .allowedExternalIpAddresses(IpAddresses.builder().build())
         .build();
   }
   
   public final OrgNetwork editNetwork() {
      return network().toBuilder()
            
         .build();
   }
   
   public final Task resetNetworkTask() {
      return Task.builder()
               .status("running")
               .startTime(dateService.iso8601DateParse("2012-03-14T12:39:23.720-04:00"))
               .operationName("networkResetNetwork")
               .operation("Resetting Network ilsolation01-Jclouds(f3ba8256-6f48-4512-aad6-600e85b4dc38)")
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
   
   public final Task editNetworkTask() {
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
