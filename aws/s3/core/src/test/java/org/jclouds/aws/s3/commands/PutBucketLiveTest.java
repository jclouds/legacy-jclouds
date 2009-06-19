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

import org.jclouds.aws.s3.S3IntegrationTest;
import static org.jclouds.aws.s3.commands.options.PutBucketOptions.Builder.createIn;
import static org.jclouds.aws.s3.commands.options.PutBucketOptions.Builder.withBucketAcl;
import org.jclouds.aws.s3.domain.S3Bucket.Metadata.LocationConstraint;
import org.jclouds.aws.s3.domain.acl.CannedAccessPolicy;
import org.jclouds.aws.s3.util.S3Utils;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * Tests integrated functionality of all PutBucket commands.
 * <p/>
 * Each test uses a different bucket name, so it should be perfectly fine to run in parallel.
 * 
 * @author Adrian Cole
 */
@Test(testName = "s3.PutBucketLiveTest")
public class PutBucketLiveTest extends S3IntegrationTest {

   @Test(groups = { "live" })
   void testPublicReadAccessPolicy() throws Exception {
      String bucketName = getScratchBucketName();
      try {
         deleteBucket(bucketName);
         client.putBucketIfNotExists(bucketName, withBucketAcl(CannedAccessPolicy.PUBLIC_READ))
                  .get(10, TimeUnit.SECONDS);
         URL url = new URL(String.format("http://%1$s.s3.amazonaws.com", bucketName));
         S3Utils.toStringAndClose(url.openStream());
      } finally {
         returnScratchBucket(bucketName);
      }
   }

   @Test(groups = { "live" })
   void testPutTwiceIsOk() throws Exception {
      String bucketName = getBucketName();
      try {
         client.putBucketIfNotExists(bucketName).get(10, TimeUnit.SECONDS);
      } finally {
         returnBucket(bucketName);
      }
   }

   @Test(expectedExceptions = IOException.class, groups = { "live" })
   void testDefaultAccessPolicy() throws Exception {
      String bucketName = getBucketName();
      try {
         URL url = new URL(String.format("http://%1$s.s3.amazonaws.com", bucketName));
         S3Utils.toStringAndClose(url.openStream());
      } finally {
         returnBucket(bucketName);
      }

   }

   /**
    * using scratch bucketName as we are changing location
    */
   @Test(groups = "live")
   void testEu() throws Exception {
      String bucketName = getScratchBucketName();
      try {
         deleteBucket(bucketName);
         client.putBucketIfNotExists(bucketName,
                  createIn(LocationConstraint.EU).withBucketAcl(CannedAccessPolicy.PUBLIC_READ)).get(
                  10, TimeUnit.SECONDS);
         URL url = new URL(String.format("http://%1$s.s3.amazonaws.com", bucketName));
         S3Utils.toStringAndClose(url.openStream());
      } finally {
         returnScratchBucket(bucketName);
      }
   }
}