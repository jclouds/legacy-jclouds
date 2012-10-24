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

package org.jclouds.abiquo.binders.infrastructure.ucs;

import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.http.HttpRequest;
import org.testng.annotations.Test;

import com.abiquo.server.core.infrastructure.OrganizationDto;

/**
 * Unit tests for the {@link BindOrganizationParameters} binder.
 * 
 * @author Francesc Montserrat
 */
@Test(groups = "unit", testName = "BindOrganizationParametersTest")
public class BindOrganizationParametersTest {
   @Test(expectedExceptions = NullPointerException.class)
   public void testInvalidNullInput() {
      BindOrganizationParameters binder = new BindOrganizationParameters();
      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://localhost")).build();
      binder.bindToRequest(request, null);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testInvalidTypeInput() {
      BindOrganizationParameters binder = new BindOrganizationParameters();
      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://localhost")).build();
      binder.bindToRequest(request, new Object());
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testBindLogicServerWithEmptyName() {
      BindOrganizationParameters binder = new BindOrganizationParameters();
      OrganizationDto dto = new OrganizationDto();
      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://localhost")).build();
      binder.bindToRequest(request, dto);
   }

   public void testBindLogicServer() {
      BindOrganizationParameters binder = new BindOrganizationParameters();
      OrganizationDto dto = new OrganizationDto();
      dto.setDn("org");
      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://localhost")).build();
      HttpRequest newRequest = binder.bindToRequest(request, dto);
      assertEquals(newRequest.getRequestLine(), "GET http://localhost?org=org HTTP/1.1");
   }
}
