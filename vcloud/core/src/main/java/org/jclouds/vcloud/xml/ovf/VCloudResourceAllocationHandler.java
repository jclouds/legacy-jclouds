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

import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.ovf.EditableResourceAllocation;
import org.jclouds.vcloud.domain.ovf.ResourceAllocation;
import org.jclouds.vcloud.domain.ovf.VCloudHardDisk;
import org.jclouds.vcloud.domain.ovf.VCloudNetworkAdapter;
import org.jclouds.vcloud.util.Utils;
import org.xml.sax.Attributes;

/**
 * @author Adrian Cole
 */
public class VCloudResourceAllocationHandler extends ResourceAllocationHandler {

   private ReferenceType edit;

   private long capacity;
   private int busType;
   private String busSubType;

   private String ipAddress;
   private boolean primaryNetworkConnection;
   private String ipAddressingMode;

   protected ResourceAllocation newResourceAllocation() {
      if (edit != null) {
         ResourceAllocation allocation = new EditableResourceAllocation(instanceID, elementName, description,
                  resourceType, resourceSubType, hostResource, address, addressOnParent, parent, connected,
                  virtualQuantity, virtualQuantityUnits, edit);
         this.edit = null;
         return allocation;
      } else if (busSubType != null) {
         ResourceAllocation allocation = new VCloudHardDisk(instanceID, elementName, description, resourceType,
                  resourceSubType, hostResource, address, addressOnParent, parent, connected, virtualQuantity,
                  virtualQuantityUnits, capacity, busType, busSubType);
         capacity = -1;
         busType = -1;
         busSubType = null;
         return allocation;
      } else if (ipAddress != null) {
         ResourceAllocation allocation = new VCloudNetworkAdapter(instanceID, elementName, description, resourceType,
                  resourceSubType, hostResource, address, addressOnParent, parent, connected, virtualQuantity,
                  virtualQuantityUnits, ipAddress, primaryNetworkConnection, ipAddressingMode);
         ipAddress = null;
         primaryNetworkConnection = false;
         ipAddressingMode = null;
         return allocation;
      } else {
         return super.newResourceAllocation();
      }
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) {
      Map<String, String> attributes = Utils.cleanseAttributes(attrs);
      if (qName.endsWith("Link")) {
         this.edit = Utils.newReferenceType(attributes);
      } else if (qName.endsWith("HostResource") && attributes.size() > 0) {
         capacity = Long.parseLong(attributes.get("capacity"));
         busType = Integer.parseInt(attributes.get("busType"));
         busSubType = attributes.get("busSubType");
      } else if (qName.endsWith("Connection") && attributes.size() > 0) {
         ipAddress = attributes.get("ipAddress");
         primaryNetworkConnection = Boolean.parseBoolean(attributes.get("primaryNetworkConnection"));
         ipAddressingMode = attributes.get("ipAddressingMode");
      }
      super.startElement(uri, localName, qName, attrs);
   }

}
