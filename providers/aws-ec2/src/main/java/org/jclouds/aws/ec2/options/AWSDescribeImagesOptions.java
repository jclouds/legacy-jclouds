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
package org.jclouds.aws.ec2.options;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.jclouds.ec2.options.DescribeImagesOptions;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * Extra options only available in Amazon's implementation
 * 
 * @see DescribeImagesOptions
 * @author Adrian Cole
 */
public class AWSDescribeImagesOptions extends DescribeImagesOptions {
   public static final AWSDescribeImagesOptions NONE = new AWSDescribeImagesOptions();

   /**
    * {@inheritDoc}
    */
   @Override
   public AWSDescribeImagesOptions executableBy(String identityId) {
      super.executableBy(identityId);
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AWSDescribeImagesOptions imageIds(String... imageIds) {
      super.imageIds(imageIds);
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AWSDescribeImagesOptions imageIds(Iterable<String> imageIds) {
      super.imageIds(imageIds);
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AWSDescribeImagesOptions ownedBy(String... owners) {
      super.ownedBy(owners);
      return this;
   }

   /**
    * You can filter the results to return information only about images that match criteria you
    * specify. For example, you could get information only about images that use a certain kernel.
    * You can specify multiple values for a filter (e.g., the image uses either kernel aki-1a2b3c4d
    * or kernel aki-9b8c7d6f). An image must match at least one of the specified values for it to be
    * included in the results.
    * <p/>
    * You can specify multiple filters (e.g., the image uses a certain kernel, and uses an Amazon
    * EBS volume as the root device). The result includes information for a particular image only if
    * it matches all your filters. If there's no match, no special message is returned; the response
    * is simply empty.
    * <p/>
    * You can use wildcards with the filter values: * matches zero or more characters, and ? matches
    * exactly one character. You can escape special characters using a backslash before the
    * character. For example, a value of \*amazon\?\\ searches for the literal string *amazon?\.
    * 
    */
   public AWSDescribeImagesOptions filters(Multimap<String, String> filters) {
      int i = 0;
      for (Entry<String, Collection<String>> filter : checkNotNull(filters, "filters").asMap().entrySet()) {
         String filterPrefix = String.format("Filter.%s.", ++i);
         formParameters.put(filterPrefix + "Name", filter.getKey());
         indexFormValuesWithPrefix(filterPrefix + "Value", filter.getValue());
      }
      return this;
   }

   /**
    * @see #filters(Multimap)
    */
   public AWSDescribeImagesOptions filters(Map<String, String> filters) {
      return filters(Multimaps.forMap(checkNotNull(filters, "filters")));
   }

   public static class Builder extends DescribeImagesOptions.Builder {

      /**
       * @see AWSDescribeImagesOptions#executableBy
       */
      public static AWSDescribeImagesOptions executableBy(String identityId) {
         AWSDescribeImagesOptions options = new AWSDescribeImagesOptions();
         return options.executableBy(identityId);
      }

      /**
       * @see AWSDescribeImagesOptions#imageIds
       */
      public static AWSDescribeImagesOptions imageIds(String... imageIds) {
         AWSDescribeImagesOptions options = new AWSDescribeImagesOptions();
         return options.imageIds(imageIds);
      }

      /**
       * @see AWSDescribeImagesOptions#filters(Multimap)
       */
      public static AWSDescribeImagesOptions filters(Multimap<String, String> filters) {
         AWSDescribeImagesOptions options = new AWSDescribeImagesOptions();
         return options.filters(filters);
      }

      /**
       * @see AWSDescribeImagesOptions#filters(Map)
       */
      public static AWSDescribeImagesOptions filters(Map<String, String> filters) {
         AWSDescribeImagesOptions options = new AWSDescribeImagesOptions();
         return options.filters(filters);
      }

      /**
       * @see AWSDescribeImagesOptions#ownedBy
       */
      public static AWSDescribeImagesOptions ownedBy(String... owners) {
         AWSDescribeImagesOptions options = new AWSDescribeImagesOptions();
         return options.ownedBy(owners);
      }

   }
}
