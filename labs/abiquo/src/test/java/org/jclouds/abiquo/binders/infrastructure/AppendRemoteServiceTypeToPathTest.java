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

package org.jclouds.abiquo.binders.infrastructure;

import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.abiquo.functions.infrastructure.ParseRemoteServiceType;
import org.jclouds.http.HttpRequest;
import org.testng.annotations.Test;

import com.abiquo.model.enumerator.RemoteServiceType;

/**
 * Unit tests for the {@link AppendRemoteServiceTypeToPath} binder.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "AppendRemoteServiceTypeToPathTest")
public class AppendRemoteServiceTypeToPathTest {
   @Test(expectedExceptions = NullPointerException.class)
   public void testGetValueWithNullInput() {
      AppendRemoteServiceTypeToPath binder = new AppendRemoteServiceTypeToPath(new ParseRemoteServiceType());
      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://localhost")).build();
      binder.getValue(request, null);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testGetValueWithInvalidInput() {
      AppendRemoteServiceTypeToPath binder = new AppendRemoteServiceTypeToPath(new ParseRemoteServiceType());
      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://localhost")).build();
      binder.getValue(request, new Object());
   }

   public void testGetValue() {
      AppendRemoteServiceTypeToPath binder = new AppendRemoteServiceTypeToPath(new ParseRemoteServiceType());
      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://localhost")).build();

      assertEquals(binder.getValue(request, RemoteServiceType.VIRTUAL_SYSTEM_MONITOR), "virtualsystemmonitor");
   }
}
