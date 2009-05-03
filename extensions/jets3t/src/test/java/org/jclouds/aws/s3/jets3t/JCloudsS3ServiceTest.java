/**
 *
 * Copyright (C) 2009 Adrian Cole <adriancole@jclouds.org>
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
package org.jclouds.aws.s3.jets3t;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.aws.s3.S3Context;
import org.jclouds.aws.s3.S3IntegrationTest;
import org.jclouds.http.config.JavaUrlHttpFutureCommandClientModule;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.security.AWSCredentials;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Tests to cover JCloudsS3Service
 * 
 * @author Adrian Cole
 * 
 */
@Test
public class JCloudsS3ServiceTest extends S3IntegrationTest {
    AWSCredentials credentials;
    S3Service service;

    @Override
    protected boolean debugEnabled() {
	return true;
    }

    /**
     * overridden only to get access to the amazon credentials used for jets3t
     * initialization.
     */
    @Override
    protected S3Context createS3Context(String AWSAccessKeyId,
	    String AWSSecretAccessKey) {
	credentials = new AWSCredentials(AWSAccessKeyId, AWSSecretAccessKey);
	return super.createS3Context(AWSAccessKeyId, AWSSecretAccessKey);
    }

    /**
     * initialize a new JCloudsS3Service, but passing
     * JavaUrlHttpFutureCommandClientModule(), as it is easier to debug in unit
     * tests.
     * 
     * @throws S3ServiceException
     */
    @BeforeMethod
    public void testJCloudsS3Service() throws S3ServiceException {
	service = new JCloudsS3Service(credentials,
		new JavaUrlHttpFutureCommandClientModule());
    }

    @Test
    public void testCheckBucketStatusString() {
	fail("Not yet implemented");
    }

    @Test
    public void testCopyObjectImplStringStringStringStringAccessControlListMapCalendarCalendarStringArrayStringArray() {
	fail("Not yet implemented");
    }

    @Test
    public void testCreateBucketImplStringStringAccessControlList() {
	fail("Not yet implemented");
    }

    @Test
    public void testDeleteBucketImplString() throws S3ServiceException,
	    InterruptedException, ExecutionException, TimeoutException {
	String bucketName = bucketPrefix + ".testDeleteBucketImplString";
	org.jclouds.aws.s3.domain.S3Bucket jcloudsBucket = createBucket(bucketName);

	service.deleteBucket(new S3Bucket(bucketName));

	assert !client.bucketExists(jcloudsBucket).get(10, TimeUnit.SECONDS);
    }

    private org.jclouds.aws.s3.domain.S3Bucket createBucket(String bucketName)
	    throws InterruptedException, ExecutionException, TimeoutException {
	org.jclouds.aws.s3.domain.S3Bucket jcloudsBucket = new org.jclouds.aws.s3.domain.S3Bucket(
		bucketName);
	client.createBucketIfNotExists(jcloudsBucket).get(10, TimeUnit.SECONDS);
	return jcloudsBucket;
    }

    @Test
    public void testDeleteObjectImplStringString() throws InterruptedException,
	    ExecutionException, TimeoutException, S3ServiceException {
	String bucketName = bucketPrefix + ".testDeleteObjectImplStringString";
	String objectKey = "key";
	String objectValue = "test";

	org.jclouds.aws.s3.domain.S3Bucket jcloudsBucket = addNewObject(
		bucketName, objectKey, objectValue);

	service.deleteObject(bucketName, objectKey);

	assertEquals(client.headObject(jcloudsBucket, objectKey).get(10,
		TimeUnit.SECONDS), org.jclouds.aws.s3.domain.S3Object.NOT_FOUND);
    }

    private org.jclouds.aws.s3.domain.S3Bucket addNewObject(String name,
	    String objectKey, String objectValue) throws InterruptedException,
	    ExecutionException, TimeoutException {
	org.jclouds.aws.s3.domain.S3Bucket jcloudsBucket = createBucket(name);
	org.jclouds.aws.s3.domain.S3Object jcloudsObject = new org.jclouds.aws.s3.domain.S3Object(
		objectKey);
	jcloudsObject.setContent(objectValue);
	client.addObject(jcloudsBucket, jcloudsObject)
		.get(10, TimeUnit.SECONDS);
	return jcloudsBucket;
    }

    @Test
    public void testGetBucketAclImplString() {
	fail("Not yet implemented");
    }

    @Test
    public void testGetBucketLocationImplString() {
	fail("Not yet implemented");
    }

    @Test
    public void testGetBucketLoggingStatusImplString() {
	fail("Not yet implemented");
    }

    @Test
    public void testGetObjectAclImplStringString() {
	fail("Not yet implemented");
    }

    @Test
    public void testGetObjectDetailsImplStringStringCalendarCalendarStringArrayStringArray() {
	fail("Not yet implemented");
    }

    @Test
    public void testGetObjectImplStringStringCalendarCalendarStringArrayStringArrayLongLong() {
	fail("Not yet implemented");
    }

    @Test
    public void testIsBucketAccessibleString() {
	fail("Not yet implemented");
    }

    @Test
    public void testIsRequesterPaysBucketImplString() {
	fail("Not yet implemented");
    }

    @Test
    public void testListAllBucketsImpl() {
	fail("Not yet implemented");
    }

    @Test
    public void testListObjectsChunkedImplStringStringStringLongStringBoolean() {
	fail("Not yet implemented");
    }

    @Test
    public void testListObjectsImplStringStringStringLong() {
	fail("Not yet implemented");
    }

    @Test
    public void testPutBucketAclImplStringAccessControlList() {
	fail("Not yet implemented");
    }

    @Test
    public void testPutObjectAclImplStringStringAccessControlList() {
	fail("Not yet implemented");
    }

    @Test
    public void testPutObjectImplStringS3Object() {
	fail("Not yet implemented");
    }

    @Test
    public void testSetBucketLoggingStatusImplStringS3BucketLoggingStatus() {
	fail("Not yet implemented");
    }

    @Test
    public void testSetRequesterPaysBucketImplStringBoolean() {
	fail("Not yet implemented");
    }

}
