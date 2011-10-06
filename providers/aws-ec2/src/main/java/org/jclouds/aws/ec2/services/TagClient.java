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

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.aws.ec2.domain.Tag;
import org.jclouds.aws.ec2.util.TagFilters.FilterName;
import org.jclouds.concurrent.Timeout;
import org.jclouds.javax.annotation.Nullable;

/**
 * Provides Tag services for EC2. For more information, refer to the Amazon EC2
 * Developer Guide.
 * 
 * @author grkvlt@apache.org
 */
@Timeout(duration = 45, timeUnit = TimeUnit.SECONDS)
public interface TagClient {
   /**
    * Creates tags.
    * 
    * @param region
    *           Region to create the tag in.
    * @param resourceIds
    *           IDs of the resources to tag.
    * @param tags
    *           The tags to create.
    * @see #describeTagsInRegion(String, Map)
    * @see #deleteTagsInRegion(String, Iterable, Map)
    * 
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-CreateTags.html" />
    */
    void createTagsInRegion(@Nullable String region, Iterable<String> resourceIds, Map<String, String> tags);
   
   /**
    * Deletes tags.
    * 
    * @param region
    *           Region to delete the tags from
    * @param resourceIds
    *           IDs of the tagged resources.
    * @param tags
    *           The tags to delete.
    * 
    * @see #describeTagsInRegion(String, Map)
    * @see #createTagsInRegion(String, Iterable, Map)
    * 
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DeleteTags.html" />
    */
   void deleteTagsInRegion(@Nullable String region, Iterable<String> resourceIds, Map<String, String> tags);

   /**
    * Returns filtered information about tags.
    * 
    * @param region
    *           The bundleTask ID is tied to the Region.
    * @param filters
    *           A collection of filters to apply before selecting the tags.
    * 
    * @see #deleteTagsInRegion(String, Iterable, Map)
    * @see #createTagsInRegion(String, Iterable, Map)
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeTags.html" />
    */
   Set<Tag> describeTagsInRegion(@Nullable String region, Map<FilterName, Iterable<?>> filters);
}
