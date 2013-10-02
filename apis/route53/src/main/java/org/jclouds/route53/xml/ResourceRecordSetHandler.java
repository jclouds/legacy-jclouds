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
package org.jclouds.route53.xml;

import static org.jclouds.util.SaxUtils.currentOrNull;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.route53.domain.ResourceRecordSet;
import org.xml.sax.Attributes;


/**
 * 
 * @author Adrian Cole
 */
public class ResourceRecordSetHandler extends ParseSax.HandlerForGeneratedRequestWithResult<ResourceRecordSet> {

   private StringBuilder currentText = new StringBuilder();
   private ResourceRecordSet.Builder builder = ResourceRecordSet.builder();

   @Override
   public ResourceRecordSet getResult() {
      try {
         return builder.build();
      } finally {
         builder = ResourceRecordSet.builder();
      }
   }

   @Override
   public void startElement(String url, String name, String qName, Attributes attributes) {
   }

   @Override
   public void endElement(String uri, String name, String qName) {
      if (qName.equals("Name")) {
         builder.name(currentOrNull(currentText));
      } else if (qName.equals("Type")) {
         builder.type(currentOrNull(currentText));
      } else if (qName.equals("TTL")) {
         builder.ttl(Integer.parseInt(currentOrNull(currentText)));
      } else if (qName.equals("Value")) {
         builder.add(currentOrNull(currentText));
      } else if (qName.equals("HostedZoneId")) {
         builder.zoneId(currentOrNull(currentText));
      } else if (qName.equals("SetIdentifier")) {
         builder.id(currentOrNull(currentText));
      } else if (qName.equals("DNSName")) {
         builder.dnsName(currentOrNull(currentText));
      } else if (qName.equals("Weight")) {
         builder.weight(Integer.parseInt(currentOrNull(currentText)));
      } else if (qName.equals("Region")) {
         builder.region(currentOrNull(currentText));
      }
      currentText = new StringBuilder();
   }

   @Override
   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
