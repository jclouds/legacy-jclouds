/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.aws.ec2.services;

import java.util.SortedSet;
import java.util.concurrent.TimeUnit;

import org.jclouds.aws.ec2.domain.Image;
import org.jclouds.aws.ec2.domain.ImageAttribute;
import org.jclouds.aws.ec2.options.CreateImageOptions;
import org.jclouds.aws.ec2.options.DescribeImagesOptions;
import org.jclouds.concurrent.Timeout;

/**
 * Provides access to EC2 via their REST API.
 * <p/>
 * 
 * @author Adrian Cole
 */
@Timeout(duration = 30, timeUnit = TimeUnit.SECONDS)
public interface AMIClient {

   /**
    * Returns information about AMIs, AKIs, and ARIs. This includes image type, product codes,
    * architecture, and kernel and RAM disk IDs. Images available to you include p ublic images,
    * private images that you own, and private images owned by other users for which you have
    * explicit launch permissions.
    * 
    * @see InstanceClient#describeInstances
    * @see #describeImageAttribute
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeImages.html"
    *      />
    * @see DescribeImagesOptions
    */
   @Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
   SortedSet<Image> describeImages(DescribeImagesOptions... options);

   /**
    * Returns information about an attribute of an AMI. Only one attribute can be specified per
    * call.
    * 
    * @param imageId
    *           The ID of the AMI for which an attribute will be described
    * @param attribute
    *           the attribute to describe
    * @see #describeImages
    * @see #modifyImageAttribute
    * @see #resetImageAttribute
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeImageAttribute.html"
    *      />
    * @see DescribeImagesOptions
    */
   String describeImageAttribute(String imageId, ImageAttribute attribute);

   /**
    * Creates an AMI that uses an Amazon EBS root device from a "running" or "stopped" instance.
    * 
    * @param name The name of the AMI that was provided during image creation.  3-128 alphanumeric characters, parenthesis (()), commas (,), slashes (/), dashes (-), or underscores(_)
    * @param instanceId The ID of the instance.
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeImages.html"
    *      />
    * @see CreateImageOptions
    * @see InstanceClient#runInstances
    * @see InstanceClient#describeInstances
    * @see InstanceClient#terminateInstances
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-CreateImage.html"
    *      />
    */
   String createImage(String name, String instanceId, CreateImageOptions... options);
}
