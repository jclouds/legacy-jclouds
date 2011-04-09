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
package org.jclouds.deltacloud.xml;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.jclouds.deltacloud.domain.HardwareProfile;
import org.jclouds.deltacloud.domain.HardwareProperty;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.util.SaxUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
public class HardwareProfileHandler extends ParseSax.HandlerWithResult<HardwareProfile> {
   private StringBuilder currentText = new StringBuilder();
   private final HardwarePropertyHandler propertyHandler;

   @Inject
   HardwareProfileHandler(HardwarePropertyHandler propertyHandler) {
      this.propertyHandler = propertyHandler;
   }

   private URI href;
   private String id;
   private String name;
   private Set<HardwareProperty> properties = Sets.newLinkedHashSet();
   private boolean inProperty;

   private HardwareProfile profile;

   public HardwareProfile getResult() {
      return profile;
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
      Map<String, String> attributes = SaxUtils.cleanseAttributes(attrs);
      if (qName.equals("property")) {
         inProperty = true;
      }

      if (inProperty) {
         propertyHandler.startElement(uri, localName, qName, attrs);
      } else if (qName.equals("hardware_profile")) {
         String href = attributes.get("href");
         if (href != null) {
            this.href = URI.create(href);
         }
         this.id = attributes.get("id");
      }
   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {

      if (inProperty)
         propertyHandler.endElement(uri, localName, qName);

      if (qName.endsWith("property")) {
         inProperty = false;
         this.properties.add(propertyHandler.getResult());
      } else if (qName.equalsIgnoreCase("name")) {
         this.name = currentText.toString().trim();
      } else if (qName.equalsIgnoreCase("hardware_profile")) {
         this.profile = new HardwareProfile(href, id, name, properties);
         this.href = null;
         this.id = null;
         this.name = null;
         this.properties = Sets.newLinkedHashSet();
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
