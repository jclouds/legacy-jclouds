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

import com.google.common.collect.ImmutableSet;
import org.jclouds.aws.s3.AWSS3ApiMetadata;
import org.jclouds.aws.s3.AWSS3Client;
import org.jclouds.aws.s3.blobstore.strategy.internal.DeleteObjectsInBatches;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.s3.blobstore.integration.S3BlobIntegrationLiveTest;
import org.testng.annotations.Test;

import java.util.Set;
import java.util.UUID;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "AWSS3BlobIntegrationLiveTest")
public class AWSS3BlobIntegrationLiveTest extends S3BlobIntegrationLiveTest {
   public AWSS3BlobIntegrationLiveTest() {
      provider = "aws-s3";
   }

   @Test(groups = {"integration", "live"})
   public void testDeleteObjectsInBatches() throws InterruptedException {
      String container = getContainerName();
      try {
         /* Create a hierarchy with two folders and a few leafs */
         ImmutableSet.Builder<String> builder = ImmutableSet.builder();
         for (int i = 0; i < 2; i++) {
            String folder = UUID.randomUUID().toString();
            for (int j = 0; j < 2; j++) {
               String key = String.format("%s/%s", folder, UUID.randomUUID().toString());

               Blob blob = view.getBlobStore().blobBuilder(key).payload("").build();
               view.getBlobStore().putBlob(container, blob);

               builder.add(key);
            }
         }
         Set<String> keys = builder.build();

         DeleteObjectsInBatches deleteAll = view.utils().injector().getInstance(DeleteObjectsInBatches.class);
         deleteAll.execute(container);

         for (String key : keys) {
            assertConsistencyAwareBlobDoesntExist(container, key);
         }

      } finally {
         returnContainer(container);
      }

   }
}
