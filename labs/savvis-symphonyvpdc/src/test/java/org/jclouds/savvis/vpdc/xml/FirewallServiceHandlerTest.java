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
package org.jclouds.savvis.vpdc.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.Factory;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.savvis.vpdc.domain.FirewallRule;
import org.jclouds.savvis.vpdc.domain.FirewallService;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code FirewallServiceHandler and @code FirewallRuleHandler}
 * 
 * @author Kedar Dave
 */
@Test(groups = "unit")
public class FirewallServiceHandlerTest {

	 /*new FirewallRule(null, null, null, null, "SERVER_TIER_FIREWALL", true, "internet" , "VM Tier01" , 
   		  "22", "allow", "Server Tier Firewall Rule", false, "Tcp"),
   		  new FirewallRule(null, null, null, null, "SERVER_TIER_FIREWALL", true, "VM Tier03" , "VM Tier03" , 
           		  null, "allow", "Server Tier Firewall Rule", false, "Icmp-ping")));*/
	
   public void test() {
      InputStream is = getClass().getResourceAsStream("/firewallService.xml");
      Injector injector = Guice.createInjector(new SaxParserModule());
      Factory factory = injector.getInstance(ParseSax.Factory.class);
      FirewallService result = factory.create(injector.getInstance(FirewallServiceHandler.class)).parse(is);
      assertEquals(result.isEnabled(), false);
      assertEquals(
            result.getFirewallRules(),
            ImmutableSet.<FirewallRule> of(
            	  FirewallRule.builder().firewallType("SERVER_TIER_FIREWALL").isEnabled(false).source("internet")
            	  	.destination("VM Tier01").port("22").protocol("Tcp").policy("allow").description("Server Tier Firewall Rule").isLogged(false).build(),
            	  FirewallRule.builder().firewallType("SERVER_TIER_FIREWALL").isEnabled(true).source("VM Tier03")
            	  	.destination("VM Tier03").protocol("Icmp-ping").policy("allow").description("Server Tier Firewall Rule").isLogged(false).build()));
   }

}
