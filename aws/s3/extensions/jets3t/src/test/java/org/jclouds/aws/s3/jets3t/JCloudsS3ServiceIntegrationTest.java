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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.IOUtils;
import org.jclouds.aws.s3.S3IntegrationTest;
import org.jclouds.aws.s3.config.StubS3ConnectionModule;
import org.jclouds.aws.s3.reference.S3Constants;
import org.jclouds.aws.s3.util.S3Utils;
import org.jclouds.http.ContentTypes;
import org.jets3t.service.S3ObjectsChunk;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

/**
 * Tests to cover JCloudsS3Service
 * 
 * @author James Murty
 * @author Adrian Cole
 */
@Test(groups = { "integration", "live" }, testName = "s3.JCloudsS3ServiceIntegrationTest")
public class JCloudsS3ServiceIntegrationTest extends S3IntegrationTest {
   AWSCredentials credentials;
   S3Service service;

   @Override
   protected boolean debugEnabled() {
      return true;
   }

   /**
    * overridden only to get access to the amazon credentials used for jets3t initialization.
    */
   @Override
   protected void createLiveS3Context(String AWSAccessKeyId, String AWSSecretAccessKey) {
      credentials = new AWSCredentials(AWSAccessKeyId, AWSSecretAccessKey);
      super.createLiveS3Context(AWSAccessKeyId, AWSSecretAccessKey);
   }

   /**
    * initialize a new JCloudsS3Service, but passing JavaUrlHttpFutureCommandClientModule(), as it
    * is easier to debug in unit tests.
    * 
    * @throws S3ServiceException
    */
   @BeforeMethod
   public void testJCloudsS3Service() throws S3ServiceException {
      service = (credentials != null) ? new JCloudsS3Service(credentials) : new JCloudsS3Service(
               new AWSCredentials("foo", "bar"), new StubS3ConnectionModule());
   }

   @Test
   public void testCreateBucketImpl() throws S3ServiceException, InterruptedException,
            ExecutionException, TimeoutException 
   {
      String bucketName = getScratchBucketName();
      try {
         S3Bucket bucket = service.createBucket(new S3Bucket(bucketName));
         assertEquals(bucket.getName(), bucketName);
         assertTrue(client.bucketExists(bucketName).get());
      } finally {
         returnScratchBucket(bucketName);
      }
   }

   @Test
   @AfterSuite
   public void testDeleteBucketImpl() throws S3ServiceException, InterruptedException,
            ExecutionException, TimeoutException {
      String bucketName = getScratchBucketName();
      try {
         service.deleteBucket(bucketName);
         assertFalse(client.bucketExists(bucketName).get(10, TimeUnit.SECONDS));
      } finally {
         returnScratchBucket(bucketName);
      }
   }

   @Test(dependsOnMethods = "testCreateBucketImpl")
   public void testDeleteObjectImpl() throws InterruptedException, ExecutionException,
            TimeoutException, S3ServiceException, IOException 
   {
      String bucketName = getBucketName();
      try {
         String objectKey = "key-testDeleteObjectImpl";
         String objectValue = "test";
   
         org.jclouds.aws.s3.domain.S3Object s3Object = new org.jclouds.aws.s3.domain.S3Object(
                  objectKey, objectValue);
         addObjectToBucket(bucketName, s3Object);
   
         service.deleteObject(bucketName, objectKey);
   
         assertEquals(client.headObject(bucketName, objectKey).get(10, TimeUnit.SECONDS),
                  org.jclouds.aws.s3.domain.S3Object.Metadata.NOT_FOUND);
      } finally {
         returnBucket(bucketName);
      }
   }

   @Test(dependsOnMethods = "testCreateBucketImpl")
   public void testGetObjectDetailsImpl() throws InterruptedException, ExecutionException,
            TimeoutException, S3ServiceException, IOException 
   {
      String bucketName = getBucketName();
      try {
         String objectKey = "key-testGetObjectDetailsImpl";
         String objectValue = "test";
         String metadataName = "metadata-name-1";
         String metadataValue = "metadata-value-1";
   
         org.jclouds.aws.s3.domain.S3Object s3Object = new org.jclouds.aws.s3.domain.S3Object(
                  objectKey, objectValue);
         s3Object.getMetadata().getUserMetadata().put(S3Constants.USER_METADATA_PREFIX + metadataName,
                  metadataValue);
         addObjectToBucket(bucketName, s3Object);
   
         S3Object objectDetails = service.getObjectDetails(new S3Bucket(bucketName), objectKey);
   
         assertEquals(objectDetails.getKey(), objectKey);
         assertEquals(objectDetails.getContentLength(), 4);
         assertNull(objectDetails.getDataInputStream());
         assertEquals(objectDetails.getMetadata(metadataName), metadataValue);
      } finally {
         returnBucket(bucketName);
      }
   }

   @Test(dependsOnMethods = "testCreateBucketImpl")
   public void testGetObjectImpl() throws InterruptedException, ExecutionException,
            TimeoutException, S3ServiceException, IOException 
   {
      String bucketName = getBucketName();
      try {
         String objectKey = "key-testGetObjectImpl";
         String objectValue = "test";
         String metadataName = "metadata-name-2";
         String metadataValue = "metadata-value-2";
   
         org.jclouds.aws.s3.domain.S3Object s3Object = new org.jclouds.aws.s3.domain.S3Object(
                  objectKey, objectValue);
         s3Object.getMetadata().getUserMetadata().put(S3Constants.USER_METADATA_PREFIX + metadataName,
                  metadataValue);
         addObjectToBucket(bucketName, s3Object);
   
         S3Object object = service.getObject(new S3Bucket(bucketName), objectKey);
   
         assertEquals(object.getKey(), objectKey);
         assertNotNull(object.getDataInputStream());
         assertEquals(IOUtils.toString(object.getDataInputStream()), objectValue);
         assertEquals(object.getContentLength(), objectValue.length());
         assertEquals(object.getMetadata(metadataName), metadataValue);
   
         // TODO: Test conditional gets
      } finally {
         returnBucket(bucketName);
      }
   }

   @Test(dependsOnMethods = "testCreateBucketImpl")
   public void testListAllBucketsImpl() throws InterruptedException, ExecutionException,
            TimeoutException, S3ServiceException 
   {
      String bucketName = getBucketName();
      try {      
         // Ensure there is at least 1 bucket in S3 account to list and compare.
         S3Bucket[] jsBuckets = service.listAllBuckets();
   
         List<org.jclouds.aws.s3.domain.S3Bucket.Metadata> jcBuckets = client.listOwnedBuckets().get(
                  10, TimeUnit.SECONDS);
   
         assert jsBuckets.length == jcBuckets.size();
   
         Iterator<org.jclouds.aws.s3.domain.S3Bucket.Metadata> jcBucketsIter = jcBuckets.iterator();
         for (S3Bucket jsBucket : jsBuckets) {
            assert jcBucketsIter.hasNext();
   
            org.jclouds.aws.s3.domain.S3Bucket.Metadata jcBucket = jcBucketsIter.next();
            assert jsBucket.getName().equals(jcBucket.getName());
         }
      } finally {
         returnBucket(bucketName);
      }
   }

   @Test(dependsOnMethods = "testCreateBucketImpl")
   public void testListObjectsChunkedImpl() throws InterruptedException, ExecutionException,
            TimeoutException, IOException, S3ServiceException {
      String bucketName = getBucketName();
      try {
         addObjectToBucket(bucketName, "item1/subobject2");
         addObjectToBucket(bucketName, "item2");
         addObjectToBucket(bucketName, "object1");
         addObjectToBucket(bucketName, "object2/subobject1");
   
         S3ObjectsChunk chunk;
   
         // Normal complete listing
         chunk = service.listObjectsChunked(bucketName, null, null, 1000, null, true);
         assertEquals(chunk.getObjects().length, 4);
         assertEquals(chunk.getCommonPrefixes().length, 0);
         assertNull(chunk.getDelimiter());
         assertNull(chunk.getPrefix());
         assertNull(chunk.getPriorLastKey());
   
         // Partial listing
         chunk = service.listObjectsChunked(bucketName, null, null, 2, null, false);
         assertEquals(chunk.getObjects().length, 2);
         assertEquals(chunk.getCommonPrefixes().length, 0);
         assertNull(chunk.getDelimiter());
         assertNull(chunk.getPrefix());
         assertEquals(chunk.getPriorLastKey(), "item2");
   
         // Complete listing, in two chunks
         chunk = service.listObjectsChunked(bucketName, null, null, 2, null, true);
         assertEquals(chunk.getObjects().length, 4);
         assertEquals(chunk.getCommonPrefixes().length, 0);
         assertNull(chunk.getDelimiter());
         assertNull(chunk.getPrefix());
         assertNull(chunk.getPriorLastKey());
   
         // Partial listing with marker
         chunk = service.listObjectsChunked(bucketName, null, null, 1000, "item1/subobject2", true);
         assertEquals(chunk.getObjects().length, 3);
         assertEquals(chunk.getCommonPrefixes().length, 0);
         assertNull(chunk.getDelimiter());
         assertNull(chunk.getPrefix());
         assertNull(chunk.getPriorLastKey());
   
         // Partial listing with marker
         chunk = service.listObjectsChunked(bucketName, null, null, 1000, "object1", true);
         assertEquals(chunk.getObjects().length, 1);
         assertEquals(chunk.getCommonPrefixes().length, 0);
         assertNull(chunk.getDelimiter());
         assertNull(chunk.getPrefix());
         assertNull(chunk.getPriorLastKey());
   
         // Prefix test
         chunk = service.listObjectsChunked(bucketName, "item", null, 1000, null, true);
         assertEquals(chunk.getObjects().length, 2);
         assertEquals(chunk.getCommonPrefixes().length, 0);
         assertNull(chunk.getDelimiter());
         assertEquals(chunk.getPrefix(), "item");
         assertNull(chunk.getPriorLastKey());
   
         // Delimiter test
         chunk = service.listObjectsChunked(bucketName, null, "/", 1000, null, true);
         assertEquals(chunk.getObjects().length, 2);
         assertEquals(chunk.getCommonPrefixes().length, 2);
         assertEquals(chunk.getDelimiter(), "/");
         assertNull(chunk.getPrefix());
         assertNull(chunk.getPriorLastKey());
   
         // Prefix & delimiter test
         chunk = service.listObjectsChunked(bucketName, "item", "/", 1000, null, true);
         assertEquals(chunk.getObjects().length, 1);
         assertEquals(chunk.getCommonPrefixes().length, 1);
         assertEquals(chunk.getDelimiter(), "/");
         assertEquals(chunk.getPrefix(), "item");
         assertNull(chunk.getPriorLastKey());
      } finally {
         returnBucket(bucketName);
      }
   }

   @Test(dependsOnMethods = "testCreateBucketImpl")
   public void testListObjectsImpl() throws InterruptedException, ExecutionException,
            TimeoutException, IOException, S3ServiceException {
      String bucketName = null;
      try {
         bucketName = getScratchBucketName();
         addObjectToBucket(bucketName, "item1/subobject2");
         addObjectToBucket(bucketName, "item2");
         addObjectToBucket(bucketName, "object1");
         addObjectToBucket(bucketName, "object2/subobject1");
   
         S3Object[] objects;
   
         // Normal complete listing
         objects = service.listObjects(bucketName, null, null, 1000);
         assertEquals(objects.length, 4);
   
         // Complete listing, in two chunks
         objects = service.listObjects(bucketName, null, null, 2);
         assertEquals(objects.length, 4);
         assertEquals(objects[0].getKey(), "item1/subobject2");
         assertEquals(objects[3].getKey(), "object2/subobject1");
   
         // Prefix test
         objects = service.listObjects(bucketName, "item", null, 1000);
         assertEquals(objects.length, 2);
   
         // Delimiter test
         objects = service.listObjects(bucketName, null, "/", 1000);
         assertEquals(objects.length, 2);
         assertEquals(objects[0].getKey(), "item2");
         assertEquals(objects[1].getKey(), "object1");
   
         // Prefix & delimiter test
         objects = service.listObjects(bucketName, "item", "/", 1000);
         assertEquals(objects.length, 1);
         assertEquals(objects[0].getKey(), "item2");
      } finally {
         returnBucket(bucketName);
      }
   }

   @Test(dependsOnMethods = "testCreateBucketImpl")
   public void testPutObjectImpl() throws S3ServiceException, InterruptedException,
            ExecutionException, TimeoutException, NoSuchAlgorithmException, IOException {
      String bucketName = getBucketName();
      try {
         String objectKey = "putObject";
   
         S3Object requestObject, jsResultObject;
         org.jclouds.aws.s3.domain.S3Object jcObject;
   
         // Upload empty object
         requestObject = new S3Object(objectKey);
         jsResultObject = service.putObject(new S3Bucket(bucketName), requestObject);
         jcObject = client.getObject(bucketName, objectKey).get(10, TimeUnit.SECONDS);
         assertEquals(jcObject.getKey(), objectKey);
         assertEquals(jcObject.getMetadata().getSize(), 0);
         assertEquals(jcObject.getMetadata().getContentType(), ContentTypes.BINARY);
         assertEquals(jsResultObject.getKey(), requestObject.getKey());
         assertEquals(jsResultObject.getContentLength(), 0);
         assertEquals(jsResultObject.getContentType(), ContentTypes.BINARY);
   
         // Upload unicode-named object
         requestObject = new S3Object("üníçòdé-object");
         jsResultObject = service.putObject(new S3Bucket(bucketName), requestObject);
         jcObject = client.getObject(bucketName, requestObject.getKey()).get(10, TimeUnit.SECONDS);
         assertEquals(jcObject.getKey(), requestObject.getKey());
         assertEquals(jcObject.getMetadata().getSize(), 0);
         assertEquals(jcObject.getMetadata().getContentType(), ContentTypes.BINARY);
         assertEquals(jsResultObject.getKey(), requestObject.getKey());
         assertEquals(jsResultObject.getContentLength(), 0);
         assertEquals(jsResultObject.getContentType(), ContentTypes.BINARY);
   
         // Upload string object
         String data = "This is my üníçòdé data";
         requestObject = new S3Object(objectKey, data);
         jsResultObject = service.putObject(new S3Bucket(bucketName), requestObject);
         jcObject = client.getObject(bucketName, objectKey).get(10, TimeUnit.SECONDS);
         assertEquals(jcObject.getMetadata().getSize(), data.getBytes("UTF-8").length);
         assertTrue(jcObject.getMetadata().getContentType().startsWith("text/plain"));
         assertEquals(jsResultObject.getContentLength(), data.getBytes("UTF-8").length);
         assertTrue(jsResultObject.getContentType().startsWith("text/plain"));
   
         // Upload object with metadata
         requestObject = new S3Object(objectKey);
         requestObject.addMetadata(S3Constants.USER_METADATA_PREFIX + "my-metadata-1", "value-1");
         jsResultObject = service.putObject(new S3Bucket(bucketName), requestObject);
         jcObject = client.getObject(bucketName, objectKey).get(10, TimeUnit.SECONDS);
         assertEquals(Iterables.getLast(jcObject.getMetadata().getUserMetadata().get(
                  S3Constants.USER_METADATA_PREFIX + "my-metadata-1")), "value-1");
         assertEquals(jsResultObject.getMetadata(S3Constants.USER_METADATA_PREFIX + "my-metadata-1"),
                  "value-1");
   
         // Upload object with public-read ACL
         requestObject = new S3Object(objectKey);
         requestObject.setAcl(AccessControlList.REST_CANNED_PUBLIC_READ);
         jsResultObject = service.putObject(new S3Bucket(bucketName), requestObject);
         jcObject = client.getObject(bucketName, objectKey).get(10, TimeUnit.SECONDS);
         // TODO: No way yet to get/lookup ACL from jClouds object
         // assertEquals(jcObject.getAcl(), CannedAccessPolicy.PUBLIC_READ);
         assertEquals(jsResultObject.getAcl(), AccessControlList.REST_CANNED_PUBLIC_READ);
   
         // TODO : Any way to test a URL lookup that works for live and stub testing?
         // URL publicUrl = new URL(
         // "http://" + bucketName + ".s3.amazonaws.com:80/" + requestObject.getKey());
         // assertEquals(((HttpURLConnection) publicUrl.openConnection()).getResponseCode(), 200);
   
         // Upload object and check MD5
         requestObject = new S3Object(objectKey);
         data = "Here is some dátà for you";
         requestObject.setDataInputStream(new ByteArrayInputStream(data.getBytes("UTF-8")));
         jsResultObject = service.putObject(new S3Bucket(bucketName), requestObject);
         jcObject = client.getObject(bucketName, objectKey).get(10, TimeUnit.SECONDS);
         assertTrue(jsResultObject.verifyData(data.getBytes("UTF-8")));
         assertEquals(jsResultObject.getMd5HashAsHex(), S3Utils.toHexString(jcObject.getMetadata()
                  .getMd5()));
      } finally {
         returnBucket(bucketName);
      }
   }

   @Test(dependsOnMethods = "testCreateBucketImpl")
   @SuppressWarnings("unchecked")
   public void testCopyObjectImpl() throws InterruptedException, ExecutionException,
            TimeoutException, IOException, S3ServiceException {
      String bucketName = getBucketName();
      try {
         String data = "This is my data";
         String sourceObjectKey = "öriginalObject"; // Notice the use of non-ASCII
         String destinationObjectKey = "déstinationObject"; // characters here.
         String metadataName = "metadata-name";
         String sourceMetadataValue = "souce-metadata-value";
         String destinationMetadataValue = "destination-metadata-value";
   
         org.jclouds.aws.s3.domain.S3Object sourceObject = new org.jclouds.aws.s3.domain.S3Object(
                  sourceObjectKey, data);
         sourceObject.getMetadata().getUserMetadata().put(
                  S3Constants.USER_METADATA_PREFIX + metadataName, sourceMetadataValue);
         addObjectToBucket(bucketName, sourceObject);
   
         S3Object destinationObject;
         Map copyResult;
         org.jclouds.aws.s3.domain.S3Object jcDestinationObject;
   
         // Copy with metadata and ACL retained
         destinationObject = new S3Object(destinationObjectKey);
         copyResult = service.copyObject(bucketName, sourceObjectKey, bucketName, destinationObject,
                  false);
         jcDestinationObject = client.getObject(bucketName, destinationObject.getKey()).get(10,
                  TimeUnit.SECONDS);
         assertEquals(jcDestinationObject.getKey(), destinationObjectKey);
         assertEquals(Iterators.getLast(jcDestinationObject.getMetadata().getUserMetadata().get(
                  S3Constants.USER_METADATA_PREFIX + metadataName).iterator()), sourceMetadataValue);
         assertEquals(copyResult.get("ETag"), S3Utils.toHexString(jcDestinationObject.getMetadata()
                  .getMd5()));
         // TODO: Test destination ACL is unchanged (ie private)
   
         // Copy with metadata replaced
         destinationObject = new S3Object(destinationObjectKey);
         destinationObject.addMetadata(S3Constants.USER_METADATA_PREFIX + metadataName,
                  destinationMetadataValue);
         copyResult = service.copyObject(bucketName, sourceObjectKey, bucketName, destinationObject,
                  true);
         jcDestinationObject = client.getObject(bucketName, destinationObject.getKey()).get(10,
                  TimeUnit.SECONDS);
         assertEquals(Iterators.getLast(jcDestinationObject.getMetadata().getUserMetadata().get(
                  S3Constants.USER_METADATA_PREFIX + metadataName).iterator()),
                  destinationMetadataValue);
         // TODO: Test destination ACL is unchanged (ie private)
   
         // Copy with ACL modified
         destinationObject = new S3Object(destinationObjectKey);
         destinationObject.setAcl(AccessControlList.REST_CANNED_PUBLIC_READ);
         copyResult = service.copyObject(bucketName, sourceObjectKey, bucketName, destinationObject,
                  false);
         jcDestinationObject = client.getObject(bucketName, destinationObject.getKey()).get(10,
                  TimeUnit.SECONDS);
         // TODO: Test destination ACL is changed (ie public-read)
      } finally {
         returnBucket(bucketName);
      }
   }

   @Test(enabled = false)
   public void testCheckBucketStatus() {
      fail("Not yet implemented");
   }

   @Test(enabled = false)
   public void testGetBucketAclImpl() {
      fail("Not yet implemented");
   }

   @Test(enabled = false)
   public void testGetBucketLocationImpl() {
      fail("Not yet implemented");
   }

   @Test(enabled = false)
   public void testGetBucketLoggingStatus() {
      fail("Not yet implemented");
   }

   @Test(enabled = false)
   public void testGetObjectAclImpl() {
      fail("Not yet implemented");
   }

   @Test(enabled = false)
   public void testPutBucketAclImpl() {
      fail("Not yet implemented");
   }

   @Test(enabled = false)
   public void testPutObjectAclImpl() {
      fail("Not yet implemented");
   }

   @Test(enabled = false)
   public void testSetBucketLoggingStatusImpl() {
      fail("Not yet implemented");
   }

   @Test(enabled = false)
   public void testSetRequesterPaysBucketImpl() {
      fail("Not yet implemented");
   }

   @Test(enabled = false)
   public void testIsBucketAccessible() {
      fail("Not yet implemented");
   }

   @Test(enabled = false)
   public void testIsRequesterPaysBucketImpl() {
      fail("Not yet implemented");
   }

}
