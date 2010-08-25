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

package org.jclouds.vcloud.xml.ovf;

import java.util.Map;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.vcloud.domain.ovf.OperatingSystem;
import org.jclouds.vcloud.util.Utils;
import org.xml.sax.Attributes;

/**
 * @author Adrian Cole
 */
public class OperatingSystemHandler extends ParseSax.HandlerWithResult<OperatingSystem> {
   private StringBuilder currentText = new StringBuilder();

   protected Integer id;
   protected String info;
   protected String description;

   public OperatingSystem getResult() {
      OperatingSystem system = new OperatingSystem(id, info, description);
      id = null;
      info = null;
      description = null;
      return system;
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) {
      Map<String, String> attributes = Utils.cleanseAttributes(attrs);
      if (qName.endsWith("OperatingSystemSection")) {
         if (attributes.containsKey("id"))
            this.id = Integer.parseInt(attributes.get("id"));
      }
   }

   @Override
   public void endElement(String uri, String localName, String qName) {
      if (qName.endsWith("Info")) {
         this.info = currentText.toString().trim();
      } else if (qName.endsWith("Description")) {
         this.description = currentText.toString().trim();
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}