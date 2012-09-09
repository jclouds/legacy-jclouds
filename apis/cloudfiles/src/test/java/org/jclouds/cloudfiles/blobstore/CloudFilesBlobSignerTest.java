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

package org.jclouds.cloudfiles.blobstore;

import org.jclouds.blobstore.BlobRequestSigner;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.cloudfiles.CloudFilesClientTest;
import org.jclouds.http.HttpRequest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Date;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

/**
 * @author Andrei Savu
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "CloudFilesBlobSignerTest")
public class CloudFilesBlobSignerTest extends CloudFilesClientTest {

   private BlobRequestSigner signer;
   private Blob.Factory blobFactory;

   public void testSignGetBlobWithTime() {
      HttpRequest request = signer.signGetBlob("container", "name", 120);

      assertRequestLineEquals(request, "GET http://storage/container/name?" +
          "temp_url_sig=4759d99d13c826bba0af2c9f0c526ca53c95abaf&temp_url_expires=123456909 HTTP/1.1");
      assertFalse(request.getHeaders().containsKey("X-Auth-Token"));
      assertPayloadEquals(request, null, null, false);

      assertEquals(request.getFilters().size(), 0);
   }

   public void testSignPutBlobWithTime() throws Exception {
      Blob blob = blobFactory.create(null);

      blob.getMetadata().setName("name");
      blob.setPayload("");
      blob.getPayload().getContentMetadata().setContentLength(2l);
      blob.getPayload().getContentMetadata().setContentMD5(new byte[]{0, 2, 4, 8});
      blob.getPayload().getContentMetadata().setContentType("text/plain");
      blob.getPayload().getContentMetadata().setExpires(new Date(1000));

      HttpRequest request = signer.signPutBlob("container", blob, 120 /* seconds */);

      assertRequestLineEquals(request, "PUT http://storage/container/name?" +
          "temp_url_sig=490690286130adac9e7144d85b320a00b1bf9e2b&temp_url_expires=123456909 HTTP/1.1");

      assertFalse(request.getHeaders().containsKey("X-Auth-Token"));
      assertContentHeadersEqual(request, "text/plain", null, null, null, (long) 2l, new byte[]{0, 2, 4, 8}, new Date(1000));

      assertEquals(request.getFilters().size(), 0);
   }

   @BeforeClass
   protected void setupFactory() throws IOException {
      super.setupFactory();
      this.blobFactory = injector.getInstance(Blob.Factory.class);
      this.signer = injector.getInstance(BlobRequestSigner.class);
   }
}
