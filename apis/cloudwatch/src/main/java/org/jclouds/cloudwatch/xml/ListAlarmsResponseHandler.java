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
import org.jclouds.cloudwatch.domain.Alarm;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.IterableWithMarkers;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.util.SaxUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @see <a href="http://docs.aws.amazon.com/AmazonCloudWatch/latest/APIReference/API_DescribeAlarms.html" />
 *
 * @author Jeremy Whitlock
 */
@Beta
public class ListAlarmsResponseHandler
      extends ParseSax.HandlerForGeneratedRequestWithResult<IterableWithMarker<Alarm>> {

   private final MetricAlarmHandler metricAlarmHandler;

   private StringBuilder currentText = new StringBuilder();
   private Set<Alarm> alarms = Sets.newLinkedHashSet();
   private String nextToken;
   private boolean inMetricAlarms;

   @Inject
   public ListAlarmsResponseHandler(MetricAlarmHandler metricAlarmHandler) {
      this.metricAlarmHandler = metricAlarmHandler;
   }

   @Override
   public void startElement(String url, String name, String qName, Attributes attributes) throws SAXException {
      if (SaxUtils.equalsOrSuffix(qName, "MetricAlarms")) {
         inMetricAlarms = true;
      }
      if (inMetricAlarms) {
         metricAlarmHandler.startElement(url, name, qName, attributes);
      }
   }

   @Override
   public void endElement(String uri, String name, String qName) throws SAXException {
      if (inMetricAlarms) {
         if (qName.equals("MetricAlarms")) {
            inMetricAlarms = false;
         } else if (qName.equals("member") && !metricAlarmHandler.shouldHandleMemberTag()) {
            alarms.add(metricAlarmHandler.getResult());
         } else {
            metricAlarmHandler.endElement(uri, name, qName);
         }
      } else if (qName.equals("NextToken")) {
         nextToken = SaxUtils.currentOrNull(currentText);
      }

      currentText = new StringBuilder();
   }

   @Override
   public void characters(char ch[], int start, int length) {
      if (inMetricAlarms) {
         metricAlarmHandler.characters(ch, start, length);
      } else {
         currentText.append(ch, start, length);
      }
   }

   @Override
   public IterableWithMarker<Alarm> getResult() {
      IterableWithMarker<Alarm> result = IterableWithMarkers.from(alarms, nextToken);

      alarms = Sets.newLinkedHashSet();
      nextToken = null;

      return result;
   }

}
