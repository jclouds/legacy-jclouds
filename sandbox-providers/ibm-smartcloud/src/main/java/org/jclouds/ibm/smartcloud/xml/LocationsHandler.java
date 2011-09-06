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

import java.util.Set;

import javax.inject.Inject;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.ibm.smartcloud.domain.Location;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
public class LocationsHandler extends ParseSax.HandlerWithResult<Set<? extends Location>> {
   private StringBuilder currentText = new StringBuilder();

   private Set<Location> locations = Sets.newLinkedHashSet();
   private final LocationHandler locationHandler;

   @Inject
   public LocationsHandler(LocationHandler locationHandler) {
      this.locationHandler = locationHandler;
   }

   public Set<? extends Location> getResult() {
      return locations;
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
      locationHandler.startElement(uri, localName, qName, attributes);
   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {
      locationHandler.endElement(uri, localName, qName);
      if (qName.equals("Location") && currentText.toString().trim().equals("")) {
         this.locations.add(locationHandler.getResult());
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      locationHandler.characters(ch, start, length);
      currentText.append(ch, start, length);
   }
}
