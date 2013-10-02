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

import com.google.common.collect.Sets;
import org.jclouds.cloudwatch.domain.Datapoint;
import org.jclouds.cloudwatch.domain.GetMetricStatisticsResponse;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.util.SaxUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import javax.inject.Inject;
import java.util.Set;

/**
 * @see <a href="http://docs.amazonwebservices.com/AmazonCloudWatch/latest/APIReference/API_GetMetricStatistics.html" />
 *
 * @author Jeremy Whitlock
 */
public class GetMetricStatisticsResponseHandlerV2 extends ParseSax.HandlerWithResult<GetMetricStatisticsResponse> {

   private StringBuilder currentText = new StringBuilder();
   private Set<Datapoint> datapoints = Sets.newLinkedHashSet();
   private String label;
   private final DatapointHandler datapointHandler;
   private boolean inDatapoints;

   @Inject
   public GetMetricStatisticsResponseHandlerV2(DatapointHandler DatapointHandler) {
      this.datapointHandler = DatapointHandler;
   }

   public GetMetricStatisticsResponse getResult() {
      return new GetMetricStatisticsResponse(datapoints, label);
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      if (qName.equals("Datapoints")) {
         inDatapoints = true;
      }
      if (inDatapoints) {
         datapointHandler.startElement(uri, localName, qName, attributes);
      }
   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {
      if (inDatapoints) {
         if (qName.equals("Datapoints")) {
            inDatapoints = false;
         } else {
            datapointHandler.endElement(uri, localName, qName);
            if (qName.equals("member")) {
               this.datapoints.add(datapointHandler.getResult());
            }
         }
      } else if (qName.equals("Label")) {
         label = SaxUtils.currentOrNull(currentText);
      }

      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      if (inDatapoints) {
         datapointHandler.characters(ch, start, length);
      } else {
         currentText.append(ch, start, length);
      }
   }

}
