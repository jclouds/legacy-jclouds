/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.azure.storage.blob.blobstore;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Properties;

import org.jclouds.azure.storage.blob.AzureBlobAsyncClient;
import org.jclouds.azure.storage.blob.config.AzureBlobRestClientModule;
import org.jclouds.blobstore.BlobRequestSigner;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.Blob.Factory;
import org.jclouds.date.TimeStamp;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.RequiresHttp;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.RestContextSpec;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code AzureBlobRequestSigner}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "AzureBlobRequestSignerTest")
public class AzureBlobRequestSignerTest extends RestClientTest<AzureBlobAsyncClient> {

   private BlobRequestSigner signer;
   private Factory blobFactory;

   public void testSignGetBlob() throws ArrayIndexOutOfBoundsException, SecurityException, IllegalArgumentException,
            NoSuchMethodException, IOException {
      HttpRequest request = signer.signGetBlob("container", "name");

      assertRequestLineEquals(request, "GET https://identity.blob.core.windows.net/container/name HTTP/1.1");
      assertNonPayloadHeadersEqual(
               request,
               "Authorization: SharedKeyLite identity:nI6ca9CdLWhPoMuSzk4perqx5pzi2hx7YBJ92FCqeXM=\nDate: Thu, 05 Jun 2008 16:38:19 GMT\nx-ms-version: 2009-09-19\n");
      assertPayloadEquals(request, null, null, false);

      assertEquals(request.getFilters().size(), 0);
   }

   public void testSignRemoveBlob() throws ArrayIndexOutOfBoundsException, SecurityException, IllegalArgumentException,
            NoSuchMethodException, IOException {
      HttpRequest request = signer.signRemoveBlob("container", "name");

      assertRequestLineEquals(request, "DELETE https://identity.blob.core.windows.net/container/name HTTP/1.1");
      assertNonPayloadHeadersEqual(
               request,
               "Authorization: SharedKeyLite identity:+BDU2AGqS9LwevaKH+jaRKNrxTZ3LsFDpnnWFKpN0jc=\nDate: Thu, 05 Jun 2008 16:38:19 GMT\nx-ms-version: 2009-09-19\n");
      assertPayloadEquals(request, null, null, false);

      assertEquals(request.getFilters().size(), 0);
   }

   public void testSignPutBlob() throws ArrayIndexOutOfBoundsException, SecurityException, IllegalArgumentException,
            NoSuchMethodException, IOException {
      Blob blob = blobFactory.create(null);
      blob.getMetadata().setName("name");
      blob.setPayload("");
      blob.getPayload().getContentMetadata().setContentLength(2l);
      blob.getPayload().getContentMetadata().setContentMD5(new byte[] { 0, 2, 4, 8 });
      blob.getPayload().getContentMetadata().setContentType("text/plain");

      HttpRequest request = signer.signPutBlob("container", blob);

      assertRequestLineEquals(request, "PUT https://identity.blob.core.windows.net/container/name HTTP/1.1");
      assertNonPayloadHeadersEqual(
               request,
               "Authorization: SharedKeyLite identity:LT+HBNzhbRsZY07kC+/JxeuAURbxTmwJaIe464LO36c=\nDate: Thu, 05 Jun 2008 16:38:19 GMT\nx-ms-blob-type: BlockBlob\nx-ms-version: 2009-09-19\n");
      assertContentHeadersEqual(request, "text/plain", null, null, null, (long) 2l, new byte[] { 0, 2, 4, 8 });

      assertEquals(request.getFilters().size(), 0);
   }

   @BeforeClass
   protected void setupFactory() throws IOException {
      super.setupFactory();
      this.blobFactory = injector.getInstance(Blob.Factory.class);
      this.signer = injector.getInstance(BlobRequestSigner.class);
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
   protected Module createModule() {
      return new TestAzureBlobRestClientModule();
   }

   @RequiresHttp
   @ConfiguresRestClient
   private static final class TestAzureBlobRestClientModule extends AzureBlobRestClientModule {
      @Override
      protected void configure() {
         super.configure();
      }

      @Override
      protected String provideTimeStamp(@TimeStamp Supplier<String> cache) {
         return "Thu, 05 Jun 2008 16:38:19 GMT";
      }
   }

   @Override
   public RestContextSpec<?, ?> createContextSpec() {
      return new RestContextFactory().createContextSpec("azureblob", "identity", "credential", new Properties());
   }

}