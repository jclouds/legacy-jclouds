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
package org.jclouds.filesystem.integration;

import static org.jclouds.blobstore.options.ListContainerOptions.Builder.maxResults;
import static org.testng.Assert.assertEquals;

import java.util.Properties;

import javax.ws.rs.core.MediaType;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.integration.internal.BaseBlobStoreIntegrationTest;
import org.jclouds.blobstore.integration.internal.BaseContainerIntegrationTest;
import org.jclouds.filesystem.reference.FilesystemConstants;
import org.jclouds.filesystem.utils.TestUtils;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * @author James Murty
 * @author Adrian Cole
 */
@Test(groups = { "integration", "live" }, testName = "blobstore.FilesystemContainerIntegrationTest")
public class FilesystemContainerIntegrationTestDisabled extends BaseContainerIntegrationTest {
   public FilesystemContainerIntegrationTestDisabled() {
      provider = "filesystem";
      BaseBlobStoreIntegrationTest.SANITY_CHECK_RETURNED_BUCKET_NAME = true;
   }

   @Override
   protected Properties setupProperties() {
      Properties props = super.setupProperties();
      props.setProperty(FilesystemConstants.PROPERTY_BASEDIR, TestUtils.TARGET_BASE_DIR);
      return props;
   }
   @Test(groups = { "integration", "live" })
   public void testNotWithDetails() throws InterruptedException {

      String key = "hello";

      // NOTE all metadata in jclouds comes out as lowercase, in an effort to normalize the
      // providers.
      Blob object = view.getBlobStore().blobBuilder(key).userMetadata(ImmutableMap.of("Adrian", "powderpuff"))
            .payload(TEST_STRING).contentType(MediaType.TEXT_PLAIN).build();
      String containerName = getContainerName();
      try {
         addBlobToContainer(containerName, object);
         validateContent(containerName, key);

         PageSet<? extends StorageMetadata> container = view.getBlobStore().list(containerName, maxResults(1));

         BlobMetadata metadata = (BlobMetadata) Iterables.getOnlyElement(container);
         // transient container should be lenient and not return metadata on undetailed listing.

         assertEquals(metadata.getUserMetadata().size(), 0);

      } finally {
         returnContainer(containerName);
      }
   }
}
