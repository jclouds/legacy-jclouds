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
package org.jclouds.aws.s3.services;

import static org.jclouds.s3.options.PutBucketOptions.Builder.withBucketAcl;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.jclouds.aws.domain.Region;
import org.jclouds.s3.domain.AccessControlList;
import org.jclouds.s3.domain.AccessControlList.GroupGranteeURI;
import org.jclouds.s3.domain.AccessControlList.Permission;
import org.jclouds.s3.domain.CannedAccessPolicy;
import org.jclouds.s3.services.BucketsLiveTest;
import org.testng.annotations.Test;

import com.google.common.base.Throwables;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "AWSBucketsLiveTest")
public class AWSBucketsLiveTest extends BucketsLiveTest {
   public AWSBucketsLiveTest() {
      provider = "aws-s3";
   }

   public void testDefaultBucketLocation() throws Exception {

      String bucketName = getContainerName();
      try {
         String location = getApi().getBucketLocation(bucketName);
         assert location.equals(Region.US_STANDARD) : "bucket: " + bucketName + " location: " + location;
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
         getApi().putBucketInRegion(Region.EU_WEST_1, bucketName + "eu", withBucketAcl(CannedAccessPolicy.PUBLIC_READ));
         assertConsistencyAware(new Runnable() {
            public void run() {
               try {
                  AccessControlList acl = getApi().getBucketACL(bucketName + "eu");
                  assertTrue(acl.hasPermission(GroupGranteeURI.ALL_USERS, Permission.READ), acl.toString());
               } catch (Exception e) {
                  Throwables.propagateIfPossible(e);
               }
            }
         });
         assertEquals(Region.EU_WEST_1, getApi().getBucketLocation(bucketName + "eu"));
         // TODO: I believe that the following should work based on the above acl assertion passing.
         // However, it fails on 403
         // URL url = new URL(String.format("http://%s.s3.amazonaws.com", bucketName));
         // Utils.toStringAndClose(url.openStream());
      } finally {
         destroyContainer(bucketName + "eu");
      }
   }
}
