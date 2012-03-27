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
package org.jclouds.aws.ec2.binders;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.net.URI;

import javax.ws.rs.HttpMethod;

import org.jclouds.http.HttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code BindTagsToIndexedFormParams}
 * 
 * @author grkvlt@apache.org
 */
@Test(groups = "unit")
public class BindTagsToIndexedFormParamsTest {
   Injector injector = Guice.createInjector();
   BindTagsToIndexedFormParams binder = injector.getInstance(BindTagsToIndexedFormParams.class);

   public void test() {
      HttpRequest request = HttpRequest.builder().method("POST").endpoint(URI.create("http://localhost")).build();
      request = binder.bindToRequest(request, ImmutableMap.<String, String>builder().put("one", "alpha").put("two", "beta").build());
      assertEquals(request.getPayload().getRawContent(), "Tag.1.Key=one&Tag.1.Value=alpha&Tag.2.Key=two&Tag.2.Value=beta");
   }

   public void testEmpty() {
      HttpRequest request = HttpRequest.builder().method("POST").endpoint(URI.create("http://localhost")).build();
      request = binder.bindToRequest(request, ImmutableMap.<String, String>builder().put("empty", "").build());
      assertEquals(request.getPayload().getRawContent(), "Tag.1.Key=empty&Tag.1.Value=");
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testMustBeArray() {
      HttpRequest request = new HttpRequest(HttpMethod.POST, URI.create("http://localhost"));
      binder.bindToRequest(request, new File("foo"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testNullIsBad() {
      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://momma")).build();
      binder.bindToRequest(request, null);
   }
}