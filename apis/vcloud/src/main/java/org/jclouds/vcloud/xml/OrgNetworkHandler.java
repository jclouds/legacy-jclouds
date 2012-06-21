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

import static org.jclouds.util.SaxUtils.equalsOrSuffix;
import static org.jclouds.vcloud.util.Utils.newReferenceType;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.util.SaxUtils;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.network.DhcpService;
import org.jclouds.vcloud.domain.network.Features;
import org.jclouds.vcloud.domain.network.FenceMode;
import org.jclouds.vcloud.domain.network.FirewallService;
import org.jclouds.vcloud.domain.network.IpRange;
import org.jclouds.vcloud.domain.network.IpScope;
import org.jclouds.vcloud.domain.network.NatService;
import org.jclouds.vcloud.domain.network.OrgNetwork;
import org.jclouds.vcloud.domain.network.firewall.FirewallPolicy;
import org.jclouds.vcloud.domain.network.firewall.FirewallProtocols;
import org.jclouds.vcloud.domain.network.firewall.FirewallRule;
import org.jclouds.vcloud.domain.network.internal.OrgNetworkImpl;
import org.jclouds.vcloud.domain.network.nat.NatPolicy;
import org.jclouds.vcloud.domain.network.nat.NatProtocol;
import org.jclouds.vcloud.domain.network.nat.NatRule;
import org.jclouds.vcloud.domain.network.nat.NatType;
import org.jclouds.vcloud.domain.network.nat.rules.MappingMode;
import org.jclouds.vcloud.domain.network.nat.rules.OneToOneVmRule;
import org.jclouds.vcloud.domain.network.nat.rules.PortForwardingRule;
import org.jclouds.vcloud.domain.network.nat.rules.VmRule;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
public class OrgNetworkHandler extends ParseSax.HandlerWithResult<OrgNetwork> {

   protected final TaskHandler taskHandler;

   @Inject
   public OrgNetworkHandler(TaskHandler taskHandler) {
      this.taskHandler = taskHandler;
   }

   protected StringBuilder currentText = new StringBuilder();

   protected ReferenceType network;
   protected ReferenceType org;
   protected String orgDescription;
   protected List<Task> tasks = Lists.newArrayList();

   protected String startAddress;
   protected String endAddress;

   protected boolean inherited;
   protected String gateway;
   protected String netmask;
   protected String dns1;
   protected String dns2;
   protected String dnsSuffix;
   protected Set<IpRange> ipRanges = Sets.newLinkedHashSet();
   protected Set<String> allocatedIpAddresses = Sets.newLinkedHashSet();

   protected IpScope ipScope;
   protected ReferenceType parentNetwork;
   protected FenceMode fenceMode;

   protected boolean serviceEnabled;
   protected Integer defaultLeaseTime;
   protected Integer maxLeaseTime;

   protected DhcpService dhcpService;

   protected boolean inFirewallRule;
   protected boolean firewallRuleEnabled;
   protected String firewallRuleDescription;
   protected FirewallPolicy firewallPolicy;

   protected boolean tcp;
   protected boolean udp;
   protected FirewallProtocols protocols;
   protected int port;
   protected String destinationIp;

   protected List<FirewallRule> firewallRules = Lists.newArrayList();
   protected FirewallService firewallService;

   protected NatType natType;
   protected NatPolicy natPolicy;

   protected MappingMode mappingMode;
   protected String externalIP;
   protected String vAppScopedVmId;
   protected int vmNicId;

   protected int externalPort;
   protected String internalIP;
   protected int internalPort;
   protected NatProtocol natProtocol;

   protected String vAppScopedLocalId;

   protected List<NatRule> natRules = Lists.newArrayList();
   protected NatService natService;

   protected Features features;
   protected OrgNetwork.Configuration configuration;

   protected ReferenceType networkPool;
   protected Set<String> allowedExternalIpAddresses = Sets.newLinkedHashSet();

   public OrgNetwork getResult() {
      return new OrgNetworkImpl(network.getName(), network.getType(), network.getHref(), org, orgDescription, tasks,
               configuration, networkPool, allowedExternalIpAddresses);
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
      Map<String, String> attributes = SaxUtils.cleanseAttributes(attrs);
      if (SaxUtils.equalsOrSuffix(qName, "OrgNetwork")) {
         network = newReferenceType(attributes);
      } else if (SaxUtils.equalsOrSuffix(qName, "FirewallRule")) {
         this.inFirewallRule = true;
      } else if (SaxUtils.equalsOrSuffix(qName, "ParentNetwork")) {
         parentNetwork = newReferenceType(attributes);
      } else if (SaxUtils.equalsOrSuffix(qName, "Link") && "up".equals(attributes.get("rel"))) {
         org = newReferenceType(attributes);
      } else {
         taskHandler.startElement(uri, localName, qName, attrs);
      }
      String type = attributes.get("type");
      if (type != null) {
         if (type.indexOf("networkPool+xml") != -1) {
            networkPool = newReferenceType(attributes);
         }
      }
   }

   public void endElement(String uri, String name, String qName) {
      taskHandler.endElement(uri, name, qName);
      if (SaxUtils.equalsOrSuffix(qName, "Task")) {
         this.tasks.add(taskHandler.getResult());
      } else if (SaxUtils.equalsOrSuffix(qName, "Description")) {
         if (inFirewallRule)
            firewallRuleDescription = currentOrNull();
         else
            orgDescription = currentOrNull();
      } else if (SaxUtils.equalsOrSuffix(qName, "FenceMode")) {
         fenceMode = FenceMode.fromValue(currentOrNull());
      } else if (SaxUtils.equalsOrSuffix(qName, "StartAddress")) {
         startAddress = currentOrNull();
      } else if (SaxUtils.equalsOrSuffix(qName, "EndAddress")) {
         endAddress = currentOrNull();
      } else if (SaxUtils.equalsOrSuffix(qName, "AllocatedIpAddress")) {
         allocatedIpAddresses.add(currentOrNull());
      } else if (SaxUtils.equalsOrSuffix(qName, "IpRange")) {
         ipRanges.add(new IpRange(startAddress, endAddress));
         this.startAddress = null;
         this.endAddress = null;
      } else if (SaxUtils.equalsOrSuffix(qName, "IsInherited")) {
         inherited = Boolean.parseBoolean(currentOrNull());
      } else if (SaxUtils.equalsOrSuffix(qName, "Gateway")) {
         gateway = currentOrNull();
      } else if (SaxUtils.equalsOrSuffix(qName, "Netmask")) {
         netmask = currentOrNull();
      } else if (SaxUtils.equalsOrSuffix(qName, "Dns1")) {
         dns1 = currentOrNull();
      } else if (SaxUtils.equalsOrSuffix(qName, "Dns2")) {
         dns2 = currentOrNull();
      } else if (SaxUtils.equalsOrSuffix(qName, "DnsSuffix")) {
         dnsSuffix = currentOrNull();
      } else if (SaxUtils.equalsOrSuffix(qName, "IpScope")) {
         ipScope = new IpScope(inherited, gateway, netmask, dns1, dns2, dnsSuffix, ipRanges, allocatedIpAddresses);
         this.inherited = false;
         this.gateway = null;
         this.netmask = null;
         this.dns1 = null;
         this.dns2 = null;
         this.dnsSuffix = null;
         this.ipRanges = Sets.newLinkedHashSet();
         this.allocatedIpAddresses = Sets.newLinkedHashSet();
      } else if (SaxUtils.equalsOrSuffix(qName, "IsEnabled")) {
         if (inFirewallRule)
            firewallRuleEnabled = Boolean.parseBoolean(currentOrNull());
         else
            serviceEnabled = Boolean.parseBoolean(currentOrNull());
      } else if (SaxUtils.equalsOrSuffix(qName, "DefaultLeaseTime")) {
         defaultLeaseTime = Integer.parseInt(currentOrNull());
      } else if (SaxUtils.equalsOrSuffix(qName, "MaxLeaseTime")) {
         maxLeaseTime = Integer.parseInt(currentOrNull());
      } else if (SaxUtils.equalsOrSuffix(qName, "DhcpService")) {
         this.dhcpService = new DhcpService(serviceEnabled, defaultLeaseTime, maxLeaseTime, Iterables
                  .getOnlyElement(ipRanges));
         this.serviceEnabled = false;
         this.defaultLeaseTime = null;
         this.maxLeaseTime = null;
         this.ipRanges = Sets.newLinkedHashSet();
      } else if (SaxUtils.equalsOrSuffix(qName, "Policy")) {
         if (inFirewallRule)
            firewallPolicy = FirewallPolicy.fromValue(currentOrNull());
         else
            natPolicy = NatPolicy.fromValue(currentOrNull());
      } else if (SaxUtils.equalsOrSuffix(qName, "Tcp")) {
         tcp = Boolean.parseBoolean(currentOrNull());
      } else if (SaxUtils.equalsOrSuffix(qName, "Udp")) {
         udp = Boolean.parseBoolean(currentOrNull());
      } else if (SaxUtils.equalsOrSuffix(qName, "Protocols")) {
         this.protocols = new FirewallProtocols(tcp, udp);
         this.tcp = false;
         this.udp = false;
      } else if (SaxUtils.equalsOrSuffix(qName, "DestinationIp")) {
         this.destinationIp = currentOrNull();
      } else if (SaxUtils.equalsOrSuffix(qName, "FirewallRule")) {
         this.inFirewallRule = false;
         this.firewallRules.add(new FirewallRule(firewallRuleEnabled, firewallRuleDescription, firewallPolicy,
                  protocols, port, destinationIp));
         this.firewallRuleEnabled = false;
         this.firewallRuleDescription = null;
         this.firewallPolicy = null;
         this.protocols = null;
         this.port = -1;
         this.destinationIp = null;
      } else if (SaxUtils.equalsOrSuffix(qName, "FirewallService")) {
         firewallService = new FirewallService(serviceEnabled, firewallRules);
         this.serviceEnabled = false;
         this.firewallRules = Lists.newArrayList();
      } else if (SaxUtils.equalsOrSuffix(qName, "NatType")) {
         natType = NatType.fromValue(currentOrNull());
      } else if (SaxUtils.equalsOrSuffix(qName, "MappingMode")) {
         mappingMode = MappingMode.fromValue(currentOrNull());
      } else if (qName.equalsIgnoreCase("ExternalIP")) {
         externalIP = currentOrNull();
      } else if (qName.equalsIgnoreCase("VAppScopedVmId")) {
         vAppScopedVmId = currentOrNull();
      } else if (qName.equalsIgnoreCase("VAppScopedLocalId")) {
         vAppScopedLocalId = currentOrNull();
      } else if (qName.equalsIgnoreCase("vmNicId")) {
         vmNicId = Integer.parseInt(currentOrNull());
      } else if (SaxUtils.equalsOrSuffix(qName, "OneToOneVmRule")) {
         natRules.add(new OneToOneVmRule(mappingMode, externalIP, vAppScopedVmId, vmNicId));
         this.mappingMode = null;
         this.externalIP = null;
         this.vAppScopedVmId = null;
         this.vmNicId = -1;
      } else if (qName.equalsIgnoreCase("ExternalPort")) {
         externalPort = Integer.parseInt(currentOrNull());
      } else if (qName.equalsIgnoreCase("InternalIP")) {
         internalIP = currentOrNull();
      } else if (qName.equalsIgnoreCase("InternalPort")) {
         internalPort = Integer.parseInt(currentOrNull());
      } else if (equalsOrSuffix(qName, "Protocol")) {
         natProtocol = NatProtocol.valueOf(currentOrNull());
      } else if (SaxUtils.equalsOrSuffix(qName, "PortForwardingRule")) {
         natRules.add(new PortForwardingRule(externalIP, externalPort, internalIP, internalPort, natProtocol));
         this.externalIP = null;
         this.externalPort = -1;
         this.internalIP = null;
         this.internalPort = -1;
         this.natProtocol = null;
      } else if (SaxUtils.equalsOrSuffix(qName, "VmRule")) {
         natRules.add(new VmRule(externalIP, externalPort, vAppScopedLocalId, vmNicId, internalPort, natProtocol));
         this.externalIP = null;
         this.externalPort = -1;
         this.vAppScopedLocalId = null;
         this.vmNicId = -1;
         this.internalPort = -1;
         this.natProtocol = null;
      } else if (SaxUtils.equalsOrSuffix(qName, "NatService")) {
         this.natService = new NatService(serviceEnabled, natType, natPolicy, natRules);
         this.serviceEnabled = false;
         this.natType = null;
         this.natPolicy = null;
         this.natRules = Lists.newArrayList();
      } else if (SaxUtils.equalsOrSuffix(qName, "Features")) {
         this.features = new Features(dhcpService, firewallService, natService);
         this.dhcpService = null;
         this.firewallService = null;
         this.natService = null;
      } else if (SaxUtils.equalsOrSuffix(qName, "Configuration")) {
         configuration = new OrgNetworkImpl.ConfigurationImpl(ipScope, parentNetwork, fenceMode, features);
         this.ipScope = null;
         this.parentNetwork = null;
         this.fenceMode = null;
         this.features = null;
      } else if (SaxUtils.equalsOrSuffix(qName, "AllowedExternalIpAddress")) {
         allowedExternalIpAddresses.add(currentOrNull());
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
