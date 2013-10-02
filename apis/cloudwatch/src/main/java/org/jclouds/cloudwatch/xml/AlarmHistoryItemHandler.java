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
package org.jclouds.cloudwatch.xml;

import javax.inject.Inject;
import java.util.Date;

import com.google.common.annotations.Beta;
import org.jclouds.cloudwatch.domain.AlarmHistoryItem;
import org.jclouds.cloudwatch.domain.HistoryItemType;
import org.jclouds.date.DateService;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.util.SaxUtils;
import org.xml.sax.SAXException;

/**
 * @see <a href="http://docs.aws.amazon.com/AmazonCloudWatch/latest/APIReference/API_AlarmHistoryItem.html" />
 *
 * @author Jeremy Whitlock
 */
@Beta
public class AlarmHistoryItemHandler extends ParseSax.HandlerForGeneratedRequestWithResult<AlarmHistoryItem> {

   protected final DateService dateService;

   private StringBuilder currentText = new StringBuilder();
   private String alarmName;
   private String historyData;
   private HistoryItemType historyItemType;
   private String historySummary;
   private Date timestamp;

   @Inject
   public AlarmHistoryItemHandler(DateService dateService) {
      this.dateService = dateService;
   }

   @Override
   public void endElement(String uri, String name, String qName) throws SAXException {
      if (qName.equals("AlarmName")) {
         alarmName = SaxUtils.currentOrNull(currentText);
      } else if (qName.equals("HistoryData")) {
         String rawJson = SaxUtils.currentOrNull(currentText);

         if (rawJson != null) {
            historyData = rawJson.trim();
         }
      } else if (qName.equals("HistoryItemType")) {
         historyItemType = HistoryItemType.fromValue(SaxUtils.currentOrNull(currentText));
      } else if (qName.equals("HistorySummary")) {
         historySummary = SaxUtils.currentOrNull(currentText);
      } else if (qName.equals("Timestamp")) {
         timestamp = dateService.iso8601DateParse(currentText.toString().trim());
      }

      currentText = new StringBuilder();
   }

   @Override
   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

   @Override
   public AlarmHistoryItem getResult() {
      AlarmHistoryItem result = new AlarmHistoryItem(alarmName, historyData, historyItemType, historySummary,
                                                     timestamp);

      alarmName = null;
      historyData = null;
      historyItemType = null;
      historySummary = null;
      timestamp = null;

      return result;
   }

}
