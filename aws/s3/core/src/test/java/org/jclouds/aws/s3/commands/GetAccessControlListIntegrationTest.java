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
package org.jclouds.aws.s3.commands;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.aws.s3.S3IntegrationTest;
import org.jclouds.aws.s3.domain.AccessControlList;
import org.jclouds.aws.s3.domain.CannedAccessPolicy;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.domain.AccessControlList.GroupGranteeURI;
import org.jclouds.aws.s3.domain.AccessControlList.Permission;
import org.jclouds.aws.s3.options.PutObjectOptions;
import org.jclouds.util.Utils;
import org.testng.annotations.Test;

/**
 * Tests integrated functionality of all commands that retrieve Access Control Lists (ACLs).
 * 
 * @author James Murty
 * @author Adrian Cole
 */
@Test(groups = { "integration", "live" }, testName = "s3.GetAccessControlListIntegrationTest")
public class GetAccessControlListIntegrationTest extends S3IntegrationTest {

   @Test
   void testPrivateAclIsDefaultForBucket() throws InterruptedException, ExecutionException,
            TimeoutException, IOException {
      String bucketName = getBucketName();
      try {
         AccessControlList acl = client.getBucketACL(bucketName).get(10, TimeUnit.SECONDS);
         assertEquals(acl.getGrants().size(), 1);
         assertTrue(acl.getOwner() != null);
         String ownerId = acl.getOwner().getId();
         assertTrue(acl.hasPermission(ownerId, Permission.FULL_CONTROL));
      } finally {
         returnBucket(bucketName);
      }

   }

   @Test
   void testPrivateAclIsDefaultForObject() throws InterruptedException, ExecutionException,
            TimeoutException, IOException {
      String privateObjectKey = "private-acl";
      String bucketName = getBucketName();
      try {
         // Private object
         addObjectToBucket(bucketName, privateObjectKey);
         AccessControlList acl = client.getObjectACL(bucketName, privateObjectKey).get(10,
                  TimeUnit.SECONDS);

         assertEquals(acl.getGrants().size(), 1);
         assertTrue(acl.getOwner() != null);
         String ownerId = acl.getOwner().getId();
         assertTrue(acl.hasPermission(ownerId, Permission.FULL_CONTROL));
      } finally {
         returnBucket(bucketName);
      }

   }

   @Test
   void testPublicReadOnObject() throws InterruptedException, ExecutionException, TimeoutException,
            IOException {
      final String publicReadObjectKey = "public-read-acl";
      final String bucketName = getBucketName();
      try {
         client.putObject(bucketName, new S3Object(publicReadObjectKey, ""), new PutObjectOptions()
                  .withAcl(CannedAccessPolicy.PUBLIC_READ));

         assertEventually(new Runnable() {
            public void run() {
               try {
                  AccessControlList acl = client.getObjectACL(bucketName, publicReadObjectKey).get(
                           10, TimeUnit.SECONDS);

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
         returnBucket(bucketName);
      }

   }

   @Test
   void testPublicWriteOnObject() throws InterruptedException, ExecutionException,
            TimeoutException, IOException {
      final String publicReadWriteObjectKey = "public-read-write-acl";
      final String bucketName = getBucketName();
      try {
         // Public Read-Write object
         client.putObject(bucketName, new S3Object(publicReadWriteObjectKey, ""),
                  new PutObjectOptions().withAcl(CannedAccessPolicy.PUBLIC_READ_WRITE));

         assertEventually(new Runnable() {
            public void run() {
               try {
                  AccessControlList acl = client.getObjectACL(bucketName, publicReadWriteObjectKey)
                           .get(10, TimeUnit.SECONDS);
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
         returnBucket(bucketName);
      }

   }
}