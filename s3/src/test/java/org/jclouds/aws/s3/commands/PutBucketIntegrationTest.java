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
package org.jclouds.aws.s3.commands;

import static org.jclouds.aws.s3.commands.options.PutBucketOptions.Builder.createIn;
import static org.jclouds.aws.s3.commands.options.PutBucketOptions.Builder.withBucketAcl;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.jclouds.aws.s3.S3IntegrationTest;
import org.jclouds.aws.s3.S3Utils;
import org.jclouds.aws.s3.domain.S3Bucket.Metadata.LocationConstraint;
import org.jclouds.aws.s3.domain.acl.CannedAccessPolicy;
import org.testng.annotations.Test;

/**
 * Tests integrated functionality of all PutBucket commands.
 * <p/>
 * Each test uses a different bucket name, so it should be perfectly fine to run
 * in parallel.
 * 
 * @author Adrian Cole
 * 
 */
@Test(groups = "integration", testName = "s3.PutBucketIntegrationTest")
public class PutBucketIntegrationTest extends S3IntegrationTest {

    @Test()
    void testPublicReadAccessPolicy() throws Exception {
	String bucketName = bucketPrefix + "public";

	client.putBucketIfNotExists(bucketName,
		withBucketAcl(CannedAccessPolicy.PUBLIC_READ)).get(10,
		TimeUnit.SECONDS);
	URL url = new URL(String.format("http://%1s.s3.amazonaws.com",
		bucketName));
	S3Utils.toStringAndClose(url.openStream());
    }

    @Test(expectedExceptions = IOException.class)
    void testDefaultAccessPolicy() throws Exception {
	String bucketName = bucketPrefix + "private";

	client.putBucketIfNotExists(bucketName).get(10, TimeUnit.SECONDS);
	URL url = new URL(String.format("http://%1s.s3.amazonaws.com",
		bucketName));
	S3Utils.toStringAndClose(url.openStream());
    }

    @Test()
    void testEu() throws Exception {
	String bucketName = (bucketPrefix + "wow").toLowerCase();
	client.putBucketIfNotExists(
		bucketName,
		createIn(LocationConstraint.EU).withBucketAcl(
			CannedAccessPolicy.PUBLIC_READ)).get(10,
		TimeUnit.SECONDS);

	URL url = new URL(String.format("http://%1s.s3.amazonaws.com",
		bucketName));
	S3Utils.toStringAndClose(url.openStream());
    }
}