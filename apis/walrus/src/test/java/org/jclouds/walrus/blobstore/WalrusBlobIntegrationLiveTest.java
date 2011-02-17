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

package org.jclouds.walrus.blobstore;

import java.io.IOException;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.s3.blobstore.integration.S3BlobIntegrationLiveTest;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "WalrusBlobIntegrationLiveTest")
public class WalrusBlobIntegrationLiveTest extends S3BlobIntegrationLiveTest {

   // no support for content encoding
   @Override
   protected void checkContentEncoding(Blob blob, String contentEncoding) {
      assert blob.getPayload().getContentMetadata().getContentEncoding() == null;
      assert blob.getMetadata().getContentMetadata().getContentEncoding() == null;
   }

   // no support for content language
   @Override
   protected void checkContentLanguage(Blob blob, String contentLanguage) {
      assert blob.getPayload().getContentMetadata().getContentLanguage() == null;
      assert blob.getMetadata().getContentMetadata().getContentLanguage() == null;
   }

   // double range not supported
   @Test(groups = { "integration", "live" })
   @Override
   public void testGetTwoRanges() throws InterruptedException, IOException {

   }

   // range not supported
   @Test(groups = { "integration", "live" })
   @Override
   public void testGetRange() throws InterruptedException, IOException {
   }
}
