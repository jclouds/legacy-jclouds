/*
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
package org.jclouds.vcloud.director.v1_5.features;

import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.ENTITY_NON_NULL;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.TASK_COMPLETE_TIMELY;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkNetworkConfigSection;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jclouds.vcloud.director.v1_5.AbstractVAppApiLiveTest;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.Vdc;
import org.jclouds.vcloud.director.v1_5.domain.Vm;
import org.jclouds.vcloud.director.v1_5.domain.network.FirewallRule;
import org.jclouds.vcloud.director.v1_5.domain.network.FirewallRuleProtocols;
import org.jclouds.vcloud.director.v1_5.domain.network.FirewallService;
import org.jclouds.vcloud.director.v1_5.domain.network.IpRange;
import org.jclouds.vcloud.director.v1_5.domain.network.IpRanges;
import org.jclouds.vcloud.director.v1_5.domain.network.IpScope;
import org.jclouds.vcloud.director.v1_5.domain.network.NatService;
import org.jclouds.vcloud.director.v1_5.domain.network.Network.FenceMode;
import org.jclouds.vcloud.director.v1_5.domain.network.NetworkConfiguration;
import org.jclouds.vcloud.director.v1_5.domain.network.NetworkConnection;
import org.jclouds.vcloud.director.v1_5.domain.network.NetworkConnection.IpAddressAllocationMode;
import org.jclouds.vcloud.director.v1_5.domain.network.NetworkFeatures;
import org.jclouds.vcloud.director.v1_5.domain.network.NetworkServiceType;
import org.jclouds.vcloud.director.v1_5.domain.network.VAppNetworkConfiguration;
import org.jclouds.vcloud.director.v1_5.domain.section.NetworkConfigSection;
import org.jclouds.vcloud.director.v1_5.domain.section.NetworkConnectionSection;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Tests the request/response behavior of {@link VAppTemplateApi}
 * 
 * NOTE The environment MUST have at least one template configured
 *
 * @author Andrea Turli
 */
@Test(groups = { "live", "user" }, singleThreaded = true, testName = "VAppNetworksLiveTest")
public class VAppNetworksLiveTest extends AbstractVAppApiLiveTest {

   private static final String HTTP_SECURITY_GROUP = "http";
   private static final String DEFAULT_SECURITY_GROUP = "default";
   private String key;
   private Reference parentNetworkRef;
   private Map<String, NetworkConfiguration> securityGroupToNetworkConfig;
   private String orgNetworkName;

   @AfterClass(alwaysRun = true, dependsOnMethods = { "cleanUpEnvironment" })
   protected void tidyUp() {
      if (key != null) {
         try {
	         Task delete = vAppTemplateApi.getMetadataApi().deleteEntry(vAppTemplateURI, key);
	         taskDoneEventually(delete);
         } catch (Exception e) {
            logger.warn(e, "Error when deleting metadata entry '%s'", key);
         }
      }
   }
   
   @BeforeClass
   void setUp() {
      parentNetworkRef = lookUpNewtorkInVdc(networkURI);
      securityGroupToNetworkConfig = createSecurityGroupToNetworkConfiguration(parentNetworkRef);
      orgNetworkName = parentNetworkRef.getName();
   }
   
   @AfterMethod
   void cleanUpVmNetworks() {
      disconnectVmFromVAppNetwork(vm);
   }

   @Test(description = "Create a vApp Network based on an org network with `default` firewall rules applied")
   public void testCreateVAppNetworkWithDefaultSecurityGroup() {
      ImmutableList<String> securityGroups = ImmutableList.of(DEFAULT_SECURITY_GROUP);
      createVAppNetworkWithSecurityGroupOnVApp(securityGroups, vAppURI);

      // Retrieve the modified section
      NetworkConfigSection modified = vAppApi.getNetworkConfigSection(vAppURI);

      // Check the retrieved object is well formed
      checkNetworkConfigSection(modified);

      /*
       * TODO
       * powerOn machine, ssh to it, run sshd on a port, trying to connect
       * `which sshd` -p 22
       *  
       */
   }
   
   @Test(description = "Create a vApp Network based on an org network with `http` firewall rules applied")
   public void testCreateVAppNetworkWithHttpSecurityGroup() {
      ImmutableList<String> securityGroups = ImmutableList.of(HTTP_SECURITY_GROUP);
      createVAppNetworkWithSecurityGroupOnVApp(securityGroups, vAppURI);

      // Retrieve the modified section
      NetworkConfigSection modified = vAppApi.getNetworkConfigSection(vAppURI);

      // Check the retrieved object is well formed
      checkNetworkConfigSection(modified);

      /*
       * TODO
       * powerOn machine, ssh to it, run sshd on a port, trying to connect
       * `which sshd` -p 22
       *  
       */
   }
   
   @Test(description = "Create a vApp Network based on an org network with both `defautl` and `http` firewall rules applied")
   public void testCreateVAppNetworkWithDefaultAndHttpSecurityGroup() {
      ImmutableList<String> securityGroups = ImmutableList.of(DEFAULT_SECURITY_GROUP, HTTP_SECURITY_GROUP);
      createVAppNetworkWithSecurityGroupOnVApp(securityGroups, vAppURI);

      // Retrieve the modified section
      NetworkConfigSection modified = vAppApi.getNetworkConfigSection(vAppURI);

      // Check the retrieved object is well formed
      checkNetworkConfigSection(modified);

      /*
       * TODO
       * powerOn machine, ssh to it, run sshd on a port, trying to connect
       * `which sshd` -p 22
       *  
       */
   }
   
   private void createVAppNetworkWithSecurityGroupOnVApp(ImmutableList<String> securityGroups, URI vAppURI) {
      String newVAppNetworkName = generateVAppNetworkName(orgNetworkName, securityGroups);
      // Create a vAppNetwork with firewall rules
      NetworkConfigSection newSection = generateNetworkConfigSection(securityGroups, newVAppNetworkName);
      Task modifyNetworkConfigSection = vAppApi.modifyNetworkConfigSection(vAppURI, newSection);
      assertTrue(retryTaskSuccess.apply(modifyNetworkConfigSection), String.format(TASK_COMPLETE_TIMELY, "modifyNetworkConfigSection"));
      attachVmToVAppNetwork(vm, newVAppNetworkName);
   }
   
   private NetworkConfigSection generateNetworkConfigSection(List<String> securityGroups, String newVAppNetworkName) {
      
      Set<FirewallRule> firewallRules = Sets.newLinkedHashSet();
      for (String securityGroup : securityGroups) {
         Set<FirewallRule> securityGroupFirewallRules = retrieveAllFirewallRules(securityGroupToNetworkConfig.get(securityGroup).getNetworkFeatures());
         firewallRules.addAll(securityGroupFirewallRules);
      }
      
      FirewallService firewallService = createFirewallService(firewallRules);
      NatService natService = createNatService();
      IpScope ipScope = createNewIpScope();      
      NetworkConfiguration newConfiguration = NetworkConfiguration.builder()
               .ipScope(ipScope)
               .parentNetwork(parentNetworkRef)
               .fenceMode(FenceMode.NAT_ROUTED)
               .retainNetInfoAcrossDeployments(false)
               .features(createNetworkFeatures(ImmutableSet.of(firewallService, natService)))
               .build();
      
      VAppNetworkConfiguration newVAppNetworkConfiguration = VAppNetworkConfiguration.builder().networkName(newVAppNetworkName).configuration(newConfiguration).build();
      return NetworkConfigSection.builder()
             .info("modified")
             .networkConfigs(ImmutableSet.of(newVAppNetworkConfiguration))
             .build();
   }

   private void attachVmToVAppNetwork(Vm vm, String vAppNetworkName) {
      Set<NetworkConnection> networkConnections = vmApi.getNetworkConnectionSection(vm.getHref())
               .getNetworkConnections();

      NetworkConnectionSection section = NetworkConnectionSection.builder()
               .info("info")
               .primaryNetworkConnectionIndex(0)
               .build();
      
      for (NetworkConnection networkConnection : networkConnections) {
         NetworkConnection newNetworkConnection = networkConnection.toBuilder()
                  .network(vAppNetworkName)
                  .isConnected(true)
                  .networkConnectionIndex(0)
                  .ipAddressAllocationMode(IpAddressAllocationMode.POOL)
                  .build();
         
         section = section.toBuilder().networkConnection(newNetworkConnection).build();
      }
      Task configureNetwork = vmApi.modifyNetworkConnectionSection(vm.getHref(), section);
      assertTaskSucceedsLong(configureNetwork);
   }

   private IpScope createNewIpScope() {
      IpRange newIpRange = createIpRange();
      IpRanges newIpRanges = IpRanges.builder()
               .ipRange(newIpRange)
               .build();
      return IpScope.builder()
               .isInherited(false)
               .gateway("192.168.2.1")
               .netmask("255.255.0.0")
               .ipRanges(newIpRanges).build();
   }

   private IpRange createIpRange() {
      IpRange newIpRange = IpRange.builder()
               .startAddress("192.168.2.100")
               .endAddress("192.168.2.199")
               .build();
      return newIpRange;
   }

   private Reference lookUpNewtorkInVdc(final URI networkURI) {
      Vdc vdc = context.getApi().getVdcApi().getVdc(vdcURI);
      assertNotNull(vdc, String.format(ENTITY_NON_NULL, VDC));

      Set<Reference> networks = vdc.getAvailableNetworks();

      // Look up the network in the Vdc with the id configured for the tests
      Optional<Reference> parentNetwork = Iterables.tryFind(networks, new Predicate<Reference>() {
         @Override
         public boolean apply(Reference reference) {
            return reference.getHref().equals(networkURI);
         }
      });
      if (!parentNetwork.isPresent()) {
         fail(String.format("Could not find network %s in vdc", networkURI.toASCIIString()));
      }
      return parentNetwork.get();
   }

   private Set<FirewallRule> retrieveAllFirewallRules(NetworkFeatures networkFeatures) {
      Set<FirewallRule> firewallRules = Sets.newLinkedHashSet();
      for (NetworkServiceType<?> networkServiceType : networkFeatures.getNetworkServices()) {
         if (networkServiceType instanceof FirewallService) {
            firewallRules.addAll(((FirewallService) networkServiceType).getFirewallRules());
         }
      }
      return firewallRules;
   }

   private NetworkFeatures createNetworkFeatures(Set<? extends NetworkServiceType<?>> networkServices) {
      NetworkFeatures networkFeatures = NetworkFeatures.builder()
               .services(networkServices)
               .build();
      return networkFeatures;
   }


   private Set<FirewallRule> createDefaultFirewallRules() {
      FirewallRuleProtocols protocols = FirewallRuleProtocols.builder()
               .any(true)
               .build();
      FirewallRule egressAll = createFirewallRule(FirewallRuleProtocols.builder().tcp(true).build(), "allow ssh ingoing traffic", -1, 22, "in");
      FirewallRule sshIngoing = createFirewallRule(protocols, "allow all outgoing traffic", -1, -1, "out");
      return ImmutableSet.of(egressAll, sshIngoing);
   }

   private Set<FirewallRule> createHttpIngoingFirewallRule() {
      FirewallRuleProtocols protocols = FirewallRuleProtocols.builder().tcp(true).build();
      FirewallRule httpIngoing = createFirewallRule(protocols , "allow http ingoing traffic", 80, 80, "in");
      FirewallRule httpsIngoing = createFirewallRule(protocols , "allow https ingoing traffic", 443, 443, "in");
      return ImmutableSet.of(httpIngoing, httpsIngoing);
   }
   
   private FirewallRule createFirewallRule(FirewallRuleProtocols protocols, String description, int sourcePort, int outPort, String direction) {
      return FirewallRule.builder()
               .isEnabled(true)
               .description(description)
               .policy("allow")
               .protocols(protocols)
               .port(outPort)
               .destinationIp("Any")
               .sourcePort(sourcePort)
               .sourceIp("Any")
               .direction(direction)
               .enableLogging(false)
               .build();
   }
   
   private FirewallService createFirewallService(Set<FirewallRule> firewallRules) {
      FirewallService firewallService = FirewallService.builder()
               .enabled(true)
               .defaultAction("drop")
               .logDefaultAction(false)
               .firewallRules(firewallRules)
               .build();
      return firewallService;
   }
   
   private NatService createNatService() {
      NatService natService = NatService.builder()
               .enabled(true)
               .natType("ipTranslation")
               .policy("allowTraffic")
               .build();
      return natService;
   }
   
   private Map<String, NetworkConfiguration> createSecurityGroupToNetworkConfiguration(Reference parentNetworkRef) {
      Set<FirewallRule> defaultFirewallRules = createDefaultFirewallRules();
      Set<FirewallRule> httpFirewallRules = createHttpIngoingFirewallRule();

      Map<String, NetworkConfiguration> securityGroupToNetworkConfigurations = Maps.newHashMap();
      securityGroupToNetworkConfigurations.put(DEFAULT_SECURITY_GROUP, createNetworkConfiguration(parentNetworkRef, defaultFirewallRules));
      securityGroupToNetworkConfigurations.put(HTTP_SECURITY_GROUP, createNetworkConfiguration(parentNetworkRef, httpFirewallRules));
      
      return securityGroupToNetworkConfigurations;
   }

   private NetworkConfiguration createNetworkConfiguration(Reference parentNetworkRef, Set<FirewallRule> newFirewallRules) {
      FirewallService firewallService = createFirewallService(newFirewallRules);

      IpScope ipScope = createNewIpScope();      

      NetworkConfiguration newConfiguration = NetworkConfiguration.builder()
               .ipScope(ipScope)
               .parentNetwork(parentNetworkRef)
               .fenceMode(FenceMode.NAT_ROUTED)
               .retainNetInfoAcrossDeployments(false)
               .features(createNetworkFeatures(ImmutableSet.of(firewallService)))
               .build();
      return newConfiguration;
   }
   
   private static String generateVAppNetworkName(String orgNetworkName, List<String> securityGroupNames) {
      return orgNetworkName + "-" + Joiner.on("+").join(securityGroupNames);
   }

   private void disconnectVmFromVAppNetwork(Vm vm) {
      
      Set<NetworkConnection> networkConnections = vmApi.getNetworkConnectionSection(vm.getHref())
               .getNetworkConnections();

      NetworkConnectionSection section = NetworkConnectionSection.builder()
               .info("info")
               .primaryNetworkConnectionIndex(0)
               .build();

      for (NetworkConnection networkConnection : networkConnections) {
         section = section
                  .toBuilder()
                  .networkConnection(networkConnection.toBuilder()
                           .network("none")
                           .ipAddressAllocationMode(IpAddressAllocationMode.NONE)
                           .build())
                  .build();
      } 
      Task cleanUpNetworks = vmApi.modifyNetworkConnectionSection(vm.getHref(), section);
      assertTaskSucceedsLong(cleanUpNetworks);
   }

}
