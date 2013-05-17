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

import static org.jclouds.blobstore.options.CreateContainerOptions.Builder.publicRead;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.domain.Location;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.util.Strings2;
import org.testng.SkipException;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * 
 * @author Adrian Cole
 */
public class BaseContainerLiveTest extends BaseBlobStoreIntegrationTest {

   private Location defaultLocation;

   @Test(groups = "live")
   public void testPublicAccess() throws InterruptedException, MalformedURLException, IOException {
      final String containerName = getScratchContainerName();
      try {
         view.getBlobStore().createContainerInLocation(null, containerName, publicRead());
         assertConsistencyAwareContainerExists(containerName);

         defaultLocation = Iterables.find(view.getBlobStore().list(), new Predicate<StorageMetadata>() {

            @Override
            public boolean apply(@Nullable StorageMetadata input) {
               return input.getName().equals(containerName);
            }

         }).getLocation();

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

   static Location findNonDefaultLocationOrSkip(BlobStore blobStore, Location defaultLocation) {
      List<? extends Location> locs = Lists.newArrayList(Iterables.filter(blobStore.listAssignableLocations(),
               Predicates.not(Predicates.equalTo(defaultLocation))));
      if (locs.size() == 0)
         throw new SkipException("No non-default location found in " + locs);
      // try to use a diverse location
      Collections.shuffle(locs);
      return locs.get(0);
   }

   @Test(groups = "live", dependsOnMethods = "testPublicAccess")
   public void testPublicAccessInNonDefaultLocation() throws InterruptedException, MalformedURLException, IOException {
      Location nonDefault = findNonDefaultLocationOrSkip(view.getBlobStore(), defaultLocation);

      String payload = "my data";
      runCreateContainerInLocation(payload, nonDefault);
   }

   @Test(groups = "live", dependsOnMethods = "testPublicAccess")
   public void testPublicAccessInNonDefaultLocationWithBigBlob() throws InterruptedException, MalformedURLException,
            IOException {
      Location nonDefault = findNonDefaultLocationOrSkip(view.getBlobStore(), defaultLocation);
      String payload = Strings.repeat("a", 1024 * 1024); // 1MB
      runCreateContainerInLocation(payload, nonDefault);
   }

   private void runCreateContainerInLocation(String payload, Location nonDefault) throws InterruptedException,
            IOException {
      String blobName = "hello";
      BlobStore blobStore = view.getBlobStore();
      final String containerName = getScratchContainerName();
      try {
         Logger.getAnonymousLogger().info(
                  String.format("creating public container %s in location %s", containerName, nonDefault.getId()));
         blobStore.createContainerInLocation(nonDefault, containerName, publicRead());
         assertConsistencyAwareContainerExists(containerName);
         assertConsistencyAwareContainerInLocation(containerName, nonDefault);

         blobStore.putBlob(containerName, blobStore.blobBuilder(blobName).payload(payload).build());

         assertConsistencyAwareContainerSize(containerName, 1);

         BlobMetadata metadata = view.getBlobStore().blobMetadata(containerName, blobName);
         assertEquals(Strings2.toStringAndClose(view.utils().http().get(metadata.getPublicUri())), payload);

         assertConsistencyAwareBlobInLocation(containerName, blobName, nonDefault);

      } finally {
         // this container is now public, so we can't reuse it directly
         recycleContainer(containerName);
      }
   }

}
