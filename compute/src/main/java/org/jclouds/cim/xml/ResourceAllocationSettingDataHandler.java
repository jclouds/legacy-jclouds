/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.cim.xml;

import static org.jclouds.util.SaxUtils.currentOrNull;
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import org.jclouds.cim.ResourceAllocationSettingData;
import org.jclouds.cim.ResourceAllocationSettingData.ConsumerVisibility;
import org.jclouds.cim.ResourceAllocationSettingData.MappingBehavior;
import org.jclouds.cim.ResourceAllocationSettingData.ResourceType;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;

/**
 * @author Adrian Cole
 */
public class ResourceAllocationSettingDataHandler extends ParseSax.HandlerWithResult<ResourceAllocationSettingData> {
   protected StringBuilder currentText = new StringBuilder();

   protected ResourceAllocationSettingData.Builder builder = ResourceAllocationSettingData.builder();

   public ResourceAllocationSettingData getResult() {
      try {
         return builder.build();
      } finally {
         builder = ResourceAllocationSettingData.builder();
      }
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) {
   }

   @Override
   public void endElement(String uri, String localName, String qName) {
      String current = currentOrNull(currentText);
      if (current != null) {
         if (equalsOrSuffix(qName, "ElementName")) {
            builder.elementName(current);
         } else if (equalsOrSuffix(qName, "InstanceID")) {
            builder.instanceID(current);
         } else if (equalsOrSuffix(qName, "Caption")) {
            builder.caption(current);
         } else if (equalsOrSuffix(qName, "Description")) {
            builder.description(current);
         } else if (equalsOrSuffix(qName, "Address")) {
            builder.address(current);
         } else if (equalsOrSuffix(qName, "AddressOnParent")) {
            builder.addressOnParent(current);
         } else if (equalsOrSuffix(qName, "AllocationUnits")) {
            builder.allocationUnits(current);
         } else if (equalsOrSuffix(qName, "AutomaticAllocation")) {
            builder.automaticAllocation(Boolean.valueOf(current));
         } else if (equalsOrSuffix(qName, "AutomaticDeallocation")) {
            builder.automaticDeallocation(Boolean.valueOf(current));
         } else if (equalsOrSuffix(qName, "ConsumerVisibility")) {
            builder.consumerVisibility(ConsumerVisibility.fromValue(current));
         } else if (equalsOrSuffix(qName, "Limit")) {
            builder.limit(Long.valueOf(current));
         } else if (equalsOrSuffix(qName, "MappingBehavior")) {
            builder.mappingBehavior(MappingBehavior.fromValue(current));
         } else if (equalsOrSuffix(qName, "OtherResourceType")) {
            builder.otherResourceType(current);
         } else if (equalsOrSuffix(qName, "Parent")) {
            builder.parent(current);
         } else if (equalsOrSuffix(qName, "PoolID")) {
            builder.poolID(current);
         } else if (equalsOrSuffix(qName, "Reservation")) {
            builder.reservation(Long.valueOf(current));
         } else if (equalsOrSuffix(qName, "ResourceSubType")) {
            builder.resourceSubType(current);
         } else if (equalsOrSuffix(qName, "ResourceType")) {
            builder.resourceType(ResourceType.fromValue(current));
         } else if (equalsOrSuffix(qName, "VirtualQuantity")) {
            builder.virtualQuantity(Long.valueOf(current));
         } else if (equalsOrSuffix(qName, "VirtualQuantityUnits")) {
            builder.virtualQuantityUnits(current);
         } else if (equalsOrSuffix(qName, "Weight")) {
            builder.weight(Integer.valueOf(current));
         } else if (equalsOrSuffix(qName, "Connection")) {
            builder.connection(current);
         } else if (equalsOrSuffix(qName, "HostResource")) {
            builder.hostResource(current);
         }
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

}
