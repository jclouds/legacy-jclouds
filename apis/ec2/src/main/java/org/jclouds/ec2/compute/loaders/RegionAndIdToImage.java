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
package org.jclouds.ec2.compute.loaders;

import static org.jclouds.ec2.options.DescribeImagesOptions.Builder.imageIds;

import java.util.concurrent.ExecutionException;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Image;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.compute.functions.EC2ImageParser;
import org.jclouds.logging.Logger;

import com.google.common.cache.CacheLoader;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class RegionAndIdToImage extends CacheLoader<RegionAndName, Image> {
   @Resource
   protected Logger logger = Logger.NULL;

   private final EC2ImageParser parser;
   private final EC2Client sync;

   @Inject
   public RegionAndIdToImage(EC2ImageParser parser, EC2Client sync) {
      this.parser = parser;
      this.sync = sync;
   }

   @Override
   public Image load(RegionAndName key) throws ExecutionException{
      try {
         org.jclouds.ec2.domain.Image image = Iterables.getOnlyElement(sync.getAMIServices()
               .describeImagesInRegion(key.getRegion(), imageIds(key.getName())));
         return parser.apply(image);
      } catch (Exception e) {
         throw new ExecutionException(message(key, e), e);
      }
   }

   public static String message(RegionAndName key, Exception e) {
      return String.format("could not find image %s/%s: %s", key.getRegion(), key.getName(), e.getMessage());
   }
}
