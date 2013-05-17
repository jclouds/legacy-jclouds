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
package org.jclouds.trmk.vcloud_0_8.xml;

import static org.jclouds.util.SaxUtils.currentOrNull;
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import java.net.URI;

import org.jclouds.http.functions.ParseSax.HandlerWithResult;
import org.jclouds.trmk.vcloud_0_8.domain.NetworkExtendedInfo;
import org.jclouds.trmk.vcloud_0_8.domain.NetworkExtendedInfo.Type;

/**
 * @author Adrian Cole
 */
public class NetworkExtendedInfoHandler extends HandlerWithResult<NetworkExtendedInfo> {

   private StringBuilder currentText = new StringBuilder();

   private String id;
   private URI href;
   private String name;
   private String rnatAddress;
   private String address;
   private String broadcastAddress;
   private String gatewayAddress;
   private Type networkType;
   private String vlan;
   private String friendlyName;

   @Override
   public NetworkExtendedInfo getResult() {
      return new NetworkExtendedInfo(id, href, name, rnatAddress, address, broadcastAddress, gatewayAddress,
            networkType, vlan, friendlyName);
   }

   public void endElement(String uri, String name, String qName) {
      String current = currentOrNull(currentText);
      if (current != null) {
         if (equalsOrSuffix(qName, "Href")) {
            href = URI.create(current);
         } else if (equalsOrSuffix(qName, "Id")) {
            id = current;
         } else if (equalsOrSuffix(qName, "Name")) {
            this.name = current;
         } else if (equalsOrSuffix(qName, "RnatAddress")) {
            rnatAddress = current;
         } else if (equalsOrSuffix(qName, "Address")) {
            address = current;
         } else if (equalsOrSuffix(qName, "BroadcastAddress")) {
            broadcastAddress = current;
         } else if (equalsOrSuffix(qName, "GatewayAddress")) {
            gatewayAddress = current;
         } else if (equalsOrSuffix(qName, "NetworkType")) {
            networkType = NetworkExtendedInfo.Type.fromValue(current);
         } else if (equalsOrSuffix(qName, "Vlan")) {
            vlan = current;
         } else if (equalsOrSuffix(qName, "FriendlyName")) {
            friendlyName = current;
         }
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

}
