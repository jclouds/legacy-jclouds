/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.s3.services;

import static org.jclouds.s3.options.ListBucketOptions.Builder.afterMarker;
import static org.jclouds.s3.options.ListBucketOptions.Builder.delimiter;
import static org.jclouds.s3.options.ListBucketOptions.Builder.maxResults;
import static org.jclouds.s3.options.ListBucketOptions.Builder.withPrefix;
import static org.jclouds.s3.options.PutBucketOptions.Builder.withBucketAcl;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.jclouds.blobstore.integration.internal.BaseBlobStoreIntegrationTest;
import org.jclouds.s3.S3ApiMetadata;
import org.jclouds.s3.S3Client;
import org.jclouds.s3.domain.AccessControlList;
import org.jclouds.s3.domain.AccessControlList.CanonicalUserGrantee;
import org.jclouds.s3.domain.AccessControlList.EmailAddressGrantee;
import org.jclouds.s3.domain.AccessControlList.Grant;
import org.jclouds.s3.domain.AccessControlList.GroupGranteeURI;
import org.jclouds.s3.domain.AccessControlList.Permission;
import org.jclouds.s3.domain.BucketLogging;
import org.jclouds.s3.domain.BucketMetadata;
import org.jclouds.s3.domain.CannedAccessPolicy;
import org.jclouds.s3.domain.ListBucketResponse;
import org.jclouds.s3.domain.Payer;
import org.jclouds.s3.domain.S3Object;
import org.jclouds.s3.internal.StubS3AsyncClient;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * 
 * @author James Murty
 * @author Adrian Cole
 */
@Test(groups = { "integration", "live" })
public class BucketsLiveTest extends BaseBlobStoreIntegrationTest {
   public BucketsLiveTest() {
      this.provider = "s3";
      BaseBlobStoreIntegrationTest.SANITY_CHECK_RETURNED_BUCKET_NAME = true;
   }

   public S3Client getApi() {
      return view.unwrap(S3ApiMetadata.CONTEXT_TOKEN).getApi();
   }

   /**
    * this method overrides bucketName to ensure it isn't found
    */
   @Test(groups = { "integration", "live" })
   public void deleteBucketIfEmptyNotFound() throws Exception {
      assert getApi().deleteBucketIfEmpty("dbienf");
   }

   @Test(groups = { "integration", "live" })
   public void deleteBucketIfEmptyButHasContents() throws Exception {
      String bucketName = getContainerName();
      try {
         addBlobToContainer(bucketName, "test");
         assert !getApi().deleteBucketIfEmpty(bucketName);
      } finally {
         returnContainer(bucketName);
      }
   }

   public void testPrivateAclIsDefaultForBucket() throws InterruptedException, ExecutionException, TimeoutException,
         IOException {
      String bucketName = getContainerName();
      try {
         AccessControlList acl = getApi().getBucketACL(bucketName);
         assertEquals(acl.getGrants().size(), 1);
         assertNotNull(acl.getOwner());
         String ownerId = acl.getOwner().getId();
         assertTrue(acl.hasPermission(ownerId, Permission.FULL_CONTROL));
      } finally {
         returnContainer(bucketName);
      }

   }

   public void testUpdateBucketACL() throws InterruptedException, ExecutionException, TimeoutException, IOException,
         Exception {
      String bucketName = getContainerName();
      try {
         // Confirm the bucket is private
         AccessControlList acl = getApi().getBucketACL(bucketName);
         String ownerId = acl.getOwner().getId();
         assertEquals(acl.getGrants().size(), 1);
         assertTrue(acl.hasPermission(ownerId, Permission.FULL_CONTROL));

         addGrantsToACL(acl);
         assertEquals(acl.getGrants().size(), 4);
         assertTrue(getApi().putBucketACL(bucketName, acl));

         // Confirm that the updated ACL has stuck.
         acl = getApi().getBucketACL(bucketName);
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
      assertTrue(acl.hasPermission(StubS3AsyncClient.TEST_ACL_ID, Permission.READ_ACP), acl.toString());
   }

   private void addGrantsToACL(AccessControlList acl) {
      String ownerId = acl.getOwner().getId();
      acl.addPermission(GroupGranteeURI.ALL_USERS, Permission.READ);
      acl.addPermission(new EmailAddressGrantee(StubS3AsyncClient.TEST_ACL_EMAIL), Permission.READ_ACP);
      acl.addPermission(new CanonicalUserGrantee(ownerId), Permission.WRITE_ACP);
   }

   public void testPublicReadAccessPolicy() throws Exception {
      String bucketName = getScratchContainerName();
      try {
         getApi().putBucketInRegion(null, bucketName, withBucketAcl(CannedAccessPolicy.PUBLIC_READ));
         AccessControlList acl = getApi().getBucketACL(bucketName);
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
         Strings2.toStringAndClose(url.openStream());
      } finally {
         returnContainer(bucketName);
      }

   }

   public void testBucketPayer() throws Exception {
      final String bucketName = getContainerName();
      try {
         assertEquals(Payer.BUCKET_OWNER, getApi().getBucketPayer(bucketName));
         getApi().setBucketPayer(bucketName, Payer.REQUESTER);
         assertConsistencyAware(new Runnable() {
            public void run() {
               try {
                  assertEquals(Payer.REQUESTER, getApi().getBucketPayer(bucketName));

               } catch (Exception e) {
                  Throwables.propagateIfPossible(e);
               }
            }
         });
         getApi().setBucketPayer(bucketName, Payer.BUCKET_OWNER);
         assertConsistencyAware(new Runnable() {
            public void run() {
               try {
                  assertEquals(Payer.BUCKET_OWNER, getApi().getBucketPayer(bucketName));
               } catch (Exception e) {
                  Throwables.propagateIfPossible(e);
               }
            }
         });
      } finally {
         destroyContainer(bucketName);
      }
   }

   public void testBucketLogging() throws Exception {
      final String bucketName = getContainerName();
      final String targetBucket = getContainerName();
      try {
         assertNull(getApi().getBucketLogging(bucketName));

         setupAclForBucketLoggingTarget(targetBucket);
         final BucketLogging logging = new BucketLogging(targetBucket, "access_log-",
               ImmutableSet.<Grant> of(new Grant(new EmailAddressGrantee(StubS3AsyncClient.TEST_ACL_EMAIL),
                     Permission.FULL_CONTROL)));

         getApi().enableBucketLogging(bucketName, logging);

         assertConsistencyAware(new Runnable() {
            public void run() {
               try {
                  BucketLogging newLogging = getApi().getBucketLogging(bucketName);
                  assert newLogging !=null;
                  AccessControlList acl = new AccessControlList();
                  for (Grant grant : newLogging.getTargetGrants()) { // TODO: add permission
                     // checking features to
                     // bucketlogging
                     acl.addPermission(grant.getGrantee(), grant.getPermission());
                  }
                  // EmailAddressGrantee is replaced by a CanonicalUserGrantee, so we cannot test by
                  // email addr
                  assertTrue(acl.hasPermission(StubS3AsyncClient.TEST_ACL_ID, Permission.FULL_CONTROL), acl.toString());
                  assertEquals(logging.getTargetBucket(), newLogging.getTargetBucket());
                  assertEquals(logging.getTargetPrefix(), newLogging.getTargetPrefix());
               } catch (Exception e) {
                  Throwables.propagateIfPossible(e);
               }
            }
         });
         getApi().disableBucketLogging(bucketName);
         assertConsistencyAware(new Runnable() {
            public void run() {
               try {
                  assertNull(getApi().getBucketLogging(bucketName));
               } catch (Exception e) {
                  Throwables.propagateIfPossible(e);
               }
            }
         });
      } finally {
         destroyContainer(bucketName);
         destroyContainer(targetBucket);
      }
   }

   private void setupAclForBucketLoggingTarget(final String targetBucket) {
      // http://docs.amazonwebservices.com/AmazonS3/latest/LoggingHowTo.html
      AccessControlList acl = getApi().getBucketACL(targetBucket);
      acl.addPermission(GroupGranteeURI.LOG_DELIVERY, Permission.WRITE);
      acl.addPermission(GroupGranteeURI.LOG_DELIVERY, Permission.READ_ACP);
      assertTrue(getApi().putBucketACL(targetBucket, acl));
   }

   void bucketExists() throws Exception {
      String bucketName = getContainerName();
      try {
         Set<BucketMetadata> list = getApi().listOwnedBuckets();
         BucketMetadata firstBucket = Iterables.get(list, 0);
         BucketMetadata toMatch = new BucketMetadata(bucketName, new Date(), firstBucket.getOwner());
         assert list.contains(toMatch);
      } finally {
         returnContainer(bucketName);
      }
   }

   protected void addAlphabetUnderRoot(String bucketName) {
      for (char letter = 'a'; letter <= 'z'; letter++) {
         S3Object blob = getApi().newS3Object();
         blob.getMetadata().setKey(letter + "");
         blob.setPayload(letter + "content");
         getApi().putObject(bucketName, blob);
      }
   }

   public void testListBucketMarker() throws InterruptedException, ExecutionException, TimeoutException {
      String bucketName = getContainerName();
      try {
         addAlphabetUnderRoot(bucketName);
         ListBucketResponse bucket = getApi().listBucket(bucketName, afterMarker("y"));
         assertEquals(bucket.getMarker(), "y");
         assert !bucket.isTruncated();
         assertEquals(bucket.size(), 1);
      } finally {
         returnContainer(bucketName);
      }
   }

   public void testListBucketDelimiter() throws InterruptedException, ExecutionException, TimeoutException {
      String bucketName = getContainerName();
      try {
         String prefix = "apps";
         addTenObjectsUnderPrefix(bucketName, prefix);
         add15UnderRoot(bucketName);
         ListBucketResponse bucket = getApi().listBucket(bucketName, delimiter("/"));
         assertEquals(bucket.getDelimiter(), "/");
         assert !bucket.isTruncated();
         assertEquals(bucket.size(), 15);
         assertEquals(bucket.getCommonPrefixes().size(), 1);
      } finally {
         returnContainer(bucketName);
      }

   }

   public void testListBucketPrefix() throws InterruptedException, ExecutionException, TimeoutException {
      String bucketName = getContainerName();
      try {
         String prefix = "apps";
         addTenObjectsUnderPrefix(bucketName, prefix);
         add15UnderRoot(bucketName);

         ListBucketResponse bucket = getApi().listBucket(bucketName, withPrefix("apps/"));
         assert !bucket.isTruncated();
         assertEquals(bucket.size(), 10);
         assertEquals(bucket.getPrefix(), "apps/");
      } finally {
         returnContainer(bucketName);
      }

   }

   public void testListBucketMaxResults() throws InterruptedException, ExecutionException, TimeoutException {
      String bucketName = getContainerName();
      try {
         addAlphabetUnderRoot(bucketName);
         ListBucketResponse bucket = getApi().listBucket(bucketName, maxResults(5));
         assertEquals(bucket.getMaxKeys(), 5);
         assert bucket.isTruncated();
         assertEquals(bucket.size(), 5);
      } finally {
         returnContainer(bucketName);
      }
   }

   protected void add15UnderRoot(String bucketName) {
      for (int i = 0; i < 15; i++) {
         S3Object blob = getApi().newS3Object();
         blob.getMetadata().setKey(i + "");
         blob.setPayload(i + "content");
         getApi().putObject(bucketName, blob);
      }
   }

   protected void addTenObjectsUnderPrefix(String bucketName, String prefix) {
      for (int i = 0; i < 10; i++) {
         S3Object blob = getApi().newS3Object();
         blob.getMetadata().setKey(prefix + "/" + i);
         blob.setPayload(i + "content");
         getApi().putObject(bucketName, blob);
      }
   }
}
