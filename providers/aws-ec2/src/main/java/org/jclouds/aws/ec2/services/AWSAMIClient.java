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
package org.jclouds.aws.ec2.services;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.aws.ec2.domain.AWSImage;
import org.jclouds.concurrent.Timeout;
import org.jclouds.ec2.options.DescribeImagesOptions;
import org.jclouds.ec2.services.AMIClient;
import org.jclouds.javax.annotation.Nullable;

/**
 * Provides access to AMI Services.
 * 
 * @author Adrian Cole
 * @author Andrew Kennedy
 */
@Timeout(duration = 45, timeUnit = TimeUnit.SECONDS)
public interface AWSAMIClient extends AMIClient {

   /**
    * Returns information about AMIs, AKIs, and ARIs.
    *
    * This includes image type, product codes, architecture, and kernel and RAM disk IDs. Images
    * available to you include public images, private images that you own, and private images owned
    * by other users for which you have explicit launch permissions.
    * 
    * @param region an AMI is tied to the Region within Amazon S3 where its files are located
    *
    * @see org.jclouds.ec2.services.InstanceClient#describeInstancesInRegion(String, String...)
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeImages.html" />
    * @see DescribeImagesOptions
    */
   @Override
   @Timeout(duration = 300, timeUnit = TimeUnit.SECONDS)
   Set<AWSImage> describeImagesInRegion(@Nullable String region, DescribeImagesOptions... options);

   /**
    * Returns the {@code productCode}s of an AMI.
    * 
    * @param region an AMI is tied to the Region within Amazon S3 where its files are located
    * @param imageId The ID of the AMI for which an attribute will be described
    *
    * @see #addProductCodesToImageInRegion(String, Iterable, String)
    * @see #removeProductCodesFromImageInRegion(String, Iterable, String)
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeImageAttribute.html" />
    */
   Set<String> getProductCodesForImageInRegion(@Nullable String region, String imageId);


   /**
    * Adds {@code productCode}s to an AMI.
    * 
    * @param region an AMI is tied to the Region within Amazon S3 where its files are located
    * @param productCodes Product codes to be added to the AMI
    * @param imageId The ID of the AMI for which an attribute will be described
    * 
    * @see #removeProductCodesFromImageInRegion(String, Iterable, String)
    * @see #getProductCodesForImageInRegion(String, String)
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-ModifyImageAttribute.html" />
    */
   void addProductCodesToImageInRegion(@Nullable String region, Iterable<String> productCodes, String imageId);

   /**
    * Removes {@code productCode}s from an AMI.
    * 
    * @param region an AMI is tied to the Region within Amazon S3 where its files are located
    * @param productCodes Product codes to be removed from the AMI
    * @param imageId The ID of the AMI for which an attribute will be described
    *
    * @see #addProductCodesToImageInRegion(String, Iterable, String)
    * @see #getProductCodesForImageInRegion(String, String)
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-ModifyImageAttribute.html" />
    */
   void removeProductCodesFromImageInRegion(@Nullable String region, Iterable<String> productCodes, String imageId);
}
