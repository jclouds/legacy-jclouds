/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.ws.rs.core.MediaType;

import org.jclouds.aws.s3.S3AsyncClient;
import org.jclouds.aws.s3.S3Client;
import org.jclouds.aws.s3.domain.ListBucketResponse;
import org.jclouds.aws.s3.domain.AccessControlList.GroupGranteeURI;
import org.jclouds.aws.s3.domain.AccessControlList.Permission;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.integration.internal.BaseBlobStoreIntegrationTest;
import org.jclouds.util.Utils;
import org.jets3t.service.S3ObjectsChunk;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.acl.GrantAndPermission;
import org.jets3t.service.acl.GroupGrantee;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.multithread.CreateObjectsEvent;
import org.jets3t.service.multithread.S3ServiceEventAdaptor;
import org.jets3t.service.multithread.S3ServiceEventListener;
import org.jets3t.service.multithread.S3ServiceMulti;
import org.jets3t.service.security.AWSCredentials;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * Tests to cover JCloudsS3Service
 * 
 * @author James Murty
 * @author Adrian Cole
 */
@Test(groups = { "live" }, testName = "jets3t.JCloudsS3ServiceIntegrationTest")
public class JCloudsS3ServiceLiveTest extends BaseBlobStoreIntegrationTest<S3AsyncClient, S3Client> {
   AWSCredentials credentials;
   S3Service service;

   /**
    * overridden only to get access to the amazon credentials used for jets3t initialization.
    * 
    * @throws S3ServiceException
    */
   @BeforeClass(groups = { "live" }, dependsOnMethods = "setUpResourcesOnThisThread")
   protected void createLiveS3Context(ITestContext testContext) throws S3ServiceException {

      String account = System.getProperty("jclouds.test.user");
      String key = System.getProperty("jclouds.test.key");

      if (account != null) {
         credentials = new AWSCredentials(account, key);
         service = new JCloudsS3Service(credentials);
      } else {
         assert false : "credentials not present";
      }
   }

   @Test
   public void testCreateBucketImpl() throws S3ServiceException, InterruptedException,
            ExecutionException, TimeoutException {
      String bucketName = getContainerName();
      try {
         S3Bucket bucket = service.createBucket(new S3Bucket(bucketName));
         assertEquals(bucket.getName(), bucketName);
         assertTrue(context.getApi().bucketExists(bucketName));
      } finally {
         returnContainer(bucketName);
      }
   }

   @Test
   public void testDeleteBucketImpl() throws S3ServiceException, InterruptedException,
            ExecutionException, TimeoutException {
      final String bucketName = getContainerName();
      service.deleteBucket(bucketName);
      assertConsistencyAware(new Runnable() {
         public void run() {
            assertFalse(context.getApi().bucketExists(bucketName));
         }
      });
   }

   @Test
   public void testDeleteObjectImpl() throws InterruptedException, ExecutionException,
            TimeoutException, S3ServiceException, IOException {
      String bucketName = getContainerName();
      try {
         String objectKey = "key-testDeleteObjectImpl";
         String objectValue = "test";
         Blob blob = context.getAsyncBlobStore().newBlob();
         blob.getMetadata().setName(objectKey);
         blob.setPayload(objectValue);
         addBlobToContainer(bucketName, blob);

         service.deleteObject(bucketName, objectKey);
      } finally {
         returnContainer(bucketName);
      }
   }

   @Test
   public void testGetObjectDetailsImpl() throws InterruptedException, ExecutionException,
            TimeoutException, S3ServiceException, IOException {
      String bucketName = getContainerName();
      try {
         String objectKey = "key-testGetObjectDetailsImpl".toLowerCase();
         String objectValue = "test";
         String metadataName = "metadata-name-1";
         String metadataValue = "metadata-value-1";

         Blob blob = context.getAsyncBlobStore().newBlob();
         blob.getMetadata().setName(objectKey);
         blob.setPayload(objectValue);
         blob.getMetadata().getUserMetadata().put(metadataName, metadataValue);
         addBlobToContainer(bucketName, blob);

         S3Object objectDetails = service.getObjectDetails(new S3Bucket(bucketName), objectKey);

         // TODO null keys from s3object! assertEquals(objectDetails.getKey(), objectKey);
         assertEquals(objectDetails.getContentLength(), 4);
         assertNull(objectDetails.getDataInputStream());
         assertEquals(objectDetails.getMetadata(metadataName), metadataValue);
      } finally {
         returnContainer(bucketName);
      }
   }

   @Test
   public void testGetObjectImpl() throws InterruptedException, ExecutionException,
            TimeoutException, S3ServiceException, IOException {
      String bucketName = getContainerName();
      try {
         String objectKey = "key-testGetObjectImpl".toLowerCase();
         String objectValue = "test";
         String metadataName = "metadata-name-2";
         String metadataValue = "metadata-value-2";

         Blob blob = context.getAsyncBlobStore().newBlob();
         blob.getMetadata().setName(objectKey);
         blob.setPayload(objectValue);
         blob.getMetadata().getUserMetadata().put(metadataName, metadataValue);
         addBlobToContainer(bucketName, blob);

         S3Object object = service.getObject(new S3Bucket(bucketName), objectKey);

         // TODO null keys from s3object! assertEquals(object.getKey(), objectKey);
         assertNotNull(object.getDataInputStream());
         assertEquals(Utils.toStringAndClose(object.getDataInputStream()), objectValue);
         assertEquals(object.getContentLength(), objectValue.length());
         assertEquals(object.getMetadata(metadataName), metadataValue);

         // TODO: Test conditional gets
      } finally {
         returnContainer(bucketName);
      }
   }

   @Test
   public void testListAllBucketsImpl() throws InterruptedException, ExecutionException,
            TimeoutException, S3ServiceException {
      String bucketName = getContainerName();
      try {
         // Ensure there is at least 1 bucket in S3 account to list and compare.
         S3Bucket[] jsBuckets = service.listAllBuckets();

         SortedSet<org.jclouds.aws.s3.domain.BucketMetadata> jcBuckets = context.getApi()
                  .listOwnedBuckets();

         assert jsBuckets.length == jcBuckets.size();

         Iterator<org.jclouds.aws.s3.domain.BucketMetadata> jcBucketsIter = jcBuckets.iterator();
         for (S3Bucket jsBucket : jsBuckets) {
            assert jcBucketsIter.hasNext();

            org.jclouds.aws.s3.domain.BucketMetadata jcBucket = jcBucketsIter.next();
            assert jsBucket.getName().equals(jcBucket.getName());
         }
      } finally {
         returnContainer(bucketName);
      }
   }

   @Test
   public void testListObjectsChunkedImpl() throws InterruptedException, ExecutionException,
            TimeoutException, IOException, S3ServiceException {
      String bucketName = getContainerName();
      try {
         addBlobToContainer(bucketName, "item1/subobject2");
         addBlobToContainer(bucketName, "item2");
         addBlobToContainer(bucketName, "object1");
         addBlobToContainer(bucketName, "object2/subobject1");

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
         returnContainer(bucketName);
      }
   }

   @Test
   public void testListObjectsImpl() throws InterruptedException, ExecutionException,
            TimeoutException, IOException, S3ServiceException {
      String bucketName = null;
      try {
         bucketName = getContainerName();
         addBlobToContainer(bucketName, "item1/subobject2");
         addBlobToContainer(bucketName, "item2");
         addBlobToContainer(bucketName, "object1");
         addBlobToContainer(bucketName, "object2/subobject1");

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
         returnContainer(bucketName);
      }
   }

   @Test
   public void testPutObjectImpl() throws S3ServiceException, InterruptedException,
            ExecutionException, TimeoutException, NoSuchAlgorithmException, IOException {
      String bucketName = getContainerName();
      try {
         String objectKey = "putObject";

         S3Object requestObject, jsResultObject;
         org.jclouds.aws.s3.domain.S3Object jcObject;

         // Upload empty object
         requestObject = new S3Object(objectKey);
         jsResultObject = service.putObject(new S3Bucket(bucketName), requestObject);
         jcObject = context.getApi().getObject(bucketName, objectKey);
         // TODO null keys from s3object! assertEquals(jcObject.getKey(), objectKey);
         assertEquals(jcObject.getMetadata().getSize(), new Long(0));
         assertEquals(jcObject.getMetadata().getContentType(), MediaType.APPLICATION_OCTET_STREAM);
         assertEquals(jsResultObject.getKey(), requestObject.getKey());
         assertEquals(jsResultObject.getContentLength(), 0);
         assertEquals(jsResultObject.getContentType(), MediaType.APPLICATION_OCTET_STREAM);

         // Upload unicode-named object
         requestObject = new S3Object("₪n₪₪₪d₪-object");
         jsResultObject = service.putObject(new S3Bucket(bucketName), requestObject);
         jcObject = context.getApi().getObject(bucketName, requestObject.getKey());
         // TODO null keys from s3object! assertEquals(jcObject.getKey(), requestObject.getKey());
         assertEquals(jcObject.getMetadata().getSize(), new Long(0));
         assertEquals(jcObject.getMetadata().getContentType(), MediaType.APPLICATION_OCTET_STREAM);
         assertEquals(jsResultObject.getKey(), requestObject.getKey());
         assertEquals(jsResultObject.getContentLength(), 0);
         assertEquals(jsResultObject.getContentType(), MediaType.APPLICATION_OCTET_STREAM);

         // Upload string object
         String data = "This is my ₪n₪₪₪d₪ data";
         requestObject = new S3Object(objectKey, data);
         jsResultObject = service.putObject(new S3Bucket(bucketName), requestObject);
         jcObject = context.getApi().getObject(bucketName, objectKey);
         assertEquals(jcObject.getMetadata().getSize(), new Long(data.getBytes("UTF-8").length));
         assertTrue(jcObject.getMetadata().getContentType().startsWith("text/plain"));
         assertEquals(jsResultObject.getContentLength(), data.getBytes("UTF-8").length);
         assertTrue(jsResultObject.getContentType().startsWith("text/plain"));

         // Upload object with metadata
         requestObject = new S3Object(objectKey);
         requestObject.addMetadata("x-amz-meta-" + "my-metadata-1", "value-1");
         jsResultObject = service.putObject(new S3Bucket(bucketName), requestObject);
         jcObject = context.getApi().getObject(bucketName, objectKey);
         assertEquals(jcObject.getMetadata().getUserMetadata().get("my-metadata-1"), "value-1");
         assertEquals(jsResultObject.getMetadata("x-amz-meta-" + "my-metadata-1"), "value-1");

         // Upload object with canned public-read ACL
         requestObject = new S3Object(objectKey);
         requestObject.setAcl(AccessControlList.REST_CANNED_PUBLIC_READ);
         jsResultObject = service.putObject(new S3Bucket(bucketName), requestObject);
         org.jclouds.aws.s3.domain.AccessControlList jcACL = context.getApi().getObjectACL(
                  bucketName, objectKey);
         assertTrue(jcACL.hasPermission(GroupGranteeURI.ALL_USERS, Permission.READ));
         assertTrue(jcACL.hasPermission(jcACL.getOwner().getId(), Permission.FULL_CONTROL));
         assertEquals(jcACL.getGrants().size(), 2);

         // TODO : Any way to test a URL lookup that works for live and stub testing?
         // URL publicUrl = new URL(
         // "http://" + bucketName + ".s3.amazonaws.com:80/" + requestObject.getKey());
         // assertEquals(((HttpURLClient) publicUrl.openClient()).getResponseCode(), 200);

         // Upload object and check MD5
         requestObject = new S3Object(objectKey);
         data = "Here is some d₪t₪ for you";
         requestObject.setDataInputStream(new ByteArrayInputStream(data.getBytes("UTF-8")));
         jsResultObject = service.putObject(new S3Bucket(bucketName), requestObject);
         jcObject = context.getApi().getObject(bucketName, objectKey);
         assertTrue(jsResultObject.verifyData(data.getBytes("UTF-8")));
         assertEquals(jsResultObject.getETag(), jcObject.getMetadata().getETag().replaceAll("\"",
                  ""));
      } finally {
         returnContainer(bucketName);
      }
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testCopyObjectImpl() throws InterruptedException, ExecutionException,
            TimeoutException, IOException, S3ServiceException {
      String bucketName = getContainerName();
      try {
         String data = "This is my data";
         String sourceObjectKey = "₪riginalObject"; // Notice the use of non-ASCII
         String destinationObjectKey = "d₪stinationObject"; // characters here.
         String metadataName = "metadata-name";
         String sourceMetadataValue = "souce-metadata-value";
         String destinationMetadataValue = "destination-metadata-value";

         Blob blob = context.getAsyncBlobStore().newBlob();
         blob.getMetadata().setName(sourceObjectKey);
         blob.setPayload(data);
         blob.getMetadata().getUserMetadata().put(metadataName, sourceMetadataValue);
         addBlobToContainer(bucketName, blob);

         S3Object destinationObject;
         Map copyResult;
         org.jclouds.aws.s3.domain.S3Object jcDestinationObject;

         // Copy with metadata and ACL retained
         destinationObject = new S3Object(destinationObjectKey);
         copyResult = service.copyObject(bucketName, sourceObjectKey, bucketName,
                  destinationObject, false);
         jcDestinationObject = context.getApi().getObject(bucketName, destinationObject.getKey());
         // TODO null keys from s3object! assertEquals(jcDestinationObject.getKey(),
         // destinationObjectKey);
         assertEquals(jcDestinationObject.getMetadata().getUserMetadata().get(metadataName),
                  sourceMetadataValue);
         assertEquals(copyResult.get("ETag"), jcDestinationObject.getMetadata().getETag());
         // Test destination ACL is unchanged (ie private)
         org.jclouds.aws.s3.domain.AccessControlList jcACL = context.getApi().getObjectACL(
                  bucketName, destinationObject.getKey());
         assertEquals(jcACL.getGrants().size(), 1);
         assertTrue(jcACL.hasPermission(jcACL.getOwner().getId(), Permission.FULL_CONTROL));

         // Copy with metadata replaced
         destinationObject = new S3Object(destinationObjectKey);
         destinationObject.addMetadata("x-amz-meta-" + metadataName, destinationMetadataValue);
         copyResult = service.copyObject(bucketName, sourceObjectKey, bucketName,
                  destinationObject, true);
         jcDestinationObject = context.getApi().getObject(bucketName, destinationObject.getKey());
         assertEquals(jcDestinationObject.getMetadata().getUserMetadata().get(metadataName),
                  destinationMetadataValue);
         // Test destination ACL is unchanged (ie private)
         jcACL = context.getApi().getObjectACL(bucketName, destinationObject.getKey());
         assertEquals(jcACL.getGrants().size(), 1);
         assertTrue(jcACL.hasPermission(jcACL.getOwner().getId(), Permission.FULL_CONTROL));

         // Copy with ACL modified
         destinationObject = new S3Object(destinationObjectKey);
         destinationObject.setAcl(AccessControlList.REST_CANNED_PUBLIC_READ);
         copyResult = service.copyObject(bucketName, sourceObjectKey, bucketName,
                  destinationObject, false);
         // Test destination ACL is changed (ie public-read)
         jcACL = context.getApi().getObjectACL(bucketName, destinationObject.getKey());
         assertEquals(jcACL.getGrants().size(), 2);
         assertTrue(jcACL.hasPermission(jcACL.getOwner().getId(), Permission.FULL_CONTROL));
         assertTrue(jcACL.hasPermission(GroupGranteeURI.ALL_USERS, Permission.READ));

      } finally {
         returnContainer(bucketName);
      }
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testPutAndGetBucketAclImpl() throws InterruptedException, ExecutionException,
            TimeoutException, S3ServiceException {
      String bucketName = getContainerName();
      try {
         S3Bucket bucket = new S3Bucket(bucketName);
         AccessControlList acl = null;

         // Confirm bucket is created private by default.
         acl = service.getBucketAcl(bucket);
         final String ownerId = acl.getOwner().getId();
         assertEquals(acl.getGrants().size(), 1);
         GrantAndPermission gap = (GrantAndPermission) Iterables.find(acl.getGrants(),
                  new Predicate<GrantAndPermission>() {
                     public boolean apply(GrantAndPermission gap) {
                        return gap.getGrantee().getIdentifier().equals(ownerId);
                     }
                  });
         assertNotNull(gap);
         assertEquals(gap.getPermission(),
                  org.jets3t.service.acl.Permission.PERMISSION_FULL_CONTROL);

         // Add read access for public, and read-acp access for authenticated users.
         acl.grantPermission(GroupGrantee.ALL_USERS,
                  org.jets3t.service.acl.Permission.PERMISSION_READ);
         acl.grantPermission(GroupGrantee.AUTHENTICATED_USERS,
                  org.jets3t.service.acl.Permission.PERMISSION_READ_ACP);
         service.putBucketAcl(bucketName, acl);
         acl = service.getBucketAcl(bucket);
         assertEquals(acl.getGrants().size(), 3);
         gap = (GrantAndPermission) Iterables.find(acl.getGrants(),
                  new Predicate<GrantAndPermission>() {
                     public boolean apply(GrantAndPermission gap) {
                        return gap.getGrantee().getIdentifier().equals(ownerId);
                     }
                  });
         assertEquals(gap.getPermission(),
                  org.jets3t.service.acl.Permission.PERMISSION_FULL_CONTROL);
         gap = (GrantAndPermission) Iterables.find(acl.getGrants(),
                  new Predicate<GrantAndPermission>() {
                     public boolean apply(GrantAndPermission gap) {
                        return gap.getGrantee().getIdentifier().equals(
                                 GroupGrantee.ALL_USERS.getIdentifier());
                     }
                  });
         assertEquals(gap.getPermission(), org.jets3t.service.acl.Permission.PERMISSION_READ);
         gap = (GrantAndPermission) Iterables.find(acl.getGrants(),
                  new Predicate<GrantAndPermission>() {
                     public boolean apply(GrantAndPermission gap) {
                        return gap.getGrantee().getIdentifier().equals(
                                 GroupGrantee.AUTHENTICATED_USERS.getIdentifier());
                     }
                  });
         assertEquals(gap.getPermission(), org.jets3t.service.acl.Permission.PERMISSION_READ_ACP);
      } finally {
         // need to delete this container as we've modified its acls
         destroyContainer(bucketName);
      }
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testGetAndPutObjectAclImpl() throws InterruptedException, ExecutionException,
            TimeoutException, S3ServiceException, NoSuchAlgorithmException, IOException {
      String bucketName = getContainerName();
      try {
         S3Bucket bucket = new S3Bucket(bucketName);
         S3Object object = new S3Object("testGetAndPutObjectAclImpl", "my data");
         AccessControlList acl = null;

         // Create default object.
         service.putObject(bucket, object);

         // Confirm object is created private by default.
         acl = service.getObjectAcl(bucket, object.getKey());
         final String ownerId = acl.getOwner().getId();
         assertEquals(acl.getGrants().size(), 1);
         GrantAndPermission gap = (GrantAndPermission) Iterables.find(acl.getGrants(),
                  new Predicate<GrantAndPermission>() {
                     public boolean apply(GrantAndPermission gap) {
                        return gap.getGrantee().getIdentifier().equals(ownerId);
                     }
                  });
         assertNotNull(gap);
         assertEquals(gap.getPermission(),
                  org.jets3t.service.acl.Permission.PERMISSION_FULL_CONTROL);

         // Add read access for public, and read-acp access for authenticated users.
         acl.grantPermission(GroupGrantee.ALL_USERS,
                  org.jets3t.service.acl.Permission.PERMISSION_READ);
         acl.grantPermission(GroupGrantee.AUTHENTICATED_USERS,
                  org.jets3t.service.acl.Permission.PERMISSION_READ_ACP);
         service.putObjectAcl(bucketName, object.getKey(), acl);
         acl = service.getObjectAcl(bucket, object.getKey());
         assertEquals(acl.getGrants().size(), 3);
         gap = (GrantAndPermission) Iterables.find(acl.getGrants(),
                  new Predicate<GrantAndPermission>() {
                     public boolean apply(GrantAndPermission gap) {
                        return gap.getGrantee().getIdentifier().equals(ownerId);
                     }
                  });
         assertEquals(gap.getPermission(),
                  org.jets3t.service.acl.Permission.PERMISSION_FULL_CONTROL);
         gap = (GrantAndPermission) Iterables.find(acl.getGrants(),
                  new Predicate<GrantAndPermission>() {
                     public boolean apply(GrantAndPermission gap) {
                        return gap.getGrantee().getIdentifier().equals(
                                 GroupGrantee.ALL_USERS.getIdentifier());
                     }
                  });
         assertEquals(gap.getPermission(), org.jets3t.service.acl.Permission.PERMISSION_READ);
         gap = (GrantAndPermission) Iterables.find(acl.getGrants(),
                  new Predicate<GrantAndPermission>() {
                     public boolean apply(GrantAndPermission gap) {
                        return gap.getGrantee().getIdentifier().equals(
                                 GroupGrantee.AUTHENTICATED_USERS.getIdentifier());
                     }
                  });
         assertEquals(gap.getPermission(), org.jets3t.service.acl.Permission.PERMISSION_READ_ACP);
      } finally {
         returnContainer(bucketName);
      }
   }

   @Test
   public void testMultiService() throws S3ServiceException, NoSuchAlgorithmException, IOException,
            InterruptedException, ExecutionException, TimeoutException {
      int OBJECT_COUNT = 50;
      int OBJECT_SIZE = 1024; // 1 KB

      byte[] dataBuffer = new byte[OBJECT_SIZE];

      String bucketName = getContainerName();
      try {
         S3Bucket bucket = new S3Bucket(bucketName);
         S3Object[] objects = new S3Object[OBJECT_COUNT];

         for (int i = 0; i < objects.length; i++) {
            InputStream dataInputStream = new ByteArrayInputStream(dataBuffer);
            objects[i] = new S3Object("testMultiServiceObject" + i);
            objects[i].setDataInputStream(dataInputStream);
            objects[i].setContentLength(dataBuffer.length);
         }

         final long[] countOfUploadCompletions = new long[1];
         S3ServiceEventListener eventListener = new S3ServiceEventAdaptor() {
            @Override
            public synchronized void s3ServiceEventPerformed(CreateObjectsEvent event) {
               if (CreateObjectsEvent.EVENT_STARTED == event.getEventCode()) {
                  // Do nothing
               } else if (CreateObjectsEvent.EVENT_COMPLETED == event.getEventCode()) {
                  // Do nothing
               } else if (CreateObjectsEvent.EVENT_ERROR == event.getEventCode()) {
                  fail("Upload should not result in error", event.getErrorCause());
               } else if (CreateObjectsEvent.EVENT_IGNORED_ERRORS == event.getEventCode()) {
                  fail("Upload should not result in ignored errors: " + event.getIgnoredErrors());
               } else if (CreateObjectsEvent.EVENT_CANCELLED == event.getEventCode()) {
                  fail("Upload should not be cancelled");
               } else if (CreateObjectsEvent.EVENT_IN_PROGRESS == event.getEventCode()) {
                  countOfUploadCompletions[0] = event.getThreadWatcher().getCompletedThreads();
               }
            }
         };

         S3ServiceMulti multiService = new S3ServiceMulti(service, eventListener);
         multiService.putObjects(bucket, objects);

         assertEquals(countOfUploadCompletions[0], OBJECT_COUNT);
         ListBucketResponse theBucket = context.getApi().listBucket(bucketName);
         assertEquals(theBucket.size(), OBJECT_COUNT);

      } finally {
         returnContainer(bucketName);
      }
   }

   @Test(enabled = false)
   public void testCheckBucketStatus() {
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
