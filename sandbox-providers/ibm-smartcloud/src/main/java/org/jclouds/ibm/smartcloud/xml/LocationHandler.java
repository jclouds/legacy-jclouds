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
package org.jclouds.ibm.smartcloud.xml;

import java.util.Map;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.ibm.smartcloud.domain.Location;
import org.jclouds.ibm.smartcloud.domain.Location.State;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Maps;

/**
 * @author Adrian Cole
 */
public class LocationHandler extends ParseSax.HandlerWithResult<Location> {
   private StringBuilder currentText = new StringBuilder();

   private String id;
   private String name;
   private String description;
   private String location;
   private Map<String, Map<String, String>> capabilities = Maps.newLinkedHashMap();
   private String capabilityName;
   private String capabilityKey;

   private Location loc;

   private State state;

   public Location getResult() {
      return loc;
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      if (qName.equalsIgnoreCase("Capability")) {
         capabilityName = attributes.getValue(attributes.getIndex("id"));
         capabilities.put(capabilityName, Maps.<String, String> newLinkedHashMap());
      } else if (qName.equalsIgnoreCase("Entry")) {
         capabilityKey = attributes.getValue(attributes.getIndex("key"));
      }
   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {
      if (qName.equalsIgnoreCase("ID")) {
         id = currentText.toString().trim();
      } else if (qName.equalsIgnoreCase("Name")) {
         name = currentText.toString().trim();
      } else if (qName.equalsIgnoreCase("Description")) {
         description = currentText.toString().trim();
         if (description.equals(""))
            description = null;
      } else if (qName.equalsIgnoreCase("State")) {
         state = State.fromValue(Integer.parseInt(currentText.toString().trim()));
      } else if (qName.equalsIgnoreCase("Value")) {
         capabilities.get(capabilityName).put(capabilityKey, currentText.toString().trim());
      } else if (qName.equalsIgnoreCase("Location")) {
         if (currentText.toString().trim().equals("")) {
            this.loc = new Location(id, name, description, location, state, capabilities);
            id = null;
            name = null;
            description = null;
            location = null;
            state = null;
            capabilities = Maps.newLinkedHashMap();
            capabilityKey = null;
            capabilityName = null;
         } else {
            location = currentText.toString().trim();
         }
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
