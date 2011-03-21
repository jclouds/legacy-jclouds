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

import static org.jclouds.util.SaxUtils.cleanseAttributes;
import static org.jclouds.vcloud.util.Utils.newReferenceType;

import java.util.Map;

import javax.annotation.Resource;

import org.jclouds.logging.Logger;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.terremark.domain.internal.TerremarkVCloudExpressNetwork;
import org.jclouds.vcloud.xml.VCloudExpressNetworkHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Adrian Cole
 */
public class TerremarkVCloudExpressNetworkHandler extends VCloudExpressNetworkHandler {

   @Resource
   protected Logger logger = Logger.NULL;

   private ReferenceType ips;
   private ReferenceType extension;

   public TerremarkVCloudExpressNetwork getResult() {
      return new TerremarkVCloudExpressNetwork(network.getName(), network.getType(), network.getHref(), description,
               dnsServers, gateway, netmask, fenceModes, dhcp, natRules, firewallRules, extension, ips);
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

}
