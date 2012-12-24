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
import org.jclouds.vcloud.director.v1_5.admin.VCloudDirectorAdminApi;
import org.jclouds.vcloud.director.v1_5.domain.Group;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.internal.VCloudDirectorAdminApiExpectTest;
import org.testng.annotations.Test;

/**
 * Test the {@link GroupApi} by observing its side effects.
 * 
 * @author danikov
 */
@Test(groups = { "unit", "admin" }, singleThreaded = true, testName = "GroupApiExpectTest")
public class GroupApiExpectTest extends VCloudDirectorAdminApiExpectTest {
   
   private Reference groupRef = Reference.builder()
         .type("application/vnd.vmware.admin.group+xml")
         .name("fff")
         .href(URI.create(endpoint + "/admin/group/fff"))
         .build();
   
   @Test(enabled = false)
   public void testGetGroup() {
      VCloudDirectorAdminApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", "/admin/group/fff")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/group/group.xml", VCloudDirectorMediaType.GROUP)
            .httpResponseBuilder().build());

      Group expected = group();

      assertEquals(api.getGroupApi().get(groupRef.getHref()), expected);
   }
   
   public static final Group group() {
      return Group.builder()
         
         .build();
   }
   
   @Test(enabled = false)
   public void testEditGroup() {
      VCloudDirectorAdminApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", "/admin/group/fff")
            .xmlFilePayload("/group/editGroupSource.xml", VCloudDirectorMediaType.GROUP)
            .acceptMedia(VCloudDirectorMediaType.GROUP)
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/group/editGroup.xml", VCloudDirectorMediaType.GROUP)
            .httpResponseBuilder().build());

      Group expected = editGroup();

      assertEquals(api.getGroupApi().edit(groupRef.getHref(), expected), expected);
   }
   
   public static Group editGroup() {
      return null; // TODO chain onto group() then toBuilder() and edit?
   }
   
   @Test
   public void testRemoveGroup() {
      VCloudDirectorAdminApi api = requestsSendResponses(loginRequest, sessionResponse, 
            new VcloudHttpRequestPrimer()
               .apiCommand("DELETE", "/admin/group/fff")
               .acceptAnyMedia()
               .httpRequestBuilder().build(), 
            new VcloudHttpResponsePrimer()
               .httpResponseBuilder().statusCode(204).build());
      
      api.getGroupApi().remove(groupRef.getHref());
   }
}
