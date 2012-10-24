/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.domain;

import static org.jclouds.abiquo.domain.DomainUtils.link;

import com.abiquo.model.enumerator.NetworkType;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.model.transport.LinksDto;
import com.abiquo.server.core.infrastructure.network.NicDto;
import com.abiquo.server.core.infrastructure.network.PrivateIpDto;
import com.abiquo.server.core.infrastructure.network.PublicIpDto;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDto;

/**
 * Network domain utilities.
 * 
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 */
public class NetworkResources {
   public static VLANNetworkDto vlanPost() {
      VLANNetworkDto vlan = new VLANNetworkDto();
      vlan.setAddress("192.168.1.0");
      vlan.setDefaultNetwork(true);
      vlan.setName("DefaultNetwork");
      vlan.setGateway("192.168.1.1");
      vlan.setMask(24);

      return vlan;
   }

   public static PrivateIpDto privateIpPut() {
      PrivateIpDto ip = new PrivateIpDto();
      ip.setId(1);
      ip.setName("private ip");
      ip.setMac("00:58:5A:c0:C3:01");
      RESTLink self = new RESTLink("self", "http://localhost/api/cloud/virtualdatacenters/1/privatenetworks/1/ips/1");
      self.setTitle("privateip");
      ip.addLink(self);
      return ip;
   }

   public static PublicIpDto publicIpToPurchase() {
      PublicIpDto ip = new PublicIpDto();
      RESTLink self = new RESTLink("purchase", "http://localhost/api/cloud/virtualdatacenters/5/publicips/purchased/1");
      ip.addLink(self);
      return ip;
   }

   public static PublicIpDto publicIpToRelease() {
      PublicIpDto ip = new PublicIpDto();
      RESTLink self = new RESTLink("release", "http://localhost/api/cloud/virtualdatacenters/5/publicips/topurchase/1");
      ip.addLink(self);
      return ip;
   }

   public static NicDto nicPut() {
      NicDto nic = new NicDto();
      nic.setId(1);
      nic.setIp("123.123.123.123");
      nic.setMac("00:58:5A:c0:C3:01");
      nic.addLink(new RESTLink("edit",
            "http://localhost/api/cloud/virtualdatacenters/1/virtualappliances/1/virtualmachines/1/network/nics/1"));

      return nic;
   }

   public static VLANNetworkDto privateNetworkPut() {
      VLANNetworkDto vlan = new VLANNetworkDto();
      vlan.setId(1);
      vlan.setAddress("192.168.1.0");
      vlan.setDefaultNetwork(true);
      vlan.setName("DefaultNetwork");
      vlan.setGateway("192.168.1.1");
      vlan.setMask(24);
      vlan.setType(NetworkType.INTERNAL);
      vlan.addLink(new RESTLink("edit", "http://localhost/api/cloud/virtualdatacenters/1/privatenetworks/1"));
      vlan.addLink(new RESTLink("ips", "http://localhost/api/cloud/virtualdatacenters/1/privatenetworks/1/ips"));

      return vlan;
   }

   public static VLANNetworkDto publicNetworkPut() {
      VLANNetworkDto vlan = new VLANNetworkDto();
      vlan.setId(1);
      vlan.setAddress("192.168.1.0");
      vlan.setDefaultNetwork(true);
      vlan.setName("PublicNetwork");
      vlan.setGateway("192.168.1.1");
      vlan.setMask(24);
      vlan.setType(NetworkType.PUBLIC);
      vlan.addLink(new RESTLink("edit", "http://localhost/api/admin/datacenters/1/network/1"));
      vlan.addLink(new RESTLink("ips", "http://localhost/api/admin/datacenters/1/network/1/ips"));

      return vlan;
   }

   public static VLANNetworkDto externalNetworkPut() {
      VLANNetworkDto vlan = new VLANNetworkDto();
      vlan.setId(1);
      vlan.setAddress("192.168.1.0");
      vlan.setDefaultNetwork(true);
      vlan.setName("ExternalNetwork");
      vlan.setGateway("192.168.1.1");
      vlan.setMask(24);
      vlan.setType(NetworkType.EXTERNAL);
      vlan.addLink(new RESTLink("edit", "http://localhost/api/admin/datacenters/1/network/1"));
      vlan.addLink(new RESTLink("enterprise", "http://localhost/api/admin/enterprises/1"));
      vlan.addLink(new RESTLink("ips", "http://localhost/api/admin/enterprises/1/limits/1/externalnetworks/1/ips"));

      return vlan;
   }

   public static VLANNetworkDto unmanagedNetworkPut() {
      VLANNetworkDto vlan = new VLANNetworkDto();
      vlan.setId(1);
      vlan.setAddress("192.168.1.0");
      vlan.setDefaultNetwork(true);
      vlan.setName("UnmanagedNetwork");
      vlan.setGateway("192.168.1.1");
      vlan.setMask(24);
      vlan.setType(NetworkType.UNMANAGED);
      vlan.addLink(new RESTLink("edit", "http://localhost/api/admin/datacenters/1/network/1"));
      vlan.addLink(new RESTLink("enterprise", "http://localhost/api/admin/enterprises/1"));
      vlan.addLink(new RESTLink("ips", "http://localhost/api/admin/enterprises/1/limits/1/externalnetworks/1/ips"));

      return vlan;
   }

   public static String vlanNetworkPostPayload() {
      StringBuffer buffer = new StringBuffer();
      buffer.append("<network>");
      buffer.append("<address>192.168.1.0</address>");
      buffer.append("<defaultNetwork>true</defaultNetwork>");
      buffer.append("<gateway>192.168.1.1</gateway>");
      buffer.append("<mask>24</mask>");
      buffer.append("<name>DefaultNetwork</name>");
      buffer.append("</network>");
      return buffer.toString();
   }

   public static String privateNetworkPutPayload() {
      StringBuffer buffer = new StringBuffer();
      buffer.append("<network>");
      buffer.append(link("/cloud/virtualdatacenters/1/privatenetworks/1", "edit"));
      buffer.append(link("/cloud/virtualdatacenters/1/privatenetworks/1/ips", "ips"));
      buffer.append("<address>192.168.1.0</address>");
      buffer.append("<defaultNetwork>true</defaultNetwork>");
      buffer.append("<gateway>192.168.1.1</gateway>");
      buffer.append("<id>1</id>");
      buffer.append("<mask>24</mask>");
      buffer.append("<name>DefaultNetwork</name>");
      buffer.append("<type>INTERNAL</type>");
      buffer.append("</network>");
      return buffer.toString();
   }

   public static String publicNetworkPutPayload() {
      StringBuffer buffer = new StringBuffer();
      buffer.append("<network>");
      buffer.append(link("/admin/datacenters/1/network/1", "edit"));
      buffer.append(link("/admin/datacenters/1/network/1/ips", "ips"));
      buffer.append("<address>192.168.1.0</address>");
      buffer.append("<defaultNetwork>true</defaultNetwork>");
      buffer.append("<gateway>192.168.1.1</gateway>");
      buffer.append("<id>1</id>");
      buffer.append("<mask>24</mask>");
      buffer.append("<name>PublicNetwork</name>");
      buffer.append("<type>PUBLIC</type>");
      buffer.append("</network>");
      return buffer.toString();
   }

   public static String externalNetworkPutPayload() {
      StringBuffer buffer = new StringBuffer();
      buffer.append("<network>");
      buffer.append(link("/admin/datacenters/1/network/1", "edit"));
      buffer.append(link("/admin/enterprises/1", "enterprise"));
      buffer.append(link("/admin/enterprises/1/limits/1/externalnetworks/1/ips", "ips"));
      buffer.append("<address>192.168.1.0</address>");
      buffer.append("<defaultNetwork>true</defaultNetwork>");
      buffer.append("<gateway>192.168.1.1</gateway>");
      buffer.append("<id>1</id>");
      buffer.append("<mask>24</mask>");
      buffer.append("<name>ExternalNetwork</name>");
      buffer.append("<type>EXTERNAL</type>");
      buffer.append("</network>");
      return buffer.toString();
   }

   public static String unmanagedNetworkPutPayload() {
      StringBuffer buffer = new StringBuffer();
      buffer.append("<network>");
      buffer.append(link("/admin/datacenters/1/network/1", "edit"));
      buffer.append(link("/admin/enterprises/1", "enterprise"));
      buffer.append(link("/admin/enterprises/1/limits/1/externalnetworks/1/ips", "ips"));
      buffer.append("<address>192.168.1.0</address>");
      buffer.append("<defaultNetwork>true</defaultNetwork>");
      buffer.append("<gateway>192.168.1.1</gateway>");
      buffer.append("<id>1</id>");
      buffer.append("<mask>24</mask>");
      buffer.append("<name>UnmanagedNetwork</name>");
      buffer.append("<type>UNMANAGED</type>");
      buffer.append("</network>");
      return buffer.toString();
   }

   public static String privateIpPutPayload() {
      StringBuffer buffer = new StringBuffer();
      buffer.append("<ipPoolManagement>");
      buffer.append(link("/cloud/virtualdatacenters/1/privatenetworks/1/ips/1", "self", "privateip"));
      buffer.append("<available>false</available>");
      buffer.append("<id>1</id>");
      buffer.append("<mac>00:58:5A:c0:C3:01</mac>");
      buffer.append("<name>private ip</name>");
      buffer.append("<quarantine>false</quarantine>");
      buffer.append("</ipPoolManagement>");
      return buffer.toString();
   }

   public static String linksDtoPayload(final LinksDto dto) {
      StringBuffer buffer = new StringBuffer();
      buffer.append("<links>");
      for (RESTLink link : dto.getLinks()) {
         buffer.append(link(link));
      }
      buffer.append("</links>");
      return buffer.toString();
   }
}
