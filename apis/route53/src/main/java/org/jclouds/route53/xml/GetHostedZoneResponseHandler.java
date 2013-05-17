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
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.route53.domain.HostedZone;
import org.jclouds.route53.domain.HostedZoneAndNameServers;
import org.xml.sax.Attributes;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.inject.Inject;

/**
 * @see <a href=
 *      "http://docs.aws.amazon.com/Route53/latest/APIReference/API_GetHostedZone.html"
 *      />
 * 
 * @author Adrian Cole
 */
public class GetHostedZoneResponseHandler extends ParseSax.HandlerForGeneratedRequestWithResult<HostedZoneAndNameServers> {

   private final HostedZoneHandler zoneHandler;

   private StringBuilder currentText = new StringBuilder();
   
   private boolean inHostedZone;

   private HostedZone zone;
   private Builder<String> nameServers = ImmutableList.<String> builder();

   @Inject
   public GetHostedZoneResponseHandler(HostedZoneHandler zoneHandler) {
      this.zoneHandler = zoneHandler;
   }

   @Override
   public HostedZoneAndNameServers getResult() {
      try {
         return HostedZoneAndNameServers.create(zone, nameServers.build());
      } finally {
         zone = null;
         nameServers = ImmutableList.<String> builder();
      }
   }

   @Override
   public void startElement(String url, String name, String qName, Attributes attributes) {
      if (equalsOrSuffix(qName, "HostedZone")) {
         inHostedZone = true;
      }
      if (inHostedZone) {
         zoneHandler.startElement(url, name, qName, attributes);
      }
   }

   @Override
   public void endElement(String uri, String name, String qName) {
      if (inHostedZone) {
         if (qName.equals("HostedZone")) {
            inHostedZone = false;
            zone = zoneHandler.getResult();
         } else {
            zoneHandler.endElement(uri, name, qName);
         }
      } else if (qName.equals("NameServer")) {
         nameServers.add(currentOrNull(currentText));
      }

      currentText = new StringBuilder();
   }

   @Override
   public void characters(char ch[], int start, int length) {
      if (inHostedZone) {
         zoneHandler.characters(ch, start, length);
      } else {
         currentText.append(ch, start, length);
      }
   }

}
