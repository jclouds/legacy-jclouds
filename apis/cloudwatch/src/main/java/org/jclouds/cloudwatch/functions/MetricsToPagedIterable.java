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
package org.jclouds.cloudwatch.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;

import org.jclouds.cloudwatch.CloudWatchApi;
import org.jclouds.cloudwatch.domain.Metric;
import org.jclouds.cloudwatch.features.MetricApi;
import org.jclouds.cloudwatch.options.ListMetricsOptions;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.internal.CallerArg0ToPagedIterable;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Beta
public class MetricsToPagedIterable extends CallerArg0ToPagedIterable<Metric, MetricsToPagedIterable> {

   private final CloudWatchApi api;

   @Inject
   protected MetricsToPagedIterable(CloudWatchApi api) {
      this.api = checkNotNull(api, "api");
   }

   @Override
   protected Function<Object, IterableWithMarker<Metric>> markerToNextForCallingArg0(final String arg0) {
      final MetricApi metricApi = api.getMetricApiForRegion(arg0);
      return new Function<Object, IterableWithMarker<Metric>>() {

         @Override
         public IterableWithMarker<Metric> apply(Object input) {
            return metricApi.list(ListMetricsOptions.Builder.afterMarker(input.toString()));
         }

         @Override
         public String toString() {
            return "listMetricsInRegion(" + arg0 + ")";
         }
      };
   }

}
