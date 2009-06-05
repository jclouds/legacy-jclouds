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
package org.jclouds.aws.s3.samples.test;

import java.util.concurrent.TimeUnit;

import org.jclouds.aws.s3.CreateListOwnedBuckets;
import org.jclouds.aws.s3.S3Context;
import org.jclouds.aws.s3.S3ContextFactory;
import org.jclouds.aws.s3.config.LiveS3ConnectionModule;
import org.jclouds.aws.s3.reference.S3Constants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Live Unit test for simple CreateListOwnedBuckets.
 * 
 * @author Carlos Fernandes
 */
@Test(testName = "s3.createListOwnedBucketsLiveTest")
public class CreateListOwnedBucketsLiveTest {

   private S3Context context;
   private final String sysAWSAccessKeyId = System
            .getProperty(S3Constants.PROPERTY_AWS_ACCESSKEYID);
   private final String sysAWSSecretAccessKey = System
            .getProperty(S3Constants.PROPERTY_AWS_SECRETACCESSKEY);
   private String bucketPrefix = (System.getProperty("user.name") + "." + this.getClass()
            .getSimpleName()).toLowerCase();

   @BeforeClass(inheritGroups = false, groups = { "live" })
   @Parameters( { S3Constants.PROPERTY_AWS_ACCESSKEYID, S3Constants.PROPERTY_AWS_SECRETACCESSKEY })
   public void setUpTest(@Optional String AWSAccessKeyId, @Optional String AWSSecretAccessKey) {

      AWSAccessKeyId = AWSAccessKeyId != null ? AWSAccessKeyId : sysAWSAccessKeyId;
      AWSSecretAccessKey = AWSSecretAccessKey != null ? AWSSecretAccessKey : sysAWSSecretAccessKey;

      context = S3ContextFactory.createS3Context(AWSAccessKeyId, AWSSecretAccessKey,
               new LiveS3ConnectionModule());

   }

   @Test(groups = { "live" })
   public void s3Test() throws Exception {

      // Init
      CreateListOwnedBuckets listMyOwnBuckets = new CreateListOwnedBuckets(context);
      String bucketName = bucketPrefix + "needstoexist";

      // Create Bucket
      assert listMyOwnBuckets.createBucket(bucketName); // Creates a random bucket first to make
      // sure list() will return something.

      // List bucket
      String string = listMyOwnBuckets.list().toString();
      assert string.length() > 0; // This test will validate if the list() operation will return any
      // string

   }

   @AfterClass
   public void tearDownClient() throws Exception {

      // Removes the bucket created for test purposes only
      context.getConnection().deleteBucketIfEmpty(bucketPrefix + "needstoexist").get(10,
               TimeUnit.SECONDS);

      context.close();
      context = null;
   }

}
