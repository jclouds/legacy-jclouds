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
import org.jclouds.vcloud.terremark.domain.ResourceAllocation;
import org.jclouds.vcloud.terremark.domain.ResourceType;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Adrian Cole
 */
public class ResourceAllocationHandler extends ParseSax.HandlerWithResult<ResourceAllocation> {
   private StringBuilder currentText = new StringBuilder();

   private Integer address;
   private Integer addressOnParent;
   private String allocationUnits;
   private String automaticAllocation;
   private String automaticDeallocation;
   private String caption;
   private String consumerVisibility;
   private String description;
   private String elementName;
   private String hostResource;
   private int instanceID;
   private String limit;
   private String mappingBehavior;
   private String otherResourceType;
   private Integer parent;
   private String poolID;
   private String reservation;
   private String resourceSubType;
   private ResourceType resourceType;
   private long virtualQuantity = 1;
   private String virtualQuantityUnits;
   private String weight;

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
            this.allocationUnits = currentText.toString().trim();
         } else if (qName.equals("AutomaticAllocation")) {
            this.automaticAllocation = currentText.toString().trim();
         } else if (qName.equals("AutomaticDeallocation")) {
            this.automaticDeallocation = currentText.toString().trim();
         } else if (qName.equals("Caption")) {
            this.caption = currentText.toString().trim();
         } else if (qName.equals("ConsumerVisibility")) {
            this.consumerVisibility = currentText.toString().trim();
         } else if (qName.equals("Description")) {
            this.description = currentText.toString().trim();
         } else if (qName.equals("ElementName")) {
            this.elementName = currentText.toString().trim();
         } else if (qName.equals("HostResource")) {
            this.hostResource = currentText.toString().trim();
         } else if (qName.equals("InstanceID")) {
            this.instanceID = Integer.parseInt(currentText.toString().trim());
         } else if (qName.equals("Limit")) {
            this.limit = currentText.toString().trim();
         } else if (qName.equals("MappingBehavior")) {
            this.mappingBehavior = currentText.toString().trim();
         } else if (qName.equals("OtherResourceType")) {
            this.otherResourceType = currentText.toString().trim();
         } else if (qName.equals("Parent")) {
            String parent = currentText.toString().trim();
            if (parent != null && !parent.equals(""))
               this.parent = Integer.parseInt(parent);
         } else if (qName.equals("PoolID")) {
            this.poolID = currentText.toString().trim();
         } else if (qName.equals("Reservation")) {
            this.reservation = currentText.toString().trim();
         } else if (qName.equals("ResourceSubType")) {
            this.resourceSubType = currentText.toString().trim();
         } else if (qName.equals("ResourceType")) {
            this.resourceType = ResourceType.fromValue(currentText.toString().trim());
         } else if (qName.equals("VirtualQuantity")) {
            String quantity = currentText.toString().trim();
            if (quantity != null && !quantity.equals(""))
               this.virtualQuantity = Long.parseLong(quantity);
         } else if (qName.equals("VirtualQuantityUnits")) {
            this.virtualQuantityUnits = currentText.toString().trim();
         } else if (qName.equals("Weight")) {
            this.weight = currentText.toString().trim();
         } else if (qName.equals("q2:Item")) {
            this.item = new ResourceAllocation(address, addressOnParent, allocationUnits,
                     automaticAllocation, automaticDeallocation, caption, consumerVisibility,
                     description, elementName, hostResource, instanceID, limit, mappingBehavior,
                     otherResourceType, parent, poolID, reservation, resourceSubType, resourceType,
                     virtualQuantity, virtualQuantityUnits, weight);
            this.address = null;
            this.addressOnParent = null;
            this.allocationUnits = null;
            this.automaticAllocation = null;
            this.automaticDeallocation = null;
            this.caption = null;
            this.consumerVisibility = null;
            this.description = null;
            this.elementName = null;
            this.hostResource = null;
            this.instanceID = -1;
            this.limit = null;
            this.mappingBehavior = null;
            this.otherResourceType = null;
            this.parent = null;
            this.poolID = null;
            this.reservation = null;
            this.resourceSubType = null;
            this.resourceType = null;
            this.virtualQuantity = 1;
            this.virtualQuantityUnits = null;
            this.weight = null;
         }
      }
      currentText = new StringBuilder();

   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
