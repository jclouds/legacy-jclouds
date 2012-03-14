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
package org.jclouds.vcloud.director.v1_5.features;

import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorClient;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.OrgNetwork;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorRestClientExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Test the {@link GroupClient} by observing its side effects.
 * 
 * @author danikov
 */
@Test(groups = { "unit", "admin", "adminNetwork"}, singleThreaded = true, testName = "AdminNetworkClientExpectTest")
public class AdminNetworkClientExpectTest extends BaseVCloudDirectorRestClientExpectTest {
   
   Reference networkRef = Reference.builder()
      .href(URI.create(endpoint+"/admin/network/b466c0c5-8a5c-4335-b703-a2e2e6b5f3e1"))
      .build();
   
   @Test
   public void testGetNetworkWithOrgNetwork() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", "/admin/network/b466c0c5-8a5c-4335-b703-a2e2e6b5f3e1")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/network/admin/orgNetwork.xml", VCloudDirectorMediaType.ORG_NETWORK)
            .httpResponseBuilder().build());

      OrgNetwork expected = orgNetwork();

      assertEquals(client.getAdminNetworkClient().getNetwork(networkRef.getHref()), expected);
   }
   
   // PUT /admin/network/{id}
   
   // POST /admin/network/{id}/action/reset
   
   public final OrgNetwork orgNetwork() {
      return NetworkClientExpectTest.orgNetwork().toNewBuilder()
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
}
