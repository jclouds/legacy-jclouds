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

import java.util.Date;

import com.google.common.annotations.Beta;
import org.jclouds.cloudwatch.domain.HistoryItemType;
import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * Options used to describe alarm history.
 *
 * @see <a href="http://docs.aws.amazon.com/AmazonCloudWatch/latest/APIReference/API_DescribeAlarmHistory.html" />
 *
 * @author Jeremy Whitlock
 */
@Beta
public class ListAlarmHistoryOptions extends BaseHttpRequestOptions {

   private static final DateService dateService = new SimpleDateFormatDateService();

   /**
    * The name of the alarm you want to filter against.
    *
    * @param alarmName the name of the alarm you want to filter against
    *
    * @return this {@code ListAlarmHistoryOptions} object
    */
   public ListAlarmHistoryOptions alarmName(String alarmName) {
      checkNotNull(alarmName, "alarmName");
      checkArgument(alarmName.length() <= 255, "alarmName must be between 1 and 255 characters in length");
      formParameters.put("AlarmName", alarmName);
      return this;
   }

   /**
    * The ending date to retrieve alarm history.
    *
    * @param endDate the ending date to retrieve alarm history
    *
    * @return this {@code ListAlarmHistoryOptions} object
    */
   public ListAlarmHistoryOptions endDate(Date endDate) {
      formParameters.put("EndDate", dateService.iso8601DateFormat(checkNotNull(endDate, "endDate")));
      return this;
   }

   /**
    * The type of alarm histories to retrieve.
    *
    * @param historyItemType type of alarm histories to retrieve
    *
    * @return this {@code ListAlarmHistoryOptions} object
    */
   public ListAlarmHistoryOptions historyItemType(HistoryItemType historyItemType) {
      checkNotNull(historyItemType, "historyItemType");
      checkArgument(historyItemType != HistoryItemType.UNRECOGNIZED, "historyItemType unrecognized");
      formParameters.put("HistoryItemType", historyItemType.toString());
      return this;
   }

   /**
    * The maximum number of alarm history records to retrieve.
    *
    * @param maxRecords maximum number of alarm history records to retrieve
    *
    * @return this {@code ListAlarmHistoryOptions} object
    */
   public ListAlarmHistoryOptions maxRecords(int maxRecords) {
      formParameters.put("MaxRecords", checkNotNull(maxRecords, "maxRecords").toString());
      return this;
   }

   /**
    * The starting date to retrieve alarm history.
    *
    * @param startDate the starting date to retrieve alarm history
    *
    * @return this {@code ListAlarmHistoryOptions} object
    */
   public ListAlarmHistoryOptions startDate(Date startDate) {
      formParameters.put("StartDate", dateService.iso8601DateFormat(checkNotNull(startDate, "startDate")));
      return this;
   }

}
