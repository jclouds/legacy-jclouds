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

import org.jclouds.cloudwatch.domain.Metric;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.IterableWithMarkers;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.util.SaxUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * @see <a href="http://docs.amazonwebservices.com/AmazonCloudWatch/latest/APIReference/API_ListMetrics.html" />
 *
 * @author Jeremy Whitlock
 */
public class ListMetricsResponseHandler extends ParseSax.HandlerForGeneratedRequestWithResult<IterableWithMarker<Metric>> {

   private final MetricHandler metricHandler;

   private StringBuilder currentText = new StringBuilder();
   private Set<Metric> metrics = Sets.newLinkedHashSet();
   private boolean inMetrics;
   private String nextToken;

   @Inject
   public ListMetricsResponseHandler(MetricHandler metricHandler) {
      this.metricHandler = metricHandler;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public IterableWithMarker<Metric> getResult() {
      return IterableWithMarkers.from(metrics, nextToken);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void startElement(String url, String name, String qName, Attributes attributes) throws SAXException {
      if (SaxUtils.equalsOrSuffix(qName, "Metrics")) {
         inMetrics = true;
      }
      if (inMetrics) {
         metricHandler.startElement(url, name, qName, attributes);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void endElement(String uri, String name, String qName) throws SAXException {
      if (inMetrics) {
         if (qName.equals("Metrics")) {
            inMetrics = false;
         } else if (qName.equals("member") && !metricHandler.inDimensions()) {
            metrics.add(metricHandler.getResult());
         } else {
            metricHandler.endElement(uri, name, qName);
         }
      } else if (qName.equals("NextToken")) {
         nextToken = SaxUtils.currentOrNull(currentText);
      }

      currentText = new StringBuilder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void characters(char ch[], int start, int length) {
      if (inMetrics) {
         metricHandler.characters(ch, start, length);
      } else {
         currentText.append(ch, start, length);
      }
   }

}
