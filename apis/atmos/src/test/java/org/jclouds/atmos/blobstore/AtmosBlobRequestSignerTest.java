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
package org.jclouds.atmos.blobstore;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Date;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.atmos.AtmosApiMetadata;
import org.jclouds.atmos.AtmosAsyncClient;
import org.jclouds.atmos.config.AtmosRestClientModule;
import org.jclouds.atmos.filters.SignRequest;
import org.jclouds.blobstore.BlobRequestSigner;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.Blob.Factory;
import org.jclouds.date.TimeStamp;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.internal.BaseAsyncClientTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.inject.Module;

/**
 * Tests behavior of {@code AtmosBlobRequestSigner}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "AtmosBlobRequestSignerTest")
public class AtmosBlobRequestSignerTest extends BaseAsyncClientTest<AtmosAsyncClient> {

   public AtmosBlobRequestSignerTest(){
      // this is base64 decoded in the signer;
      credential = "aaaabbbb"; 
   }
   
   private BlobRequestSigner signer;
   private Factory blobFactory;

   public void testSignGetBlob() throws ArrayIndexOutOfBoundsException, SecurityException, IllegalArgumentException,
            NoSuchMethodException, IOException {
      HttpRequest request = signer.signGetBlob("container", "name");

      assertRequestLineEquals(request, "GET https://accesspoint.atmosonline.com/rest/namespace/container/name HTTP/1.1");
      assertNonPayloadHeadersEqual(
               request,
               "Accept: */*\nDate: Thu, 05 Jun 2008 16:38:19 GMT\nx-emc-signature: DHDKwV6IPsJJvtrI9ktTiKq9us4=\nx-emc-uid: identity\n");
      assertPayloadEquals(request, null, null, false);

      assertEquals(request.getFilters().size(), 0);
   }

   public void testSignRemoveBlob() throws ArrayIndexOutOfBoundsException, SecurityException, IllegalArgumentException,
            NoSuchMethodException, IOException {
      HttpRequest request = signer.signRemoveBlob("container", "name");

      assertRequestLineEquals(request,
               "DELETE https://accesspoint.atmosonline.com/rest/namespace/container/name HTTP/1.1");
      assertNonPayloadHeadersEqual(
               request,
               "Accept: */*\nDate: Thu, 05 Jun 2008 16:38:19 GMT\nx-emc-signature: cPnxwSdWfIjChx8sox+43U9oo20=\nx-emc-uid: identity\n");
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
      blob.getPayload().getContentMetadata().setExpires(new Date(1000));
      
      HttpRequest request = signer.signPutBlob("container", blob);

      assertRequestLineEquals(request,
               "POST https://accesspoint.atmosonline.com/rest/namespace/container/name HTTP/1.1");
      assertNonPayloadHeadersEqual(
               request,
               "Accept: */*\nDate: Thu, 05 Jun 2008 16:38:19 GMT\nExpect: 100-continue\nx-emc-signature: 7Cbdnu+YA5rG9J/C9RlHk07mU7w=\nx-emc-uid: identity\n");

      assertContentHeadersEqual(request, "text/plain", null, null, null, 2L, new byte[] { 0, 2, 4, 8 }, new Date(1000));

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
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), SignRequest.class);
   }

   @Override
   protected Module createModule() {
      return new TestAtmosRestClientModule();
   }

      @ConfiguresRestClient
   private static final class TestAtmosRestClientModule extends AtmosRestClientModule {
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
   public ApiMetadata createApiMetadata() {
      return new AtmosApiMetadata();
   }

}
