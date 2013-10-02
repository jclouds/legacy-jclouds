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

import java.util.Set;

import com.google.common.annotations.Beta;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import org.jclouds.cloudwatch.domain.AlarmHistoryItem;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.IterableWithMarkers;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.util.SaxUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @see <a href="http://docs.aws.amazon.com/AmazonCloudWatch/latest/APIReference/API_DescribeAlarmHistory.html" />
 *
 * @author Jeremy Whitlock
 */
@Beta
public class ListAlarmHistoryResponseHandler
      extends ParseSax.HandlerForGeneratedRequestWithResult<IterableWithMarker<AlarmHistoryItem>> {

   private final AlarmHistoryItemHandler alarmHistoryItemHandler;

   private StringBuilder currentText = new StringBuilder();
   private Set<AlarmHistoryItem> alarmHistoryItems = Sets.newLinkedHashSet();
   private String nextToken;
   private boolean inAlarmHistoryItems;

   @Inject
   public ListAlarmHistoryResponseHandler(AlarmHistoryItemHandler alarmHistoryItemHandler) {
      this.alarmHistoryItemHandler = alarmHistoryItemHandler;
   }

   @Override
   public void startElement(String url, String name, String qName, Attributes attributes) throws SAXException {
      if (SaxUtils.equalsOrSuffix(qName, "AlarmHistoryItems")) {
         inAlarmHistoryItems = true;
      }
      if (inAlarmHistoryItems) {
         alarmHistoryItemHandler.startElement(url, name, qName, attributes);
      }
   }

   @Override
   public void endElement(String uri, String name, String qName) throws SAXException {
      if (inAlarmHistoryItems) {
         if (qName.equals("AlarmHistoryItems")) {
            inAlarmHistoryItems = false;
         } else if (qName.equals("member")) {
            alarmHistoryItems.add(alarmHistoryItemHandler.getResult());
         } else {
            alarmHistoryItemHandler.endElement(uri, name, qName);
         }
      } else if (qName.equals("NextToken")) {
         nextToken = SaxUtils.currentOrNull(currentText);
      }

      currentText = new StringBuilder();
   }

   @Override
   public void characters(char ch[], int start, int length) {
      if (inAlarmHistoryItems) {
         alarmHistoryItemHandler.characters(ch, start, length);
      } else {
         currentText.append(ch, start, length);
      }
   }

   @Override
   public IterableWithMarker<AlarmHistoryItem> getResult() {
      IterableWithMarker<AlarmHistoryItem> result = IterableWithMarkers.from(alarmHistoryItems, nextToken);

      alarmHistoryItems = Sets.newLinkedHashSet();
      nextToken = null;

      return result;
   }

}
