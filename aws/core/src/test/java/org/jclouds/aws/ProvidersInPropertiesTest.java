/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.aws;

import org.jclouds.blobstore.util.BlobStoreUtils;
import org.jclouds.compute.util.ComputeServiceUtils;
import org.jclouds.util.Utils;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Test(groups = "unit")
public class ProvidersInPropertiesTest {

   @Test
   public void testSupportedComputeServiceProviders() {
      Iterable<String> providers = ComputeServiceUtils.getSupportedProviders();
      assert !Iterables.contains(providers, "sqs") : providers;
      assert !Iterables.contains(providers, "elb") : providers;
      assert !Iterables.contains(providers, "s3") : providers;
      assert !Iterables.contains(providers, "walrus") : providers;
      assert !Iterables.contains(providers, "googlestorage") : providers;
      assert Iterables.contains(providers, "ec2") : providers;
      assert Iterables.contains(providers, "eucalyptus") : providers;
   }

   @Test
   public void testSupportedProviders() {
      Iterable<String> providers = Utils.getSupportedProviders();
      assert Iterables.contains(providers, "sqs") : providers;
      assert Iterables.contains(providers, "elb") : providers;
      assert Iterables.contains(providers, "s3") : providers;
      assert Iterables.contains(providers, "walrus") : providers;
      assert Iterables.contains(providers, "googlestorage") : providers;
      assert Iterables.contains(providers, "ec2") : providers;
      assert Iterables.contains(providers, "eucalyptus") : providers;
   }

   @Test
   public void testSupportedBlobStoreProviders() {
      Iterable<String> providers = BlobStoreUtils.getSupportedProviders();
      assert !Iterables.contains(providers, "sqs") : providers;
      assert !Iterables.contains(providers, "elb") : providers;
      assert Iterables.contains(providers, "s3") : providers;
      assert Iterables.contains(providers, "walrus") : providers;
      assert Iterables.contains(providers, "googlestorage") : providers;
      assert !Iterables.contains(providers, "ec2") : providers;
      assert !Iterables.contains(providers, "eucalyptus") : providers;
   }

}
