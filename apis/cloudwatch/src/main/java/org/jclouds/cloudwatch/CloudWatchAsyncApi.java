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
package org.jclouds.cloudwatch;

import java.io.Closeable;
import java.util.Set;

import com.google.inject.Provides;
import org.jclouds.cloudwatch.features.AlarmAsyncApi;
import org.jclouds.cloudwatch.features.MetricAsyncApi;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.Region;
import org.jclouds.location.functions.RegionToEndpointOrProviderIfNull;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;

/**
 * Provides access to Amazon CloudWatch via the Query API
 * <p/>
 * 
 * @see <a
 *      href="http://docs.amazonwebservices.com/AmazonCloudWatch/latest/APIReference"
 *      />
 * @author Adrian Cole
 * @deprecated please use {@code org.jclouds.ContextBuilder#buildApi(CloudWatchApi.class)} as
 *             {@link CloudWatchAsyncApi} interface will be removed in jclouds 1.7.
 */
@Deprecated
public interface CloudWatchAsyncApi extends Closeable {
   /**
    * 
    * @return the Region codes configured
    */
   @Provides
   @Region
   Set<String> getConfiguredRegions();

   /**
    * Provides asynchronous access to Metric features.
    */
   @Delegate
   MetricAsyncApi getMetricApi();

   /**
    * Provides asynchronous access to Metric features.
    */
   @Delegate
   MetricAsyncApi getMetricApiForRegion(@EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region);

   /**
    * Provides asynchronous access to Alarm features.
    */
   @Delegate
   AlarmAsyncApi getAlarmApi();

   /**
    * Provides asynchronous access to Metric features.
    */
   @Delegate
   AlarmAsyncApi getAlarmApiForRegion(@EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region);
}
