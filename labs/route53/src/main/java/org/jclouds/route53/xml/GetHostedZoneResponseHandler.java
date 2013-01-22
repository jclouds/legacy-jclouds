/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.route53.xml;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.route53.domain.ZoneAndNameServers;
import org.jclouds.util.SaxUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.inject.Inject;

/**
 * @see <a href=
 *      "http://docs.aws.amazon.com/Route53/latest/APIReference/API_GetHostedZone.html"
 *      />
 * 
 * @author Adrian Cole
 */
public class GetHostedZoneResponseHandler extends ParseSax.HandlerForGeneratedRequestWithResult<ZoneAndNameServers> {

   private final ZoneHandler zoneHandler;

   private StringBuilder currentText = new StringBuilder();
   private ZoneAndNameServers.Builder builder = ZoneAndNameServers.builder();
   private boolean inZone;

   @Inject
   public GetHostedZoneResponseHandler(ZoneHandler zoneHandler) {
      this.zoneHandler = zoneHandler;
   }

   @Override
   public ZoneAndNameServers getResult() {
      try {
         return builder.build();
      } finally {
         builder = ZoneAndNameServers.builder();
      }
   }

   @Override
   public void startElement(String url, String name, String qName, Attributes attributes) throws SAXException {
      if (SaxUtils.equalsOrSuffix(qName, "HostedZone")) {
         inZone = true;
      }
      if (inZone) {
         zoneHandler.startElement(url, name, qName, attributes);
      }
   }

   @Override
   public void endElement(String uri, String name, String qName) throws SAXException {
      if (inZone) {
         if (qName.equals("HostedZone")) {
            inZone = false;
            builder.zone(zoneHandler.getResult());
         } else {
            zoneHandler.endElement(uri, name, qName);
         }
      } else if (qName.equals("NameServer")) {
         builder.addNameServer(SaxUtils.currentOrNull(currentText));
      }

      currentText = new StringBuilder();
   }

   @Override
   public void characters(char ch[], int start, int length) {
      if (inZone) {
         zoneHandler.characters(ch, start, length);
      } else {
         currentText.append(ch, start, length);
      }
   }

}
