/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.features;

import static org.jclouds.abiquo.domain.DomainUtils.withHeader;

import java.io.IOException;
import java.lang.reflect.Method;

import org.jclouds.abiquo.domain.AdminResources;
import org.jclouds.abiquo.domain.EnterpriseResources;
import org.jclouds.http.functions.ParseXMLWithJAXB;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.abiquo.server.core.enterprise.PrivilegesDto;
import com.abiquo.server.core.enterprise.RoleDto;
import com.abiquo.server.core.enterprise.RolesDto;
import com.abiquo.server.core.enterprise.UserDto;
import com.google.inject.TypeLiteral;

/**
 * Tests annotation parsing of {@code AdminAsyncApi}
 * 
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 */
@Test(groups = "unit", testName = "AdminAsyncApiTest")
public class AdminAsyncApiTest extends BaseAbiquoAsyncApiTest<AdminAsyncApi> {
   /*********************** Role ***********************/

   public void testListRoles() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AdminAsyncApi.class.getMethod("listRoles");
      GeneratedHttpRequest request = processor.createRequest(method);

      assertRequestLineEquals(request, "GET http://localhost/api/admin/roles HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + RolesDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetRoleFromUser() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AdminAsyncApi.class.getMethod("getRole", UserDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, EnterpriseResources.userPut());

      assertRequestLineEquals(request, "GET http://localhost/api/admin/roles/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + RoleDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testCreateRole() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AdminAsyncApi.class.getMethod("createRole", RoleDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, AdminResources.rolePost());

      assertRequestLineEquals(request, "POST http://localhost/api/admin/roles HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + RoleDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, withHeader(AdminResources.rolePostPayload()), RoleDto.class,
            RoleDto.BASE_MEDIA_TYPE, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testDeleteRole() throws SecurityException, NoSuchMethodException {
      Method method = AdminAsyncApi.class.getMethod("deleteRole", RoleDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, AdminResources.rolePut());

      assertRequestLineEquals(request, "DELETE http://localhost/api/admin/roles/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testUpdateRole() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AdminAsyncApi.class.getMethod("updateRole", RoleDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, AdminResources.rolePut());

      assertRequestLineEquals(request, "PUT http://localhost/api/admin/roles/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + RoleDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, withHeader(AdminResources.rolePutPayload()), RoleDto.class, RoleDto.BASE_MEDIA_TYPE,
            false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetRoleById() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AdminAsyncApi.class.getMethod("getRole", Integer.class);
      GeneratedHttpRequest request = processor.createRequest(method, 1);

      assertRequestLineEquals(request, "GET http://localhost/api/admin/roles/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + RoleDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testListPrivilegesByRoles() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AdminAsyncApi.class.getMethod("listPrivileges", RoleDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, AdminResources.rolePut());

      assertRequestLineEquals(request, "GET http://localhost/api/admin/roles/1/action/privileges HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + PrivilegesDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   /*********************** Current User **********************/

   public void testGetCurrentUser() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AdminAsyncApi.class.getMethod("getCurrentUser");
      GeneratedHttpRequest request = processor.createRequest(method, 1);

      assertRequestLineEquals(request, "GET http://localhost/api/login HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + UserDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<AdminAsyncApi>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<AdminAsyncApi>>() {
      };
   }

}
