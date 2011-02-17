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

package org.jclouds.vcloud.terremark.xml;

import java.net.URI;

import javax.annotation.Resource;

import org.jclouds.http.functions.ParseSax.HandlerWithResult;
import org.jclouds.logging.Logger;
import org.jclouds.vcloud.terremark.domain.TerremarkNetwork;
import org.jclouds.vcloud.terremark.domain.TerremarkNetwork.Type;

/**
 * @author Adrian Cole
 */
public class TerremarkNetworkHandler extends HandlerWithResult<TerremarkNetwork> {

   @Resource
   protected Logger logger = Logger.NULL;
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

   protected String currentOrNull() {
      String returnVal = currentText.toString().trim();
      return returnVal.equals("") ? null : returnVal;
   }

   @Override
   public TerremarkNetwork getResult() {
      return new TerremarkNetwork(id, href, name, rnatAddress, address, broadcastAddress, gatewayAddress, networkType,
               vlan, friendlyName);
   }

   public void endElement(String uri, String name, String qName) {
      String current = currentOrNull();
      if (current != null) {
         if (qName.equals("Href")) {
            href = URI.create(current);
         } else if (qName.equals("Id")) {
            id = current;
         } else if (qName.equals("Name")) {
            this.name = current;
         } else if (qName.equals("RnatAddress")) {
            rnatAddress = current;
         } else if (qName.equals("Address")) {
            address = current;
         } else if (qName.equals("BroadcastAddress")) {
            broadcastAddress = current;
         } else if (qName.equals("GatewayAddress")) {
            gatewayAddress = current;
         } else if (qName.equals("NetworkType")) {
            networkType = TerremarkNetwork.Type.fromValue(current);
         } else if (qName.equals("Vlan")) {
            vlan = current;
         } else if (qName.equals("FriendlyName")) {
            friendlyName = current;
         }
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

}