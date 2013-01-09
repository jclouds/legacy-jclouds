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

import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;

import org.jclouds.abiquo.rest.annotations.EndpointLink;
import org.jclouds.http.HttpRequest;
import org.jclouds.reflect.Invocation;
import org.jclouds.rest.binders.BindException;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.abiquo.model.rest.RESTLink;
import com.abiquo.model.transport.SingleResourceTransportDto;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.Invokable;

/**
 * Unit tests for the {@link BindToPath} binder.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "BindToPathTest")
public class BindToPathTest {
   @Test(expectedExceptions = NullPointerException.class)
   public void testInvalidNullRequest() throws SecurityException, NoSuchMethodException {
      BindToPath binder = new BindToPath();
      binder.bindToRequest(null, new Object());
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testInvalidRequestType() throws SecurityException, NoSuchMethodException {
      BindToPath binder = new BindToPath();
      binder.bindToRequest(HttpRequest.builder().method("m").endpoint("http://localhost").build(), new Object());
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testInvalidNullInput() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> withEndpointLink = Invokable.from(TestEndpointLink.class.getMethod("withEndpointLink", TestDto.class));
      GeneratedHttpRequest request = GeneratedHttpRequest.builder(TestEndpointLink.class)
            .invocation(Invocation.create(withEndpointLink, ImmutableList.<Object> of(new TestDto())))
            .method(HttpMethod.GET)
            .endpoint(URI.create("http://localhost")).build();

      BindToPath binder = new BindToPath();
      binder.bindToRequest(request, null);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testInvalidInputType() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> withEndpointLink = Invokable.from(TestEndpointLink.class.getMethod("withEndpointLink", TestDto.class));
      GeneratedHttpRequest request = GeneratedHttpRequest.builder(TestEndpointLink.class)
            .invocation(Invocation.create(withEndpointLink, ImmutableList.<Object> of(new TestDto())))
            .method(HttpMethod.GET)
            .endpoint(URI.create("http://localhost")).build();

      BindToPath binder = new BindToPath();
      binder.bindToRequest(request, new Object());
   }

   @Test(expectedExceptions = BindException.class)
   public void testAnnotationNotPresent() throws SecurityException, NoSuchMethodException {
      TestDto dto = new TestDto();
      Invokable<?, ?> withoutEndpointLink = Invokable.from(TestEndpointLink.class.getMethod("withoutEndpointLink", TestDto.class));
      GeneratedHttpRequest request = GeneratedHttpRequest.builder(TestEndpointLink.class)
            .invocation(Invocation.create(withoutEndpointLink, ImmutableList.<Object> of(dto)))
            .method(HttpMethod.GET)
            .endpoint(URI.create("http://localhost")).build();

      BindToPath binder = new BindToPath();
      binder.bindToRequest(request, dto);
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testLinkNotPresent() throws SecurityException, NoSuchMethodException {
      TestDto dto = new TestDto();
      Invokable<?, ?> withUnexistingLink = Invokable.from(TestEndpointLink.class.getMethod("withUnexistingLink", TestDto.class));
      GeneratedHttpRequest request = GeneratedHttpRequest.builder(TestEndpointLink.class)
            .invocation(Invocation.create(withUnexistingLink, ImmutableList.<Object> of(dto)))
            .method(HttpMethod.GET)
            .endpoint(URI.create("http://localhost")).build();

      BindToPath binder = new BindToPath();
      binder.bindToRequest(request, dto);
   }

   public void testBindWithoutParameters() throws SecurityException, NoSuchMethodException {
      TestDto dto = new TestDto();
      Invokable<?, ?> withEndpointLink = Invokable.from(TestEndpointLink.class.getMethod("withEndpointLink", TestDto.class));
      GeneratedHttpRequest request = GeneratedHttpRequest.builder(TestEndpointLink.class)
            .invocation(Invocation.create(withEndpointLink, ImmutableList.<Object> of(dto)))
            .method(HttpMethod.GET)
            .endpoint(URI.create("http://localhost")).build();

      BindToPath binder = new BindToPath();
      GeneratedHttpRequest newRequest = binder.bindToRequest(request, dto);
      assertEquals(newRequest.getRequestLine(), "GET http://linkuri HTTP/1.1");
   }

   public void testBindWithQueryParameters() throws SecurityException, NoSuchMethodException {
      TestDto dto = new TestDto();
      Invokable<?, ?> withEndpointLink = Invokable.from(TestEndpointLink.class.getMethod("withEndpointLink", TestDto.class));
      GeneratedHttpRequest request = GeneratedHttpRequest.builder(TestEndpointLink.class)
            .invocation(Invocation.create(withEndpointLink, ImmutableList.<Object> of(dto)))
            .method(HttpMethod.GET)
            .endpoint(URI.create("http://localhost?param=value")).build();

      BindToPath binder = new BindToPath();
      GeneratedHttpRequest newRequest = binder.bindToRequest(request, dto);
      assertEquals(newRequest.getRequestLine(), "GET http://linkuri?param=value HTTP/1.1");
   }

   static interface TestEndpointLink {
      @GET
      void withEndpointLink(@EndpointLink("edit") TestDto dto);

      @GET
      void withUnexistingLink(@EndpointLink("unexisting") TestDto dto);

      @GET
      void withoutEndpointLink(TestDto dto);
   }

   static class TestDto extends SingleResourceTransportDto {

      private static final long serialVersionUID = 5381713583837345158L;

      public TestDto() {
         addLink(new RESTLink("edit", "http://linkuri"));
      }

      @Override
      public String getMediaType() {
         return MediaType.APPLICATION_XML;
      }

      @Override
      public String getBaseMediaType() {
         return MediaType.APPLICATION_XML;
      }
   }
}
