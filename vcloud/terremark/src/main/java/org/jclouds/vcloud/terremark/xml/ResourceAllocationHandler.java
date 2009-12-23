/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.vcloud.terremark.xml;

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

   private Integer address;
   private Integer addressOnParent;
   private String hostResource;
   private String allocationUnits;
   private String description;
   private String elementName;
   private int instanceID;
   private Integer parent;
   private String resourceSubType;
   private ResourceType resourceType;
   private long virtualQuantity = 1;
   private String virtualQuantityUnits;

   private ResourceAllocation item;

   private boolean skip;


   public ResourceAllocation getResult() {
      return item;
   }

   public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
      if (attributes.getIndex("xsi:nil") != -1 || attributes.getIndex("xmlns") == -1) {
         String ns = attributes.getValue(attributes.getIndex("xmlns"));
         if ("http://schemas.dmtf.org/wbem/wscim/1/cim-schema/2/CIM_ResourceAllocationSettingData"
                  .equals(ns)) {
            skip = false;
         } else {
            skip = true;
            return;
         }
      } else {
         skip = false;
      }
   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {
      if (!skip) {
         if (qName.equals("Address")) {
            String address = currentText.toString().trim();
            if (address != null && !address.equals(""))
               this.address = Integer.parseInt(address);
         } else if (qName.equals("AddressOnParent")) {
            String addressOnParent = currentText.toString().trim();
            if (addressOnParent != null && !addressOnParent.equals(""))
               this.addressOnParent = Integer.parseInt(addressOnParent);
         } else if (qName.equals("AllocationUnits")) {
            allocationUnits = currentText.toString().trim();
         } else if (qName.equals("Description")) {
            description = currentText.toString().trim();
         } else if (qName.equals("ElementName")) {
            elementName = currentText.toString().trim();
         } else if (qName.equals("HostResource")) {
            hostResource = currentText.toString().trim();
         } else if (qName.equals("InstanceID")) {
            instanceID = Integer.parseInt(currentText.toString().trim());
         } else if (qName.equals("Parent")) {
            String parent = currentText.toString().trim();
            if (parent != null && !parent.equals(""))
               this.parent = Integer.parseInt(parent);
         } else if (qName.equals("ResourceSubType")) {
            resourceSubType = currentText.toString().trim();
         } else if (qName.equals("ResourceType")) {
            resourceType = ResourceType.fromValue(currentText.toString().trim());
         } else if (qName.equals("VirtualQuantity")) {
            String quantity = currentText.toString().trim();
            if (quantity != null && !quantity.equals(""))
               virtualQuantity = Long.parseLong(quantity);
         } else if (qName.equals("VirtualQuantityUnits")) {
            virtualQuantityUnits = currentText.toString().trim();
         } else if (qName.equals("q2:Item")) {
            item = new ResourceAllocation(instanceID, elementName, description, resourceType,
                     resourceSubType, hostResource, address, addressOnParent, parent, null, virtualQuantity,
                     allocationUnits != null ? allocationUnits : virtualQuantityUnits);
            address = null;
            addressOnParent = null;
            allocationUnits = null;
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
      }
      currentText = new StringBuilder();

   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
