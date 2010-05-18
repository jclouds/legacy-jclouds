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
package org.jclouds.aws.ec2.compute.functions;

import static org.jclouds.aws.ec2.options.DescribeImagesOptions.Builder.imageIds;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.aws.ec2.compute.domain.RegionAndName;
import org.jclouds.aws.ec2.services.AMIClient;
import org.jclouds.compute.domain.Image;
import org.jclouds.logging.Logger;
import org.jclouds.rest.ResourceNotFoundException;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public final class RegionAndIdToImage implements Function<RegionAndName, Image> {
   @Resource
   protected Logger logger = Logger.NULL;

   private final ImageParser parser;
   private final AMIClient sync;

   @Inject
   public RegionAndIdToImage(ImageParser parser, AMIClient sync) {
      this.parser = parser;
      this.sync = sync;
   }

   public Image apply(RegionAndName key) {
      try {
         org.jclouds.aws.ec2.domain.Image image = Iterables.getOnlyElement(sync
                  .describeImagesInRegion(key.getRegion(), imageIds(key.getName())));
         return parser.apply(image);
      } catch (ResourceNotFoundException e) {
         logger.warn(e, "no image found for %s/%s: %s", key.getRegion(), key.getName(), e
                  .getMessage());
         return null;
      }
   }
}