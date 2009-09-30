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
package org.jclouds.aws.s3.integration;

import static org.jclouds.aws.s3.internal.StubS3BlobStore.TEST_ACL_EMAIL;
import static org.jclouds.aws.s3.internal.StubS3BlobStore.TEST_ACL_ID;
import static org.jclouds.aws.s3.options.CopyObjectOptions.Builder.ifSourceETagDoesntMatch;
import static org.jclouds.aws.s3.options.CopyObjectOptions.Builder.ifSourceETagMatches;
import static org.jclouds.aws.s3.options.CopyObjectOptions.Builder.ifSourceModifiedSince;
import static org.jclouds.aws.s3.options.CopyObjectOptions.Builder.ifSourceUnmodifiedSince;
import static org.jclouds.aws.s3.options.CopyObjectOptions.Builder.overrideMetadataWith;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.aws.s3.S3BlobStore;
import org.jclouds.aws.s3.domain.AccessControlList;
import org.jclouds.aws.s3.domain.BucketMetadata;
import org.jclouds.aws.s3.domain.CannedAccessPolicy;
import org.jclouds.aws.s3.domain.ObjectMetadata;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.domain.AccessControlList.CanonicalUserGrantee;
import org.jclouds.aws.s3.domain.AccessControlList.EmailAddressGrantee;
import org.jclouds.aws.s3.domain.AccessControlList.GroupGranteeURI;
import org.jclouds.aws.s3.domain.AccessControlList.Permission;
import org.jclouds.aws.s3.options.PutObjectOptions;
import org.jclouds.blobstore.integration.internal.BaseBlobIntegrationTest;
import org.jclouds.http.HttpResponseException;
import org.jclouds.util.Utils;
import org.joda.time.DateTime;
import org.testng.annotations.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * 
 * @author James Murty
 * @author Adrian Cole
 */
@Test(groups = { "integration", "live" }, testName = "s3.S3BlobIntegrationTest")
public class S3BlobIntegrationTest extends
         BaseBlobIntegrationTest<S3BlobStore, BucketMetadata, ObjectMetadata, S3Object> {
   String sourceKey = "apples";
   String destinationKey = "pears";

   public void testPublicWriteOnObject() throws InterruptedException, ExecutionException,
            TimeoutException, IOException {
      final String publicReadWriteObjectKey = "public-read-write-acl";
      final String containerName = getContainerName();
      try {
         // Public Read-Write object
         context.getApi().putBlob(containerName, new S3Object(publicReadWriteObjectKey, ""),
                  new PutObjectOptions().withAcl(CannedAccessPolicy.PUBLIC_READ_WRITE)).get(10,
                  TimeUnit.SECONDS);

         assertEventually(new Runnable() {
            public void run() {
               try {
                  AccessControlList acl = context.getApi().getBlobACL(containerName,
                           publicReadWriteObjectKey).get(10, TimeUnit.SECONDS);
                  assertEquals(acl.getGrants().size(), 3);
                  assertEquals(acl.getPermissions(GroupGranteeURI.ALL_USERS).size(), 2);
                  assertTrue(acl.getOwner() != null);
                  String ownerId = acl.getOwner().getId();
                  assertTrue(acl.hasPermission(ownerId, Permission.FULL_CONTROL));
                  assertTrue(acl.hasPermission(GroupGranteeURI.ALL_USERS, Permission.READ));
                  assertTrue(acl.hasPermission(GroupGranteeURI.ALL_USERS, Permission.WRITE));
                  assertFalse(acl.hasPermission(GroupGranteeURI.ALL_USERS, Permission.READ_ACP));
                  assertFalse(acl.hasPermission(GroupGranteeURI.ALL_USERS, Permission.WRITE_ACP));
                  assertFalse(acl.hasPermission(GroupGranteeURI.ALL_USERS, Permission.FULL_CONTROL));
               } catch (Exception e) {
                  Utils.<RuntimeException> rethrowIfRuntimeOrSameType(e);
               }
            }
         });
      } finally {
         returnContainer(containerName);
      }

   }

   public void testUpdateObjectACL() throws InterruptedException, ExecutionException,
            TimeoutException, IOException {
      String containerName = getContainerName();
      try {
         String objectKey = "private-acl";

         // Private object
         addBlobToContainer(containerName, objectKey);
         AccessControlList acl = context.getApi().getBlobACL(containerName, objectKey).get(10,
                  TimeUnit.SECONDS);
         String ownerId = acl.getOwner().getId();

         assertEquals(acl.getGrants().size(), 1);
         assertTrue(acl.hasPermission(ownerId, Permission.FULL_CONTROL));

         addGrantsToACL(acl);
         assertEquals(acl.getGrants().size(), 4);
         assertTrue(context.getApi().putBlobACL(containerName, objectKey, acl).get(10,
                  TimeUnit.SECONDS));

         // Confirm that the updated ACL has stuck.
         acl = context.getApi().getBlobACL(containerName, objectKey).get(10, TimeUnit.SECONDS);
         checkGrants(acl);

         /*
          * Revoke all of owner's permissions!
          */
         acl.revokeAllPermissions(new CanonicalUserGrantee(ownerId));
         if (!ownerId.equals(TEST_ACL_ID))
            acl.revokeAllPermissions(new CanonicalUserGrantee(TEST_ACL_ID));
         assertEquals(acl.getGrants().size(), 1);
         // Only public read permission should remain...
         assertTrue(acl.hasPermission(GroupGranteeURI.ALL_USERS, Permission.READ));

         // Update the object's ACL settings
         assertTrue(context.getApi().putBlobACL(containerName, objectKey, acl).get(10,
                  TimeUnit.SECONDS));

         // Confirm that the updated ACL has stuck
         acl = context.getApi().getBlobACL(containerName, objectKey).get(10, TimeUnit.SECONDS);
         assertEquals(acl.getGrants().size(), 1);
         assertEquals(acl.getPermissions(ownerId).size(), 0);
         assertTrue(acl.hasPermission(GroupGranteeURI.ALL_USERS, Permission.READ), acl.toString());
      } finally {
         returnContainer(containerName);
      }

   }

   private void checkGrants(AccessControlList acl) {
      String ownerId = acl.getOwner().getId();

      assertEquals(acl.getGrants().size(), 4, acl.toString());

      assertTrue(acl.hasPermission(ownerId, Permission.FULL_CONTROL), acl.toString());
      assertTrue(acl.hasPermission(GroupGranteeURI.ALL_USERS, Permission.READ), acl.toString());
      assertTrue(acl.hasPermission(ownerId, Permission.WRITE_ACP), acl.toString());
      // EmailAddressGrantee is replaced by a CanonicalUserGrantee, so we cannot test by email addr
      assertTrue(acl.hasPermission(TEST_ACL_ID, Permission.READ_ACP), acl.toString());
   }

   private void addGrantsToACL(AccessControlList acl) {
      String ownerId = acl.getOwner().getId();
      acl.addPermission(GroupGranteeURI.ALL_USERS, Permission.READ);
      acl.addPermission(new EmailAddressGrantee(TEST_ACL_EMAIL), Permission.READ_ACP);
      acl.addPermission(new CanonicalUserGrantee(ownerId), Permission.WRITE_ACP);
   }

   public void testPrivateAclIsDefaultForObject() throws InterruptedException, ExecutionException,
            TimeoutException, IOException {
      String privateObjectKey = "private-acl";
      String containerName = getContainerName();
      try {
         // Private object
         addBlobToContainer(containerName, privateObjectKey);
         AccessControlList acl = context.getApi().getBlobACL(containerName, privateObjectKey).get(
                  10, TimeUnit.SECONDS);

         assertEquals(acl.getGrants().size(), 1);
         assertTrue(acl.getOwner() != null);
         String ownerId = acl.getOwner().getId();
         assertTrue(acl.hasPermission(ownerId, Permission.FULL_CONTROL));
      } finally {
         returnContainer(containerName);
      }

   }

   public void testPublicReadOnObject() throws InterruptedException, ExecutionException,
            TimeoutException, IOException {
      final String publicReadObjectKey = "public-read-acl";
      final String containerName = getContainerName();
      try {
         context.getApi().putBlob(containerName, new S3Object(publicReadObjectKey, ""),
                  new PutObjectOptions().withAcl(CannedAccessPolicy.PUBLIC_READ)).get(10,
                  TimeUnit.SECONDS);

         assertEventually(new Runnable() {
            public void run() {
               try {
                  AccessControlList acl = context.getApi().getBlobACL(containerName,
                           publicReadObjectKey).get(10, TimeUnit.SECONDS);

                  assertEquals(acl.getGrants().size(), 2);
                  assertEquals(acl.getPermissions(GroupGranteeURI.ALL_USERS).size(), 1);
                  assertTrue(acl.getOwner() != null);
                  String ownerId = acl.getOwner().getId();
                  assertTrue(acl.hasPermission(ownerId, Permission.FULL_CONTROL));
                  assertTrue(acl.hasPermission(GroupGranteeURI.ALL_USERS, Permission.READ));
               } catch (Exception e) {
                  Utils.<RuntimeException> rethrowIfRuntimeOrSameType(e);
               }
            }
         });

      } finally {
         returnContainer(containerName);
      }

   }

   public void testMetadataWithCacheControlAndContentDisposition() throws Exception {
      String key = "hello";

      S3Object object = context.newBlob(key);
      object.setData(TEST_STRING);
      object.getMetadata().setCacheControl("no-cache");
      object.getMetadata().setContentDisposition("attachment; filename=hello.txt");
      String containerName = getContainerName();
      try {
         addBlobToContainer(containerName, object);
         S3Object newObject = validateContent(containerName, key);

         assertEquals(newObject.getMetadata().getCacheControl(), "no-cache");
         assertEquals(newObject.getMetadata().getContentDisposition(),
                  "attachment; filename=hello.txt");
      } finally {
         returnContainer(containerName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testMetadataContentEncoding() throws Exception {
      String key = "hello";

      S3Object object = context.newBlob(key);
      object.setData(TEST_STRING);
      object.getMetadata().setContentEncoding("x-compress");
      String containerName = getContainerName();
      try {
         addBlobToContainer(containerName, object);
         S3Object newObject = validateContent(containerName, key);

         assertEquals(newObject.getMetadata().getContentEncoding(), "x-compress");
      } finally {
         returnContainer(containerName);
      }
   }

   public void testCopyObject() throws Exception {
      String containerName = getContainerName();
      String destinationContainer = getContainerName();

      try {
         addToContainerAndValidate(containerName, sourceKey);

         context.getApi().copyBlob(containerName, sourceKey, destinationContainer, destinationKey)
                  .get(10, TimeUnit.SECONDS);

         validateContent(destinationContainer, destinationKey);
      } finally {
         returnContainer(containerName);
         returnContainer(destinationContainer);

      }
   }

   private void addToContainerAndValidate(String containerName, String sourceKey)
            throws InterruptedException, ExecutionException, TimeoutException, IOException {
      addBlobToContainer(containerName, sourceKey);
      validateContent(containerName, sourceKey);
   }

   // TODO: fails on linux and windows
   public void testCopyIfModifiedSince() throws InterruptedException, ExecutionException,
            TimeoutException, IOException {
      String containerName = getContainerName();
      String destinationContainer = getContainerName();
      try {
         DateTime before = new DateTime();
         addToContainerAndValidate(containerName, sourceKey + "mod");
         DateTime after = new DateTime().plusSeconds(1);

         context.getApi().copyBlob(containerName, sourceKey + "mod", destinationContainer,
                  destinationKey, ifSourceModifiedSince(before)).get(10, TimeUnit.SECONDS);
         validateContent(destinationContainer, destinationKey);

         try {
            context.getApi().copyBlob(containerName, sourceKey + "mod", destinationContainer,
                     destinationKey, ifSourceModifiedSince(after)).get(10, TimeUnit.SECONDS);
         } catch (ExecutionException e) {
            if (e.getCause() instanceof HttpResponseException) {
               HttpResponseException ex = (HttpResponseException) e.getCause();
               assertEquals(ex.getResponse().getStatusCode(), 412);
            } else {
               throw e;
            }
         }
      } finally {
         returnContainer(containerName);
         returnContainer(destinationContainer);

      }
   }

   // TODO: fails on linux and windows
   public void testCopyIfUnmodifiedSince() throws InterruptedException, ExecutionException,
            TimeoutException, IOException {
      String containerName = getContainerName();
      String destinationContainer = getContainerName();
      try {
         DateTime before = new DateTime();
         addToContainerAndValidate(containerName, sourceKey + "un");
         DateTime after = new DateTime().plusSeconds(1);

         context.getApi().copyBlob(containerName, sourceKey + "un", destinationContainer,
                  destinationKey, ifSourceUnmodifiedSince(after)).get(10, TimeUnit.SECONDS);
         validateContent(destinationContainer, destinationKey);

         try {
            context.getApi().copyBlob(containerName, sourceKey + "un", destinationContainer,
                     destinationKey, ifSourceModifiedSince(before)).get(10, TimeUnit.SECONDS);
         } catch (ExecutionException e) {
            HttpResponseException ex = (HttpResponseException) e.getCause();
            assertEquals(ex.getResponse().getStatusCode(), 412);
         }
      } finally {
         returnContainer(containerName);
         returnContainer(destinationContainer);
      }
   }

   public void testCopyIfMatch() throws InterruptedException, ExecutionException, TimeoutException,
            IOException {
      String containerName = getContainerName();
      String destinationContainer = getContainerName();
      try {
         addToContainerAndValidate(containerName, sourceKey);

         context.getApi().copyBlob(containerName, sourceKey, destinationContainer, destinationKey,
                  ifSourceETagMatches(goodETag)).get(10, TimeUnit.SECONDS);
         validateContent(destinationContainer, destinationKey);

         try {
            context.getApi().copyBlob(containerName, sourceKey, destinationContainer,
                     destinationKey, ifSourceETagMatches(badETag)).get(10, TimeUnit.SECONDS);
         } catch (ExecutionException e) {
            HttpResponseException ex = (HttpResponseException) e.getCause();
            assertEquals(ex.getResponse().getStatusCode(), 412);
         }
      } finally {
         returnContainer(containerName);
         returnContainer(destinationContainer);
      }
   }

   public void testCopyIfNoneMatch() throws IOException, InterruptedException, ExecutionException,
            TimeoutException {
      String containerName = getContainerName();
      String destinationContainer = getContainerName();
      try {
         addToContainerAndValidate(containerName, sourceKey);

         context.getApi().copyBlob(containerName, sourceKey, destinationContainer, destinationKey,
                  ifSourceETagDoesntMatch(badETag)).get(10, TimeUnit.SECONDS);
         validateContent(destinationContainer, destinationKey);

         try {
            context.getApi().copyBlob(containerName, sourceKey, destinationContainer,
                     destinationKey, ifSourceETagDoesntMatch(goodETag)).get(10, TimeUnit.SECONDS);
         } catch (ExecutionException e) {
            HttpResponseException ex = (HttpResponseException) e.getCause();
            assertEquals(ex.getResponse().getStatusCode(), 412);
         }
      } finally {
         returnContainer(containerName);
         returnContainer(destinationContainer);

      }
   }

   public void testCopyWithMetadata() throws InterruptedException, ExecutionException,
            TimeoutException, IOException {
      String containerName = getContainerName();
      String destinationContainer = getContainerName();
      try {
         addToContainerAndValidate(containerName, sourceKey);

         Multimap<String, String> metadata = HashMultimap.create();
         metadata.put("adrian", "cole");

         context.getApi().copyBlob(containerName, sourceKey, destinationContainer, destinationKey,
                  overrideMetadataWith(metadata)).get(10, TimeUnit.SECONDS);

         validateContent(destinationContainer, destinationKey);

         ObjectMetadata objectMeta = context.getApi().blobMetadata(destinationContainer,
                  destinationKey);

         assertEquals(objectMeta.getUserMetadata(), metadata);
      } finally {
         returnContainer(containerName);
         returnContainer(destinationContainer);

      }
   }

}