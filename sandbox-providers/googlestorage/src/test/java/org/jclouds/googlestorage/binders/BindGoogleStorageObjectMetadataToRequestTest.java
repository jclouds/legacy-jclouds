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
package org.jclouds.googlestorage.binders;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.net.URI;

import javax.ws.rs.HttpMethod;

import org.jclouds.http.HttpRequest;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.s3.S3AsyncClient;
import org.jclouds.s3.domain.S3Object;
import org.jclouds.s3.internal.BaseS3AsyncClientTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code BindGoogleStorageObjectMetadataToRequest}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "BindGoogleStorageObjectMetadataToRequestTest")
public class BindGoogleStorageObjectMetadataToRequestTest extends BaseS3AsyncClientTest<S3AsyncClient> {

   @Override
   protected TypeLiteral<RestAnnotationProcessor<S3AsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<S3AsyncClient>>() {
      };
   }

   @Test
   public void testPassWithMinimumDetailsAndPayload5GB() {
      S3Object object = injector.getInstance(S3Object.Factory.class).create(null);
      Payload payload = Payloads.newStringPayload("");
      payload.getContentMetadata().setContentLength(5 * 1024 * 1024 * 1024l);
      object.setPayload(payload);
      object.getMetadata().setKey("foo");

      HttpRequest request = HttpRequest.builder().method("PUT").endpoint(URI.create("http://localhost")).build();
      BindGoogleStorageObjectMetadataToRequest binder = injector
               .getInstance(BindGoogleStorageObjectMetadataToRequest.class);

      assertEquals(binder.bindToRequest(request, object), HttpRequest.builder().method("PUT").endpoint(
               URI.create("http://localhost")).build());
   }

   @Test
   public void testExtendedPropertiesBind() {
      S3Object object = injector.getInstance(S3Object.Factory.class).create(null);
      Payload payload = Payloads.newStringPayload("");
      payload.getContentMetadata().setContentLength(5 * 1024 * 1024 * 1024l);
      object.setPayload(payload);
      object.getMetadata().setKey("foo");
      object.getMetadata().getUserMetadata().putAll(ImmutableMap.of("foo", "bar"));

      HttpRequest request = HttpRequest.builder().method("PUT").endpoint(URI.create("http://localhost")).build();
      BindGoogleStorageObjectMetadataToRequest binder = injector
               .getInstance(BindGoogleStorageObjectMetadataToRequest.class);

      assertEquals(binder.bindToRequest(request, object), HttpRequest.builder().method("PUT").endpoint(
               URI.create("http://localhost")).headers(ImmutableMultimap.of("x-amz-meta-foo", "bar")).build());
   }

   public void testNoContentLengthIsChunked() {
      S3Object object = injector.getInstance(S3Object.Factory.class).create(null);
      Payload payload = Payloads.newStringPayload("");
      payload.getContentMetadata().setContentLength(null);
      object.setPayload(payload);
      object.getMetadata().setKey("foo");

      HttpRequest request = HttpRequest.builder().method("PUT").endpoint(URI.create("http://localhost")).build();
      BindGoogleStorageObjectMetadataToRequest binder = injector
               .getInstance(BindGoogleStorageObjectMetadataToRequest.class);

      assertEquals(binder.bindToRequest(request, object), HttpRequest.builder().method("PUT").endpoint(
               URI.create("http://localhost")).headers(ImmutableMultimap.of("Transfer-Encoding", "chunked")).build());
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testNoNameIsBad() {
      S3Object object = injector.getInstance(S3Object.Factory.class).create(null);
      Payload payload = Payloads.newStringPayload("");
      payload.getContentMetadata().setContentLength(5368709120000l);
      object.setPayload(payload);

      HttpRequest request = HttpRequest.builder().method("PUT").endpoint(URI.create("http://localhost")).build();
      BindGoogleStorageObjectMetadataToRequest binder = injector
               .getInstance(BindGoogleStorageObjectMetadataToRequest.class);
      binder.bindToRequest(request, object);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testOver5GBIsBad() {
      S3Object object = injector.getInstance(S3Object.Factory.class).create(null);
      Payload payload = Payloads.newStringPayload("");
      payload.getContentMetadata().setContentLength(5 * 1024 * 1024 * 1024l + 1);
      object.setPayload(payload);
      object.getMetadata().setKey("foo");

      HttpRequest request = HttpRequest.builder().method("PUT").endpoint(URI.create("http://localhost")).build();
      BindGoogleStorageObjectMetadataToRequest binder = injector
               .getInstance(BindGoogleStorageObjectMetadataToRequest.class);
      binder.bindToRequest(request, object);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testMustBeS3Object() {
      HttpRequest request = HttpRequest.builder().method(HttpMethod.POST).endpoint(URI.create("http://localhost")).build();
      injector.getInstance(BindGoogleStorageObjectMetadataToRequest.class).bindToRequest(request, new File("foo"));
   }

   @Test(expectedExceptions = { NullPointerException.class, IllegalStateException.class })
   public void testNullIsBad() {
      BindGoogleStorageObjectMetadataToRequest binder = injector
               .getInstance(BindGoogleStorageObjectMetadataToRequest.class);
      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://momma")).build();
      binder.bindToRequest(request, null);
   }

}
