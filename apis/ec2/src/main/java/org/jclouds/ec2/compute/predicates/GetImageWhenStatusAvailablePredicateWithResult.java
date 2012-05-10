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
package org.jclouds.ec2.compute.predicates;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.aws.util.AWSUtils;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.compute.functions.EC2ImageParser;
import org.jclouds.ec2.options.DescribeImagesOptions;
import org.jclouds.logging.Logger;
import org.jclouds.predicates.PredicateWithResult;

import com.google.common.collect.Iterables;

/**
 * §
 * @author David Alves
 *
 */
public final class GetImageWhenStatusAvailablePredicateWithResult implements PredicateWithResult<String, Image> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final EC2Client ec2Client;
   private final EC2ImageParser ec2ImageToImage;
   private org.jclouds.ec2.domain.Image result;
   private RuntimeException lastFailure;

   @Inject
   public GetImageWhenStatusAvailablePredicateWithResult(EC2Client ec2Client, EC2ImageParser ec2ImageToImage) {
      this.ec2Client = ec2Client;
      this.ec2ImageToImage = ec2ImageToImage;
   }

   @Override
   public boolean apply(String input) {
      String[] parts = AWSUtils.parseHandle(input);
      String region = parts[0];
      String imageId = parts[1];
      result = checkNotNull(findImage(imageId, region));
      switch (result.getImageState()) {
         case AVAILABLE:
            logger.info("<< Image %s is available for use.", input);
            return true;
         case UNRECOGNIZED:
            logger.debug("<< Image %s is not available yet.", input);
            return false;
         default:
            lastFailure = new IllegalStateException("Image was not created: " + input);
            throw lastFailure;
      }
   }

   @Override
   public Image getResult() {
      return ec2ImageToImage.apply(result);
   }

   @Override
   public Throwable getLastFailure() {
      return lastFailure;
   }

   private org.jclouds.ec2.domain.Image findImage(String id, String region) {
      return Iterables.getOnlyElement(ec2Client.getAMIServices().describeImagesInRegion(region,
               new DescribeImagesOptions().imageIds(id)));

   }
}