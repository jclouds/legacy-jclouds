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
package org.jclouds.atmos.blobstore.integration;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.integration.internal.BaseBlobIntegrationTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = { "integration", "live" })
public class AtmosIntegrationLiveTest extends BaseBlobIntegrationTest {
   public AtmosIntegrationLiveTest() {
      provider = "atmos";
   }
   @DataProvider(name = "delete")
   // no unicode support
   @Override
   public Object[][] createData() {
      return new Object[][] { { "normal" } };
   }

   @Override
   @Test(enabled = false)
   public void testGetIfMatch() {
      // no etag support
   }

   @Override
   @Test(enabled = false)
   public void testGetIfModifiedSince() {
      // not supported
   }

   @Override
   @Test(enabled = false)
   public void testGetIfNoneMatch() {
      // no etag support
   }

   @Override
   @Test(enabled = false)
   public void testGetIfUnmodifiedSince() {
      // not supported
   }

   @Override
   @Test(enabled = false)
   public void testGetRange() {
      // TODO this should work
   }

   @Override
   @Test(enabled = false)
   public void testGetTwoRanges() {
      // not supported
   }

   // not supported
   @Override
   protected void checkContentDisposition(Blob blob, String contentDisposition) {
      assert blob.getPayload().getContentMetadata().getContentDisposition() == null;
      assert blob.getMetadata().getContentMetadata().getContentDisposition() == null;
   }

   // not supported
   @Override
   protected void checkContentEncoding(Blob blob, String contentEncoding) {
      assert blob.getPayload().getContentMetadata().getContentEncoding() == null;
      assert blob.getMetadata().getContentMetadata().getContentEncoding() == null;
   }

   // not supported
   @Override
   protected void checkContentLanguage(Blob blob, String contentLanguage) {
      assert blob.getPayload().getContentMetadata().getContentLanguage() == null;
      assert blob.getMetadata().getContentMetadata().getContentLanguage() == null;
   }

   @Override
   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testPutObjectStream() throws InterruptedException, IOException, ExecutionException {
      super.testPutObjectStream();
   }

   // not supported
   @Override
   protected void checkMD5(BlobMetadata metadata) throws IOException {
      assertEquals(metadata.getContentMetadata().getContentMD5(), null);
   }

   @Test(enabled = false)
   // problem with the stub and md5, live is fine
   public void testMetadata() {
      // TODO
   }

   @Test(enabled = false)
   // problem with the stub and md5, live is fine
   public void testPutObject() throws Exception {
      // TODO
   }

}
