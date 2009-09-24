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

import static org.jclouds.aws.s3.options.PutBucketOptions.Builder.createIn;
import static org.jclouds.aws.s3.options.PutBucketOptions.Builder.withBucketAcl;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.jclouds.aws.s3.S3BlobStore;
import org.jclouds.aws.s3.domain.AccessControlList;
import org.jclouds.aws.s3.domain.BucketMetadata;
import org.jclouds.aws.s3.domain.CannedAccessPolicy;
import org.jclouds.aws.s3.domain.ObjectMetadata;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.domain.AccessControlList.GroupGranteeURI;
import org.jclouds.aws.s3.domain.AccessControlList.Permission;
import org.jclouds.aws.s3.domain.BucketMetadata.LocationConstraint;
import org.jclouds.blobstore.integration.internal.BaseContainerLiveTest;
import org.jclouds.util.Utils;
import org.testng.annotations.Test;

/**
 * @author James Murty
 * @author Adrian Cole
 */
@Test(groups = { "live" }, testName = "s3.S3ContainerLiveTest")
public class S3ContainerLiveTest extends
         BaseContainerLiveTest<S3BlobStore, BucketMetadata, ObjectMetadata, S3Object> {

   public void testPublicReadAccessPolicy() throws Exception {
      String containerName = getScratchContainerName();
      try {
         client.createContainer(containerName, withBucketAcl(CannedAccessPolicy.PUBLIC_READ)).get(
                  10, TimeUnit.SECONDS);
         AccessControlList acl = client.getContainerACL(containerName).get(10, TimeUnit.SECONDS);
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
         client.createContainer(containerName + "eu",
                  createIn(LocationConstraint.EU).withBucketAcl(CannedAccessPolicy.PUBLIC_READ))
                  .get(30, TimeUnit.SECONDS);
         assertEventually(new Runnable() {
            public void run() {
               try {
                  AccessControlList acl = client.getContainerACL(containerName + "eu").get(30,
                           TimeUnit.SECONDS);
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
}