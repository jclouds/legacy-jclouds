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

package org.jclouds.rackspace.cloudfiles.blobstore;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Properties;

import org.jclouds.blobstore.BlobRequestSigner;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.Blob.Factory;
import org.jclouds.http.HttpRequest;
import org.jclouds.io.payloads.PhantomPayload;
import org.jclouds.rackspace.cloudfiles.CloudFilesAsyncClient;
import org.jclouds.rackspace.cloudfiles.config.CloudFilesRestClientModule;
import org.jclouds.rackspace.cloudservers.TestRackspaceAuthenticationRestClientModule;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.RestContextFactory.ContextSpec;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.Module;
import com.google.inject.TypeLiteral;

@Test(groups = "unit", testName = "cloudfiles.CloudFilesBlobRequestSignerTest")
public class CloudFilesBlobRequestSignerTest extends RestClientTest<CloudFilesAsyncClient> {

   private BlobRequestSigner signer;
   private Factory blobFactory;

   public void testSignGetBlob() throws ArrayIndexOutOfBoundsException, SecurityException, IllegalArgumentException,
            NoSuchMethodException, IOException {
      HttpRequest request = signer.signGetBlob("container", "name");

      assertRequestLineEquals(request, "GET http://storageUrl/container/name HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "X-Auth-Token: testtoken\n");
      assertPayloadEquals(request, null, null, false);

      assertEquals(request.getFilters().size(), 0);
   }

   public void testSignRemoveBlob() throws ArrayIndexOutOfBoundsException, SecurityException, IllegalArgumentException,
            NoSuchMethodException, IOException {
      HttpRequest request = signer.signRemoveBlob("container", "name");

      assertRequestLineEquals(request, "DELETE http://storageUrl/container/name HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "X-Auth-Token: testtoken\n");
      assertPayloadEquals(request, null, null, false);

      assertEquals(request.getFilters().size(), 0);
   }

   public void testSignPutBlob() throws ArrayIndexOutOfBoundsException, SecurityException, IllegalArgumentException,
            NoSuchMethodException, IOException {
      Blob blob = blobFactory.create(null);
      blob.getMetadata().setName("name");
      blob.setPayload(new PhantomPayload(2l, new byte[] { 0, 2, 4, 8 }));
      blob.getPayload().setContentType("text/plain");

      HttpRequest request = signer.signPutBlob("container", blob);

      assertRequestLineEquals(request, "PUT http://storageUrl/container/name HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "X-Auth-Token: testtoken\n");
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
   protected TypeLiteral<RestAnnotationProcessor<CloudFilesAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<CloudFilesAsyncClient>>() {
      };
   }

   @Override
   protected Module createModule() {
      return new CloudFilesRestClientModule(new TestRackspaceAuthenticationRestClientModule());
   }

   @Override
   public ContextSpec<?, ?> createContextSpec() {
      return new RestContextFactory().createContextSpec("cloudfiles", "identity", "credential", new Properties());
   }

}