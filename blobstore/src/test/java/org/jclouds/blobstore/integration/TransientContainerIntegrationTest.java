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
package org.jclouds.blobstore.integration;

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.jclouds.blobstore.options.ListContainerOptions.Builder.maxResults;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import javax.ws.rs.core.MediaType;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.integration.internal.BaseContainerIntegrationTest;
import org.jclouds.domain.Location;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * @author James Murty
 * @author Adrian Cole
 */
@Test(groups = { "integration", "live" })
public class TransientContainerIntegrationTest extends BaseContainerIntegrationTest {
   public TransientContainerIntegrationTest() {
      provider = "transient";
   }
   
   @Test(groups = { "integration", "live" })
   public void testNotWithDetails() throws InterruptedException {

      String key = "hello";
      // NOTE all metadata in jclouds comes out as lowercase, in an effort to normalize the
      // providers.
      Blob blob = view.getBlobStore().blobBuilder("hello").userMetadata(ImmutableMap.of("Adrian", "powderpuff"))
            .payload(TEST_STRING).contentType(MediaType.TEXT_PLAIN).build();

      String containerName = getContainerName();
      try {
         addBlobToContainer(containerName, blob);
         validateContent(containerName, key);

         PageSet<? extends StorageMetadata> container = view.getBlobStore().list(containerName, maxResults(1));

         BlobMetadata metadata = (BlobMetadata) getOnlyElement(container);
         // transient container should be lenient and not return metadata on undetailed listing.

         assertEquals(metadata.getUserMetadata().size(), 0);

      } finally {
         returnContainer(containerName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testDuplicateCreateContainer() {
      BlobStore blobStore = view.getBlobStore();
      Location location = null;
      String container = "container";
      boolean created;

      created = blobStore.createContainerInLocation(location, container);
      assertTrue(created);

      created = blobStore.createContainerInLocation(location, container);
      assertFalse(created);
   }
}
