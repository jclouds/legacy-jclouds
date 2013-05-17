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
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to Amazon EC2 via the Query API
 * <p/>
 * 
 * @see <a
 *      href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeTags.html"
 *      >doc</a>
 * @see TagApi
 * @author Adrian Cole
 */
@SinceApiVersion("2010-08-31")
@RequestFilters(FormSigner.class)
@VirtualHost
public interface TagAsyncApi {
   /**
    * @see TagApi#applyToResources(Iterable, Iterable)
    * @see <a
    *      href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-CreateTags.html">docs</a>
    */
   @Named("CreateTags")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "CreateTags")
   ListenableFuture<Void> applyToResources(@BinderParam(BindTagsToIndexedFormParams.class) Iterable<String> tags,
         @BinderParam(BindResourceIdsToIndexedFormParams.class) Iterable<String> resourceIds);

   /**
    * @see TagApi#applyToResources(Map, Iterable)
    * @see <a
    *      href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-CreateTags.html">docs</a>
    */
   @Named("CreateTags")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "CreateTags")
   ListenableFuture<Void> applyToResources(@BinderParam(BindTagsToIndexedFormParams.class) Map<String, String> tags,
         @BinderParam(BindResourceIdsToIndexedFormParams.class) Iterable<String> resourceIds);

   /**
    * @see TagApi#list()
    * @see <a
    *      href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeTags.html">docs</a>
    */
   @Named("DescribeTags")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeTags")
   @XMLResponseParser(DescribeTagsResponseHandler.class)
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   ListenableFuture<FluentIterable<Tag>> list();

   /**
    * @see TagApi#filter
    * @see <a
    *      href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeTags.html">docs</a>
    */
   @Named("DescribeTags")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeTags")
   @XMLResponseParser(DescribeTagsResponseHandler.class)
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   ListenableFuture<FluentIterable<Tag>> filter(
         @BinderParam(BindFiltersToIndexedFormParams.class) Multimap<String, String> filter);

   /**
    * @see TagApi#deleteFromResources
    * @see <a
    *      href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DeleteTags.html">docs</a>
    */
   @Named("DeleteTags")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DeleteTags")
   ListenableFuture<Void> deleteFromResources(
         @BinderParam(BindTagKeysToIndexedFormParams.class) Iterable<String> tags,
         @BinderParam(BindResourceIdsToIndexedFormParams.class) Iterable<String> resourceIds);

   /**
    * @see TagApi#conditionallyDeleteFromResources
    * @see <a
    *      href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DeleteTags.html">docs</a>
    */
   @Named("DeleteTags")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DeleteTags")
   ListenableFuture<Void> conditionallyDeleteFromResources(
         @BinderParam(BindTagsToIndexedFormParams.class) Map<String, String> conditionalTagValues,
         @BinderParam(BindResourceIdsToIndexedFormParams.class) Iterable<String> resourceIds);

}
