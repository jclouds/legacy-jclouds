/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.vcloud.xml;

import java.util.Set;

import javax.annotation.Resource;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.logging.Logger;
import org.jclouds.vcloud.domain.FirewallRule;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.NatRule;
import org.jclouds.vcloud.domain.Network;
import org.jclouds.vcloud.domain.FirewallRule.Policy;
import org.jclouds.vcloud.domain.FirewallRule.Protocol;
import org.jclouds.vcloud.domain.internal.NetworkImpl;
import org.jclouds.vcloud.util.Utils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
public class NetworkHandler extends ParseSax.HandlerWithResult<Network> {

   @Resource
   protected Logger logger = Logger.NULL;

   private StringBuilder currentText = new StringBuilder();

   private NamedResource network;

   private String description;

   private Set<String> dnsServers = Sets.newLinkedHashSet();
   private String gateway;
   private String netmask;
   private Set<String> fenceModes = Sets.newLinkedHashSet();
   private Boolean dhcp;
   private Set<NatRule> natRules = Sets.newLinkedHashSet();
   private Set<FirewallRule> firewallRules = Sets.newLinkedHashSet();

   private String externalIP;
   private Integer externalPort;
   private String internalIP;
   private Integer internalPort;

   private Policy policy;
   private Protocol protocol;
   private String sourceIP;
   private String sourcePort;

   public Network getResult() {
      return new NetworkImpl(network.getId(), network.getName(), network
            .getLocation(), description, dnsServers, gateway, netmask,
            fenceModes, dhcp, natRules, firewallRules);
   }

   @Override
   public void startElement(String uri, String localName, String qName,
         Attributes attributes) throws SAXException {
      if (qName.equals("Network")) {
         network = Utils.newNamedResource(attributes);
      }
   }

   public void endElement(String uri, String name, String qName) {
      if (qName.equals("Description")) {
         description = currentOrNull();
      } else if (qName.equals("Dns")) {
         dnsServers.add(currentOrNull());
      } else if (qName.equals("Gateway")) {
         gateway = currentOrNull();
      } else if (qName.equals("Netmask")) {
         netmask = currentOrNull();
      } else if (qName.equals("FenceMode")) {
         fenceModes.add(currentOrNull());
      } else if (qName.equals("Dhcp")) {
         dhcp = new Boolean(currentOrNull());
      } else if (qName.equals("NatRule")) {
         natRules.add(new NatRule(externalIP, externalPort, internalIP,
               internalPort));
         externalIP = null;
         externalPort = null;
         internalIP = null;
         internalPort = null;
      } else if (qName.equals("ExternalIP")) {
         externalIP = currentOrNull();
      } else if (qName.equals("ExternalPort")) {
         externalPort = Integer.parseInt(currentOrNull());
      } else if (qName.equals("InternalIP")) {
         internalIP = currentOrNull();
      } else if (qName.equals("InternalPort")) {
         internalPort = Integer.parseInt(currentOrNull());
      } else if (qName.equals("FirewallRule")) {
         firewallRules.add(new FirewallRule(policy, protocol, sourceIP,
               sourcePort));
         policy = null;
         protocol = null;
         sourceIP = null;
         sourcePort = null;
      } else if (qName.equals("Policy")) {
         policy = Policy.fromValue(currentOrNull());
      } else if (qName.equals("Policy")) {
         protocol = Protocol.fromValue(currentOrNull());
      } else if (qName.equals("SourceIp")) {
         sourceIP = currentOrNull();
      } else if (qName.equals("SourcePort")) {
         sourcePort = currentOrNull();
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
