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

package org.jclouds.vcloud.xml;

import static org.jclouds.vcloud.util.Utils.cleanseAttributes;
import static org.jclouds.vcloud.util.Utils.newReferenceType;

import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.logging.Logger;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.network.FenceMode;
import org.jclouds.vcloud.domain.network.VCloudExpressNetwork;
import org.jclouds.vcloud.domain.network.firewall.FirewallPolicy;
import org.jclouds.vcloud.domain.network.firewall.FirewallRule;
import org.jclouds.vcloud.domain.network.internal.VCloudExpressNetworkImpl;
import org.jclouds.vcloud.domain.network.nat.NatProtocol;
import org.jclouds.vcloud.domain.network.nat.rules.PortForwardingRule;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
public class VCloudExpressNetworkHandler extends ParseSax.HandlerWithResult<VCloudExpressNetwork> {

   @Resource
   protected Logger logger = Logger.NULL;

   protected StringBuilder currentText = new StringBuilder();

   protected ReferenceType network;

   protected String description;

   protected Set<String> dnsServers = Sets.newLinkedHashSet();
   protected String gateway;
   protected String netmask;
   protected Set<FenceMode> fenceModes = Sets.newLinkedHashSet();
   protected Boolean dhcp;
   protected Set<PortForwardingRule> natRules = Sets.newLinkedHashSet();
   protected Set<FirewallRule> firewallRules = Sets.newLinkedHashSet();

   protected String externalIP;
   protected Integer externalPort;
   protected String internalIP;
   protected Integer internalPort;

   protected FirewallPolicy policy;
   protected String sourceIP;
   protected int sourcePort;

   public VCloudExpressNetwork getResult() {
      return new VCloudExpressNetworkImpl(network.getName(), network.getType(), network.getHref(), description,
               dnsServers, gateway, netmask, fenceModes, dhcp, natRules, firewallRules);
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
      Map<String, String> attributes = cleanseAttributes(attrs);
      if (qName.equals("Network")) {
         network = newReferenceType(attributes);
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
         try {
            fenceModes.add(FenceMode.fromValue(currentOrNull()));
         } catch (IllegalArgumentException e) {
            fenceModes.add(FenceMode.BRIDGED);
         }
      } else if (qName.equals("Dhcp")) {
         dhcp = new Boolean(currentOrNull());
      } else if (qName.equals("NatRule")) {
         natRules.add(new PortForwardingRule(externalIP, externalPort, internalIP, internalPort, NatProtocol.TCP_UDP));
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
         firewallRules.add(new FirewallRule(true, null, policy, null, sourcePort, sourceIP));
         policy = null;
         sourceIP = null;
         sourcePort = -1;
      } else if (qName.equals("Policy")) {
         policy = FirewallPolicy.fromValue(currentOrNull());
      } else if (qName.equals("SourceIp")) {
         sourceIP = currentOrNull();
      } else if (qName.equals("SourcePort")) {
         sourcePort = Integer.parseInt(currentOrNull());
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
