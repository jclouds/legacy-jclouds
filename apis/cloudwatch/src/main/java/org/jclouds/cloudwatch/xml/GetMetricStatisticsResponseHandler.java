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

import javax.inject.Inject;

import org.jclouds.cloudwatch.domain.Datapoint;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
public class GetMetricStatisticsResponseHandler extends ParseSax.HandlerWithResult<Set<Datapoint>> {

   private Set<Datapoint> datapoints = Sets.newLinkedHashSet();
   private final DatapointHandler datapointHandler;

   @Inject
   public GetMetricStatisticsResponseHandler(DatapointHandler DatapointHandler) {
      this.datapointHandler = DatapointHandler;
   }

   public Set<Datapoint> getResult() {
      return datapoints;
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      datapointHandler.startElement(uri, localName, qName, attributes);
   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {
      datapointHandler.endElement(uri, localName, qName);
      if (qName.equals("member")) {
         this.datapoints.add(datapointHandler.getResult());
      }
   }

   public void characters(char ch[], int start, int length) {
      datapointHandler.characters(ch, start, length);
   }

}
