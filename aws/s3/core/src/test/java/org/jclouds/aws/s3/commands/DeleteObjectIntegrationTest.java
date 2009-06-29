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
package org.jclouds.aws.s3.commands;

import static org.testng.Assert.assertEquals;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.aws.AWSResponseException;
import org.jclouds.aws.s3.S3IntegrationTest;
import org.jclouds.aws.s3.domain.S3Bucket;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests integrated functionality of all deleteObject commands.
 * <p/>
 * Each test uses a different bucket name, so it should be perfectly fine to run in parallel.
 * 
 * @author Adrian Cole
 */
@Test(groups = { "integration", "live" }, testName = "s3.DeleteObjectIntegrationTest")
public class DeleteObjectIntegrationTest extends S3IntegrationTest {

   private static final String LOCAL_ENCODING = System.getProperty("file.encoding");

   @Test
   void deleteObjectNotFound() throws Exception {
      String bucketName = getBucketName();
      String key = "test";
      try {
         assert client.deleteObject(bucketName, key).get(10, TimeUnit.SECONDS);
      } finally {
         returnBucket(bucketName);
      }
   }

   @DataProvider(name = "delete")
   public Object[][] createData() {
      return new Object[][] { { "sp ace" }, { "unic¿de" }, { "qu?stion" } };
   }

   @Test(dataProvider = "delete")
   void deleteObject(String key) throws Exception {
      String bucketName = getBucketName();
      try {
         addObjectToBucket(bucketName, key);
         assert client.deleteObject(bucketName, key).get(10, TimeUnit.SECONDS);
         assertBucketEmptyDeleting(bucketName, key);
      } finally {
         returnBucket(bucketName);
      }
   }

   private void assertBucketEmptyDeleting(String bucketName, String key)
            throws InterruptedException, ExecutionException, TimeoutException {
      S3Bucket listing = client.listBucket(bucketName).get(10, TimeUnit.SECONDS);
      assertEquals(listing.getContents().size(), 0, String.format(
               "deleting %s, we still have %s left in bucket %s, using encoding %s", key, listing
                        .getContents().size(), bucketName, LOCAL_ENCODING));
   }

   @Test
   void deleteObjectNoBucket() throws Exception {
      try {
         client.deleteObject("donb", "test").get(10, TimeUnit.SECONDS);
      } catch (ExecutionException e) {
         assert e.getCause() instanceof AWSResponseException;
         assertEquals(((AWSResponseException) e.getCause()).getResponse().getStatusCode(), 404);
      }
   }

}