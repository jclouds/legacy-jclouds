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
import static org.jclouds.aws.s3.options.ListBucketOptions.Builder.afterMarker;
import static org.jclouds.aws.s3.options.ListBucketOptions.Builder.delimiter;
import static org.jclouds.aws.s3.options.ListBucketOptions.Builder.maxResults;
import static org.jclouds.aws.s3.options.ListBucketOptions.Builder.withPrefix;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.aws.s3.S3BlobStore;
import org.jclouds.aws.s3.domain.AccessControlList;
import org.jclouds.aws.s3.domain.BucketMetadata;
import org.jclouds.aws.s3.domain.ListBucketResponse;
import org.jclouds.aws.s3.domain.ObjectMetadata;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.domain.AccessControlList.CanonicalUserGrantee;
import org.jclouds.aws.s3.domain.AccessControlList.EmailAddressGrantee;
import org.jclouds.aws.s3.domain.AccessControlList.GroupGranteeURI;
import org.jclouds.aws.s3.domain.AccessControlList.Permission;
import org.jclouds.blobstore.integration.internal.BaseContainerIntegrationTest;
import org.testng.annotations.Test;

/**
 * @author James Murty
 * @author Adrian Cole
 */
@Test(groups = { "integration", "live" }, testName = "s3.S3ContainerIntegrationTest")
public class S3ContainerIntegrationTest extends
         BaseContainerIntegrationTest<S3BlobStore, BucketMetadata, ObjectMetadata, S3Object> {

   public void testListContainerDelimiter() throws InterruptedException, ExecutionException,
            TimeoutException, UnsupportedEncodingException {
      String containerName = getContainerName();
      try {
         String prefix = "apps";
         addTenObjectsUnderPrefix(containerName, prefix);
         add15UnderRoot(containerName);
         ListBucketResponse container = client.listBlobs(containerName, delimiter("/")).get(10,
                  TimeUnit.SECONDS);
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

         ListBucketResponse container = client.listBlobs(containerName, withPrefix("apps/")).get(
                  10, TimeUnit.SECONDS);
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
         AccessControlList acl = client.getContainerACL(containerName).get(10, TimeUnit.SECONDS);
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
         AccessControlList acl = client.getContainerACL(containerName).get(10, TimeUnit.SECONDS);
         String ownerId = acl.getOwner().getId();
         assertEquals(acl.getGrants().size(), 1);
         assertTrue(acl.hasPermission(ownerId, Permission.FULL_CONTROL));

         addGrantsToACL(acl);
         assertEquals(acl.getGrants().size(), 4);
         assertTrue(client.putContainerACL(containerName, acl).get(10, TimeUnit.SECONDS));

         // Confirm that the updated ACL has stuck.
         acl = client.getContainerACL(containerName).get(10, TimeUnit.SECONDS);
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
         ListBucketResponse container = client.listBlobs(containerName, afterMarker("y")).get(10,
                  TimeUnit.SECONDS);
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
         ListBucketResponse container = client.listBlobs(containerName, maxResults(5)).get(10,
                  TimeUnit.SECONDS);
         assertEquals(container.getMaxResults(), 5);
         assert container.isTruncated();
         assertEquals(container.size(), 5);
      } finally {
         returnContainer(containerName);
      }
   }

}