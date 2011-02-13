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

package org.jclouds.blobstore.integration.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.crypto.CryptoStreams;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Tests integrated functionality of all PutObject commands.
 * <p/>
 * Each test uses a different container name, so it should be perfectly fine to run in parallel.
 * 
 * @author Adrian Cole
 */
@Test(groups = { "live" })
public class BaseBlobLiveTest extends BaseBlobStoreIntegrationTest {

   private static final String sysHttpStreamUrl = System.getProperty("jclouds.blobstore.httpstream.url");
   private static final String sysHttpStreamETag = System.getProperty("jclouds.blobstore.httpstream.md5");

   @Test
   @Parameters({ "jclouds.blobstore.httpstream.url", "jclouds.blobstore.httpstream.md5" })
   public void testCopyUrl(@Optional String httpStreamUrl, @Optional String httpStreamETag) throws Exception {
      httpStreamUrl = checkNotNull(httpStreamUrl != null ? httpStreamUrl : sysHttpStreamUrl, "httpStreamUrl");

      httpStreamETag = checkNotNull(httpStreamETag != null ? httpStreamETag : sysHttpStreamETag, "httpStreamMd5");

      String name = "hello";

      URL url = new URL(httpStreamUrl);
      byte[] md5 = CryptoStreams.hex(httpStreamETag);

      URLConnection connection = url.openConnection();
      long length = connection.getContentLength();
      InputStream input = connection.getInputStream();

      Blob blob = context.getBlobStore().blobBuilder(name).payload(input).contentLength(length).contentMD5(md5).build();
      String container = getContainerName();
      try {
         context.getBlobStore().putBlob(container, blob);
         checkMD5(container, name, md5);
      } finally {
         returnContainer(container);
      }
   }

   protected void checkMD5(String container, String name, byte[] md5) {
      assertEquals(context.getBlobStore().blobMetadata(container, name).getContentMetadata().getContentMD5(), md5);
   }

}