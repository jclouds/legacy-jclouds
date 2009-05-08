/**
 *
 * Copyright (C) 2009 Adrian Cole <adrian@jclouds.org>
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
package org.jclouds.aws.s3;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jclouds.aws.s3.domain.S3Bucket;
import org.jclouds.aws.s3.domain.S3Object;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "s3.AmazonS3Test")
public class S3ConnectionTest extends S3IntegrationTest {

    @Test
    void testListBuckets() throws Exception {
	listAllMyBuckets();
    }

    List<S3Bucket.Metadata> listAllMyBuckets() throws Exception {
	return client.getOwnedBuckets().get(10, TimeUnit.SECONDS);
    }

    S3Object getObject() throws Exception {
	return client.getObject(bucketPrefix + "adrianjbosstest", "3366").get(
		10, TimeUnit.SECONDS);
    }

    S3Object.Metadata headObject() throws Exception {
	String bucketName = bucketPrefix + "adrianjbosstest";
	return client.headObject(bucketName, "3366").get(10,
		TimeUnit.SECONDS);
    }

    Boolean deleteBucket() throws Exception {
	String bucketName = bucketPrefix + "adrianjbosstest";
	return client.deleteBucketIfEmpty(bucketName).get(10, TimeUnit.SECONDS);
    }

    Boolean deleteObject() throws Exception {
	String bucketName = bucketPrefix + "adrianjbosstest";
	return client.deleteObject(bucketName, "3366")
		.get(10, TimeUnit.SECONDS);
    }

    S3Bucket getBucket() throws Exception {
	String bucketName = bucketPrefix + "adrianjbosstest";
	return client.listBucket(bucketName).get(10, TimeUnit.SECONDS);
    }

}