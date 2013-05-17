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
package org.jclouds.sqs;

import java.io.Closeable;
import java.net.URI;
import java.util.Set;

import org.jclouds.aws.filters.FormSigner;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.Region;
import org.jclouds.location.functions.RegionToEndpointOrProviderIfNull;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.sqs.features.MessageAsyncApi;
import org.jclouds.sqs.features.PermissionAsyncApi;
import org.jclouds.sqs.features.QueueAsyncApi;

import com.google.common.annotations.Beta;
import com.google.inject.Provides;

/**
 * Provides access to SQS via REST API.
 * <p/>
 * 
 * @see <a
 *      href="http://docs.amazonwebservices.com/AWSSimpleQueueService/2011-10-01/APIReference/Welcome.html">SQS
 *      documentation</a>
 * @author Adrian Cole
 * @deprecated please use {@code org.jclouds.ContextBuilder#buildApi(SQSApi.class)} as
 *             {@link SQSAsyncApi} interface will be removed in jclouds 1.7.
 */
@Deprecated
@Beta
@RequestFilters(FormSigner.class)
@VirtualHost
public interface SQSAsyncApi extends Closeable {
   /**
    * 
    * @return the Region codes configured
    */
   @Provides
   @Region
   Set<String> getConfiguredRegions();

   /**
    * Provides asynchronous access to Queue features.
    */
   @Delegate
   QueueAsyncApi getQueueApi();

   @Delegate
   QueueAsyncApi getQueueApiForRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region);

   /**
    * Provides asynchronous access to Message features.
    */
   @Delegate
   MessageAsyncApi getMessageApiForQueue(@EndpointParam URI queue);

   /**
    * Provides asynchronous access to Permission features.
    */
   @Delegate
   PermissionAsyncApi getPermissionApiForQueue(@EndpointParam URI queue);

}
