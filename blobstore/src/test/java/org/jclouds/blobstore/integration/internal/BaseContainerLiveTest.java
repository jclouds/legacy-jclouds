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
         context.getBlobStore().createContainerInLocation(null, containerName, publicRead());
         assertConsistencyAwareContainerSize(containerName, 0);

         context.getBlobStore().putBlob(containerName,
                  context.getBlobStore().blobBuilder("hello").payload(TEST_STRING).build());
         assertConsistencyAwareContainerSize(containerName, 1);

         BlobMetadata metadata = context.getBlobStore().blobMetadata(containerName, "hello");

         assertEquals(Strings2.toStringAndClose(metadata.getPublicUri().toURL().openStream()), TEST_STRING);

      } finally {
         // this container is now public, so we can't reuse it directly
         recycleContainer(containerName);
      }
   }
}