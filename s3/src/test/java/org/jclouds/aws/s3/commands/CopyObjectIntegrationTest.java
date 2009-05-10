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

import static org.jclouds.aws.s3.commands.options.CopyObjectOptions.Builder.ifSourceMd5DoesntMatch;
import static org.jclouds.aws.s3.commands.options.CopyObjectOptions.Builder.ifSourceMd5Matches;
import static org.jclouds.aws.s3.commands.options.CopyObjectOptions.Builder.ifSourceModifiedSince;
import static org.jclouds.aws.s3.commands.options.CopyObjectOptions.Builder.ifSourceUnmodifiedSince;
import static org.jclouds.aws.s3.commands.options.CopyObjectOptions.Builder.overrideAcl;
import static org.jclouds.aws.s3.commands.options.CopyObjectOptions.Builder.overrideMetadataWith;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.aws.s3.S3IntegrationTest;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.domain.acl.CannedAccessPolicy;
import org.jclouds.aws.s3.reference.S3Headers;
import org.jclouds.aws.s3.util.S3Utils;
import org.jclouds.http.HttpResponseException;
import org.joda.time.DateTime;
import org.testng.annotations.Test;


import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

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
    String sourceKey = "apples";
    String destinationKey = "pears";


    @Test()
    void testCannedAccessPolicyPublic() throws Exception {
	String sourceBucket = bucketPrefix + "tcapp";
	String destinationBucket = sourceBucket + "dest";

	setupSourceBucket(sourceBucket, sourceKey);

	createBucketAndEnsureEmpty(destinationBucket);
	client.copyObject(sourceBucket, sourceKey, destinationBucket,
		destinationKey,overrideAcl(CannedAccessPolicy.PUBLIC_READ)).get(10, TimeUnit.SECONDS);

	validateContent(destinationBucket, destinationKey);

	URL url = new URL(String.format("http://%1$s.s3.amazonaws.com/%2$s",
		destinationBucket, destinationKey));
	S3Utils.toStringAndClose(url.openStream());

    }
    @Test()
    void testCopyObject() throws Exception {
	String sourceBucket = bucketPrefix + "testcopyobject";
	String destinationBucket = sourceBucket + "dest";

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
	addToBucketAndValidate(sourceBucket, sourceKey);
    }

    private void addToBucketAndValidate(String sourceBucket, String sourceKey)
	    throws InterruptedException, ExecutionException, TimeoutException,
	    IOException {
	addObjectToBucket(sourceBucket, sourceKey);
	validateContent(sourceBucket, sourceKey);
    }

    @Test
    void testCopyIfModifiedSince() throws InterruptedException,
	    ExecutionException, TimeoutException, IOException {
	String sourceBucket = bucketPrefix + "tcims";
	String destinationBucket = sourceBucket + "dest";

	DateTime before = new DateTime();
	setupSourceBucket(sourceBucket, sourceKey);
	DateTime after = new DateTime().plusSeconds(1);

	createBucketAndEnsureEmpty(destinationBucket);
	client.copyObject(sourceBucket, sourceKey, destinationBucket,
		destinationKey, ifSourceModifiedSince(before)).get(10,
		TimeUnit.SECONDS);
	validateContent(destinationBucket, destinationKey);

	try {
	    client.copyObject(sourceBucket, sourceKey, destinationBucket,
		    destinationKey, ifSourceModifiedSince(after)).get(10,
		    TimeUnit.SECONDS);
	} catch (ExecutionException e) {
	    HttpResponseException ex = (HttpResponseException) e.getCause();
	    assertEquals(ex.getResponse().getStatusCode(), 412);
	}
    }

    @Test
    void testCopyIfUnmodifiedSince() throws InterruptedException,
	    ExecutionException, TimeoutException, IOException {
	String sourceBucket = bucketPrefix + "tcius";
	String destinationBucket = sourceBucket + "dest";

	DateTime before = new DateTime();
	setupSourceBucket(sourceBucket, sourceKey);
	DateTime after = new DateTime().plusSeconds(1);

	createBucketAndEnsureEmpty(destinationBucket);
	client.copyObject(sourceBucket, sourceKey, destinationBucket,
		destinationKey, ifSourceUnmodifiedSince(after)).get(10,
		TimeUnit.SECONDS);
	validateContent(destinationBucket, destinationKey);

	try {
	    client.copyObject(sourceBucket, sourceKey, destinationBucket,
		    destinationKey, ifSourceModifiedSince(before)).get(10,
		    TimeUnit.SECONDS);
	} catch (ExecutionException e) {
	    HttpResponseException ex = (HttpResponseException) e.getCause();
	    assertEquals(ex.getResponse().getStatusCode(), 412);
	}
    }

    @Test
    void testCopyIfMatch() throws InterruptedException, ExecutionException,
	    TimeoutException, IOException {
	String sourceBucket = bucketPrefix + "tcim";

	String destinationBucket = sourceBucket + "dest";

	setupSourceBucket(sourceBucket, sourceKey);

	createBucketAndEnsureEmpty(destinationBucket);
	client.copyObject(sourceBucket, sourceKey, destinationBucket,
		destinationKey, ifSourceMd5Matches(goodMd5)).get(10,
		TimeUnit.SECONDS);
	validateContent(destinationBucket, destinationKey);

	try {
	    client.copyObject(sourceBucket, sourceKey, destinationBucket,
		    destinationKey, ifSourceMd5Matches(badMd5)).get(10,
		    TimeUnit.SECONDS);
	} catch (ExecutionException e) {
	    HttpResponseException ex = (HttpResponseException) e.getCause();
	    assertEquals(ex.getResponse().getStatusCode(), 412);
	}
    }

    @Test
    void testCopyIfNoneMatch() throws IOException, InterruptedException,
	    ExecutionException, TimeoutException {
	String sourceBucket = bucketPrefix + "tcinm";

	String destinationBucket = sourceBucket + "dest";

	setupSourceBucket(sourceBucket, sourceKey);

	createBucketAndEnsureEmpty(destinationBucket);
	client.copyObject(sourceBucket, sourceKey, destinationBucket,
		destinationKey, ifSourceMd5DoesntMatch(badMd5)).get(10,
		TimeUnit.SECONDS);
	validateContent(destinationBucket, destinationKey);

	try {
	    client.copyObject(sourceBucket, sourceKey, destinationBucket,
		    destinationKey, ifSourceMd5DoesntMatch(goodMd5)).get(10,
		    TimeUnit.SECONDS);
	} catch (ExecutionException e) {
	    HttpResponseException ex = (HttpResponseException) e.getCause();
	    assertEquals(ex.getResponse().getStatusCode(), 412);
	}
    }

    @Test
    void testCopyWithMetadata() throws InterruptedException,
	    ExecutionException, TimeoutException, IOException {
	String sourceBucket = bucketPrefix + "tcwm";
	String destinationBucket = sourceBucket + "dest";

	setupSourceBucket(sourceBucket, sourceKey);

	Multimap<String, String> metadata = HashMultimap.create();
	metadata.put(S3Headers.USER_METADATA_PREFIX + "adrian", "cole");

	createBucketAndEnsureEmpty(destinationBucket);
	client.copyObject(sourceBucket, sourceKey, destinationBucket,
		destinationKey, overrideMetadataWith(metadata)).get(10,
		TimeUnit.SECONDS);

	validateContent(destinationBucket, destinationKey);

	S3Object.Metadata objectMeta = client.headObject(destinationBucket,
		destinationKey).get(10, TimeUnit.SECONDS);

	assertEquals(objectMeta.getUserMetadata(), metadata);
    }

}