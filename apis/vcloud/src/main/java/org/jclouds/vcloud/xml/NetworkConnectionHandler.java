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
package org.jclouds.vcloud.xml;

import java.util.Map;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.util.SaxUtils;
import org.jclouds.vcloud.domain.NetworkConnection;
import org.jclouds.vcloud.domain.network.IpAddressAllocationMode;
import org.xml.sax.Attributes;

/**
 * @author Adrian Cole
 */
public class NetworkConnectionHandler extends ParseSax.HandlerWithResult<NetworkConnection> {
   protected StringBuilder currentText = new StringBuilder();

   protected String network;
   protected int networkConnectionIndex;
   protected String ipAddress;
   protected String externalIpAddress;
   protected boolean connected;
   protected String MACAddress;
   protected IpAddressAllocationMode ipAddressAllocationMode;

   public NetworkConnection getResult() {
      NetworkConnection connection = new NetworkConnection(network, networkConnectionIndex, ipAddress,
               externalIpAddress, connected, MACAddress, ipAddressAllocationMode);
      this.network = null;
      this.networkConnectionIndex = -1;
      this.ipAddress = null;
      this.externalIpAddress = null;
      this.connected = false;
      this.MACAddress = null;
      this.ipAddressAllocationMode = null;
      return connection;
   }

   public void startElement(String uri, String localName, String qName, Attributes attrs) {
      Map<String, String> attributes = SaxUtils.cleanseAttributes(attrs);
      if (qName.endsWith("NetworkConnection")) {
         network = attributes.get("network");
      }
   }

   @Override
   public void endElement(String uri, String localName, String qName) {
      if (qName.endsWith("NetworkConnectionIndex")) {
         this.networkConnectionIndex = Integer.parseInt(currentOrNull());
      } else if (qName.endsWith("IpAddress")) {
         this.ipAddress = currentOrNull();
      } else if (qName.endsWith("ExternalIpAddress")) {
         this.externalIpAddress = currentOrNull();
      } else if (qName.endsWith("IsConnected")) {
         this.connected = Boolean.parseBoolean(currentOrNull());
      } else if (qName.endsWith("MACAddress")) {
         this.MACAddress = currentOrNull();
      } else if (qName.endsWith("IpAddressAllocationMode")) {
         this.ipAddressAllocationMode = IpAddressAllocationMode.valueOf(currentOrNull());
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
