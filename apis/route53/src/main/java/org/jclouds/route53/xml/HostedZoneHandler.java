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
import org.jclouds.route53.domain.HostedZone;
import org.xml.sax.Attributes;


/**
 * 
 * @author Adrian Cole
 */
public class HostedZoneHandler extends ParseSax.HandlerForGeneratedRequestWithResult<HostedZone> {

   private StringBuilder currentText = new StringBuilder();
   private HostedZone.Builder builder = HostedZone.builder();

   @Override
   public HostedZone getResult() {
      try {
         return builder.build();
      } finally {
         builder = HostedZone.builder();
      }
   }

   @Override
   public void startElement(String url, String name, String qName, Attributes attributes) {
   }

   @Override
   public void endElement(String uri, String name, String qName) {
      if (qName.equals("Id")) {
         builder.id(currentOrNull(currentText).replace("/hostedzone/", ""));
      } else if (qName.equals("Name")) {
         builder.name(currentOrNull(currentText));
      } else if (qName.equals("CallerReference")) {
         builder.callerReference(currentOrNull(currentText));
      } else if (qName.equals("Comment")) {
         builder.comment(currentOrNull(currentText));
      } else if (qName.equals("ResourceRecordSetCount")) {
         builder.resourceRecordSetCount(Integer.parseInt(currentOrNull(currentText)));
      }
      currentText = new StringBuilder();
   }

   @Override
   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
