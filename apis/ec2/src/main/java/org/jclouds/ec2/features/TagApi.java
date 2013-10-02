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

import static org.jclouds.aws.reference.FormParameters.ACTION;

import java.util.Map;

import javax.inject.Named;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.Fallbacks.EmptyFluentIterableOnNotFoundOr404;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.ec2.binders.BindFiltersToIndexedFormParams;
import org.jclouds.ec2.binders.BindResourceIdsToIndexedFormParams;
import org.jclouds.ec2.binders.BindTagKeysToIndexedFormParams;
import org.jclouds.ec2.binders.BindTagsToIndexedFormParams;
import org.jclouds.ec2.domain.Tag;
import org.jclouds.ec2.xml.DescribeTagsResponseHandler;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SinceApiVersion;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Multimap;
/**
 * Provides access to Amazon EC2 via the Query API
 * <p/>
 * 
 * @see <a
 *      href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeTags.html"
 *      >doc</a>
 * @author Adrian Cole
 */
@SinceApiVersion("2010-08-31")
@RequestFilters(FormSigner.class)
@VirtualHost
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
   @Named("CreateTags")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "CreateTags")
   void applyToResources(@BinderParam(BindTagsToIndexedFormParams.class) Iterable<String> tags,
         @BinderParam(BindResourceIdsToIndexedFormParams.class) Iterable<String> resourceIds);

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
   @Named("CreateTags")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "CreateTags")
   void applyToResources(@BinderParam(BindTagsToIndexedFormParams.class) Map<String, String> tags,
         @BinderParam(BindResourceIdsToIndexedFormParams.class) Iterable<String> resourceIds);

   /**
    * Describes all of your tags for your EC2 resources.
    * 
    * @return tags or empty if there are none
    * @see <a href=
    *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeTags.html"
    *      >docs</href>
    */
   @Named("DescribeTags")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeTags")
   @XMLResponseParser(DescribeTagsResponseHandler.class)
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
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
   @Named("DescribeTags")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeTags")
   @XMLResponseParser(DescribeTagsResponseHandler.class)
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   FluentIterable<Tag> filter(
         @BinderParam(BindFiltersToIndexedFormParams.class) Multimap<String, String> filter);

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
   @Named("DeleteTags")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DeleteTags")
   void deleteFromResources(
         @BinderParam(BindTagKeysToIndexedFormParams.class) Iterable<String> tags,
         @BinderParam(BindResourceIdsToIndexedFormParams.class) Iterable<String> resourceIds);

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
   @Named("DeleteTags")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DeleteTags")
   void conditionallyDeleteFromResources(
         @BinderParam(BindTagsToIndexedFormParams.class) Map<String, String> conditionalTagValues,
         @BinderParam(BindResourceIdsToIndexedFormParams.class) Iterable<String> resourceIds);

}
