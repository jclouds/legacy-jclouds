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
package org.jclouds.mezeo.pcs2.integration;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.jclouds.blobstore.integration.internal.BaseBlobIntegrationTest;
import org.jclouds.mezeo.pcs2.PCSConnection;
import org.jclouds.mezeo.pcs2.domain.ContainerMetadata;
import org.jclouds.mezeo.pcs2.domain.FileMetadata;
import org.jclouds.mezeo.pcs2.domain.PCSFile;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = { "integration", "live" }, testName = "cloudfiles.PCSBlobIntegrationTest")
public class PCSBlobStoreIntegrationTest extends
         BaseBlobIntegrationTest<PCSConnection, ContainerMetadata, FileMetadata, PCSFile> {
   @Override
   protected void validateMetadata(FileMetadata metadata) {
      assertEquals(metadata.getContentType(), "text/plain");
      assertEquals(metadata.getSize(), TEST_STRING.length());
      assertEquals(metadata.getUserMetadata().get("adrian"), Collections
               .singletonList("powderpuff"));
      // Content-MD5 not supported http://code.google.com/p/jclouds/issues/detail?id=105
      // assertEquals(metadata.getContentMD5(), HttpUtils.md5(TEST_STRING.getBytes()));
   }

   @Override
   @Test(enabled = false)
   public void testGetIfMatch() throws InterruptedException, ExecutionException, TimeoutException,
            IOException {
      // etag not supported http://code.google.com/p/jclouds/issues/detail?id=105
   }

   @Override
   @Test(enabled = false)
   public void testGetIfModifiedSince() throws InterruptedException, ExecutionException,
            TimeoutException, IOException {
      // unsupported
   }

   @Override
   @Test(enabled = false)
   public void testGetIfNoneMatch() throws InterruptedException, ExecutionException,
            TimeoutException, IOException {
      // etag not supported http://code.google.com/p/jclouds/issues/detail?id=105
   }

   @Override
   @Test(enabled = false)
   public void testGetIfUnmodifiedSince() throws InterruptedException, ExecutionException,
            TimeoutException, IOException {
      // unsupported
   }

   @Override
   @Test(enabled = false)
   public void testGetRange() throws InterruptedException, ExecutionException, TimeoutException,
            IOException {
      // unsupported http://code.google.com/p/jclouds/issues/detail?id=106
   }

   @Override
   @Test(enabled = false)
   public void testGetStartAt() throws InterruptedException, ExecutionException, TimeoutException,
            IOException {
      // unsupported http://code.google.com/p/jclouds/issues/detail?id=106
   }

   @Override
   @Test(enabled = false)
   public void testGetTail() throws InterruptedException, ExecutionException, TimeoutException,
            IOException {
      // unsupported http://code.google.com/p/jclouds/issues/detail?id=106
   }

   @Override
   @Test(enabled = false)
   public void testGetTwoRanges() throws InterruptedException, ExecutionException,
            TimeoutException, IOException {
      // unsupported http://code.google.com/p/jclouds/issues/detail?id=106
   }

   @DataProvider(name = "delete")
   @Override
   public Object[][] createData() {
      return new Object[][] { { "normal" } };
   }

}