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
package org.jclouds.aws.s3.blobstore.integration;

import static org.jclouds.blobstore.options.CreateContainerOptions.Builder.publicRead;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.NoSuchElementException;
import java.util.Set;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.domain.Location;
import org.jclouds.s3.blobstore.integration.S3ContainerLiveTest;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.common.base.Strings;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "AWSS3ContainerLiveTest")
public class AWSS3ContainerLiveTest extends S3ContainerLiveTest {
   public AWSS3ContainerLiveTest() {
      provider = "aws-s3";
   }

   @Test(groups = { "live" })
   public void testCreateBlobInLocation() throws InterruptedException, MalformedURLException, IOException {
      String payload = "my data";
      runCreateContainerInLocation(payload);
   }
   
   @Test(groups = { "live" })
   public void testCreateBigBlobInLocation() throws InterruptedException, MalformedURLException, IOException {
      String payload = Strings.repeat("a", 1024*1024); // 1MB
      runCreateContainerInLocation(payload);
   }
   
   @Test(groups = { "live" })
   private void runCreateContainerInLocation(String payload) throws InterruptedException, MalformedURLException, IOException {
      // Don't recycle the container; want to guarantee it doesn't already exist and thus is not in the wrong location
      String containerName = CONTAINER_PREFIX + "-in-loc";
      String blobName = "hello";
      BlobStore blobStore = view.getBlobStore();
      try {
         String locationId = "EU";
         Location location = findLocation(blobStore, locationId);
         blobStore.createContainerInLocation(location, containerName, publicRead());
         blobStore.putBlob(containerName, blobStore.blobBuilder(blobName).payload(payload).build());
         
         assertConsistencyAwareContainerSize(containerName, 1);

         BlobMetadata metadata = view.getBlobStore().blobMetadata(containerName, blobName);
         assertEquals(Strings2.toStringAndClose(view.utils().http().get(metadata.getPublicUri())), payload);

         assertConsistencyAwareBlobInLocation(containerName, blobName, location);

      } finally {
         blobStore.removeBlob(containerName, blobName);
         blobStore.deleteContainer(containerName);
      }
   }
   
   private Location findLocation(BlobStore blobStore, String id) {
      Set<? extends Location> locs = blobStore.listAssignableLocations();
      for (Location loc : locs) {
         if (loc.getId().equals(id)) {
            return loc;
         }
      }
      throw new NoSuchElementException("No location found with id '"+id+"'; contenders were "+locs);
   }

}
