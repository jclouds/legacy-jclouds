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

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.inject.Inject;
import org.jclouds.cloudwatch.CloudWatchApi;
import org.jclouds.cloudwatch.domain.AlarmHistoryItem;
import org.jclouds.cloudwatch.features.AlarmApi;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.internal.CallerArg0ToPagedIterable;

/**
 * @author Jeremy Whitlock
 */
@Beta
public class ListAlarmHistoryToPagedIterable
      extends CallerArg0ToPagedIterable<AlarmHistoryItem, ListAlarmHistoryToPagedIterable> {

   private final CloudWatchApi api;

   @Inject
   ListAlarmHistoryToPagedIterable(CloudWatchApi api) {
      this.api = checkNotNull(api, "api");
   }

   @Override
   protected Function<Object, IterableWithMarker<AlarmHistoryItem>> markerToNextForCallingArg0(final String arg0) {
      final AlarmApi alarmApi = api.getAlarmApiForRegion(arg0);
      return new Function<Object, IterableWithMarker<AlarmHistoryItem>>() {

         @Override
         public IterableWithMarker<AlarmHistoryItem> apply(Object input) {
            return alarmApi.listHistoryAt(input.toString());
         }

         @Override
         public String toString() {
            return "listHistory(" + arg0 + ")";
         }
      };
   }

}
