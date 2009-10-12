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
package org.jclouds.aws.s3;

import static org.jclouds.aws.s3.internal.StubS3Connection.TEST_ACL_EMAIL;
import static org.jclouds.aws.s3.internal.StubS3Connection.TEST_ACL_ID;
import static org.jclouds.aws.s3.options.CopyObjectOptions.Builder.ifSourceETagDoesntMatch;
import static org.jclouds.aws.s3.options.CopyObjectOptions.Builder.ifSourceETagMatches;
import static org.jclouds.aws.s3.options.CopyObjectOptions.Builder.ifSourceModifiedSince;
import static org.jclouds.aws.s3.options.CopyObjectOptions.Builder.ifSourceUnmodifiedSince;
import static org.jclouds.aws.s3.options.CopyObjectOptions.Builder.overrideAcl;
import static org.jclouds.aws.s3.options.CopyObjectOptions.Builder.overrideMetadataWith;
import static org.jclouds.aws.s3.options.ListBucketOptions.Builder.afterMarker;
import static org.jclouds.aws.s3.options.ListBucketOptions.Builder.delimiter;
import static org.jclouds.aws.s3.options.ListBucketOptions.Builder.maxResults;
import static org.jclouds.aws.s3.options.ListBucketOptions.Builder.withPrefix;
import static org.jclouds.aws.s3.options.PutBucketOptions.Builder.createIn;
import static org.jclouds.aws.s3.options.PutBucketOptions.Builder.withBucketAcl;
import static org.jclouds.aws.s3.options.PutObjectOptions.Builder.withAcl;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.aws.s3.domain.AccessControlList;
import org.jclouds.aws.s3.domain.BucketMetadata;
import org.jclouds.aws.s3.domain.CannedAccessPolicy;
import org.jclouds.aws.s3.domain.ListBucketResponse;
import org.jclouds.aws.s3.domain.ObjectMetadata;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.domain.AccessControlList.CanonicalUserGrantee;
import org.jclouds.aws.s3.domain.AccessControlList.EmailAddressGrantee;
import org.jclouds.aws.s3.domain.AccessControlList.GroupGranteeURI;
import org.jclouds.aws.s3.domain.AccessControlList.Permission;
import org.jclouds.aws.s3.domain.BucketMetadata.LocationConstraint;
import org.jclouds.aws.s3.options.PutObjectOptions;
import org.jclouds.blobstore.integration.internal.BaseBlobStoreIntegrationTest;
import org.jclouds.http.HttpResponseException;
import org.jclouds.util.Utils;
import org.joda.time.DateTime;
import org.testng.annotations.Test;

import com.google.common.collect.Maps;

/**
 * 
 * @author James Murty
 * @author Adrian Cole
 */
@Test(groups = { "integration", "live" }, testName = "s3.S3ConnectionLiveTest")
public class S3ConnectionLiveTest extends
         BaseBlobStoreIntegrationTest<S3Connection, BucketMetadata, ObjectMetadata, S3Object> {

   /**
    * this method overrides containerName to ensure it isn't found
    */
   @Test(groups = { "integration", "live" })
   public void deleteContainerIfEmptyNotFound() throws Exception {
      assert context.getApi().deleteBucketIfEmpty("dbienf").get(10, TimeUnit.SECONDS);
   }

   @Test(groups = { "integration", "live" })
   public void deleteContainerIfEmptyButHasContents() throws Exception {
      String containerName = getContainerName();
      try {
         addBlobToContainer(containerName, "test");
         assert !context.getApi().deleteBucketIfEmpty(containerName).get(10, TimeUnit.SECONDS);
      } finally {
         returnContainer(containerName);
      }
   }

   public void testPutCannedAccessPolicyPublic() throws Exception {
      String containerName = getContainerName();
      try {
         String key = "hello";

         context.getApi().putObject(containerName, new S3Object(key, TEST_STRING),

         withAcl(CannedAccessPolicy.PUBLIC_READ)).get(10, TimeUnit.SECONDS);

         URL url = new URL(String.format("http://%1$s.s3.amazonaws.com/%2$s", containerName, key));
         Utils.toStringAndClose(url.openStream());
      } finally {
         returnContainer(containerName);
      }

   }

   public void testCopyCannedAccessPolicyPublic() throws Exception {
      String containerName = getContainerName();
      String destinationContainer = getContainerName();
      try {
         addBlobToContainer(containerName, sourceKey);
         validateContent(containerName, sourceKey);

         context.getApi().copyObject(containerName, sourceKey, destinationContainer,
                  destinationKey, overrideAcl(CannedAccessPolicy.PUBLIC_READ)).get(10,
                  TimeUnit.SECONDS);

         validateContent(destinationContainer, destinationKey);

         URL url = new URL(String.format("http://%1$s.s3.amazonaws.com/%2$s", destinationContainer,
                  destinationKey));
         Utils.toStringAndClose(url.openStream());

      } finally {
         returnContainer(containerName);
         returnContainer(destinationContainer);
      }
   }

   String sourceKey = "apples";
   String destinationKey = "pears";

   public void testPublicWriteOnObject() throws InterruptedException, ExecutionException,
            TimeoutException, IOException {
      final String publicReadWriteObjectKey = "public-read-write-acl";
      final String containerName = getContainerName();
      try {
         // Public Read-Write object
         context.getApi().putObject(containerName, new S3Object(publicReadWriteObjectKey, ""),
                  new PutObjectOptions().withAcl(CannedAccessPolicy.PUBLIC_READ_WRITE)).get(10,
                  TimeUnit.SECONDS);

         assertEventually(new Runnable() {
            public void run() {
               try {
                  AccessControlList acl = context.getApi().getObjectACL(containerName,
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
         AccessControlList acl = context.getApi().getObjectACL(containerName, objectKey).get(10,
                  TimeUnit.SECONDS);
         String ownerId = acl.getOwner().getId();

         assertEquals(acl.getGrants().size(), 1);
         assertTrue(acl.hasPermission(ownerId, Permission.FULL_CONTROL));

         addGrantsToACL(acl);
         assertEquals(acl.getGrants().size(), 4);
         assertTrue(context.getApi().putObjectACL(containerName, objectKey, acl).get(10,
                  TimeUnit.SECONDS));

         // Confirm that the updated ACL has stuck.
         acl = context.getApi().getObjectACL(containerName, objectKey).get(10, TimeUnit.SECONDS);
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
         assertTrue(context.getApi().putObjectACL(containerName, objectKey, acl).get(10,
                  TimeUnit.SECONDS));

         // Confirm that the updated ACL has stuck
         acl = context.getApi().getObjectACL(containerName, objectKey).get(10, TimeUnit.SECONDS);
         assertEquals(acl.getGrants().size(), 1);
         assertEquals(acl.getPermissions(ownerId).size(), 0);
         assertTrue(acl.hasPermission(GroupGranteeURI.ALL_USERS, Permission.READ), acl.toString());
      } finally {
         returnContainer(containerName);
      }

   }

   public void testPrivateAclIsDefaultForObject() throws InterruptedException, ExecutionException,
            TimeoutException, IOException {
      String privateObjectKey = "private-acl";
      String containerName = getContainerName();
      try {
         // Private object
         addBlobToContainer(containerName, privateObjectKey);
         AccessControlList acl = context.getApi().getObjectACL(containerName, privateObjectKey)
                  .get(10, TimeUnit.SECONDS);

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
         context.getApi().putObject(containerName, new S3Object(publicReadObjectKey, ""),
                  new PutObjectOptions().withAcl(CannedAccessPolicy.PUBLIC_READ)).get(10,
                  TimeUnit.SECONDS);

         assertEventually(new Runnable() {
            public void run() {
               try {
                  AccessControlList acl = context.getApi().getObjectACL(containerName,
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

         context.getApi()
                  .copyObject(containerName, sourceKey, destinationContainer, destinationKey).get(
                           10, TimeUnit.SECONDS);

         validateContent(destinationContainer, destinationKey);
      } finally {
         returnContainer(containerName);
         returnContainer(destinationContainer);

      }
   }

   protected String addToContainerAndValidate(String containerName, String sourceKey)
            throws InterruptedException, ExecutionException, TimeoutException, IOException {
      String etag = addBlobToContainer(containerName, sourceKey);
      validateContent(containerName, sourceKey);
      return etag;
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

         context.getApi().copyObject(containerName, sourceKey + "mod", destinationContainer,
                  destinationKey, ifSourceModifiedSince(before)).get(10, TimeUnit.SECONDS);
         validateContent(destinationContainer, destinationKey);

         try {
            context.getApi().copyObject(containerName, sourceKey + "mod", destinationContainer,
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

         context.getApi().copyObject(containerName, sourceKey + "un", destinationContainer,
                  destinationKey, ifSourceUnmodifiedSince(after)).get(10, TimeUnit.SECONDS);
         validateContent(destinationContainer, destinationKey);

         try {
            context.getApi().copyObject(containerName, sourceKey + "un", destinationContainer,
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
         String goodETag = addToContainerAndValidate(containerName, sourceKey);

         context.getApi().copyObject(containerName, sourceKey, destinationContainer,
                  destinationKey, ifSourceETagMatches(goodETag)).get(10, TimeUnit.SECONDS);
         validateContent(destinationContainer, destinationKey);

         try {
            context.getApi().copyObject(containerName, sourceKey, destinationContainer,
                     destinationKey, ifSourceETagMatches("setsds")).get(10, TimeUnit.SECONDS);
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
         String goodETag = addToContainerAndValidate(containerName, sourceKey);

         context.getApi().copyObject(containerName, sourceKey, destinationContainer,
                  destinationKey, ifSourceETagDoesntMatch("asfasdf")).get(10, TimeUnit.SECONDS);
         validateContent(destinationContainer, destinationKey);

         try {
            context.getApi().copyObject(containerName, sourceKey, destinationContainer,
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

         Map<String, String> metadata = Maps.newHashMap();
         metadata.put("adrian", "cole");

         context.getApi().copyObject(containerName, sourceKey, destinationContainer,
                  destinationKey, overrideMetadataWith(metadata)).get(10, TimeUnit.SECONDS);

         validateContent(destinationContainer, destinationKey);

         ObjectMetadata objectMeta = context.getApi().headObject(destinationContainer,
                  destinationKey);

         assertEquals(objectMeta.getUserMetadata(), metadata);
      } finally {
         returnContainer(containerName);
         returnContainer(destinationContainer);

      }
   }

   public void testListContainerDelimiter() throws InterruptedException, ExecutionException,
            TimeoutException, UnsupportedEncodingException {
      String containerName = getContainerName();
      try {
         String prefix = "apps";
         addTenObjectsUnderPrefix(containerName, prefix);
         add15UnderRoot(containerName);
         ListBucketResponse container = context.getApi().listBucket(containerName, delimiter("/"))
                  .get(10, TimeUnit.SECONDS);
         assertEquals(container.getDelimiter(), "/");
         assert !container.isTruncated();
         assertEquals(container.size(), 15);
         assertEquals(container.getCommonPrefixes().size(), 1);
      } finally {
         returnContainer(containerName);
      }

   }

   public void testListContainerPrefix() throws InterruptedException, ExecutionException,
            TimeoutException, UnsupportedEncodingException {
      String containerName = getContainerName();
      try {
         String prefix = "apps";
         addTenObjectsUnderPrefix(containerName, prefix);
         add15UnderRoot(containerName);

         ListBucketResponse container = context.getApi().listBucket(containerName,
                  withPrefix("apps/")).get(10, TimeUnit.SECONDS);
         assert !container.isTruncated();
         assertEquals(container.size(), 10);
         assertEquals(container.getPrefix(), "apps/");
      } finally {
         returnContainer(containerName);
      }

   }

   public void testPrivateAclIsDefaultForContainer() throws InterruptedException,
            ExecutionException, TimeoutException, IOException {
      String containerName = getContainerName();
      try {
         AccessControlList acl = context.getApi().getBucketACL(containerName).get(10,
                  TimeUnit.SECONDS);
         assertEquals(acl.getGrants().size(), 1);
         assertTrue(acl.getOwner() != null);
         String ownerId = acl.getOwner().getId();
         assertTrue(acl.hasPermission(ownerId, Permission.FULL_CONTROL));
      } finally {
         returnContainer(containerName);
      }

   }

   public void testUpdateContainerACL() throws InterruptedException, ExecutionException,
            TimeoutException, IOException, Exception {
      String containerName = getContainerName();
      try {
         // Confirm the container is private
         AccessControlList acl = context.getApi().getBucketACL(containerName).get(10,
                  TimeUnit.SECONDS);
         String ownerId = acl.getOwner().getId();
         assertEquals(acl.getGrants().size(), 1);
         assertTrue(acl.hasPermission(ownerId, Permission.FULL_CONTROL));

         addGrantsToACL(acl);
         assertEquals(acl.getGrants().size(), 4);
         assertTrue(context.getApi().putBucketACL(containerName, acl).get(10, TimeUnit.SECONDS));

         // Confirm that the updated ACL has stuck.
         acl = context.getApi().getBucketACL(containerName).get(10, TimeUnit.SECONDS);
         checkGrants(acl);
      } finally {
         destroyContainer(containerName);
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

   public void testListContainerMarker() throws InterruptedException, ExecutionException,
            TimeoutException, UnsupportedEncodingException {
      String containerName = getContainerName();
      try {
         addAlphabetUnderRoot(containerName);
         ListBucketResponse container = context.getApi()
                  .listBucket(containerName, afterMarker("y")).get(10, TimeUnit.SECONDS);
         assertEquals(container.getMarker(), "y");
         assert !container.isTruncated();
         assertEquals(container.size(), 1);
      } finally {
         returnContainer(containerName);
      }
   }

   public void testListContainerMaxResults() throws InterruptedException, ExecutionException,
            TimeoutException, UnsupportedEncodingException {
      String containerName = getContainerName();
      try {
         addAlphabetUnderRoot(containerName);
         ListBucketResponse container = context.getApi().listBucket(containerName, maxResults(5))
                  .get(10, TimeUnit.SECONDS);
         assertEquals(container.getMaxResults(), 5);
         assert container.isTruncated();
         assertEquals(container.size(), 5);
      } finally {
         returnContainer(containerName);
      }
   }

   public void testPublicReadAccessPolicy() throws Exception {
      String containerName = getScratchContainerName();
      try {
         context.getApi().putBucketIfNotExists(containerName,
                  withBucketAcl(CannedAccessPolicy.PUBLIC_READ)).get(10, TimeUnit.SECONDS);
         AccessControlList acl = context.getApi().getBucketACL(containerName).get(10,
                  TimeUnit.SECONDS);
         assertTrue(acl.hasPermission(GroupGranteeURI.ALL_USERS, Permission.READ), acl.toString());
         // TODO: I believe that the following should work based on the above acl assertion passing.
         // However, it fails on 403
         // URL url = new URL(String.format("http://%s.s3.amazonaws.com", containerName));
         // Utils.toStringAndClose(url.openStream());
      } finally {
         destroyContainer(containerName);
      }
   }

   @Test(expectedExceptions = IOException.class)
   public void testDefaultAccessPolicy() throws Exception {
      String containerName = getContainerName();
      try {
         URL url = new URL(String.format("https://%s.s3.amazonaws.com", containerName));
         Utils.toStringAndClose(url.openStream());
      } finally {
         returnContainer(containerName);
      }

   }

   /**
    * using scratch containerName as we are changing location
    */
   public void testEu() throws Exception {
      final String containerName = getScratchContainerName();
      try {
         context.getApi().putBucketIfNotExists(containerName + "eu",
                  createIn(LocationConstraint.EU).withBucketAcl(CannedAccessPolicy.PUBLIC_READ))
                  .get(30, TimeUnit.SECONDS);
         assertEventually(new Runnable() {
            public void run() {
               try {
                  AccessControlList acl = context.getApi().getBucketACL(containerName + "eu").get(
                           30, TimeUnit.SECONDS);
                  assertTrue(acl.hasPermission(GroupGranteeURI.ALL_USERS, Permission.READ), acl
                           .toString());
               } catch (Exception e) {
                  Utils.<RuntimeException> rethrowIfRuntimeOrSameType(e);
               }
            }
         });
         // TODO: I believe that the following should work based on the above acl assertion passing.
         // However, it fails on 403
         // URL url = new URL(String.format("http://%s.s3.amazonaws.com", containerName));
         // Utils.toStringAndClose(url.openStream());
      } finally {
         destroyContainer(containerName + "eu");
      }
   }

   void containerExists() throws Exception {
      String containerName = getContainerName();
      try {
         SortedSet<BucketMetadata> list = context.getApi().listOwnedBuckets();
         BucketMetadata firstContainer = list.first();
         BucketMetadata toMatch = new BucketMetadata(containerName);
         toMatch.setOwner(firstContainer.getOwner());
         assert list.contains(toMatch);
      } finally {
         returnContainer(containerName);
      }
   }

   protected void addAlphabetUnderRoot(String containerName) throws InterruptedException,
            ExecutionException, TimeoutException {
      for (char letter = 'a'; letter <= 'z'; letter++) {
         S3Object blob = context.newBlob(letter + "");
         blob.setData(letter + "content");
         context.getApi().putObject(containerName, blob).get(10, TimeUnit.SECONDS);
      }
   }

   protected void add15UnderRoot(String containerName) throws InterruptedException,
            ExecutionException, TimeoutException {
      for (int i = 0; i < 15; i++) {
         S3Object blob = context.newBlob(i + "");
         blob.setData(i + "content");
         context.getApi().putObject(containerName, blob).get(10, TimeUnit.SECONDS);
      }
   }

   protected void addTenObjectsUnderPrefix(String containerName, String prefix)
            throws InterruptedException, ExecutionException, TimeoutException {
      for (int i = 0; i < 10; i++) {
         S3Object blob = context.newBlob(prefix + "/" + i);
         blob.setData(i + "content");
         context.getApi().putObject(containerName, blob).get(10, TimeUnit.SECONDS);
      }
   }
}