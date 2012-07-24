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
package org.jclouds.vcloud.filters;

import static org.testng.Assert.assertEquals;

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.http.HttpRequest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;

/**
 * @author Adrian Cole
 */
@Test(testName = "AddVCloudAuthorizationAndCookieToRequestTest")
public class AddVCloudAuthorizationAndCookieToRequestTest {

   private AddVCloudAuthorizationAndCookieToRequest filter;

   @BeforeTest
   void setUp() {
      filter = new AddVCloudAuthorizationAndCookieToRequest(new Supplier<String>() {
         public String get() {
            return "token";
         }
      });
   }

   @Test
   public void testApply() {
      HttpRequest request = HttpRequest.builder().method("GET").endpoint("http://localhost").build();
      request = filter.filter(request);
      assertEquals(request.getHeaders().size(), 2);
      assertEquals(request.getFirstHeaderOrNull(HttpHeaders.COOKIE), "vcloud-token=token");
      assertEquals(request.getFirstHeaderOrNull("x-vcloud-authorization"), "token");
   }

}
