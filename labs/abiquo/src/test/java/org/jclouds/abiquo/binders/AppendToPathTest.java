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

package org.jclouds.abiquo.binders;

import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.http.HttpRequest;
import org.testng.annotations.Test;

/**
 * Unit tests for the {@link AppendToPath} binder.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "AppendToPathTest")
public class AppendToPathTest {
   @Test(expectedExceptions = NullPointerException.class)
   public void testInvalidNullInput() {
      AppendToPath binder = new AppendToPath();
      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://localhost")).build();
      binder.bindToRequest(request, null);
   }

   public void testBindString() {
      AppendToPath binder = new AppendToPath();
      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://localhost")).build();
      HttpRequest newRequest = binder.bindToRequest(request, "expanded/path");
      assertEquals(newRequest.getRequestLine(), "GET http://localhost/expanded/path HTTP/1.1");
   }

   public void testBindNumber() {
      AppendToPath binder = new AppendToPath();
      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://localhost")).build();
      HttpRequest newRequest = binder.bindToRequest(request, 57);
      assertEquals(newRequest.getRequestLine(), "GET http://localhost/57 HTTP/1.1");
   }
}
