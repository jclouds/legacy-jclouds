/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.vcloud.xml.ovf;

import java.util.Map;

import org.jclouds.cim.ResourceAllocationSettingData;
import org.jclouds.cim.xml.ResourceAllocationSettingDataHandler;
import org.jclouds.util.SaxUtils;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.ovf.EditableResourceAllocationSettingData;
import org.jclouds.vcloud.domain.ovf.VCloudHardDisk;
import org.jclouds.vcloud.domain.ovf.VCloudNetworkAdapter;
import org.jclouds.vcloud.util.Utils;
import org.xml.sax.Attributes;

/**
 * @author Adrian Cole
 */
public class VCloudResourceAllocationSettingDataHandler extends ResourceAllocationSettingDataHandler {

   private ReferenceType edit;

   private long capacity;
   private int busType;
   private String busSubType;

   private String ipAddress;
   private boolean primaryNetworkConnection;
   private String ipAddressingMode;

   public ResourceAllocationSettingData getResult() {
      try {
         ResourceAllocationSettingData from = super.getResult();
         if (edit != null) {
            return EditableResourceAllocationSettingData.builder().fromResourceAllocationSettingData(from).edit(edit)
                     .build();
         } else if (busSubType != null) {
            return VCloudHardDisk.builder().fromResourceAllocationSettingData(from).capacity(capacity).busType(busType)
                     .busSubType(busSubType).build();
         } else if (ipAddress != null) {
            return VCloudNetworkAdapter.builder().fromResourceAllocationSettingData(from).ipAddress(ipAddress)
                     .primaryNetworkConnection(primaryNetworkConnection).ipAddressingMode(ipAddressingMode).build();
         } else {
            return from;
         }
      } finally {
         ipAddress = null;
         primaryNetworkConnection = false;
         ipAddressingMode = null;
         capacity = -1;
         busType = -1;
         busSubType = null;
         edit = null;
      }
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) {
      Map<String, String> attributes = SaxUtils.cleanseAttributes(attrs);
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
