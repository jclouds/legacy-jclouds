/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.openstack.swift.binders;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.net.URI;

import javax.ws.rs.HttpMethod;

import org.jclouds.http.HttpRequest;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.openstack.swift.CommonSwiftAsyncClient;
import org.jclouds.openstack.swift.CommonSwiftClientTest;
import org.jclouds.openstack.swift.domain.SwiftObject;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code BindSwiftObjectMetadataToRequest}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "BindSwiftObjectMetadataToRequestTest")
public class BindSwiftObjectMetadataToRequestTest extends CommonSwiftClientTest<CommonSwiftAsyncClient> {
   @Override
   protected TypeLiteral<RestAnnotationProcessor<CommonSwiftAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<CommonSwiftAsyncClient>>() {
      };
   }

   @Test
   public void testPassWithMinimumDetailsAndPayload5GB() {
      SwiftObject object = injector.getInstance(SwiftObject.Factory.class).create(null);
      Payload payload = Payloads.newStringPayload("");
      payload.getContentMetadata().setContentLength(5 * 1024 * 1024 * 1024l);
      object.setPayload(payload);
      object.getInfo().setName("foo");

      HttpRequest request = HttpRequest.builder().method("PUT").endpoint(URI.create("http://localhost")).build();
      BindSwiftObjectMetadataToRequest binder = injector.getInstance(BindSwiftObjectMetadataToRequest.class);

      assertEquals(binder.bindToRequest(request, object), HttpRequest.builder().method("PUT").endpoint(
               URI.create("http://localhost")).build());
   }

   @Test
   public void testExtendedPropertiesBind() {
      SwiftObject object = injector.getInstance(SwiftObject.Factory.class).create(null);
      Payload payload = Payloads.newStringPayload("");
      payload.getContentMetadata().setContentLength(5 * 1024 * 1024 * 1024l);
      object.setPayload(payload);
      object.getInfo().setName("foo");
      object.getInfo().getMetadata().putAll(ImmutableMap.of("foo", "bar"));

      HttpRequest request = HttpRequest.builder().method("PUT").endpoint(URI.create("http://localhost")).build();
      BindSwiftObjectMetadataToRequest binder = injector.getInstance(BindSwiftObjectMetadataToRequest.class);

      assertEquals(binder.bindToRequest(request, object), HttpRequest.builder().method("PUT").endpoint(
               URI.create("http://localhost")).headers(ImmutableMultimap.of("X-Object-Meta-foo", "bar")).build());
   }

   public void testNoContentLengthIsChunked() {
      SwiftObject object = injector.getInstance(SwiftObject.Factory.class).create(null);
      Payload payload = Payloads.newStringPayload("");
      payload.getContentMetadata().setContentLength(null);
      object.setPayload(payload);
      object.getInfo().setName("foo");

      HttpRequest request = HttpRequest.builder().method("PUT").endpoint(URI.create("http://localhost")).build();
      BindSwiftObjectMetadataToRequest binder = injector.getInstance(BindSwiftObjectMetadataToRequest.class);

      assertEquals(binder.bindToRequest(request, object), HttpRequest.builder().method("PUT").endpoint(
               URI.create("http://localhost")).headers(ImmutableMultimap.of("Transfer-Encoding", "chunked")).build());
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testNoNameIsBad() {
      SwiftObject object = injector.getInstance(SwiftObject.Factory.class).create(null);
      Payload payload = Payloads.newStringPayload("");
      payload.getContentMetadata().setContentLength(5368709120000l);
      object.setPayload(payload);

      HttpRequest request = HttpRequest.builder().method("PUT").endpoint(URI.create("http://localhost")).build();
      BindSwiftObjectMetadataToRequest binder = injector.getInstance(BindSwiftObjectMetadataToRequest.class);
      binder.bindToRequest(request, object);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testOver5GBIsBad() {
      SwiftObject object = injector.getInstance(SwiftObject.Factory.class).create(null);
      Payload payload = Payloads.newStringPayload("");
      payload.getContentMetadata().setContentLength(5 * 1024 * 1024 * 1024l + 1);
      object.setPayload(payload);
      object.getInfo().setName("foo");

      HttpRequest request = HttpRequest.builder().method("PUT").endpoint(URI.create("http://localhost")).build();
      BindSwiftObjectMetadataToRequest binder = injector.getInstance(BindSwiftObjectMetadataToRequest.class);
      binder.bindToRequest(request, object);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testMustBeSwiftObject() {
      HttpRequest request = new HttpRequest(HttpMethod.POST, URI.create("http://localhost"));
      injector.getInstance(BindSwiftObjectMetadataToRequest.class).bindToRequest(request, new File("foo"));
   }

   @Test(expectedExceptions = { NullPointerException.class, IllegalStateException.class })
   public void testNullIsBad() {
      BindSwiftObjectMetadataToRequest binder = injector.getInstance(BindSwiftObjectMetadataToRequest.class);
      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://momma")).build();
      binder.bindToRequest(request, null);
   }

}
