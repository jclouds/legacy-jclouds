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
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.Region;
import org.jclouds.location.functions.RegionToEndpointOrProviderIfNull;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.sqs.features.MessageApi;
import org.jclouds.sqs.features.PermissionApi;
import org.jclouds.sqs.features.QueueApi;

import com.google.common.annotations.Beta;
import com.google.inject.Provides;

/**
 * Provides access to SQS via their REST API.
 * <p/>
 * 
 * @author Adrian Cole
 * @see SQSAsyncApi
 */
@Beta
public interface SQSApi extends Closeable {
   
   /**
    * 
    * @return the Region codes configured
    */
   @Provides
   @Region
   Set<String> getConfiguredRegions();

   /**
    * Provides synchronous access to Queue features.
    */
   @Delegate
   QueueApi getQueueApi();

   @Delegate
   QueueApi getQueueApiForRegion(@EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region);

   /**
    * Provides synchronous access to Message features.
    */
   @Delegate
   MessageApi getMessageApiForQueue(@EndpointParam URI queue);

   /**
    * Provides synchronous access to Permission features.
    */
   @Delegate
   PermissionApi getPermissionApiForQueue(@EndpointParam URI queue);

}
