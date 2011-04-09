/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.slicehost.xml;

import java.util.Set;

import javax.inject.Inject;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.slicehost.domain.Flavor;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
public class FlavorsHandler extends ParseSax.HandlerWithResult<Set<? extends Flavor>> {
   private StringBuilder currentText = new StringBuilder();

   private Set<Flavor> slices = Sets.newLinkedHashSet();
   private final FlavorHandler locationHandler;

   @Inject
   public FlavorsHandler(FlavorHandler locationHandler) {
      this.locationHandler = locationHandler;
   }

   public Set<? extends Flavor> getResult() {
      return slices;
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      locationHandler.startElement(uri, localName, qName, attributes);
   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {
      locationHandler.endElement(uri, localName, qName);
      if (qName.equals("flavor") && currentText.toString().trim().equals("")) {
         this.slices.add(locationHandler.getResult());
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      locationHandler.characters(ch, start, length);
      currentText.append(ch, start, length);
   }
}
