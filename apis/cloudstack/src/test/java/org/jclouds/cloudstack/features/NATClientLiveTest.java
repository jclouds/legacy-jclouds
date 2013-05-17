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
package org.jclouds.cloudstack.features;

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.cloudstack.domain.IPForwardingRule;
import org.jclouds.cloudstack.internal.BaseCloudStackClientLiveTest;
import org.jclouds.cloudstack.options.ListIPForwardingRulesOptions;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code NATClientLiveTest}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "NATClientLiveTest")
public class NATClientLiveTest extends BaseCloudStackClientLiveTest {

   @Test(enabled = false)
   // takes too long
   public void testListIPForwardingRules() throws Exception {
      Set<IPForwardingRule> response = client.getNATClient().listIPForwardingRules();
      assert null != response;
      assertTrue(response.size() >= 0);
      for (IPForwardingRule rule : response) {
         IPForwardingRule newDetails = getOnlyElement(client.getNATClient().listIPForwardingRules(
               ListIPForwardingRulesOptions.Builder.id(rule.getId())));
         assertEquals(rule.getId(), newDetails.getId());
         checkRule(rule);
      }
   }

   protected void checkRule(IPForwardingRule rule) {
      assertEquals(rule.getId(), client.getNATClient().getIPForwardingRule(rule.getId()).getId());
      assert rule.getId() != null : rule;
      assert rule.getIPAddress() != null : rule;
      assert rule.getIPAddressId() != null : rule;
      assert rule.getStartPort() > 0 : rule;
      assert rule.getProtocol() != null : rule;
      assert rule.getEndPort() > 0 : rule;
      assert rule.getState() != null : rule;
      assert rule.getVirtualMachineId() != null : rule;
      assert rule.getVirtualMachineName() != null : rule;

   }
}
