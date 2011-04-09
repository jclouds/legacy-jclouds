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
package org.jclouds.s3.binders;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.net.URI;

import javax.ws.rs.HttpMethod;

import org.jclouds.s3.BaseS3AsyncClientTest;
import org.jclouds.s3.domain.S3Object;
import org.jclouds.blobstore.binders.BindMapToHeadersWithPrefix;
import org.jclouds.http.HttpRequest;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;

/**
 * Tests behavior of {@code BindS3ObjectMetadataToRequest}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "BindS3ObjectMetadataToRequestTest")
public class BindS3ObjectMetadataToRequestTest extends BaseS3AsyncClientTest {

   @Test
   public void testPassWithMinimumDetailsAndPayload5GB() {
      S3Object object = injector.getInstance(S3Object.Factory.class).create(null);
      Payload payload = Payloads.newStringPayload("");
      payload.getContentMetadata().setContentLength(5368709120l);
      object.setPayload(payload);
      object.getMetadata().setKey("foo");

      HttpRequest request = HttpRequest.builder().method("PUT").endpoint(URI.create("http://localhost")).build();
      BindS3ObjectMetadataToRequest binder = injector.getInstance(BindS3ObjectMetadataToRequest.class);

      assertEquals(binder.bindToRequest(request, object),
            HttpRequest.builder().method("PUT").endpoint(URI.create("http://localhost")).build());
   }

   @Test
   public void testExtendedPropertiesBind() {
      S3Object object = injector.getInstance(S3Object.Factory.class).create(null);
      Payload payload = Payloads.newStringPayload("");
      payload.getContentMetadata().setContentLength(5368709120l);
      object.setPayload(payload);
      object.getMetadata().setKey("foo");
      object.getMetadata().setCacheControl("no-cache");

      HttpRequest request = HttpRequest.builder().method("PUT").endpoint(URI.create("http://localhost")).build();
      BindS3ObjectMetadataToRequest binder = injector.getInstance(BindS3ObjectMetadataToRequest.class);

      assertEquals(
            binder.bindToRequest(request, object),
            HttpRequest.builder().method("PUT").endpoint(URI.create("http://localhost"))
                  .headers(ImmutableMultimap.of("Cache-Control", "no-cache")).build());
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testNoContentLengthIsBad() {
      S3Object object = injector.getInstance(S3Object.Factory.class).create(null);
      Payload payload = Payloads.newStringPayload("");
      payload.getContentMetadata().setContentLength(null);
      object.setPayload(payload);
      object.getMetadata().setKey("foo");

      HttpRequest request = HttpRequest.builder().method("PUT").endpoint(URI.create("http://localhost")).build();
      BindS3ObjectMetadataToRequest binder = injector.getInstance(BindS3ObjectMetadataToRequest.class);
      binder.bindToRequest(request, object);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testNoKeyIsBad() {
      S3Object object = injector.getInstance(S3Object.Factory.class).create(null);
      Payload payload = Payloads.newStringPayload("");
      payload.getContentMetadata().setContentLength(5368709120000l);
      object.setPayload(payload);

      HttpRequest request = HttpRequest.builder().method("PUT").endpoint(URI.create("http://localhost")).build();
      BindS3ObjectMetadataToRequest binder = injector.getInstance(BindS3ObjectMetadataToRequest.class);
      binder.bindToRequest(request, object);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testOver5GBIsBad() {
      S3Object object = injector.getInstance(S3Object.Factory.class).create(null);
      Payload payload = Payloads.newStringPayload("");
      payload.getContentMetadata().setContentLength(5368709120000l);
      object.setPayload(payload);
      object.getMetadata().setKey("foo");

      HttpRequest request = HttpRequest.builder().method("PUT").endpoint(URI.create("http://localhost")).build();
      BindS3ObjectMetadataToRequest binder = injector.getInstance(BindS3ObjectMetadataToRequest.class);
      binder.bindToRequest(request, object);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testMustBeS3Object() {
      HttpRequest request = new HttpRequest(HttpMethod.POST, URI.create("http://localhost"));
      injector.getInstance(BindS3ObjectMetadataToRequest.class).bindToRequest(request, new File("foo"));
   }

   @Test(expectedExceptions = { NullPointerException.class, IllegalStateException.class })
   public void testNullIsBad() {
      BindMapToHeadersWithPrefix binder = new BindMapToHeadersWithPrefix("prefix:");
      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://momma")).build();
      binder.bindToRequest(request, null);
   }
}
