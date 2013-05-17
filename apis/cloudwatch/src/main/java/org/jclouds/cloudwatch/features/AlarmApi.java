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
package org.jclouds.cloudwatch.features;

import com.google.common.annotations.Beta;
import com.google.common.collect.FluentIterable;
import org.jclouds.cloudwatch.domain.Alarm;
import org.jclouds.cloudwatch.domain.AlarmHistoryItem;
import org.jclouds.cloudwatch.options.ListAlarmHistoryOptions;
import org.jclouds.cloudwatch.options.ListAlarmsForMetric;
import org.jclouds.cloudwatch.options.ListAlarmsOptions;
import org.jclouds.cloudwatch.options.SaveAlarmOptions;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.javax.annotation.Nullable;

/**
 * Provides access to Amazon CloudWatch via the Query API
 * <p/>
 *
 * @see AlarmAsyncApi
 * @see <a href="http://docs.amazonwebservices.com/AmazonCloudWatch/latest/APIReference" />
 * @author Jeremy Whitlock
 */
@Beta
public interface AlarmApi {

   /**
    * Deletes all specified alarms.
    *
    * <p/>
    * <h3>Note</h3> In the event of an error, no alarms are deleted.
    *
    * @param alarmNames the list of alarms to delete
    */
   void delete(Iterable<String> alarmNames);

   /**
    * Return all history for all alarms.
    *
    * @return the response object
    */
   PagedIterable<AlarmHistoryItem> listHistory();

   /**
    * Return all history based on the options query
    *
    * @return the response object
    */
   PagedIterable<AlarmHistoryItem> listHistory(ListAlarmHistoryOptions options);

   /**
    * Return a single page of history for the specified alarm.
    *
    * @param nextToken the token corresponding with the data you want to get
    *
    * @return the response object
    */
   IterableWithMarker<AlarmHistoryItem> listHistoryAt(String nextToken);

   /**
    * Return all alarms.
    *
    * @return the response object
    */
   PagedIterable<Alarm> list();

   /**
    * Return all alarms based on the options query
    *
    * @param options the options describing the alarms query
    *
    * @return the response object
    */
   PagedIterable<Alarm> list(ListAlarmsOptions options);

   /**
    * Return a single page of alarms based on the options query
    *
    * @param nextToken the token corresponding with the data you want to get
    *
    * @return the response object
    */
   IterableWithMarker<Alarm> listAt(String nextToken);

   /**
    * Return alarms all alarms for a single metric.
    *
    * @param options the options describing the alarms for metric query
    *
    * @return the response object
    */
   FluentIterable<Alarm> listForMetric(ListAlarmsForMetric options);

   /**
    * Disables actions for the specified alarms.
    *
    * @param alarmNames the list of alarms to disable
    */
   void disable(Iterable<String> alarmNames);

   /**
    * Enables actions for the specified alarms.
    *
    * @param alarmNames the list of alarms to enable
    */
   void enable(Iterable<String> alarmNames);

   /**
    * Creates or updates an alarm and associates it with the specified Amazon CloudWatch metric.
    *
    * @param options the options describing the metric alarm to create/update
    */
   void save(SaveAlarmOptions options);

   /**
    * Temporarily sets the state of an alarm.
    *
    * @param alarmName the descriptive name for the alarm
    * @param stateReason the reason that this alarm is set to this specific state (in human-readable text format)
    * @param stateReasonData the reason that this alarm is set to this specific state (in machine-readable JSON format)
    * @param state the value of the state
    */
   void setState(String alarmName, String stateReason, @Nullable String stateReasonData, Alarm.State state);

}
