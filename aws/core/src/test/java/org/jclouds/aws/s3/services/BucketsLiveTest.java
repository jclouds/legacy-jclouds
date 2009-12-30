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
package org.jclouds.aws.s3.services;

import static org.jclouds.aws.s3.options.ListBucketOptions.Builder.afterMarker;
import static org.jclouds.aws.s3.options.ListBucketOptions.Builder.delimiter;
import static org.jclouds.aws.s3.options.ListBucketOptions.Builder.maxResults;
import static org.jclouds.aws.s3.options.ListBucketOptions.Builder.withPrefix;
import static org.jclouds.aws.s3.options.PutBucketOptions.Builder.createIn;
import static org.jclouds.aws.s3.options.PutBucketOptions.Builder.withBucketAcl;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Date;
import java.util.SortedSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.jclouds.aws.s3.S3AsyncClient;
import org.jclouds.aws.s3.S3Client;
import org.jclouds.aws.s3.domain.AccessControlList;
import org.jclouds.aws.s3.domain.BucketMetadata;
import org.jclouds.aws.s3.domain.CannedAccessPolicy;
import org.jclouds.aws.s3.domain.ListBucketResponse;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.domain.AccessControlList.CanonicalUserGrantee;
import org.jclouds.aws.s3.domain.AccessControlList.EmailAddressGrantee;
import org.jclouds.aws.s3.domain.AccessControlList.GroupGranteeURI;
import org.jclouds.aws.s3.domain.AccessControlList.Permission;
import org.jclouds.aws.s3.domain.BucketMetadata.LocationConstraint;
import org.jclouds.aws.s3.internal.StubS3AsyncClient;
import org.jclouds.blobstore.integration.internal.BaseBlobStoreIntegrationTest;
import org.jclouds.util.Utils;
import org.testng.annotations.Test;

/**
 * 
 * @author James Murty
 * @author Adrian Cole
 */
@Test(groups = { "integration", "live" }, testName = "s3.S3ClientLiveTest")
public class BucketsLiveTest extends BaseBlobStoreIntegrationTest<S3AsyncClient, S3Client> {

   /**
    * this method overrides bucketName to ensure it isn't found
    */
   @Test(groups = { "integration", "live" })
   public void deleteBucketIfEmptyNotFound() throws Exception {
      assert context.getApi().deleteBucketIfEmpty("dbienf");
   }

   @Test(groups = { "integration", "live" })
   public void deleteBucketIfEmptyButHasContents() throws Exception {
      String bucketName = getContainerName();
      try {
         addBlobToContainer(bucketName, "test");
         assert !context.getApi().deleteBucketIfEmpty(bucketName);
      } finally {
         returnContainer(bucketName);
      }
   }

   public void testPrivateAclIsDefaultForBucket() throws InterruptedException, ExecutionException,
            TimeoutException, IOException {
      String bucketName = getContainerName();
      try {
         AccessControlList acl = context.getApi().getBucketACL(bucketName);
         assertEquals(acl.getGrants().size(), 1);
         assertTrue(acl.getOwner() != null);
         String ownerId = acl.getOwner().getId();
         assertTrue(acl.hasPermission(ownerId, Permission.FULL_CONTROL));
      } finally {
         returnContainer(bucketName);
      }

   }

   public void testUpdateBucketACL() throws InterruptedException, ExecutionException,
            TimeoutException, IOException, Exception {
      String bucketName = getContainerName();
      try {
         // Confirm the bucket is private
         AccessControlList acl = context.getApi().getBucketACL(bucketName);
         String ownerId = acl.getOwner().getId();
         assertEquals(acl.getGrants().size(), 1);
         assertTrue(acl.hasPermission(ownerId, Permission.FULL_CONTROL));

         addGrantsToACL(acl);
         assertEquals(acl.getGrants().size(), 4);
         assertTrue(context.getApi().putBucketACL(bucketName, acl));

         // Confirm that the updated ACL has stuck.
         acl = context.getApi().getBucketACL(bucketName);
         checkGrants(acl);
      } finally {
         destroyContainer(bucketName);
      }

   }

   private void checkGrants(AccessControlList acl) {
      String ownerId = acl.getOwner().getId();

      assertEquals(acl.getGrants().size(), 4, acl.toString());

      assertTrue(acl.hasPermission(ownerId, Permission.FULL_CONTROL), acl.toString());
      assertTrue(acl.hasPermission(GroupGranteeURI.ALL_USERS, Permission.READ), acl.toString());
      assertTrue(acl.hasPermission(ownerId, Permission.WRITE_ACP), acl.toString());
      // EmailAddressGrantee is replaced by a CanonicalUserGrantee, so we cannot test by email addr
      assertTrue(acl.hasPermission(StubS3AsyncClient.TEST_ACL_ID, Permission.READ_ACP), acl
               .toString());
   }

   private void addGrantsToACL(AccessControlList acl) {
      String ownerId = acl.getOwner().getId();
      acl.addPermission(GroupGranteeURI.ALL_USERS, Permission.READ);
      acl.addPermission(new EmailAddressGrantee(StubS3AsyncClient.TEST_ACL_EMAIL),
               Permission.READ_ACP);
      acl.addPermission(new CanonicalUserGrantee(ownerId), Permission.WRITE_ACP);
   }

   public void testPublicReadAccessPolicy() throws Exception {
      String bucketName = getScratchContainerName();
      try {
         context.getApi().putBucketIfNotExists(bucketName,
                  withBucketAcl(CannedAccessPolicy.PUBLIC_READ));
         AccessControlList acl = context.getApi().getBucketACL(bucketName);
         assertTrue(acl.hasPermission(GroupGranteeURI.ALL_USERS, Permission.READ), acl.toString());
         // TODO: I believe that the following should work based on the above acl assertion passing.
         // However, it fails on 403
         // URL url = new URL(String.format("http://%s.s3.amazonaws.com", bucketName));
         // Utils.toStringAndClose(url.openStream());
      } finally {
         destroyContainer(bucketName);
      }
   }

   @Test(expectedExceptions = IOException.class)
   public void testDefaultAccessPolicy() throws Exception {
      String bucketName = getContainerName();
      try {
         URL url = new URL(String.format("https://%s.s3.amazonaws.com", bucketName));
         Utils.toStringAndClose(url.openStream());
      } finally {
         returnContainer(bucketName);
      }

   }

   public void testDefaultBucketLocation() throws Exception {
      String bucketName = getContainerName();
      try {
         assertEquals(LocationConstraint.US_STANDARD, context.getApi()
                  .getBucketLocation(bucketName));
      } finally {
         returnContainer(bucketName);
      }

   }

   /**
    * using scratch bucketName as we are changing location
    */
   public void testEu() throws Exception {
      final String bucketName = getScratchContainerName();
      try {
         context.getApi().putBucketIfNotExists(bucketName + "eu",
                  createIn(LocationConstraint.EU).withBucketAcl(CannedAccessPolicy.PUBLIC_READ));
         assertConsistencyAware(new Runnable() {
            public void run() {
               try {
                  AccessControlList acl = context.getApi().getBucketACL(bucketName + "eu");
                  assertTrue(acl.hasPermission(GroupGranteeURI.ALL_USERS, Permission.READ), acl
                           .toString());
               } catch (Exception e) {
                  Utils.<RuntimeException> rethrowIfRuntimeOrSameType(e);
               }
            }
         });
         assertEquals(LocationConstraint.EU, context.getApi().getBucketLocation(bucketName + "eu"));
         // TODO: I believe that the following should work based on the above acl assertion passing.
         // However, it fails on 403
         // URL url = new URL(String.format("http://%s.s3.amazonaws.com", bucketName));
         // Utils.toStringAndClose(url.openStream());
      } finally {
         destroyContainer(bucketName + "eu");
      }
   }

   /**
    * using scratch bucketName as we are changing location
    */
   public void testNorthernCalifornia() throws Exception {
      final String bucketName = getScratchContainerName();
      try {
         context.getApi().putBucketIfNotExists(
                  bucketName + "cali",
                  createIn(LocationConstraint.US_WEST)
                           .withBucketAcl(CannedAccessPolicy.PUBLIC_READ));
         assertConsistencyAware(new Runnable() {
            public void run() {
               try {
                  AccessControlList acl = context.getApi().getBucketACL(bucketName + "cali");
                  assertTrue(acl.hasPermission(GroupGranteeURI.ALL_USERS, Permission.READ), acl
                           .toString());
               } catch (Exception e) {
                  Utils.<RuntimeException> rethrowIfRuntimeOrSameType(e);
               }
            }
         });
         assertEquals(LocationConstraint.US_WEST, context.getApi().getBucketLocation(
                  bucketName + "cali"));
         // TODO: I believe that the following should work based on the above acl assertion passing.
         // However, it fails on 403
         // URL url = new URL(String.format("http://%s.s3.amazonaws.com", bucketName));
         // Utils.toStringAndClose(url.openStream());
      } finally {
         destroyContainer(bucketName + "cali");
      }
   }

   void bucketExists() throws Exception {
      String bucketName = getContainerName();
      try {
         SortedSet<BucketMetadata> list = context.getApi().listOwnedBuckets();
         BucketMetadata firstBucket = list.first();
         BucketMetadata toMatch = new BucketMetadata(bucketName, new Date(), firstBucket.getOwner());
         assert list.contains(toMatch);
      } finally {
         returnContainer(bucketName);
      }
   }

   protected void addAlphabetUnderRoot(String bucketName) {
      for (char letter = 'a'; letter <= 'z'; letter++) {
         S3Object blob = context.getApi().newS3Object();
         blob.getMetadata().setKey(letter + "");
         blob.setPayload(letter + "content");
         context.getApi().putObject(bucketName, blob);
      }
   }

   public void testListBucketMarker() throws InterruptedException, ExecutionException,
            TimeoutException {
      String bucketName = getContainerName();
      try {
         addAlphabetUnderRoot(bucketName);
         ListBucketResponse bucket = context.getApi().listBucket(bucketName, afterMarker("y"));
         assertEquals(bucket.getMarker(), "y");
         assert !bucket.isTruncated();
         assertEquals(bucket.size(), 1);
      } finally {
         returnContainer(bucketName);
      }
   }

   public void testListBucketDelimiter() throws InterruptedException, ExecutionException,
            TimeoutException, UnsupportedEncodingException {
      String bucketName = getContainerName();
      try {
         String prefix = "apps";
         addTenObjectsUnderPrefix(bucketName, prefix);
         add15UnderRoot(bucketName);
         ListBucketResponse bucket = context.getApi().listBucket(bucketName, delimiter("/"));
         assertEquals(bucket.getDelimiter(), "/");
         assert !bucket.isTruncated();
         assertEquals(bucket.size(), 15);
         assertEquals(bucket.getCommonPrefixes().size(), 1);
      } finally {
         returnContainer(bucketName);
      }

   }

   public void testListBucketPrefix() throws InterruptedException, ExecutionException,
            TimeoutException, UnsupportedEncodingException {
      String bucketName = getContainerName();
      try {
         String prefix = "apps";
         addTenObjectsUnderPrefix(bucketName, prefix);
         add15UnderRoot(bucketName);

         ListBucketResponse bucket = context.getApi().listBucket(bucketName, withPrefix("apps/"));
         assert !bucket.isTruncated();
         assertEquals(bucket.size(), 10);
         assertEquals(bucket.getPrefix(), "apps/");
      } finally {
         returnContainer(bucketName);
      }

   }

   public void testListBucketMaxResults() throws InterruptedException, ExecutionException,
            TimeoutException, UnsupportedEncodingException {
      String bucketName = getContainerName();
      try {
         addAlphabetUnderRoot(bucketName);
         ListBucketResponse bucket = context.getApi().listBucket(bucketName, maxResults(5));
         assertEquals(bucket.getMaxKeys(), 5);
         assert bucket.isTruncated();
         assertEquals(bucket.size(), 5);
      } finally {
         returnContainer(bucketName);
      }
   }

   protected void add15UnderRoot(String bucketName) {
      for (int i = 0; i < 15; i++) {
         S3Object blob = context.getApi().newS3Object();
         blob.getMetadata().setKey(i + "");
         blob.setPayload(i + "content");
         context.getApi().putObject(bucketName, blob);
      }
   }

   protected void addTenObjectsUnderPrefix(String bucketName, String prefix) {
      for (int i = 0; i < 10; i++) {
         S3Object blob = context.getApi().newS3Object();
         blob.getMetadata().setKey(prefix + "/" + i);
         blob.setPayload(i + "content");
         context.getApi().putObject(bucketName, blob);
      }
   }
}