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
import org.jclouds.vcloud.domain.ResourceAllocation;
import org.jclouds.vcloud.domain.ResourceType;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Adrian Cole
 */
public class ResourceAllocationHandler extends ParseSax.HandlerWithResult<ResourceAllocation> {
   private StringBuilder currentText = new StringBuilder();

   Integer address;
   Integer addressOnParent;
   String hostResource;
   String allocationUnits;
   String automaticAllocation;
   Boolean connected;
   String description;
   String elementName;
   int instanceID;
   Integer parent;
   String resourceSubType;
   ResourceType resourceType;
   long virtualQuantity = 1;
   String virtualQuantityUnits;

   private org.jclouds.vcloud.domain.ResourceAllocation allocation;

   public org.jclouds.vcloud.domain.ResourceAllocation getResult() {
      return allocation;
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
      if (qName.endsWith("Connection")) {
         connected = new Boolean(attributes.getValue(attributes.getIndex("connected")));
      }
   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {
      String current = currentOrNull();
      if (current != null) {
         if (qName.endsWith("Address")) {
            address = Integer.parseInt(current);
         } else if (qName.endsWith("AddressOnParent")) {
            addressOnParent = Integer.parseInt(current);
         } else if (qName.endsWith("AllocationUnits")) {
            allocationUnits = current;
         } else if (qName.endsWith("Description")) {
            description = current;
         } else if (qName.endsWith("ElementName")) {
            elementName = current;
         } else if (qName.endsWith("InstanceID")) {
            instanceID = Integer.parseInt(current);
         } else if (qName.endsWith("Parent")) {
            parent = Integer.parseInt(current);
         } else if (qName.endsWith("ResourceSubType")) {
            resourceSubType = current;
         } else if (qName.endsWith("ResourceType")) {
            resourceType = ResourceType.fromValue(current);
         } else if (qName.endsWith("VirtualQuantity")) {
            virtualQuantity = Long.parseLong(current);
         } else if (qName.endsWith("VirtualQuantityUnits")) {
            virtualQuantityUnits = current;
         } else if (qName.endsWith("HostResource")) {
            hostResource = currentText.toString().trim();
            virtualQuantity = Long.parseLong(current);
            virtualQuantityUnits = "byte * 2^20";
         }
      } else if (qName.endsWith("Item")) {
         if (allocationUnits != null)
            virtualQuantityUnits = allocationUnits;
         this.allocation = new ResourceAllocation(instanceID, elementName, description,
                  resourceType, resourceSubType, hostResource, address, addressOnParent, parent,
                  connected, virtualQuantity, virtualQuantityUnits);
         address = null;
         addressOnParent = null;
         allocationUnits = null;
         automaticAllocation = null;
         connected = null;
         description = null;
         elementName = null;
         instanceID = -1;
         parent = null;
         resourceSubType = null;
         resourceType = null;
         virtualQuantity = 1;
         virtualQuantityUnits = null;
         hostResource = null;
      }

      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

   protected String currentOrNull() {
      String returnVal = currentText.toString().trim();
      return returnVal.equals("") ? null : returnVal;
   }
}
