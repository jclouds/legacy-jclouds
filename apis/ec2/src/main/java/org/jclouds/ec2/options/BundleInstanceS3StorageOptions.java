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
package org.jclouds.ec2.options;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.jclouds.domain.Credentials;
import org.jclouds.ec2.options.internal.BaseEC2RequestOptions;
import org.jclouds.location.Provider;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Supplier;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;

/**
 * Contains options supported in the Form API for the RegisterImage operation.
 * <h2>
 * Usage</h2> The recommended way to instantiate a
 * BundleInstanceS3StorageOptions object is to statically import
 * BundleInstanceS3StorageOptions.Builder.* and invoke a static creation method
 * followed by an instance mutator (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.ec2.options.BundleInstanceS3StorageOptions.Builder.*
 * <p/>
 * EC2Client connection = // get connection
 * String imageId = connection.getWindowsServices().bundleInstanceInRegion(...bucketOwnedBy(anotherAccessKey));
 * <code>
 * 
 * @author Adrian Cole
 * @see <a
 *      href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-BundleInstance.html"
 *      />
 */
public class BundleInstanceS3StorageOptions extends BaseEC2RequestOptions {

   @Inject
   @VisibleForTesting
   @Provider
   Supplier<Credentials> creds;

   @Override
   public Multimap<String, String> buildFormParameters() {
      if (getAwsAccessKeyId() == null) {
         checkState(creds != null, "creds should have been injected");
         bucketOwnedBy(creds.get().identity);
      }
      return super.buildFormParameters();
   }

   /**
    * 
    * @param ccessKeyId
    *           The Access Key ID of the owner of the Amazon S3 bucket.
    */
   public BundleInstanceS3StorageOptions bucketOwnedBy(String ccessKeyId) {
      formParameters.put("Storage.S3.AWSAccessKeyId", checkNotNull(ccessKeyId, "ccessKeyId"));
      return this;
   }

   /**
    * 
    * @return The Access Key ID of the owner of the Amazon S3 bucket.
    */
   public String getAwsAccessKeyId() {
      return getFirstFormOrNull("Storage.S3.AWSAccessKeyId");
   }

   public static class Builder {
      /**
       * @see BundleInstanceS3StorageOptions#bucketOwnedBy(accessKeyId)
       */
      public static BundleInstanceS3StorageOptions bucketOwnedBy(String accessKeyId) {
         BundleInstanceS3StorageOptions options = new BundleInstanceS3StorageOptions();
         return options.bucketOwnedBy(accessKeyId);
      }

   }
}
