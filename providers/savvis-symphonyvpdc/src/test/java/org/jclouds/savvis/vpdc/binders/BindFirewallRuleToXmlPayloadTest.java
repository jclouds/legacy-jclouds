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
package org.jclouds.savvis.vpdc.binders;

import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jclouds.savvis.vpdc.domain.FirewallRule;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code BindVMSpecToXmlPayload}
 * 
 * @author Kedar Dave
 */
@Test(groups = "unit")
public class BindFirewallRuleToXmlPayloadTest {

   public void test() throws IOException {
      String expected = Strings2.toStringAndClose(getClass().getResourceAsStream("/firewallService-default.xml"));

      FirewallRule firewallRule = FirewallRule.builder().firewallType("SERVER_TIER_FIREWALL").isEnabled(true).source("internet")
	  	.destination("VM Tier01").port("22").protocol("Tcp").policy("allow").description("Server Tier Firewall Rule").isLogged(false).build();

      assertEquals(new BindFirewallRuleToXmlPayload().generateXml(firewallRule), expected);
   }
}
