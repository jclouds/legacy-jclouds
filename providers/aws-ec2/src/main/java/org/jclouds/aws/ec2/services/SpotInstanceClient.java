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

package org.jclouds.aws.ec2.services;

import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import org.jclouds.aws.ec2.options.DescribeSpotPriceHistoryOptions;
import org.jclouds.aws.ec2.options.RequestSpotInstancesOptions;
import org.jclouds.concurrent.Timeout;

/**
 * Provides Spot Instance services for EC2. For more information, refer to the Amazon EC2 Developer
 * Guide.
 * <p/>
 * 
 * @author Adrian Cole
 */
@Timeout(duration = 45, timeUnit = TimeUnit.SECONDS)
public interface SpotInstanceClient {
   /**
    * Describes Spot Instance requests. Spot Instances are instances that Amazon EC2 starts on your
    * behalf when the maximum price that you specify exceeds the current Spot Price. Amazon EC2
    * periodically sets the Spot Price based on available Spot Instance capacity and current spot
    * instance requests. For conceptual information about Spot Instances, refer to the Amazon
    * Elastic Compute Cloud Developer Guide or Amazon Elastic Compute Cloud User Guide.
    * 
    * @param region
    *           Region where the spot instance service is running
    * @param requestIds
    *           Specifies the ID of the Spot Instance request.
    * 
    * @see #requestSpotInstancesInRegion
    * @see #cancelSpotInstanceRequestsInRegion
    * @see #describeSpotPriceHistoryInRegion
    * @see <a href=
    *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeSpotInstanceRequests.html"
    *      />
    * @return TODO
    */
   String describeSpotInstanceRequestsInRegion(@Nullable String region, String... requestIds);

   /**
    * Creates a Spot Instance request. Spot Instances are instances that Amazon EC2 starts on your
    * behalf when the maximum price that you specify exceeds the current Spot Price. Amazon EC2
    * periodically sets the Spot Price based on available Spot Instance capacity and current spot
    * instance requests. For conceptual information about Spot Instances, refer to the Amazon
    * Elastic Compute Cloud Developer Guide or Amazon Elastic Compute Cloud User Guide.
    * 
    * @param region
    *           Region where the spot instance service is running
    * @param nullableAvailabilityZone
    *           The availability zone to launch the instances in, or null to let the system choose
    * @param imageId
    *           The AMI ID.
    * @param instanceCount
    *           The maximum number of Spot Instances to launch.
    * @param spotPrice
    *           Specifies the maximum hourly price for any Spot Instance launched to fulfill the
    *           request.
    * @param options
    *           control the duration of the request, grouping, and the size and parameters of the
    *           server to run
    * 
    * @see #describeSpotInstanceRequestsInRegion
    * @see #cancelSpotInstanceRequestsInRegion
    * @see #describeSpotPriceHistoryInRegion
    * @see <a href=
    *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-RequestSpotInstances.html"
    *      />
    * @return TODO
    */
   String requestSpotInstancesInRegion(@Nullable String region, @Nullable String nullableAvailabilityZone,
         String imageId, int instanceCount, float spotPrice, RequestSpotInstancesOptions... options);

   /**
    * 
    * Describes Spot Price history. Spot Instances are instances that Amazon EC2 starts on your
    * behalf when the maximum price that you specify exceeds the current Spot Price. Amazon EC2
    * periodically sets the Spot Price based on available Spot Instance capacity and current spot
    * instance requests. For conceptual information about Spot Instances, refer to the Amazon
    * Elastic Compute Cloud Developer Guide or Amazon Elastic Compute Cloud User Guide.
    * 
    * @param region
    *           Region where the spot instance service is running
    * @param options
    *           options to control the list
    * 
    * @see #describeSpotInstanceRequestsInRegion
    * @see #requestSpotInstancesInRegion
    * @see #cancelSpotInstanceRequestsInRegion
    * @see <a href=
    *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeSpotInstanceRequests.html"
    *      />
    * @return TODO
    */
   String describeSpotPriceHistoryInRegion(@Nullable String region, DescribeSpotPriceHistoryOptions... options);

   /**
    * Cancels one or more Spot Instance requests. Spot Instances are instances that Amazon EC2
    * starts on your behalf when the maximum price that you specify exceeds the current Spot Price.
    * Amazon EC2 periodically sets the Spot Price based on available Spot Instance capacity and
    * current spot instance requests. For conceptual information about Spot Instances, refer to the
    * Amazon Elastic Compute Cloud Developer Guide or Amazon Elastic Compute Cloud User Guide.
    * 
    * @param region
    *           Region where the spot instance service is running
    * @param requestIds
    *           Specifies the ID of the Spot Instance request.
    * 
    * @see #describeSpotInstanceRequestsInRegion
    * @see #requestSpotInstancesInRegion
    * @see #describeSpotPriceHistoryInRegion
    * @see <a href=
    *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-CancelSpotInstanceRequests.html"
    *      />
    * @return TODO
    */
   String cancelSpotInstanceRequestsInRegion(@Nullable String region, String... requestIds);

}
