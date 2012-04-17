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
import java.util.Collections;

import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.admin.VCloudDirectorAdminClient;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.User;
import org.jclouds.vcloud.director.v1_5.internal.VCloudDirectorAdminClientExpectTest;
import org.testng.annotations.Test;

/**
 * Test the {@link UserClient} by observing its side effects.
 * 
 * @author danikov
 */
@Test(groups = { "unit", "admin" }, singleThreaded = true, testName = "UserClientExpectTest")
public class UserClientExpectTest extends VCloudDirectorAdminClientExpectTest {
   
   private Reference orgRef = Reference.builder()
         .href(URI.create(endpoint + "/admin/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0"))
         .build();
   
   private Reference userRef = Reference.builder()
         .href(URI.create(endpoint + "/admin/user/b37223f3-8792-477a-820f-334998f61cd6"))
         .build();
   
   @Test
   public void testCreateUser() {
      VCloudDirectorAdminClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", "/admin/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0/catalogs")
            .xmlFilePayload("/user/createUserSource.xml", VCloudDirectorMediaType.USER)
            .acceptMedia(VCloudDirectorMediaType.USER)
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/user/createUser.xml", VCloudDirectorMediaType.USER)
            .httpResponseBuilder().build());

      User source = createUserSource();
      User expected = createUser();

      assertEquals(client.getUserClient().createUser(orgRef.getHref(), source), expected);
   }
   
   public static final User createUserSource() {
      return User.builder()
            .name("test")
            .fullName("testFullName")
            .emailAddress("test@test.com")
            .telephone("555-1234")
            .isEnabled(false)
            .im("testIM")
            .isAlertEnabled(false)
            .alertEmailPrefix("testPrefix")
            .alertEmail("testAlert@test.com")
            .isExternal(false)
            .isGroupRole(false)
            .role(Reference.builder()
               .type("application/vnd.vmware.admin.role+xml")
               .name("vApp User")
               .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/role/ff1e0c91-1288-3664-82b7-a6fa303af4d1"))
               .build())
            .password("password")
            .groups(Collections.<Reference>emptyList())
            .build();
   }
   
   public static final User createUser() {
      return createUserSource().toBuilder()
         .id("urn:vcloud:user:b37223f3-8792-477a-820f-334998f61cd6")
         .type("application/vnd.vmware.admin.user+xml")
         .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/user/b37223f3-8792-477a-820f-334998f61cd6"))
         .link(Link.builder()
            .rel("edit")
            .type("application/vnd.vmware.admin.user+xml")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/user/b37223f3-8792-477a-820f-334998f61cd6"))
            .build())
         .link(Link.builder()
            .rel("remove")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/user/b37223f3-8792-477a-820f-334998f61cd6"))
            .build())
         .isLocked(false)
         .isDefaultCached(false)
         .storedVmQuota(0)
         .deployedVmQuota(0)
         .password(null)
         .build();
   }
   
   @Test
   public void testGetUser() {
      VCloudDirectorAdminClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", "/admin/user/b37223f3-8792-477a-820f-334998f61cd6")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/user/user.xml", VCloudDirectorMediaType.USER)
            .httpResponseBuilder().build());

      User expected = user();

      assertEquals(client.getUserClient().getUser(userRef.getHref()), expected);
   }
   
   public static final User user() {
      return createUser().toBuilder()
         .nameInSource("test")
         .build();
   }
 
   @Test
   public void testUpdateUser() {
      VCloudDirectorAdminClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", "/admin/user/b37223f3-8792-477a-820f-334998f61cd6")
            .xmlFilePayload("/user/updateUserSource.xml", VCloudDirectorMediaType.USER)
            .acceptMedia(VCloudDirectorMediaType.USER)
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/user/updateUser.xml", VCloudDirectorMediaType.USER)
            .httpResponseBuilder().build());

      User source = updateUserSource();
      User expected = updateUser();

      assertEquals(client.getUserClient().updateUser(userRef.getHref(), source), expected);
   }
   
   public static final User updateUserSource() {
      return user().toBuilder()
         .fullName("new"+user().getFullName())
         .emailAddress("new"+user().getEmailAddress())
         .telephone("1-"+user().getTelephone())
         .isEnabled(true)
         .im("new"+user().getIM())
         .isAlertEnabled(true)
         .alertEmailPrefix("new"+user().getAlertEmailPrefix())
         .alertEmail("new"+user().getAlertEmail())
         .storedVmQuota(1)
         .deployedVmQuota(1)
         .password("newPassword")
         .build();
   }
   
   public static final User updateUser() {
      return updateUserSource().toBuilder()
         .password(null)
         .build();
   }
 
   @Test
   public void testDeleteUser() {
      VCloudDirectorAdminClient client = requestsSendResponses(loginRequest, sessionResponse, 
            new VcloudHttpRequestPrimer()
               .apiCommand("DELETE", "/admin/user/b37223f3-8792-477a-820f-334998f61cd6")
               .acceptAnyMedia()
               .httpRequestBuilder().build(), 
            new VcloudHttpResponsePrimer()
               .httpResponseBuilder().statusCode(204).build());
      
      client.getUserClient().deleteUser(userRef.getHref());
   }
   
   @Test
   public void testUnlockUser() {
      VCloudDirectorAdminClient client = requestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer()
                  .apiCommand("POST", "/admin/user/b37223f3-8792-477a-820f-334998f61cd6/action/unlock")
                  .acceptAnyMedia()
                  .httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer()
                  .httpResponseBuilder().statusCode(204).build());

      client.getUserClient().unlockUser(userRef.getHref());
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testUnlockUserFailNotFound() {
      VCloudDirectorAdminClient client = requestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer()
                  .apiCommand("POST", "/admin/user/b37223f3-8792-477a-820f-334998f61cd6/action/unlock")
                  .acceptAnyMedia()
                  .httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer()
                  .httpResponseBuilder().statusCode(403)
                  .payload(payloadFromResourceWithContentType("/org/error400.xml", VCloudDirectorMediaType.ERROR))
                  .build());

      client.getUserClient().unlockUser(userRef.getHref());
   }
}
