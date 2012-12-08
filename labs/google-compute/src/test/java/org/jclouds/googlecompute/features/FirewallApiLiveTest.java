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

package org.jclouds.googlecompute.features;

import com.google.common.collect.Lists;
import org.jclouds.collect.PagedIterable;
import org.jclouds.googlecompute.domain.Firewall;
import org.jclouds.googlecompute.domain.FirewallRule;
import org.jclouds.googlecompute.internal.BaseGoogleComputeApiLiveTest;
import org.jclouds.googlecompute.options.ListOptions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author David Alves
 */
public class FirewallApiLiveTest extends BaseGoogleComputeApiLiveTest {

   private static final String FIREWALL_NAME = "firewall-api-live-test-firewall";
   private static final int TIME_WAIT = 10;

   private Firewall firewall;

   @BeforeClass(groups = {"integration", "live"})
   public void setupContext() {
      super.setupContext();
      firewall = Firewall.builder()
              .name(FIREWALL_NAME)
              .network(getDefaultNetworkUrl(getUserProject()))
              .addAllowed(
                      FirewallRule.builder()
                              .IPProtocol("tcp")
                              .addPort("22")
                              .build()
              )
              .addSourceRange("10.0.0.0/8")
              .addSourceTag("tag1")
              .addTargetTag("tag2")
              .build();
   }

   private FirewallApi api() {
      return context.getApi().getFirewallApi();
   }

   @Test(groups = "live")
   public void testInsertFirewall() {

      assertOperationDoneSucessfully(api().insert(getUserProject(), firewall), TIME_WAIT);

   }

   @Test(groups = "live", dependsOnMethods = "testInsertFirewall")
   public void testGetFirewall() {

      Firewall firewall = api().get(getUserProject(), FIREWALL_NAME);
      assertNotNull(firewall);
      assertFirewallEquals(firewall, this.firewall);
   }

   @Test(groups = "live", dependsOnMethods = "testGetFirewall")
   public void testListFirewall() {

      PagedIterable<Firewall> firewalls = api().list(getUserProject(), ListOptions.builder()
              .filter("name eq " + FIREWALL_NAME)
              .build());

      List<Firewall> firewallsAsList = Lists.newArrayList(firewalls.concat());

      assertEquals(firewallsAsList.size(), 1);

      assertFirewallEquals(getOnlyElement(firewallsAsList), firewall);

   }

   @Test(groups = "live", dependsOnMethods = "testListFirewall")
   public void testDeleteFirewall() {

      assertOperationDoneSucessfully(api().delete(getUserProject(), FIREWALL_NAME), TIME_WAIT);
   }

   private void assertFirewallEquals(Firewall result, Firewall expected) {
      assertEquals(result.getName(), expected.getName());
      assertEquals(getOnlyElement(result.getSourceRanges()), getOnlyElement(expected.getSourceRanges()));
      assertEquals(getOnlyElement(result.getSourceTags()), getOnlyElement(expected.getSourceTags()));
      assertEquals(getOnlyElement(result.getTargetTags()), getOnlyElement(expected.getTargetTags()));
      assertEquals(getOnlyElement(result.getAllowed()), getOnlyElement(expected.getAllowed()));
   }

}
