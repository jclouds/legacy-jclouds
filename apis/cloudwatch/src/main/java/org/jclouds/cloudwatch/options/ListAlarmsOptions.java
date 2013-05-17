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
package org.jclouds.cloudwatch.options;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.jclouds.cloudwatch.domain.Alarm;
import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * Options used to describe alarms.
 *
 * @see <a href="http://docs.aws.amazon.com/AmazonCloudWatch/latest/APIReference/API_DescribeAlarms.html" />
 *
 * @author Jeremy Whitlock
 */
public class ListAlarmsOptions extends BaseHttpRequestOptions {

   int alarmIndex = 1;

   /**
    * The action name prefix.
    *
    * @param actionPrefix the action name prefix
    *
    * @return this {@code ListAlarmsOptions} object
    */
   public ListAlarmsOptions actionPrefix(String actionPrefix) {
      checkNotNull(actionPrefix, "actionPrefix");
      checkArgument(actionPrefix.length() <= 1024, "actionPrefix must be between 1 and 1024 characters in length");
      formParameters.put("ActionPrefix", actionPrefix);
      return this;
   }

   /**
    * The alarm name prefix.
    *
    * @param alarmNamePrefix the alarm name prefix
    *
    * @return this {@code ListAlarmsOptions} object
    */
   public ListAlarmsOptions alarmNamePrefix(String alarmNamePrefix) {
      checkNotNull(alarmNamePrefix, "alarmNamePrefix");
      checkArgument(alarmNamePrefix.length() <= 255, "actionPrefix must be between 1 and 255 characters in length");
      formParameters.put("AlarmNamePrefix", alarmNamePrefix);
      return this;
   }

   /**
    * The list of alarm names to retrieve information for.
    *
    * @param alarmNames the alarm names
    *
    * @return this {@code ListAlarmsOptions} object
    */
   public ListAlarmsOptions alarmNames(Set<String> alarmNames) {
      for (String alarmName : checkNotNull(alarmNames, "alarmNames")) {
         alarmName(alarmName);
      }
      return this;
   }

   /**
    * The alarm name to retrieve information for.
    *
    * @param alarmName the alarm name
    *
    * @return this {@code ListAlarmsOptions} object
    */
   public ListAlarmsOptions alarmName(String alarmName) {
      checkArgument(alarmIndex <= 100, "maximum number of alarm names is 100");
      formParameters.put("AlarmNames.member." + alarmIndex, checkNotNull(alarmName, "alarmName"));
      alarmIndex++;
      return this;
   }

   /**
    * The maximum number of alarm descriptions to retrieve.
    *
    * @param maxRecords  maximum number of alarm descriptions to retrieve
    *
    * @return this {@code ListAlarmsOptions} object
    */
   public ListAlarmsOptions maxRecords(int maxRecords) {
      formParameters.put("MaxRecords", checkNotNull(maxRecords, "maxRecords").toString());
      return this;
   }

   /**
    * The state value to be used in matching alarms.
    *
    * @param state state value to be used in matching alarms
    *
    * @return this {@code ListAlarmsOptions} object
    */
   public ListAlarmsOptions state(Alarm.State state) {
      checkNotNull(state, "state");
      checkArgument(state != Alarm.State.UNRECOGNIZED, "state unrecognized");
      formParameters.put("StateValue", state.toString());
      return this;
   }

}
