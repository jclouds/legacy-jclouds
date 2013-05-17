/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.ec2.features;

import java.util.Map;
import org.jclouds.ec2.domain.Tag;
import org.jclouds.ec2.util.TagFilterBuilder;
import org.jclouds.rest.annotations.SinceApiVersion;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Multimap;

/**
 * To help you manage your Amazon EC2 instances, images, and other Amazon EC2
 * resources, you can assign your own metadata to each resource in the form of
 * tags.
 * 
 * @see <a
 *      href="http://docs.amazonwebservices.com/AWSEC2/latest/UserGuide/Using_Tags.html"
 *      >doc</a>
 * @see TagAsyncApi
 * @author Adrian Cole
 */
@SinceApiVersion("2010-08-31")
public interface TagApi {

   /**
    * Adds or overwrites one or more tags for the specified resource or
    * resources. Each resource can have a maximum of 10 tags. Each tag consists
    * of a key and optional value. Tag keys must be unique per resource.
    * 
    * <h4>example</h4>
    * 
    * <pre>
    * tagApi.applyToResources(ImmutableMap.of(&quot;group&quot;, &quot;backend&quot;), ImmutableSet.of(&quot;i-1a2b3c4d&quot;));
    * </pre>
    * 
    * @param tags
    *           key to an optional value.
    * @param resourceIds
    *           The ID of a resource to tag. For example, {@code ami-1a2b3c4d}
    * 
    * @see <a href=
    *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-CreateTags.html"
    *      >docs</href>
    */
   void applyToResources(Map<String, String> tags, Iterable<String> resourceIds);

   /**
    * like {@link #applyToResources(Map, Iterable)} except that the tags have no
    * values.
    * 
    * <h4>example</h4>
    * 
    * <pre>
    * tagApi.applyToResources(ImmutableSet.of(&quot;production&quot;, &quot;pci-compliant&quot;), ImmutableSet.of(&quot;i-1a2b3c4d&quot;));
    * </pre>
    * 
    * @see #applyToResources(Map, Iterable)
    */
   void applyToResources(Iterable<String> tags, Iterable<String> resourceIds);

   /**
    * Describes all of your tags for your EC2 resources.
    * 
    * @return tags or empty if there are none
    * @see <a href=
    *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeTags.html"
    *      >docs</href>
    */
   FluentIterable<Tag> list();

   /**
    * Describes tags for your EC2 resources qualified by a filter
    * 
    * <h4>example</h4>
    * 
    * <pre>
    * tags = tagApi.filter(new TagFilterBuilder().image().put(&quot;version&quot;, &quot;1.0&quot;).build());
    * </pre>
    * 
    * @param filter
    *           which is typically built by {@link TagFilterBuilder}
    * @return tags or empty if there are none that match the filter
    * @see <a href=
    *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeTags.html"
    *      >docs</href>
    */
   FluentIterable<Tag> filter(Multimap<String, String> filter);

   /**
    * Deletes a specific set of tags from a specific set of resources. This call
    * is designed to follow a {@link #list() list} or {@link #filter(Multimap)
    * filter} call. You first determine what tags a resource has, and then you
    * call {@link TagApi#deleteFromResources(Iterable, Iterable) delete} with
    * the resource ID and the specific tags you want to delete.
    * 
    * <h4>example</h4>
    * 
    * <pre>
    * tagApi.deleteFromResources(ImmutableSet.of(&quot;Purpose&quot;), ImmutableSet.of(&quot;ami-1a2b3c4d&quot;));
    * </pre>
    * 
    * @param tags
    *           the tag keys
    * @param resourceIds
    *           The ID of a resource with the tag. For example,
    *           {@code ami-1a2b3c4d}
    * @see <a href=
    *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DeleteTags.html"
    *      >docs</href>
    */
   void deleteFromResources(Iterable<String> tags, Iterable<String> resourceIds);

   /**
    * like {@link #deleteFromResources(Iterable, Iterable)}, except that the
    * tags are only deleted if they match the value.
    * 
    * <h4>example</h4>
    * 
    * <pre>
    * tagApi.conditionallyDeleteFromResources(ImmutableMap.of(&quot;Purpose&quot;, &quot;production&quot;), ImmutableSet.of(&quot;ami-1a2b3c4d&quot;));
    * </pre>
    * 
    * @param conditionalTagValues
    *           tag id to value it must match before deleting. For a tag without
    *           a value, supply empty string.
    * @param resourceIds
    *           The ID of a resource with the tag. For example,
    *           {@code ami-1a2b3c4d}
    * @see #deleteFromResources(Iterable, Iterable)
    */
   void conditionallyDeleteFromResources(Map<String, String> conditionalTagValues, Iterable<String> resourceIds);

}
