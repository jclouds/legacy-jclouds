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
import com.google.inject.Inject;
import org.jclouds.cloudwatch.domain.Dimension;
import org.jclouds.cloudwatch.domain.Metric;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.util.SaxUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.Set;

/**
 * @see <a href="http://docs.amazonwebservices.com/AmazonCloudWatch/latest/APIReference/API_Metric.html" />
 *
 * @author Jeremy Whitlock
 */
public class MetricHandler extends ParseSax.HandlerForGeneratedRequestWithResult<Metric> {

   private final DimensionHandler dimensionHandler;

   private StringBuilder currentText = new StringBuilder();
   private Set<Dimension> dimensions = Sets.newLinkedHashSet();
   private boolean inDimensions;
   private String metricName;
   private String namespace;

   @Inject
   public MetricHandler(DimensionHandler dimensionHandler) {
      this.dimensionHandler = dimensionHandler;
   }

    public boolean inDimensions() {
        return inDimensions;
    }

   /**
    * {@inheritDoc}
    */
   @Override
   public Metric getResult() {
      Metric metric = new Metric(metricName, namespace, dimensions);

      // Reset since this handler is created once but produces N results
      dimensions = Sets.newLinkedHashSet();
      metricName = null;
      namespace = null;

      return metric;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void startElement(String url, String name, String qName, Attributes attributes) throws SAXException {
      if (!inDimensions && SaxUtils.equalsOrSuffix(qName, "member")) {
         inDimensions = true;
      }
      if (inDimensions) {
         dimensionHandler.startElement(url, name, qName, attributes);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void endElement(String uri, String name, String qName) throws SAXException {
      if (inDimensions) {
         if (qName.equals("Dimensions")) {
            inDimensions = false;
         } else if (qName.equals("member")) {
            dimensions.add(dimensionHandler.getResult());
         } else {
            dimensionHandler.endElement(uri, name, qName);
         }
      } else if (qName.equals("MetricName")) {
         metricName = SaxUtils.currentOrNull(currentText);
      } else if (qName.equals("Namespace")) {
         namespace = SaxUtils.currentOrNull(currentText);
      }

      currentText = new StringBuilder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void characters(char ch[], int start, int length) {
      if (inDimensions) {
         dimensionHandler.characters(ch, start, length);
      } else {
         currentText.append(ch, start, length);
      }
   }

}
