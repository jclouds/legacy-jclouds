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
package org.jclouds.blobstore.integration.internal;

import static org.jclouds.blobstore.options.CreateContainerOptions.Builder.publicRead;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.MalformedURLException;

import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
public class BaseContainerLiveTest extends BaseBlobStoreIntegrationTest {

   @Test(groups = { "live" })
   public void testPublicAccess() throws InterruptedException, MalformedURLException, IOException {
      final String containerName = getScratchContainerName();
      try {
         view.getBlobStore().createContainerInLocation(null, containerName, publicRead());
         assertConsistencyAwareContainerSize(containerName, 0);

         view.getBlobStore().putBlob(containerName,
                  view.getBlobStore().blobBuilder("hello").payload(TEST_STRING).build());
         assertConsistencyAwareContainerSize(containerName, 1);

         BlobMetadata metadata = view.getBlobStore().blobMetadata(containerName, "hello");

         assert metadata.getPublicUri() != null : metadata;

         assertEquals(Strings2.toStringAndClose(view.utils().http().get(metadata.getPublicUri())), TEST_STRING);

      } finally {
         // this container is now public, so we can't reuse it directly
         recycleContainer(containerName);
      }
   }
}