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

package org.jclouds.ovf.xml;

import static org.jclouds.util.SaxUtils.cleanseAttributes;
import static org.jclouds.util.SaxUtils.currentOrNull;
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import java.util.Map;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.ovf.OperatingSystemSection;
import org.xml.sax.Attributes;

/**
 * @author Adrian Cole
 */
public class OperatingSystemSectionHandler extends ParseSax.HandlerWithResult<OperatingSystemSection> {
   private StringBuilder currentText = new StringBuilder();

   protected Integer id;
   protected String info;
   protected String description;

   public OperatingSystemSection getResult() {
      OperatingSystemSection system = new OperatingSystemSection(id, info, description);
      id = null;
      info = null;
      description = null;
      return system;
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) {
      Map<String, String> attributes = cleanseAttributes(attrs);
      if (equalsOrSuffix(qName, "OperatingSystemSection")) {
         if (attributes.containsKey("id"))
            this.id = Integer.parseInt(attributes.get("id"));
      }
   }

   @Override
   public void endElement(String uri, String localName, String qName) {
      if (equalsOrSuffix(qName, "Info")) {
         this.info = currentOrNull(currentText);
      } else if (equalsOrSuffix(qName, "Description")) {
         this.description = currentOrNull(currentText);
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}