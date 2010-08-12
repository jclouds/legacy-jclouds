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

import org.jclouds.http.functions.ParseSax;
import org.jclouds.vcloud.domain.VirtualSystem;
import org.xml.sax.SAXException;

/**
 * @author Adrian Cole
 */
public class VirtualSystemHandler extends ParseSax.HandlerWithResult<VirtualSystem> {
   private StringBuilder currentText = new StringBuilder();

   private String elementName;
   private int instanceID;
   private String virtualSystemIdentifier;
   private String virtualSystemType;

   private org.jclouds.vcloud.domain.VirtualSystem system;

   public org.jclouds.vcloud.domain.VirtualSystem getResult() {
      return system;
   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {

      if (qName.endsWith("ElementName")) {
         this.elementName = currentText.toString().trim();
      } else if (qName.endsWith("InstanceID")) {
         this.instanceID = Integer.parseInt(currentText.toString().trim());
      } else if (qName.endsWith("VirtualSystemIdentifier")) {
         this.virtualSystemIdentifier = currentText.toString().trim();
      } else if (qName.endsWith("VirtualSystemType")) {
         this.virtualSystemType = currentText.toString().trim();
      } else if (qName.endsWith("System")) {
         this.system = new org.jclouds.vcloud.domain.VirtualSystem(instanceID, elementName,
                  virtualSystemIdentifier, virtualSystemType);
         this.elementName = null;
         this.instanceID = -1;
         this.virtualSystemIdentifier = null;
         this.virtualSystemType = null;
      }

      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
