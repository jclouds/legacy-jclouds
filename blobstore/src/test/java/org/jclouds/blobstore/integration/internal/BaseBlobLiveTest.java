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
package org.jclouds.blobstore.integration.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.io.BaseEncoding.base16;
import static org.testng.Assert.assertEquals;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
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
   private static final String sysHttpStreamMD5 = System.getProperty("jclouds.blobstore.httpstream.md5");

   @Test
   @Parameters( { "jclouds.blobstore.httpstream.url", "jclouds.blobstore.httpstream.md5" })
   public void testCopyUrl(@Optional String httpStreamUrl, @Optional String httpStreamMD5) throws Exception {
      httpStreamUrl = checkNotNull(httpStreamUrl != null ? httpStreamUrl : sysHttpStreamUrl, "httpStreamUrl");

      httpStreamMD5 = checkNotNull(httpStreamMD5 != null ? httpStreamMD5 : sysHttpStreamMD5, "httpStreamMd5");

      HttpResponse response = view.utils().http().invoke(HttpRequest.builder().method("GET").endpoint(httpStreamUrl).build());
      long length = response.getPayload().getContentMetadata().getContentLength();

      String name = "hello";
      byte[] md5 = base16().lowerCase().decode(httpStreamMD5);

      Blob blob = view.getBlobStore().blobBuilder(name).payload(response.getPayload()).contentLength(length)
               .contentMD5(md5).build();
      String container = getContainerName();
      try {
         view.getBlobStore().putBlob(container, blob);
         checkMD5(container, name, md5);
      } finally {
         returnContainer(container);
      }
   }

   protected void checkMD5(String container, String name, byte[] md5) {
      assertEquals(view.getBlobStore().blobMetadata(container, name).getContentMetadata().getContentMD5(), md5);
   }

}
