/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.aws.s3.integration;

import static org.jclouds.aws.s3.options.CopyObjectOptions.Builder.overrideAcl;
import static org.jclouds.aws.s3.options.PutObjectOptions.Builder.withAcl;

import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.jclouds.aws.s3.S3BlobStore;
import org.jclouds.aws.s3.domain.BucketMetadata;
import org.jclouds.aws.s3.domain.CannedAccessPolicy;
import org.jclouds.aws.s3.domain.ObjectMetadata;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.blobstore.integration.internal.BaseBlobLiveTest;
import org.jclouds.util.Utils;
import org.testng.annotations.Test;

/**
 * 
 * @author James Murty
 * @author Adrian Cole
 */
@Test(groups = { "live" }, testName = "s3.S3BlobLiveTest")
public class S3BlobLiveTest extends
         BaseBlobLiveTest<S3BlobStore, BucketMetadata, ObjectMetadata, S3Object> {

   public void testPutCannedAccessPolicyPublic() throws Exception {
      String containerName = getContainerName();
      try {
         String key = "hello";

         context.getApi().putBlob(containerName, new S3Object(key, TEST_STRING),

         withAcl(CannedAccessPolicy.PUBLIC_READ)).get(10, TimeUnit.SECONDS);

         URL url = new URL(String.format("http://%1$s.s3.amazonaws.com/%2$s", containerName, key));
         Utils.toStringAndClose(url.openStream());
      } finally {
         returnContainer(containerName);
      }

   }

   String sourceKey = "apples";
   String destinationKey = "pears";

   public void testCopyCannedAccessPolicyPublic() throws Exception {
      String containerName = getContainerName();
      String destinationContainer = getContainerName();
      try {
         addBlobToContainer(containerName, sourceKey);
         validateContent(containerName, sourceKey);

         context.getApi().copyBlob(containerName, sourceKey, destinationContainer, destinationKey,
                  overrideAcl(CannedAccessPolicy.PUBLIC_READ)).get(10, TimeUnit.SECONDS);

         validateContent(destinationContainer, destinationKey);

         URL url = new URL(String.format("http://%1$s.s3.amazonaws.com/%2$s", destinationContainer,
                  destinationKey));
         Utils.toStringAndClose(url.openStream());

      } finally {
         returnContainer(containerName);
         returnContainer(destinationContainer);
      }
   }

}