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
package org.jclouds.aws.ec2.compute.suppliers;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.aws.ec2.options.AWSDescribeImagesOptions.Builder.filters;

import java.util.Map.Entry;
import java.util.concurrent.Callable;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.ec2.compute.functions.EC2ImageParser;
import org.jclouds.ec2.compute.strategy.DescribeImagesParallel;
import org.jclouds.ec2.options.DescribeImagesOptions;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.inject.assistedinject.Assisted;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class CallForImages implements Callable<Iterable<Image>> {
   public interface Factory {
      CallForImages parseImagesFromRegionsUsingFilter(Iterable<String> regions, Multimap<String, String> filter);
   }

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final Iterable<String> regions;
   private final DescribeImagesParallel describer;
   private final EC2ImageParser parser;
   private final Multimap<String, String> filter;

   @Inject
   protected CallForImages(DescribeImagesParallel describer, EC2ImageParser parser, @Assisted Iterable<String> regions,
            @Assisted Multimap<String, String> filter) {
      this.regions = regions;
      this.describer = describer;
      this.filter = filter;
      this.parser = parser;
   }

   public Iterable<Image> call() {

      logger.debug(">> providing images");

      Builder<String, DescribeImagesOptions> builder = ImmutableMap.builder();
      for (String region : regions)
         builder.put(region, filters(filter));

      Iterable<Entry<String, DescribeImagesOptions>> queries = builder.build().entrySet();

      Iterable<Image> returnVal = filter(transform(describer.apply(queries), parser), Predicates.notNull());
      if (logger.isDebugEnabled())
         logger.debug("<< images(%s)", Iterables.size(returnVal));
      return returnVal;
   }

   @Override
   public String toString() {
      return String.format("desribingImages(filter=%s,regions=%s)", filter, regions);
   }

}