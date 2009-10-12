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
package org.jclouds.azure.storage.blob.integration;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.jclouds.azure.storage.blob.AzureBlobConnection;
import org.jclouds.azure.storage.blob.domain.Blob;
import org.jclouds.azure.storage.blob.domain.BlobMetadata;
import org.jclouds.azure.storage.blob.domain.ContainerMetadata;
import org.jclouds.blobstore.integration.internal.BaseBlobIntegrationTest;
import org.jclouds.http.HttpUtils;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = { "integration", "live" }, testName = "azureblob.AzureBlobIntegrationTest")
public class AzureBlobIntegrationTest extends
         BaseBlobIntegrationTest<AzureBlobConnection, ContainerMetadata, BlobMetadata, Blob> {

   @Override
   @Test(enabled = false)
   public void testGetIfModifiedSince() throws InterruptedException, ExecutionException,
            TimeoutException, IOException {
      // http://code.google.com/p/jclouds/issues/detail?id=98
   }

   @Override
   @Test(enabled = false)
   public void testGetStartAt() throws InterruptedException, ExecutionException, TimeoutException,
            IOException {
      // http://code.google.com/p/jclouds/issues/detail?id=91
   }

   @Override
   @Test(enabled = false)
   public void testGetTail() throws InterruptedException, ExecutionException, TimeoutException,
            IOException {
      // http://code.google.com/p/jclouds/issues/detail?id=91
   }

   protected void validateMetadata(BlobMetadata metadata) {
      assertEquals(metadata.getContentType(), "text/plain");
      // we can't check this while hacking around HEAD being broken, as GET of the first byte will
      // show incorrect length 1, the returned size, as opposed to the real length. This is an ok
      // tradeoff, as a container list will contain the correct size of the objects in an
      // inexpensive fashion
      // http://code.google.com/p/jclouds/issues/detail?id=92
      // assertEquals(metadata.getSize(), TEST_STRING.length());
      assertEquals(metadata.getUserMetadata().get("adrian"), "powderpuff");
      assertEquals(metadata.getContentMD5(), HttpUtils.md5(TEST_STRING.getBytes()));
   }

}