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
package org.jclouds.trmk.vcloud_0_8.xml;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.jclouds.http.functions.ParseSax.HandlerWithResult;
import org.jclouds.logging.Logger;
import org.jclouds.trmk.vcloud_0_8.domain.NetworkAdapter;
import org.jclouds.trmk.vcloud_0_8.domain.Subnet;
import org.jclouds.trmk.vcloud_0_8.domain.VAppExtendedInfo;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Lists;

/**
 * @author Seshu Pasam
 */
public class VAppExtendedInfoHandler extends HandlerWithResult<VAppExtendedInfo> {

   @Resource
   protected Logger logger = Logger.NULL;
   private StringBuilder currentText = new StringBuilder();

   private String id;
   private URI href;
   private String name;
   private String longName;
   private List<String> tags;
   private List<NetworkAdapter> networkAdapters = Lists.newArrayList();
   private boolean inAdapters;
   private String macAddress;
   private String adapterName;
   private boolean inSubnet;
   private Subnet subnet;
   private URI subnetLocation;
   private String subnetName;

   protected String currentOrNull() {
      String returnVal = currentText.toString().trim();
      return returnVal.equals("") ? null : returnVal;
   }

   @Override
   public VAppExtendedInfo getResult() {
      return new VAppExtendedInfo(id, href, name, tags, longName, networkAdapters);
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      if (qName.equals("NetworkAdapters")) {
         inAdapters = true;
      } else if (qName.equals("Subnet")) {
         inSubnet = true;
      }
   }

   public void endElement(String uri, String name, String qName) {
      String current = currentOrNull();
      if (current != null) {
         if (qName.equals("Id")) {
            this.id = current;
         } else if (qName.equals("Tags")) {
            this.tags = Arrays.asList(current.split(","));
         } else if (qName.equals("LongName")) {
            this.longName = current;
         } else if (qName.equals("Href")) {
            if (inSubnet) {
               this.subnetLocation = URI.create(current);
            } else {
               this.href = URI.create(current);
            }
         } else if (qName.equals("Name")) {
            if (inSubnet) {
               this.subnetName = current;
            } else if (inAdapters) {
               this.adapterName = current;
            } else {
               this.name = current;
            }
         } else if (qName.equals("NetworkAdapters")) {
             inAdapters = false;
         } else if (qName.equals("NetworkAdapter")) {
             networkAdapters.add(new NetworkAdapter(macAddress, adapterName, subnet));
             macAddress = null;
             adapterName = null;
             subnet = null;
         } else if (qName.equals("MacAddress")) {
             macAddress = current;
         } else if (qName.equals("Subnet")) {
             subnet = new Subnet(subnetLocation, subnetName);
             subnetLocation = null;
             subnetName = null;
             inSubnet = false;
         }
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
