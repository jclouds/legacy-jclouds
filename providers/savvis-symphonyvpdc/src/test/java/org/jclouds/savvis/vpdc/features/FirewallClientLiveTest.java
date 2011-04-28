/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.savvis.vpdc.features;

import java.util.concurrent.TimeUnit;

import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.savvis.vpdc.domain.FirewallRule;
import org.jclouds.savvis.vpdc.domain.Resource;
import org.jclouds.savvis.vpdc.domain.Task;
import org.jclouds.savvis.vpdc.predicates.TaskSuccess;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public class FirewallClientLiveTest extends BaseVPDCClientLiveTest {

   private FirewallClient client;
   private String billingSiteId;
   private String vpdcId;

   @Override
   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      super.setupClient();
      client = restContext.getApi().getFirewallClient();
   }
   
   @Test(groups = "live")
   public void testAddFirewallRule() throws Exception {
	  billingSiteId = restContext.getApi().getBrowsingClient().getOrg(null).getId();// default
	  vpdcId = Iterables.find(restContext.getApi().getBrowsingClient().getOrg(billingSiteId).getVDCs(),
	           new Predicate<Resource>() {
	
	              // try to find the first VDC owned by the current user
	              // check here for what the email property might be, or in
	              // the jclouds-wire.log
	              @Override
	              public boolean apply(Resource arg0) {
	                 String description = restContext.getApi().getBrowsingClient().getVDCInOrg(billingSiteId,
	                          arg0.getId()).getDescription();
	                 return description.indexOf(email) != -1;
	              }
	
	           }).getId();
	   
      String networkTierName = Iterables.get(
               restContext.getApi().getBrowsingClient().getVDCInOrg(billingSiteId, vpdcId).getAvailableNetworks(), 0)
               .getName();   
	      
	   FirewallRule firewallRule = FirewallRule.builder().firewallType("SERVER_TIER_FIREWALL").isEnabled(true).source("internet")
	  	.destination(networkTierName).port("10000").protocol("Tcp").policy("allow").description("Server Tier Firewall Rule").isLogged(false).build();
	   
	   System.out.printf("adding firewall rule:%s %n", firewallRule.toString());
	   
	   Task task = client.addFirewallRule(billingSiteId, vpdcId, firewallRule);
	   
	   // make sure there's no error
	   assert task.getId() != null && task.getError() == null : task;
	   
	   assert this.taskTester.apply(task.getId());
   }
   
   @Test(groups = "live", dependsOnMethods = {"testAddFirewallRule"})
   public void testDeleteFirewallRule() throws Exception {
	   billingSiteId = restContext.getApi().getBrowsingClient().getOrg(null).getId();// default
	   vpdcId = Iterables.find(restContext.getApi().getBrowsingClient().getOrg(billingSiteId).getVDCs(),
	               new Predicate<Resource>() {

	                  // try to find the first VDC owned by the current user
	                  // check here for what the email property might be, or in
	                  // the jclouds-wire.log
	                  @Override
	                  public boolean apply(Resource arg0) {
	                     String description = restContext.getApi().getBrowsingClient().getVDCInOrg(billingSiteId,
	                              arg0.getId()).getDescription();
	                     return description.indexOf(email) != -1;
	                  }

	               }).getId();
	      
      String networkTierName = Iterables.get(
               restContext.getApi().getBrowsingClient().getVDCInOrg(billingSiteId, vpdcId).getAvailableNetworks(), 0)
               .getName();
	      
	   FirewallRule firewallRule = FirewallRule.builder().firewallType("SERVER_TIER_FIREWALL").isEnabled(true).source("internet")
	  	.destination(networkTierName).port("10000").protocol("Tcp").policy("allow").description("Server Tier Firewall Rule").isLogged(false).build();

	   System.out.printf("deleting firewall rule:%s %n", firewallRule.toString());
	   
	   Task task = client.deleteFirewallRule(billingSiteId, vpdcId, firewallRule);
	   
	   // make sure there's no error
	   assert task.getId() != null && task.getError() == null : task;

	   assert this.taskTester.apply(task.getId());
   }

   @AfterGroups(groups = "live")
   protected void tearDown() {
      //TODO cleanup resources
      super.tearDown();
   }
}