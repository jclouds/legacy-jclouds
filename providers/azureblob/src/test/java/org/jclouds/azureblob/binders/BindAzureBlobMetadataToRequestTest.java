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
package org.jclouds.azureblob.binders;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.net.URI;
import java.util.Properties;

import javax.ws.rs.HttpMethod;

import org.jclouds.azureblob.AzureBlobAsyncClient;
import org.jclouds.azureblob.domain.AzureBlob;
import org.jclouds.http.HttpRequest;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.RestContextSpec;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code BindAzureBlobMetadataToRequest}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "BindAzureBlobMetadataToRequestTest")
public class BindAzureBlobMetadataToRequestTest extends RestClientTest<AzureBlobAsyncClient> {

   @Test
   public void testPassWithMinimumDetailsAndPayload64MB() {
      AzureBlob blob = injector.getInstance(AzureBlob.Factory.class).create(null);
      Payload payload = Payloads.newStringPayload("");
      payload.getContentMetadata().setContentLength(64 * 1024 * 1024l);
      blob.setPayload(payload);
      blob.getProperties().setName("foo");

      HttpRequest request = HttpRequest.builder().method("PUT").endpoint(URI.create("http://localhost")).build();
      BindAzureBlobMetadataToRequest binder = injector.getInstance(BindAzureBlobMetadataToRequest.class);

      assertEquals(
            binder.bindToRequest(request, blob),
            HttpRequest.builder().method("PUT").endpoint(URI.create("http://localhost"))
                  .headers(ImmutableMultimap.of("x-ms-blob-type", "BlockBlob")).build());
   }

   @Test
   public void testExtendedPropertiesBind() {
      AzureBlob blob = injector.getInstance(AzureBlob.Factory.class).create(null);
      Payload payload = Payloads.newStringPayload("");
      payload.getContentMetadata().setContentLength(64 * 1024 * 1024l);
      blob.setPayload(payload);
      blob.getProperties().setName("foo");
      blob.getProperties().setMetadata(ImmutableMap.of("foo", "bar"));

      HttpRequest request = HttpRequest.builder().method("PUT").endpoint(URI.create("http://localhost")).build();
      BindAzureBlobMetadataToRequest binder = injector.getInstance(BindAzureBlobMetadataToRequest.class);

      assertEquals(
            binder.bindToRequest(request, blob),
            HttpRequest.builder().method("PUT").endpoint(URI.create("http://localhost"))
                  .headers(ImmutableMultimap.of("x-ms-blob-type", "BlockBlob", "x-ms-meta-foo", "bar")).build());
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testNoContentLengthIsBad() {
      AzureBlob blob = injector.getInstance(AzureBlob.Factory.class).create(null);
      Payload payload = Payloads.newStringPayload("");
      payload.getContentMetadata().setContentLength(null);
      blob.setPayload(payload);
      blob.getProperties().setName("foo");

      HttpRequest request = HttpRequest.builder().method("PUT").endpoint(URI.create("http://localhost")).build();
      BindAzureBlobMetadataToRequest binder = injector.getInstance(BindAzureBlobMetadataToRequest.class);
      binder.bindToRequest(request, blob);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testNoNameIsBad() {
      AzureBlob blob = injector.getInstance(AzureBlob.Factory.class).create(null);
      Payload payload = Payloads.newStringPayload("");
      payload.getContentMetadata().setContentLength(5368709120000l);
      blob.setPayload(payload);

      HttpRequest request = HttpRequest.builder().method("PUT").endpoint(URI.create("http://localhost")).build();
      BindAzureBlobMetadataToRequest binder = injector.getInstance(BindAzureBlobMetadataToRequest.class);
      binder.bindToRequest(request, blob);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testOver64MBIsBad() {
      AzureBlob blob = injector.getInstance(AzureBlob.Factory.class).create(null);
      Payload payload = Payloads.newStringPayload("");
      payload.getContentMetadata().setContentLength(64 * 1024 * 1024l + 1);
      blob.setPayload(payload);
      blob.getProperties().setName("foo");

      HttpRequest request = HttpRequest.builder().method("PUT").endpoint(URI.create("http://localhost")).build();
      BindAzureBlobMetadataToRequest binder = injector.getInstance(BindAzureBlobMetadataToRequest.class);
      binder.bindToRequest(request, blob);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testMustBeAzureBlob() {
      HttpRequest request = new HttpRequest(HttpMethod.POST, URI.create("http://localhost"));
      injector.getInstance(BindAzureBlobMetadataToRequest.class).bindToRequest(request, new File("foo"));
   }

   @Test(expectedExceptions = { NullPointerException.class, IllegalStateException.class })
   public void testNullIsBad() {
      BindAzureBlobMetadataToRequest binder = injector.getInstance(BindAzureBlobMetadataToRequest.class);
      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://momma")).build();
      binder.bindToRequest(request, null);
   }

   @Override
   protected void checkFilters(HttpRequest request) {
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<AzureBlobAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<AzureBlobAsyncClient>>() {
      };
   }

   @Override
   public RestContextSpec<?, ?> createContextSpec() {
      return new RestContextFactory().createContextSpec("azureblob", "identity", "credential", new Properties());
   }
}
