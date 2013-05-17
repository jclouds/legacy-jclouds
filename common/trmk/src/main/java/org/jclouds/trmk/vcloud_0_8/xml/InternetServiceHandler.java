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
package org.jclouds.trmk.vcloud_0_8.xml;

import static org.jclouds.util.SaxUtils.currentOrNull;
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import java.net.URI;

import org.jclouds.http.functions.ParseSax.HandlerWithResult;
import org.jclouds.trmk.vcloud_0_8.domain.InternetService;
import org.jclouds.trmk.vcloud_0_8.domain.Protocol;
import org.jclouds.trmk.vcloud_0_8.domain.PublicIpAddress;
import org.xml.sax.Attributes;

/**
 * @author Adrian Cole
 */
public class InternetServiceHandler extends HandlerWithResult<InternetService> {

   private StringBuilder currentText = new StringBuilder();

   private boolean inPublicIpAddress;
   private URI location;
   private URI addressLocation;
   private String serviceName;
   private String address;
   private PublicIpAddress publicIpAddress;
   private int port;
   private String description;
   private int timeout;
   private boolean enabled;
   private Protocol protocol;

   protected int depth = 0;

   private int thisDepth;

   @Override
   public InternetService getResult() {
      return new InternetService(serviceName, location, publicIpAddress, port, protocol, enabled, timeout, description);
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) {
      depth++;
      if (equalsOrSuffix(qName, "InternetService")) {
         thisDepth = depth;
      } else if (equalsOrSuffix(qName, "PublicIpAddress")) {
         inPublicIpAddress = true;
      }
   }

   public void endElement(String uri, String name, String qName) {
      depth--;
      if (equalsOrSuffix(qName, "PublicIpAddress")) {
         inPublicIpAddress = false;
         publicIpAddress = new PublicIpAddress(address, addressLocation);
         address = null;
         addressLocation = null;
      } else {
         String value = currentOrNull(currentText);
         if (value != null && !value.equals("")) {
            if (depth == thisDepth) {
               if (equalsOrSuffix(qName, "Href")) {
                  location = URI.create(value);
               } else if (equalsOrSuffix(qName, "Name")) {
                  serviceName = value;
               } else if (equalsOrSuffix(qName, "Port")) {
                  port = Integer.parseInt(value);
               } else if (equalsOrSuffix(qName, "Protocol")) {
                  protocol = Protocol.valueOf(value);
               } else if (equalsOrSuffix(qName, "Enabled")) {
                  enabled = Boolean.parseBoolean(value);
               } else if (equalsOrSuffix(qName, "Timeout")) {
                  timeout = Integer.parseInt(value);
               } else if (equalsOrSuffix(qName, "Description")) {
                  description = currentOrNull(currentText);
               }
            } else if (inPublicIpAddress) {
               if (equalsOrSuffix(qName, "Href")) {
                  addressLocation = URI.create(value);
               } else if (equalsOrSuffix(qName, "Name")) {
                  address = value;
               }
            }
         }
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

}
