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
import java.text.ParseException;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Set;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.date.DateCodec;
import org.jclouds.date.internal.SimpleDateCodecFactory;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.domain.Location;
import org.jclouds.io.ContentMetadataCodec;
import org.jclouds.io.ContentMetadataCodec.DefaultContentMetadataCodec;
import org.jclouds.s3.blobstore.integration.S3ContainerLiveTest;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.inject.AbstractModule;
import com.google.inject.Module;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "AWSS3ContainerLiveTest")
public class AWSS3ContainerLiveTest extends S3ContainerLiveTest {
   public AWSS3ContainerLiveTest() {
      provider = "aws-s3";
   }

   @Test(groups = { "live" })
   public void testCreateBlobWithExpiry() throws InterruptedException, MalformedURLException, IOException {
      final String containerName = getScratchContainerName();
      BlobStore blobStore = view.getBlobStore();
      try {
         final String blobName = "hello";
         final Date expires = new Date( (System.currentTimeMillis() / 1000) * 1000 + 60*1000);
         
         blobStore.createContainerInLocation(null, containerName, publicRead());
         blobStore.putBlob(containerName, blobStore.blobBuilder(blobName).payload(TEST_STRING).expires(expires).build());

         assertConsistencyAwareBlobExpiryMetadata(containerName, blobName, expires);

      } finally {
         recycleContainer(containerName);
      }
   }
   
   @Test(groups = { "live" })
   public void testCreateBlobWithMalformedExpiry() throws InterruptedException, MalformedURLException, IOException {
      // Create a blob that has a malformed Expires value; requires overriding the ContentMetadataCodec in Guice...
      final ContentMetadataCodec contentMetadataCodec = new DefaultContentMetadataCodec(new SimpleDateCodecFactory(new SimpleDateFormatDateService())) {
         @Override
         protected DateCodec getExpiresDateCodec() {
            return new DateCodec() {
               @Override public Date toDate(String date) throws ParseException {
                  return new Date();
               }
               @Override public String toString(Date date) {
                  return "wrong";
               }
            };
         }
      };
      
      Module customModule = new AbstractModule() {
         @Override
         protected void configure() {
            bind(ContentMetadataCodec.class).toInstance(contentMetadataCodec);
         }
      };
      
      Iterable<Module> modules = Iterables.concat(setupModules(), ImmutableList.of(customModule));
      BlobStoreContext naughtyBlobStoreContext = createView(setupProperties(), modules);
      BlobStore naughtyBlobStore = naughtyBlobStoreContext.getBlobStore();
      
      final String containerName = getScratchContainerName();
      
      try {
         final String blobName = "hello";
         
         naughtyBlobStore.createContainerInLocation(null, containerName, publicRead());
         naughtyBlobStore.putBlob(containerName, naughtyBlobStore.blobBuilder(blobName)
                  .payload(TEST_STRING).expires(new Date(System.currentTimeMillis() + 60*1000)).build());

         assertConsistencyAwareBlobExpiryMetadata(containerName, blobName, new Date(0));

      } finally {
         recycleContainer(containerName);
      }
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
   
   private void runCreateContainerInLocation(String payload) throws InterruptedException, MalformedURLException, IOException {
      String blobName = "hello";
      BlobStore blobStore = view.getBlobStore();
      final String containerName = getScratchContainerName();
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
         // this container is now public, so we can't reuse it directly
         recycleContainer(containerName);
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
