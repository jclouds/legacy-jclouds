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

import static org.jclouds.trmk.vcloud_0_8.util.Utils.newReferenceType;
import static org.jclouds.util.SaxUtils.cleanseAttributes;
import static org.jclouds.util.SaxUtils.currentOrNull;

import java.util.Map;

import javax.annotation.Resource;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.logging.Logger;
import org.jclouds.trmk.vcloud_0_8.domain.FenceMode;
import org.jclouds.trmk.vcloud_0_8.domain.Network;
import org.jclouds.trmk.vcloud_0_8.domain.ReferenceType;
import org.jclouds.trmk.vcloud_0_8.domain.internal.NetworkImpl;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Adrian Cole
 */
public class NetworkHandler extends ParseSax.HandlerWithResult<Network> {

   @Resource
   protected Logger logger = Logger.NULL;

   protected StringBuilder currentText = new StringBuilder();

   protected ReferenceType network;

   protected String description;

   protected String gateway;
   protected String netmask;
   protected FenceMode fenceMode;

   private ReferenceType ips;
   private ReferenceType extension;

   public Network getResult() {
      return new NetworkImpl(network.getName(), network.getType(), network.getHref(), description, gateway, netmask,
            fenceMode, extension, ips);
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
      Map<String, String> attributes = cleanseAttributes(attrs);
      if (qName.equals("Network")) {
         network = newReferenceType(attributes);
      } else if (qName.equals("Link")) {
         if ("IP Addresses".equals(attributes.get("name"))) {
            ips = newReferenceType(attributes);
         } else if ("down".equals(attributes.get("rel"))) {
            extension = newReferenceType(attributes);
         }
      }
   }

   public void endElement(String uri, String name, String qName) {
      if (qName.equals("Description")) {
         description = currentOrNull(currentText);
      } else if (qName.equals("Gateway")) {
         gateway = currentOrNull(currentText);
      } else if (qName.equals("Netmask")) {
         netmask = currentOrNull(currentText);
      } else if (qName.equals("FenceMode")) {
         fenceMode = FenceMode.fromValue(currentOrNull(currentText));
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

}
