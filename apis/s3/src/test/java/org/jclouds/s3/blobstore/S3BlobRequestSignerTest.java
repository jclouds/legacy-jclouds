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
package org.jclouds.s3.blobstore;

import static org.testng.Assert.assertEquals;

import java.io.IOException;

import javax.inject.Provider;

import org.jclouds.blobstore.BlobRequestSigner;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobBuilder;
import org.jclouds.date.TimeStamp;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.RequiresHttp;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.s3.BaseS3AsyncClientTest;
import org.jclouds.s3.S3AsyncClient;
import org.jclouds.s3.S3Client;
import org.jclouds.s3.config.S3RestClientModule;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code S3BlobRequestSigner}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "S3BlobRequestSignerTest")
public class S3BlobRequestSignerTest extends BaseS3AsyncClientTest<S3AsyncClient> {

   @Override
   protected TypeLiteral<RestAnnotationProcessor<S3AsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<S3AsyncClient>>() {
      };
   }

   private BlobRequestSigner signer;
   private Provider<BlobBuilder> blobFactory;

   public void testSignGetBlob() throws ArrayIndexOutOfBoundsException, SecurityException, IllegalArgumentException,
            NoSuchMethodException, IOException {
      HttpRequest request = signer.signGetBlob("container", "name");

      assertRequestLineEquals(request, "GET https://container.s3.amazonaws.com/name HTTP/1.1");
      assertNonPayloadHeadersEqual(
               request,
               "Authorization: AWS identity:0uvBv1wEskuhFHYJF/L6kEV9A7o=\nDate: Thu, 05 Jun 2008 16:38:19 GMT\nHost: container.s3.amazonaws.com\n");
      assertPayloadEquals(request, null, null, false);

      assertEquals(request.getFilters().size(), 0);
   }

   public void testSignRemoveBlob() throws ArrayIndexOutOfBoundsException, SecurityException, IllegalArgumentException,
            NoSuchMethodException, IOException {
      HttpRequest request = signer.signRemoveBlob("container", "name");

      assertRequestLineEquals(request, "DELETE https://container.s3.amazonaws.com/name HTTP/1.1");
      assertNonPayloadHeadersEqual(
               request,
               "Authorization: AWS identity:4FnyjdX/ULdDMRbVlLNjZfEo9RQ=\nDate: Thu, 05 Jun 2008 16:38:19 GMT\nHost: container.s3.amazonaws.com\n");
      assertPayloadEquals(request, null, null, false);

      assertEquals(request.getFilters().size(), 0);
   }

   public void testSignPutBlob() throws ArrayIndexOutOfBoundsException, SecurityException, IllegalArgumentException,
            NoSuchMethodException, IOException {
      Blob blob = blobFactory.get().name("name").forSigning().contentLength(2l).contentMD5(new byte[] { 0, 2, 4, 8 }).contentType(
               "text/plain").build();

      HttpRequest request = signer.signPutBlob("container", blob);

      assertRequestLineEquals(request, "PUT https://container.s3.amazonaws.com/name HTTP/1.1");
      assertNonPayloadHeadersEqual(
               request,
               "Authorization: AWS identity:j9Dy/lmmvlCKjA4lkqZenLxMkR4=\nDate: Thu, 05 Jun 2008 16:38:19 GMT\nHost: container.s3.amazonaws.com\n");

      assertContentHeadersEqual(request, "text/plain", null, null, null, (long) 2l, new byte[] { 0, 2, 4, 8 });

      assertEquals(request.getFilters().size(), 0);
   }

   @BeforeClass
   protected void setupFactory() throws IOException {
      super.setupFactory();
      this.blobFactory = injector.getProvider(BlobBuilder.class);
      this.signer = injector.getInstance(BlobRequestSigner.class);
   }

   @Override
   protected Module createModule() {
      return new TestS3RestClientModule();
   }

   @RequiresHttp
   @ConfiguresRestClient
   private static final class TestS3RestClientModule extends S3RestClientModule<S3Client, S3AsyncClient> {

      public TestS3RestClientModule() {
         super(S3Client.class, S3AsyncClient.class);
      }

      @Override
      protected String provideTimeStamp(@TimeStamp Supplier<String> cache) {
         return "Thu, 05 Jun 2008 16:38:19 GMT";
      }
   }

}
