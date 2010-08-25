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

package org.jclouds.vcloud.xml;

import static org.jclouds.vcloud.util.Utils.newReferenceType;

import java.util.Map;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.vcloud.domain.OperatingSystem;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.util.Utils;
import org.xml.sax.Attributes;

/**
 * @author Adrian Cole
 */
public class OperatingSystemHandler extends ParseSax.HandlerWithResult<OperatingSystem> {
   private StringBuilder currentText = new StringBuilder();

   protected ReferenceType os;
   protected Integer id;
   protected String info;
   protected String vmwOsType;
   protected String description;
   protected ReferenceType edit;

   public OperatingSystem getResult() {
      OperatingSystem system = new OperatingSystem(os.getName(), os.getType(), os.getHref(), id, info, vmwOsType,
               description, edit);
      os = null;
      id = null;
      info = null;
      vmwOsType = null;
      description = null;
      edit = null;
      return system;
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) {
      Map<String, String> attributes = Utils.cleanseAttributes(attrs);
      if (qName.endsWith("Link")) {
         this.edit = Utils.newReferenceType(attributes);
      } else if (qName.endsWith("OperatingSystemSection")) {
         if (attributes.containsKey("href") && attributes.get("href").endsWith("/")) {
            String href = attributes.get("href");
            attributes.put("href", href.substring(0, href.lastIndexOf('/')));
         }
         os = newReferenceType(attributes);
         vmwOsType = attributes.get("osType");
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