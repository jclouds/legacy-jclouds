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
package org.jclouds.ec2.services;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.ec2.domain.BundleTask;
import org.jclouds.ec2.domain.PasswordData;
import org.jclouds.ec2.options.BundleInstanceS3StorageOptions;
import org.jclouds.javax.annotation.Nullable;

/**
 * Provides windows services for EC2. For more information, refer to the Amazon
 * EC2 Developer Guide.
 * <p/>
 * 
 * @author Adrian Cole
 */
@Timeout(duration = 45, timeUnit = TimeUnit.SECONDS)
public interface WindowsClient {
   /**
    * Bundles the Windows instance. This procedure is not applicable for Linux
    * and UNIX instances. For more information, go to the Amazon Elastic Compute
    * Cloud Developer Guide or Amazon Elastic Compute Cloud Getting Started
    * Guide.
    * 
    * @param region
    *           Bundles are tied to the Region where its files are located
    *           within Amazon S3.
    * 
    * @param instanceId
    *           The ID of the instance to bundle.
    * @param prefix
    *           Specifies the beginning of the file name of the AMI.
    * @param bucket
    *           The bucket in which to store the AMI. You can specify a bucket
    *           that you already own or a new bucket that Amazon EC2 creates on
    *           your behalf. If you specify a bucket that belongs to som eone
    *           else, Amazon EC2 returns an error.
    * @param uploadPolicy
    *           An Amazon S3 upload policy that gives Amazon EC2 permission to
    *           upload items into Amazon S3 on the user's behalf.
    *           <p/>
    *           ex.
    * 
    *           <pre>
    * {"expiration": "2008-08-30T08:49:09Z","conditions": ["bucket": "my-bucket"},["starts-with", "$key", "my-new-image"]]}
    * </pre>
    * 
    * @param options
    *           if the bucket isn't owned by you, use this to set the bucket's
    *           accesskeyid
    * @return status of the work
    * 
    * @see #cancelBundleTaskInRegion
    * @see #describeBundleTasksInRegion
    * 
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-BundleInstance.html"
    *      />
    */
   BundleTask bundleInstanceInRegion(@Nullable String region, String instanceId, String prefix, String bucket,
         String uploadPolicy, BundleInstanceS3StorageOptions... options);

   /**
    * Cancels an Amazon EC2 bundling operation.
    * 
    * @param region
    *           The bundleTask ID is tied to the Region.
    * @param bundleId
    *           The ID of the bundle task to cancel.
    * @return task for the cancel.
    * 
    * @see #bundleInstanceInRegion
    * @see #describeBundleTasksInRegion
    * 
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-CancelBundleTask.html"
    *      />
    */
   BundleTask cancelBundleTaskInRegion(@Nullable String region, String bundleId);

   /**
    * 
    * Describes current bundling tasks.
    * 
    * @param region
    *           The bundleTask ID is tied to the Region.
    * 
    * @see #cancelBundleTaskInRegion
    * @see #bundleInstanceInRegion
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeBundleTasks.html"
    *      />
    */
   Set<BundleTask> describeBundleTasksInRegion(@Nullable String region, String... bundleTaskIds);

   /**
    *
    * Retrieves the encrypted administrator password for the instances running Windows.
    *
    * @param region The region where the instance is based
    * @param instanceId The ID of the instance to query
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-GetPasswordData.html" />
    */
   PasswordData getPasswordDataInRegion(@Nullable String region, String instanceId);
}
