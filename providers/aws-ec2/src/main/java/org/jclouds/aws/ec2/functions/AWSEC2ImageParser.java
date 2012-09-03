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
package org.jclouds.aws.ec2.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.aws.ec2.domain.AWSImage;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;

/**
 * @author Andrew Kennedy
 */
@Singleton
public class AWSEC2ImageParser implements Function<AWSImage, Image> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final Function<org.jclouds.ec2.domain.Image, Image> ec2ImageParser;

   @Inject
   public AWSEC2ImageParser(Function<org.jclouds.ec2.domain.Image, Image> ec2ImageParser) {
      this.ec2ImageParser = checkNotNull(ec2ImageParser, "ec2ImageParser");
   }

   @Override
   public Image apply(AWSImage from) {
      Image ec2Image = ec2ImageParser.apply(from);

      if (ec2Image == null) {
         return null;
      } else {
         ImageBuilder builder = ImageBuilder.fromImage(ec2Image);
   
         // Set tags to list of tag names
         builder.tags(from.getTags().keySet());
   
         // Update userMetadata by adding tags from image
         Map<String, String> userMetadata = ImmutableMap.<String, String>builder()
               .putAll(ec2Image.getUserMetadata())
               .putAll(from.getTags())
               .build();
         builder.userMetadata(userMetadata);
   
         return builder.build();
      }
   }
}
