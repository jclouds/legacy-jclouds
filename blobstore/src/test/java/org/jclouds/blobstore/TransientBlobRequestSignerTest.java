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
package org.jclouds.blobstore;

import static org.testng.Assert.assertEquals;

import java.io.IOException;

import javax.inject.Provider;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobBuilder;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.internal.BaseAsyncClientTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code LocalBlobRequestSigner}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "TransientBlobRequestSignerTest")
public class TransientBlobRequestSignerTest extends BaseAsyncClientTest<LocalAsyncBlobStore> {

   private BlobRequestSigner signer;
   private Provider<BlobBuilder> blobFactory;
   private final String endpoint = new TransientApiMetadata().getDefaultEndpoint().get();
   private final String containerName = "container";
   private final String blobName = "blob";
   private final String fullUrl = String.format("%s/%s/%s", endpoint, containerName, blobName);

   public void testSignGetBlob() throws ArrayIndexOutOfBoundsException, SecurityException, IllegalArgumentException,
            NoSuchMethodException, IOException {
      HttpRequest request = signer.signGetBlob(containerName, blobName);

      assertRequestLineEquals(request, "GET " + fullUrl + " HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Authorization: Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==\n");
      assertPayloadEquals(request, null, null, false);

      assertEquals(request.getFilters().size(), 0);
   }

   public void testSignRemoveBlob() throws ArrayIndexOutOfBoundsException, SecurityException, IllegalArgumentException,
            NoSuchMethodException, IOException {
      HttpRequest request = signer.signRemoveBlob(containerName, blobName);

      assertRequestLineEquals(request, "DELETE " + fullUrl + " HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Authorization: Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==\n");
      assertPayloadEquals(request, null, null, false);

      assertEquals(request.getFilters().size(), 0);
   }

   public void testSignPutBlob() throws ArrayIndexOutOfBoundsException, SecurityException, IllegalArgumentException,
            NoSuchMethodException, IOException {
      Blob blob = blobFactory.get().name(blobName).forSigning().contentLength(2l).contentMD5(new byte[] { 0, 2, 4, 8 })
               .contentType("text/plain").build();

      assertEquals(blob.getPayload().getContentMetadata().getContentMD5(), new byte[] { 0, 2, 4, 8 });

      HttpRequest request = signer.signPutBlob(containerName, blob);

      assertRequestLineEquals(request, "PUT " + fullUrl + " HTTP/1.1");
      assertNonPayloadHeadersEqual(
               request,
               "Authorization: Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==\nContent-Length: 2\nContent-MD5: AAIECA==\nContent-Type: text/plain\n");
      assertContentHeadersEqual(request, "text/plain", null, null, null, 2L, new byte[] { 0, 2, 4, 8 }, null);

      assertEquals(request.getFilters().size(), 0);
   }

   public void testSignPutBlobWithGenerate() throws ArrayIndexOutOfBoundsException, SecurityException,
            IllegalArgumentException, NoSuchMethodException, IOException {
      Blob blob = blobFactory.get().name(blobName).payload("foo").calculateMD5().contentType("text/plain").build();
      byte[] md5 = new byte[] { -84, -67, 24, -37, 76, -62, -8, 92, -19, -17, 101, 79, -52, -60, -92, -40 };
      
      assertEquals(blob.getPayload().getContentMetadata().getContentMD5(), md5);

      HttpRequest request = signer.signPutBlob(containerName, blob);

      assertRequestLineEquals(request, "PUT " + fullUrl + " HTTP/1.1");
      assertNonPayloadHeadersEqual(
               request,
               "Authorization: Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==\nContent-Length: 3\nContent-MD5: rL0Y20zC+Fzt72VPzMSk2A==\nContent-Type: text/plain\n");
      assertContentHeadersEqual(request, "text/plain", null, null, null, 3L, md5, null);

      assertEquals(request.getFilters().size(), 0);
   }

   @BeforeClass
   protected void setupFactory() throws IOException {
      super.setupFactory();
      this.blobFactory = injector.getProvider(BlobBuilder.class);
      this.signer = injector.getInstance(BlobRequestSigner.class);
   }

   @Override
   protected void checkFilters(HttpRequest request) {
   }

   @Override
   public ApiMetadata createApiMetadata() {
      return new TransientApiMetadata();
   }

}
