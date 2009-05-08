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

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.aws.s3.S3IntegrationTest;
import org.testng.annotations.Test;

/**
 * Tests integrated functionality of all copyObject commands.
 * <p/>
 * Each test uses a different bucket name, so it should be perfectly fine to run
 * in parallel.
 * 
 * @author Adrian Cole
 * 
 */
@Test(groups = "integration", testName = "s3.CopyObjectIntegrationTest")
public class CopyObjectIntegrationTest extends S3IntegrationTest {

    @Test()
    void testCopyObject() throws Exception {
	String sourceBucket = bucketPrefix + "testCopyObject";
	String sourceKey = "apples";
	String destinationBucket = bucketPrefix + "testCopyObjectDestination";
	String destinationKey = "pears";

	setupSourceBucket(sourceBucket, sourceKey);

	createBucketAndEnsureEmpty(destinationBucket);
	client.copyObject(sourceBucket, sourceKey, destinationBucket,
		destinationKey).get(10, TimeUnit.SECONDS);

	validateContent(destinationBucket, destinationKey);

    }

    private void setupSourceBucket(String sourceBucket, String sourceKey)
	    throws InterruptedException, ExecutionException, TimeoutException,
	    IOException {
	createBucketAndEnsureEmpty(sourceBucket);
	addObjectToBucket(sourceBucket, sourceKey);
	validateContent(sourceBucket, sourceKey);
    }

    @Test
    void testCopyIfModifiedSince() {
	// TODO
    }

    @Test
    void testCopyIfUnmodifiedSince() {
	// TODO
    }

    @Test
    void testCopyIfMatch() {
	// TODO
    }

    @Test
    void testCopyIfNoneMatch() {
	// TODO
    }

    @Test
    void testCopyWithMetadata() {

    }

}