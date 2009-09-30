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

import java.util.List;

import org.jclouds.aws.s3.S3BlobStore;
import org.jclouds.aws.s3.domain.BucketMetadata;
import org.jclouds.aws.s3.domain.ObjectMetadata;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.blobstore.integration.internal.BaseServiceIntegrationTest;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = { "integration", "live" }, testName = "s3.S3ServiceIntegrationTest")
public class S3ServiceIntegrationTest extends
         BaseServiceIntegrationTest<S3BlobStore, BucketMetadata, ObjectMetadata, S3Object> {

   void containerExists() throws Exception {
      String containerName = getContainerName();
      try {
         List<BucketMetadata> list = context.getApi().listContainers();
         BucketMetadata firstContainer = list.get(0);
         BucketMetadata toMatch = new BucketMetadata(containerName);
         toMatch.setOwner(firstContainer.getOwner());
         assert list.contains(toMatch);
      } finally {
         returnContainer(containerName);
      }
   }
}