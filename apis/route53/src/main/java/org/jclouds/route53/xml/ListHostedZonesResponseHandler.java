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

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.IterableWithMarkers;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.route53.domain.HostedZone;
import org.xml.sax.Attributes;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.inject.Inject;

/**
 * @see <a href=
 *      "http://docs.aws.amazon.com/Route53/latest/APIReference/API_ListHostedZones.html"
 *      />
 * 
 * @author Adrian Cole
 */
public class ListHostedZonesResponseHandler extends
      ParseSax.HandlerForGeneratedRequestWithResult<IterableWithMarker<HostedZone>> {

   private final HostedZoneHandler zoneHandler;

   private StringBuilder currentText = new StringBuilder();
   private Builder<HostedZone> zones = ImmutableList.<HostedZone> builder();
   private boolean inHostedZones;
   private String afterMarker;

   @Inject
   public ListHostedZonesResponseHandler(HostedZoneHandler zoneHandler) {
      this.zoneHandler = zoneHandler;
   }

   @Override
   public IterableWithMarker<HostedZone> getResult() {
      try {
         return IterableWithMarkers.from(zones.build(), afterMarker);
      } finally {
         zones = ImmutableList.<HostedZone> builder();
      }
   }

   @Override
   public void startElement(String url, String name, String qName, Attributes attributes) {
      if (equalsOrSuffix(qName, "HostedZones")) {
         inHostedZones = true;
      }
      if (inHostedZones) {
         zoneHandler.startElement(url, name, qName, attributes);
      }
   }

   @Override
   public void endElement(String uri, String name, String qName) {
      if (inHostedZones) {
         if (qName.equals("HostedZones")) {
            inHostedZones = false;
         } else if (qName.equals("HostedZone")) {
            zones.add(zoneHandler.getResult());
         } else {
            zoneHandler.endElement(uri, name, qName);
         }
      } else if (qName.equals("NextMarker")) {
         afterMarker = currentOrNull(currentText);
      }

      currentText = new StringBuilder();
   }

   @Override
   public void characters(char ch[], int start, int length) {
      if (inHostedZones) {
         zoneHandler.characters(ch, start, length);
      } else {
         currentText.append(ch, start, length);
      }
   }
}
