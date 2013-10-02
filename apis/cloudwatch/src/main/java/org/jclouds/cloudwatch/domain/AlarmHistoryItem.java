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
package org.jclouds.cloudwatch.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;

/**
 * @see <a href="http://docs.aws.amazon.com/AmazonCloudWatch/latest/APIReference/API_AlarmHistoryItem.html" />
 *
 * @author Jeremy Whitlock
 */
@Beta
public class AlarmHistoryItem {

   private final String alarmName;
   private final String historyData;
   private final HistoryItemType historyItemType;
   private final String historySummary;
   private final Date timestamp;

   public AlarmHistoryItem(String alarmName, String historyData, HistoryItemType historyItemType,
                           String historySummary, Date timestamp) {
      this.alarmName = checkNotNull(alarmName, "alarmName");
      this.historyData = checkNotNull(historyData, "historyData for %s", alarmName);
      this.historyItemType = checkNotNull(historyItemType, "historyItemType for %s", alarmName);
      this.historySummary = checkNotNull(historySummary, "historySummary for %s", alarmName);
      this.timestamp = checkNotNull(timestamp, "timestamp for %s", alarmName);
   }

   /**
    * return the descriptive name for the alarm
    */
   public String getAlarmName() {
      return alarmName;
   }

   /**
    * return the machine-readable data about the alarm in JSON format
    */
   public String getHistoryData() {
      return historyData;
   }

   /**
    * return the type of alarm history item
    */
   public HistoryItemType getHistoryItemType() {
      return historyItemType;
   }

   /**
    * return the human-readable summary of the alarm history
    */
   public String getHistorySummary() {
      return historySummary;
   }

   /**
    * return the time stamp for the alarm history item
    */
   public Date getTimestamp() {
      return timestamp;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(alarmName, historyData, historyItemType, historySummary, timestamp);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      AlarmHistoryItem other = (AlarmHistoryItem) obj;
      return equal(this.alarmName, other.alarmName) &&
            equal(this.historyData, other.historyData) &&
            equal(this.historyItemType, other.historyItemType) &&
            equal(this.historySummary, other.historySummary) &&
            equal(this.timestamp, other.timestamp);
   }

   @Override
   public String toString() {
      return toStringHelper(this)
            .add("alarmName", alarmName)
            .add("historyData", historyData)
            .add("historyItemType", historyItemType)
            .add("historySummary", historySummary)
            .add("timestamp", timestamp).toString();
   }

}
