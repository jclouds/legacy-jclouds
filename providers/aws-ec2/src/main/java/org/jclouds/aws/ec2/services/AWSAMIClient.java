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

import org.jclouds.concurrent.Timeout;
import org.jclouds.ec2.options.DescribeImagesOptions;
import org.jclouds.ec2.services.AMIClient;
import org.jclouds.javax.annotation.Nullable;

/**
 * Provides access to EC2 via their REST API.
 * <p/>
 * 
 * @author Adrian Cole
 */
@Timeout(duration = 45, timeUnit = TimeUnit.SECONDS)
public interface AWSAMIClient extends AMIClient{


   /**
    * Returns the Product Codes of an image.
    * 
    * @param region
    *           AMIs are tied to the Region where its files are located within Amazon S3.
    * @param imageId
    *           The ID of the AMI for which an attribute will be described
    * @see #describeImages
    * @see #modifyImageAttribute
    * @see #resetImageAttribute
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeImageAttribute.html"
    *      />
    * @see DescribeImagesOptions
    */
   Set<String> getProductCodesForImageInRegion(@Nullable String region, String imageId);


   /**
    * Adds {@code productCode}s to an AMI.
    * 
    * @param region
    *           AMIs are tied to the Region where its files are located within Amazon S3.
    * @param productCodes
    *           Product Codes
    * @param imageId
    *           The AMI ID.
    * 
    * @see #removeProductCodesFromImage
    * @see #describeImageAttribute
    * @see #resetImageAttribute
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-ModifyImageAttribute.html"
    *      />
    */
   void addProductCodesToImageInRegion(@Nullable String region, Iterable<String> productCodes, String imageId);

   /**
    * Removes {@code productCode}s from an AMI.
    * 
    * @param region
    *           AMIs are tied to the Region where its files are located within Amazon S3.
    * @param productCodes
    *           Product Codes
    * @param imageId
    *           The AMI ID.
    * 
    * @see #addProductCodesToImage
    * @see #describeImageAttribute
    * @see #resetImageAttribute
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-ModifyImageAttribute.html"
    *      />
    */
   void removeProductCodesFromImageInRegion(@Nullable String region, Iterable<String> productCodes,
            String imageId);
}
