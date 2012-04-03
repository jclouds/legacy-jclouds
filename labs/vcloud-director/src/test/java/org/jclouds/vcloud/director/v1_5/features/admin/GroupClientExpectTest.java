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
import org.jclouds.vcloud.director.v1_5.domain.Group;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.features.admin.GroupClient;
import org.jclouds.vcloud.director.v1_5.internal.VCloudDirectorAdminClientExpectTest;
import org.testng.annotations.Test;

/**
 * Test the {@link GroupClient} by observing its side effects.
 * 
 * @author danikov
 */
@Test(groups = { "unit", "admin", "group"}, singleThreaded = true, testName = "GroupClientExpectTest")
public class GroupClientExpectTest extends VCloudDirectorAdminClientExpectTest {
   
   private Reference groupRef = Reference.builder()
         .type("application/vnd.vmware.admin.group+xml")
         .name("???")
         .href(URI.create(endpoint + "/admin/group/???"))
         .build();
   
   @Test(enabled = false)
   public void testGetGroup() {
      VCloudDirectorAdminClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", "/admin/group/???")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/group/group.xml", VCloudDirectorMediaType.GROUP)
            .httpResponseBuilder().build());

      Group expected = group();

      assertEquals(client.getGroupClient().getGroup(groupRef.getHref()), expected);
   }
   
   public static final Group group() {
      return Group.builder()
         
         .build();
   }
   
   @Test(enabled = false)
   public void testUpdateGroup() {
      VCloudDirectorAdminClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", "/admin/group/???")
            .xmlFilePayload("/group/updateGroupSource.xml", VCloudDirectorMediaType.GROUP)
            .acceptMedia(VCloudDirectorMediaType.GROUP)
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/group/updateGroup.xml", VCloudDirectorMediaType.GROUP)
            .httpResponseBuilder().build());

      Group expected = updateGroup();

      assertEquals(client.getGroupClient().updateGroup(groupRef.getHref(), expected), expected);
   }
   
   public static Group updateGroup() {
      return null; // TODO chain onto group() then toBuilder() and modify?
   }
   
   @Test
   public void testDeleteGroup() {
      VCloudDirectorAdminClient client = requestsSendResponses(loginRequest, sessionResponse, 
            new VcloudHttpRequestPrimer()
               .apiCommand("DELETE", "/admin/group/???")
               .acceptAnyMedia()
               .httpRequestBuilder().build(), 
            new VcloudHttpResponsePrimer()
               .httpResponseBuilder().statusCode(204).build());
      
      client.getCatalogClient().deleteCatalog(groupRef.getHref());
   }
}
