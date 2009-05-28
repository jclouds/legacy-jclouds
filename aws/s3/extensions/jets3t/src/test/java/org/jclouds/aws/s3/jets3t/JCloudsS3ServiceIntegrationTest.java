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
package org.jclouds.aws.s3.jets3t;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.IOUtils;
import org.jclouds.aws.s3.S3IntegrationTest;
import org.jclouds.aws.s3.config.StubS3ConnectionModule;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * Tests to cover JCloudsS3Service
 *
 * @author Adrian Cole
 */
@Test(groups = {"integration", "live"}, testName = "s3.JCloudsS3ServiceIntegrationTest")
public class JCloudsS3ServiceIntegrationTest extends S3IntegrationTest {
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
    protected void createLiveS3Context(String AWSAccessKeyId, String AWSSecretAccessKey) {
        credentials = new AWSCredentials(AWSAccessKeyId, AWSSecretAccessKey);
        super.createLiveS3Context(AWSAccessKeyId, AWSSecretAccessKey);
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
        service = (credentials != null) ? new JCloudsS3Service(credentials) :
                new JCloudsS3Service(new AWSCredentials("foo", "bar"), new StubS3ConnectionModule());
    }

    @Test(enabled = false)
    public void testCheckBucketStatusString() {
        fail("Not yet implemented");
    }

    @Test(enabled = false)
    public void testCopyObjectImplStringStringStringStringAccessControlListMapCalendarCalendarStringArrayStringArray() {
        fail("Not yet implemented");
    }

    @Test
    public void testCreateBucketImplStringStringAccessControlList() 
    	throws S3ServiceException, InterruptedException, ExecutionException 
	{
        String bucketName = bucketPrefix + ".testCreateBucketImplStringStringAccessControlList";
        S3Bucket bucket = service.createBucket(new S3Bucket(bucketName));
        assertEquals(bucket.getName(), bucketName);
        assertTrue(client.bucketExists(bucketName).get());
        
        client.deleteBucketIfEmpty(bucketName);
    }

    @Test
    public void testDeleteBucketImplString() throws S3ServiceException,
            InterruptedException, ExecutionException, TimeoutException {
        String bucketName = bucketPrefix + ".testDeleteBucketImplString";
        service.deleteBucket(bucketName);
        assert !client.bucketExists(bucketName).get(10, TimeUnit.SECONDS);
    }

    private void createBucket(String bucketName) throws InterruptedException,
            ExecutionException, TimeoutException {
        client.putBucketIfNotExists(bucketName).get(10, TimeUnit.SECONDS);
    }

    @Test
    public void testDeleteObjectImplStringString() throws InterruptedException,
            ExecutionException, TimeoutException, S3ServiceException {
        String bucketName = bucketPrefix + ".testDeleteObjectImplStringString";
        String objectKey = "key";
        String objectValue = "test";

        addNewObject(bucketName, objectKey, objectValue);

        service.deleteObject(bucketName, objectKey);

        assertEquals(
    		client.headObject(bucketName, objectKey).get(10, TimeUnit.SECONDS), 
    		org.jclouds.aws.s3.domain.S3Object.Metadata.NOT_FOUND);

        client.deleteBucketIfEmpty(bucketName);
    }

    private void addNewObject(String name, String objectKey, String objectValue)
            throws InterruptedException, ExecutionException, TimeoutException {
        createBucket(name);
        org.jclouds.aws.s3.domain.S3Object jcloudsObject = new org.jclouds.aws.s3.domain.S3Object(
                objectKey);
        jcloudsObject.setData(objectValue);
        Multimap<String, String> userMetadata = HashMultimap.create();
        userMetadata.put("metadata-name", "metadata-value");
        jcloudsObject.getMetadata().setUserMetadata(userMetadata);
        client.putObject(name, jcloudsObject).get(10, TimeUnit.SECONDS);
        
		org.jclouds.aws.s3.domain.S3Object createdObject = 
    		client.getObject(name, jcloudsObject.getKey()).get(10, TimeUnit.SECONDS);
		assertTrue(createdObject != org.jclouds.aws.s3.domain.S3Object.NOT_FOUND,
			"object should exist but doesn't");
		assertEquals(createdObject.getMetadata().getKey(), objectKey, "object misnamed");
    }

    @Test(enabled = false)
    public void testGetBucketAclImplString() {
        fail("Not yet implemented");
    }

    @Test(enabled = false)
    public void testGetBucketLocationImplString() {
        fail("Not yet implemented");
    }

    @Test(enabled = false)
    public void testGetBucketLoggingStatusImplString() {
        fail("Not yet implemented");
    }

    @Test(enabled = false)
    public void testGetObjectAclImplStringString() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetObjectDetailsImplStringStringCalendarCalendarStringArrayStringArray() 
    	throws InterruptedException, ExecutionException, TimeoutException, S3ServiceException 
	{
        String bucketName = bucketPrefix + ".testGetObjectDetailsImplStringStringCalendarCalendarStringArrayStringArray";
        String objectKey = "key";
        String objectValue = "test";

        addNewObject(bucketName, objectKey, objectValue);

        S3Object objectDetails = service.getObjectDetails(new S3Bucket(bucketName), objectKey);

        assertEquals(objectDetails.getKey(), objectKey);
        assertEquals(objectDetails.getContentLength(), 0);
        assertNull(objectDetails.getDataInputStream());
        // assertEquals(objectDetails.getMetadata("metadata-name"), "metadata-value");  // TODO
        		
        client.deleteObject(bucketName, objectKey);
        client.deleteBucketIfEmpty(bucketName);
    }

    @Test
    public void testGetObjectImplStringStringCalendarCalendarStringArrayStringArrayLongLong() 
    	throws InterruptedException, ExecutionException, TimeoutException, S3ServiceException, IOException 
	{
        String bucketName = bucketPrefix + ".testGetObjectImplStringStringCalendarCalendarStringArrayStringArrayLongLong";
        String objectKey = "key";
        String objectValue = "test";

        addNewObject(bucketName, objectKey, objectValue);

        S3Object object = service.getObject(new S3Bucket(bucketName), objectKey);
        
        assertEquals(object.getKey(), objectKey);
        assertNotNull(object.getDataInputStream());
        assertEquals(IOUtils.toString(object.getDataInputStream()), objectValue);
        // assertEquals(object.getContentLength(), objectKey.length());  // TODO
        // assertEquals(objectDetails.getMetadata("metadata-name"), "metadata-value");  // TODO
        		
        // TODO: Test conditional gets
        
        client.deleteObject(bucketName, objectKey);
        client.deleteBucketIfEmpty(bucketName);
    }

    @Test(enabled = false)
    public void testIsBucketAccessibleString() {
        fail("Not yet implemented");
    }

    @Test(enabled = false)
    public void testIsRequesterPaysBucketImplString() {
        fail("Not yet implemented");
    }

    @Test
    public void testListAllBucketsImpl() throws InterruptedException,
            ExecutionException, TimeoutException, S3ServiceException {
        // Ensure there is at least 1 bucket in S3 account to list and compare.
        String bucketName = bucketPrefix + ".testListAllBucketsImplString";
        createBucket(bucketName);

        S3Bucket[] jsBuckets = service.listAllBuckets();

        List<org.jclouds.aws.s3.domain.S3Bucket.Metadata> jcBuckets = client
                .listOwnedBuckets().get(10, TimeUnit.SECONDS);

        assert jsBuckets.length == jcBuckets.size();
        
        Iterator<org.jclouds.aws.s3.domain.S3Bucket.Metadata> jcBucketsIter = jcBuckets
                .iterator();
        for (S3Bucket jsBucket : jsBuckets) {
            assert jcBucketsIter.hasNext();

            org.jclouds.aws.s3.domain.S3Bucket.Metadata jcBucket = jcBucketsIter
                    .next();
            assert jsBucket.getName().equals(jcBucket.getName());
        }

        client.deleteBucketIfEmpty(bucketName);
    }

    @Test(enabled = false)
    public void testListObjectsChunkedImplStringStringStringLongStringBoolean() {
        fail("Not yet implemented");
    }

    @Test(enabled = false)
    public void testListObjectsImplStringStringStringLong() {
        fail("Not yet implemented");
    }

    @Test(enabled = false)
    public void testPutBucketAclImplStringAccessControlList() {
        fail("Not yet implemented");
    }

    @Test(enabled = false)
    public void testPutObjectAclImplStringStringAccessControlList() {
        fail("Not yet implemented");
    }

    @Test(enabled = false)
    public void testPutObjectImplStringS3Object() {
        fail("Not yet implemented");
    }

    @Test(enabled = false)
    public void testSetBucketLoggingStatusImplStringS3BucketLoggingStatus() {
        fail("Not yet implemented");
    }

    @Test(enabled = false)
    public void testSetRequesterPaysBucketImplStringBoolean() {
        fail("Not yet implemented");
    }

}
