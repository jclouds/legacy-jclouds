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
package org.jclouds.cloudfiles.blobstore.integration;

import java.io.IOException;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.openstack.swift.blobstore.integration.SwiftBlobIntegrationLiveTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live")
public class CloudFilesBlobIntegrationLiveTest extends SwiftBlobIntegrationLiveTest {
   public CloudFilesBlobIntegrationLiveTest(){
      provider = "cloudfiles";
   }
   
   @Override
   protected void checkContentDisposition(Blob blob, String contentDisposition) {
      assert blob.getPayload().getContentMetadata().getContentDisposition().startsWith(contentDisposition) : blob
               .getPayload().getContentMetadata().getContentDisposition();
      assert blob.getMetadata().getContentMetadata().getContentDisposition().startsWith(contentDisposition) : blob
               .getMetadata().getContentMetadata().getContentDisposition();
   }

   @Test(groups = { "integration", "live" })
   public void testChunksAreDeletedWhenMultipartBlobIsDeleted() throws IOException, InterruptedException {
      String containerName = getContainerName();
      try {
         BlobStore blobStore = view.getBlobStore();

         long countBefore = blobStore.countBlobs(containerName);
         String blobName = "deleteme.txt";
         addMultipartBlobToContainer(containerName, blobName);

         blobStore.removeBlob(containerName, blobName);
         long countAfter = blobStore.countBlobs(containerName);

         assertEquals(countAfter, countBefore);
      } finally {
          returnContainer(containerName);
      }
   }
}
