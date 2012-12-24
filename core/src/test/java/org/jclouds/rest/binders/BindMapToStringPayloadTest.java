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
package org.jclouds.rest.binders;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.lang.reflect.Method;

import javax.ws.rs.PathParam;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Tests behavior of {@code BindMapToStringPayload}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class BindMapToStringPayloadTest {
   static interface TestPayload {
      @org.jclouds.rest.annotations.Payload("name {fooble}")
      void testPayload(@PathParam("foo") String path);

      void noPayload(String path);
      
      @Payload("%7B\"changePassword\":%7B\"adminPass\":\"{adminPass}\"%7D%7D")
      void changeAdminPass(@PayloadParam("adminPass") String adminPass);
   }

   @Test
   public void testCorrect() throws SecurityException, NoSuchMethodException {
      Method testPayload = TestPayload.class.getMethod("testPayload", String.class);
      GeneratedHttpRequest request = GeneratedHttpRequest.builder()
            .declaring(TestPayload.class).javaMethod(testPayload).args(ImmutableList.<Object> of("robot"))
            .method("POST").endpoint("http://localhost").build();

      GeneratedHttpRequest newRequest = binder()
            .bindToRequest(request, ImmutableMap.<String,Object>of("fooble", "robot"));

      assertEquals(newRequest.getRequestLine(), request.getRequestLine());
      assertEquals(newRequest.getPayload().getRawContent(), "name robot");
   }
   
   @Test
   public void testDecodes() throws SecurityException, NoSuchMethodException {
      Method testPayload = TestPayload.class.getMethod("changeAdminPass", String.class);
      GeneratedHttpRequest request = GeneratedHttpRequest.builder()
            .declaring(TestPayload.class).javaMethod(testPayload).args(ImmutableList.<Object> of("foo"))
            .method("POST").endpoint("http://localhost").build();

      GeneratedHttpRequest newRequest = binder()
            .bindToRequest(request, ImmutableMap.<String,Object>of("adminPass", "foo"));

      assertEquals(newRequest.getRequestLine(), request.getRequestLine());
      assertEquals(newRequest.getPayload().getRawContent(), "{\"changePassword\":{\"adminPass\":\"foo\"}}");
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testMustHavePayloadAnnotation() throws SecurityException, NoSuchMethodException {
      Method noPayload = TestPayload.class.getMethod("noPayload", String.class);
      GeneratedHttpRequest request = GeneratedHttpRequest.builder()
            .declaring(TestPayload.class).javaMethod(noPayload).args(ImmutableList.<Object> of("robot"))
            .method("POST").endpoint("http://localhost").build();
      binder().bindToRequest(request, ImmutableMap.<String,Object>of("fooble", "robot"));
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testMustBeMap() {
      BindMapToStringPayload binder = binder();
      HttpRequest request = HttpRequest.builder().method("POST").endpoint("http://localhost").build();
      binder.bindToRequest(request, new File("foo"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testNullIsBad() {
      BindMapToStringPayload binder = binder();
      HttpRequest request = HttpRequest.builder().method("GET").endpoint("http://momma").build();
      binder.bindToRequest(request, null);
   }

   public BindMapToStringPayload binder() {
      return new BindMapToStringPayload();
   }
}
