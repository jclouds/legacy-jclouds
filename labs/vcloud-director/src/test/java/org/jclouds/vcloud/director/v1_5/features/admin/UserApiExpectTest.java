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

import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.ADMIN_ORG;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.ENTITY;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.ERROR;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.ORG;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.USER;
import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.Collections;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.admin.VCloudDirectorAdminApi;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.User;
import org.jclouds.vcloud.director.v1_5.internal.VCloudDirectorAdminApiExpectTest;
import org.testng.annotations.Test;

import com.google.common.net.HttpHeaders;

/**
 * Test the {@link UserApi} by observing its side effects.
 * 
 * @author danikov, Adrian Cole
 */
@Test(groups = { "unit", "admin" }, singleThreaded = true, testName = "UserApiExpectTest")
public class UserApiExpectTest extends VCloudDirectorAdminApiExpectTest {
   
   private static String user = "7212e451-76e1-4631-b2de-ba1dfd8080e4";
   private static String userUrn = "urn:vcloud:user:" + user;
   private static URI userHref = URI.create(endpoint + "/user/" + user);
   
   private static String org = "7212e451-76e1-4631-b2de-asdasdasd";
   private static String orgUrn = "urn:vcloud:org:" + org;
   private static URI orgHref = URI.create(endpoint + "/org/" + org);
   private static URI orgAdminHref = URI.create(endpoint + "/admin/org/" + org);
   
   private HttpRequest create = HttpRequest.builder()
            .method("POST")
            .endpoint(orgAdminHref + "/users")
            .addHeader("Accept", USER)
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token)
            .payload(payloadFromResourceWithContentType("/user/createUserSource.xml", VCloudDirectorMediaType.USER))
            .build();

   private HttpResponse createResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResourceWithContentType("/user/createUser.xml", USER + ";version=1.5"))
            .build();
    
   @Test
   public void testCreateUserHref() {
      VCloudDirectorAdminApi api = requestsSendResponses(loginRequest, sessionResponse, create, createResponse);
      assertEquals(api.getUserApi().createUserInOrg(createUserSource(), orgAdminHref), createUser());
   }

   private HttpRequest resolveOrg = HttpRequest.builder()
            .method("GET")
            .endpoint(endpoint + "/entity/" + orgUrn)
            .addHeader("Accept", "*/*")
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token)
            .build();
   
   private String orgEntity = asString(createXMLBuilder("Entity").a("xmlns", "http://www.vmware.com/vcloud/v1.5")
                                                             .a("name", orgUrn)
                                                             .a("id", orgUrn)
                                                             .a("type", ENTITY)
                                                             .a("href", endpoint + "/entity/" + userUrn)
                                  .e("Link").a("rel", "alternate").a("type", ORG).a("href", orgHref.toString()).up()
                                  .e("Link").a("rel", "alternate").a("type", ADMIN_ORG).a("href", orgAdminHref.toString()).up());
   
   private HttpResponse resolveOrgResponse = HttpResponse.builder()
           .statusCode(200)
           .payload(payloadFromStringWithContentType(orgEntity, ENTITY + ";version=1.5"))
           .build();
   
   @Test
   public void testCreateUserUrn() {
      VCloudDirectorAdminApi api = requestsSendResponses(loginRequest, sessionResponse, resolveOrg, resolveOrgResponse, create, createResponse);
      assertEquals(api.getUserApi().createUserInOrg(createUserSource(), orgUrn), createUser());
   }
   
   HttpRequest get = HttpRequest.builder()
            .method("GET")
            .endpoint(userHref)
            .addHeader("Accept", "*/*")
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token)
            .build();

    HttpResponse getResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResourceWithContentType("/user/user.xml", ORG + ";version=1.5"))
            .build();
    
   @Test
   public void testGetUserHref() {
      VCloudDirectorAdminApi api = requestsSendResponses(loginRequest, sessionResponse, get, getResponse);
      assertEquals(api.getUserApi().get(userHref), user());
   }
   
   HttpRequest resolveUser = HttpRequest.builder()
            .method("GET")
            .endpoint(endpoint + "/entity/" + userUrn)
            .addHeader("Accept", "*/*")
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token)
            .build();
   
   String userEntity = asString(createXMLBuilder("Entity").a("xmlns", "http://www.vmware.com/vcloud/v1.5")
                                                             .a("name", userUrn)
                                                             .a("id", userUrn)
                                                             .a("type", ENTITY)
                                                             .a("href", endpoint + "/entity/" + userUrn)
                                  .e("Link").a("rel", "alternate").a("type", USER).a("href", userHref.toString()).up());
   
   HttpResponse resolveUserResponse = HttpResponse.builder()
           .statusCode(200)
           .payload(payloadFromStringWithContentType(userEntity, ENTITY + ";version=1.5"))
           .build();
   
   @Test
   public void testGetUserUrn() {
      VCloudDirectorAdminApi api = requestsSendResponses(loginRequest, sessionResponse, resolveUser, resolveUserResponse, get, getResponse);
      assertEquals(api.getUserApi().get(userUrn), user());
   }
   
   HttpRequest update = HttpRequest.builder()
            .method("PUT")
            .endpoint(userHref)
            .addHeader("Accept", USER)
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token)
            .payload(payloadFromResourceWithContentType("/user/updateUserSource.xml", USER))
            .build();

   HttpResponse updateResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResourceWithContentType("/user/updateUser.xml", USER))
            .build();

   @Test
   public void testUpdateUserHref() {
      VCloudDirectorAdminApi api = requestsSendResponses(loginRequest, sessionResponse, update, updateResponse);
      assertEquals(api.getUserApi().update(userHref, updateUserSource()), updateUser());
   }
   
   @Test
   public void testUpdateUserUrn() {
      VCloudDirectorAdminApi api = requestsSendResponses(loginRequest, sessionResponse, resolveUser, resolveUserResponse, update, updateResponse);
      assertEquals(api.getUserApi().update(userUrn, updateUserSource()), updateUser());
   }
   
   HttpRequest unlock = HttpRequest.builder()
            .method("POST")
            .endpoint(userHref + "/action/unlock")
            .addHeader("Accept", "*/*")
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token)
            .build();

   HttpResponse unlockResponse = HttpResponse.builder()
            .statusCode(204)
            .build();

   @Test
   public void testUnlockUserHref() {
      VCloudDirectorAdminApi api = requestsSendResponses(loginRequest, sessionResponse, unlock, unlockResponse);
      api.getUserApi().unlock(userHref);
   }
   
   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testUnlockUserHrefNotFound() {
      VCloudDirectorAdminApi api = requestsSendResponses(loginRequest, sessionResponse, unlock,  HttpResponse.builder()
               .statusCode(403)
               .payload(payloadFromResourceWithContentType("/org/error400.xml", ERROR))
               .build());
      api.getUserApi().unlock(userHref);
   }
   
   @Test
   public void testUnlockUserUrn() {
      VCloudDirectorAdminApi api = requestsSendResponses(loginRequest, sessionResponse, resolveUser, resolveUserResponse, unlock, unlockResponse);
      api.getUserApi().unlock(userUrn);
   }
   
   HttpRequest delete = HttpRequest.builder()
            .method("DELETE")
            .endpoint(userHref)
            .addHeader("Accept", "*/*")
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token).build();

   HttpResponse deleteResponse = HttpResponse.builder()
            .statusCode(200)
            .build();
      
   @Test
   public void testDeleteUserHref() {
      VCloudDirectorAdminApi api = requestsSendResponses(loginRequest, sessionResponse, delete, deleteResponse);
      api.getUserApi().delete(userHref);
   }

   @Test
   public void testDeleteUserUrn() {
      VCloudDirectorAdminApi api = requestsSendResponses(loginRequest, sessionResponse, resolveUser, resolveUserResponse, delete, deleteResponse);
      api.getUserApi().delete(userUrn);
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
   
   public static final User user() {
      return createUser().toBuilder()
         .nameInSource("test")
         .build();
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
 
}
