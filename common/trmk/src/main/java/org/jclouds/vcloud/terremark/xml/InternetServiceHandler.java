/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.vcloud.terremark.xml;

import java.net.URI;

import javax.annotation.Resource;

import org.jclouds.http.functions.ParseSax.HandlerWithResult;
import org.jclouds.logging.Logger;
import org.jclouds.vcloud.terremark.domain.InternetService;
import org.jclouds.vcloud.terremark.domain.Protocol;
import org.jclouds.vcloud.terremark.domain.PublicIpAddress;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Adrian Cole
 */
public class InternetServiceHandler extends HandlerWithResult<InternetService> {

   @Resource
   protected Logger logger = Logger.NULL;
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

   protected String currentOrNull() {
      String returnVal = currentText.toString().trim();
      return returnVal.equals("") ? null : returnVal;
   }

   @Override
   public InternetService getResult() {
      return new InternetService(serviceName, location, publicIpAddress, port, protocol, enabled, timeout, description);
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      if (qName.equals("PublicIpAddress")) {
         inPublicIpAddress = true;
      }
   }

   public void endElement(String uri, String name, String qName) {
      String current = currentOrNull();
      if (qName.equals("PublicIpAddress")) {
         publicIpAddress = new PublicIpAddress(address, addressLocation);
         address = null;
         addressLocation = null;
         inPublicIpAddress = false;
      } else if (current != null) {
         if (qName.equals("Href")) {
            if (inPublicIpAddress)
               addressLocation = URI.create(current);
            else
               location = URI.create(current);
         } else if (qName.equals("Name")) {
            if (inPublicIpAddress)
               address = current;
            else
               serviceName = current;
         } else if (qName.equals("Port")) {
            port = Integer.parseInt(current);
         } else if (qName.equals("Protocol")) {
            protocol = Protocol.valueOf(current);
         } else if (qName.equals("Enabled")) {
            enabled = Boolean.parseBoolean(current);
         } else if (qName.equals("Timeout")) {
            timeout = Integer.parseInt(current);
         } else if (qName.equals("Description")) {
            description = current;
         }
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

}