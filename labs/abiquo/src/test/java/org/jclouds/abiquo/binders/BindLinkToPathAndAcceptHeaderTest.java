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
import static org.testng.Assert.assertNotNull;

import java.net.URI;

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.http.HttpRequest;
import org.testng.annotations.Test;

/**
 * Unit tests for the {@link BindLinkToPathAndAcceptHeader} class.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "BindLinkToPathAndAcceptHeaderTest")
public class BindLinkToPathAndAcceptHeaderTest {
   @Test(expectedExceptions = NullPointerException.class)
   public void testInvalidNullInput() {
      BindLinkToPathAndAcceptHeader binder = new BindLinkToPathAndAcceptHeader();
      binder.addHeader(null, HttpHeaders.ACCEPT, null);
   }

   public void testAddHeader() {
      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://localhost")).build();

      BindLinkToPathAndAcceptHeader binder = new BindLinkToPathAndAcceptHeader();
      HttpRequest updatedRequest = binder.addHeader(request, HttpHeaders.ACCEPT,
            "application/vnd.abiquo.datacenters+xml");

      String accept = updatedRequest.getFirstHeaderOrNull(HttpHeaders.ACCEPT);

      assertNotNull(accept);
      assertEquals(accept, "application/vnd.abiquo.datacenters+xml");
   }
}
