/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.ec2.options;

import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.singleton;
import static org.jclouds.ec2.options.BundleInstanceS3StorageOptions.Builder.bucketOwnedBy;
import static org.testng.Assert.assertEquals;

import org.jclouds.http.options.HttpRequestOptions;
import org.testng.annotations.Test;

/**
 * Tests possible uses of BundleInstanceS3StorageOptions and
 * BundleInstanceS3StorageOptions.Builder.*
 * 
 * @author Adrian Cole
 */
public class BundleInstanceS3StorageOptionsTest {

   @Test
   public void testAssignability() {
      assert HttpRequestOptions.class.isAssignableFrom(BundleInstanceS3StorageOptions.class);
      assert !String.class.isAssignableFrom(BundleInstanceS3StorageOptions.class);
   }

   @Test
   public void testBucketOwnedBy() {
      BundleInstanceS3StorageOptions options = new BundleInstanceS3StorageOptions();
      options.bucketOwnedBy("test");
      assertEquals(options.buildFormParameters().get("Storage.S3.AWSAccessKeyId"), singleton("test"));
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testNullBucketOwnedByNotInjected() {
      BundleInstanceS3StorageOptions options = new BundleInstanceS3StorageOptions();
      assertEquals(options.buildFormParameters().get("Storage.S3.AWSAccessKeyId"), EMPTY_LIST);
   }

   @Test
   public void testNullBucketOwnedBy() {
      BundleInstanceS3StorageOptions options = new BundleInstanceS3StorageOptions();
      options.currentAwsAccessKeyId = "foo";
      assertEquals(options.buildFormParameters().get("Storage.S3.AWSAccessKeyId"), singleton("foo"));
   }

   @Test
   public void testBucketOwnedByStatic() {
      BundleInstanceS3StorageOptions options = bucketOwnedBy("test");
      assertEquals(options.buildFormParameters().get("Storage.S3.AWSAccessKeyId"), singleton("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testBucketOwnedByNPE() {
      bucketOwnedBy(null);
   }

}
