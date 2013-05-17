/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.azureblob.binders;

import static org.testng.Assert.assertEquals;

import java.io.File;

import org.jclouds.azureblob.AzureBlobAsyncClient;
import org.jclouds.azureblob.AzureBlobProviderMetadata;
import org.jclouds.azureblob.domain.AzureBlob;
import org.jclouds.http.HttpRequest;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.rest.internal.BaseAsyncClientTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * Tests behavior of {@code BindAzureBlobMetadataToRequest}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "BindAzureBlobMetadataToRequestTest")
public class BindAzureBlobMetadataToRequestTest extends BaseAsyncClientTest<AzureBlobAsyncClient> {

   @Test
   public void testPassWithMinimumDetailsAndPayload64MB() {
      AzureBlob blob = injector.getInstance(AzureBlob.Factory.class).create(null);
      Payload payload = Payloads.newStringPayload("");
      payload.getContentMetadata().setContentLength(64 * 1024 * 1024l);
      blob.setPayload(payload);
      blob.getProperties().setName("foo");

      HttpRequest request = HttpRequest.builder().method("PUT").endpoint("http://localhost").build();
      BindAzureBlobMetadataToRequest binder = injector.getInstance(BindAzureBlobMetadataToRequest.class);

      assertEquals(
            binder.bindToRequest(request, blob),
            HttpRequest.builder().method("PUT").endpoint("http://localhost")
                  .addHeader("x-ms-blob-type", "BlockBlob").build());
   }

   @Test
   public void testExtendedPropertiesBind() {
      AzureBlob blob = injector.getInstance(AzureBlob.Factory.class).create(null);
      Payload payload = Payloads.newStringPayload("");
      payload.getContentMetadata().setContentLength(64 * 1024 * 1024l);
      blob.setPayload(payload);
      blob.getProperties().setName("foo");
      blob.getProperties().setMetadata(ImmutableMap.of("foo", "bar"));

      HttpRequest request = HttpRequest.builder().method("PUT").endpoint("http://localhost").build();
      BindAzureBlobMetadataToRequest binder = injector.getInstance(BindAzureBlobMetadataToRequest.class);

      assertEquals(
            binder.bindToRequest(request, blob),
            HttpRequest.builder().method("PUT").endpoint("http://localhost")
                       .addHeader("x-ms-blob-type", "BlockBlob")
                       .addHeader("x-ms-meta-foo", "bar").build());
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testNoContentLengthIsBad() {
      AzureBlob blob = injector.getInstance(AzureBlob.Factory.class).create(null);
      Payload payload = Payloads.newStringPayload("");
      payload.getContentMetadata().setContentLength(null);
      blob.setPayload(payload);
      blob.getProperties().setName("foo");

      HttpRequest request = HttpRequest.builder().method("PUT").endpoint("http://localhost").build();
      BindAzureBlobMetadataToRequest binder = injector.getInstance(BindAzureBlobMetadataToRequest.class);
      binder.bindToRequest(request, blob);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testNoNameIsBad() {
      AzureBlob blob = injector.getInstance(AzureBlob.Factory.class).create(null);
      Payload payload = Payloads.newStringPayload("");
      payload.getContentMetadata().setContentLength(5368709120000l);
      blob.setPayload(payload);

      HttpRequest request = HttpRequest.builder().method("PUT").endpoint("http://localhost").build();
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

      HttpRequest request = HttpRequest.builder().method("PUT").endpoint("http://localhost").build();
      BindAzureBlobMetadataToRequest binder = injector.getInstance(BindAzureBlobMetadataToRequest.class);
      binder.bindToRequest(request, blob);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testMustBeAzureBlob() {
      HttpRequest request = HttpRequest.builder().method("POST").endpoint("http://localhost").build();;
      injector.getInstance(BindAzureBlobMetadataToRequest.class).bindToRequest(request, new File("foo"));
   }

   @Test(expectedExceptions = { NullPointerException.class, IllegalStateException.class })
   public void testNullIsBad() {
      BindAzureBlobMetadataToRequest binder = injector.getInstance(BindAzureBlobMetadataToRequest.class);
      HttpRequest request = HttpRequest.builder().method("GET").endpoint("http://momma").build();
      binder.bindToRequest(request, null);
   }

   @Override
   protected void checkFilters(HttpRequest request) {
   }

   @Override
   public AzureBlobProviderMetadata createProviderMetadata() {
      return new AzureBlobProviderMetadata();
   }
}
