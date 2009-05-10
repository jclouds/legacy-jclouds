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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.jclouds.aws.s3.S3IntegrationTest;
import org.jclouds.aws.s3.S3ResponseException;
import org.jclouds.aws.s3.domain.S3Object;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

/**
 * Tests integrated functionality of all deleteObject commands.
 * <p/>
 * Each test uses a different bucket name, so it should be perfectly fine to run
 * in parallel.
 * 
 * @author Adrian Cole
 * 
 */
@Test(groups = "integration", testName = "s3.DeleteObjectIntegrationTest")
public class DeleteObjectIntegrationTest extends S3IntegrationTest {

    @Test()
    void deleteObjectNotFound() throws Exception {
	String bucketName = bucketPrefix + "donf";
	createBucketAndEnsureEmpty(bucketName);
	addObjectToBucket(bucketName, "test");
	assert client.deleteObject(bucketName, "test")
		.get(10, TimeUnit.SECONDS);
    }

    @Test
    void deleteObjectNoBucket() throws Exception {
	String bucketName = bucketPrefix + "donb";
	try {
	    client.deleteObject(bucketName, "test").get(10, TimeUnit.SECONDS);
	} catch (ExecutionException e) {
	    assert e.getCause() instanceof S3ResponseException;
	    assertEquals(((S3ResponseException) e.getCause()).getResponse()
		    .getStatusCode(), 404);
	}
    }

    @Test()
    void deleteObject() throws Exception {
	String bucketName = bucketPrefix + "do";
	createBucketAndEnsureEmpty(bucketName);
	addObjectToBucket(bucketName, "test");
	assert client.deleteObject(bucketName, "test")
		.get(10, TimeUnit.SECONDS);
	assert client.headObject(bucketName, "test").get(10, TimeUnit.SECONDS) == S3Object.Metadata.NOT_FOUND;

    }
}