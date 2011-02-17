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

package org.jclouds.cloudstack.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.cloudstack.domain.SecurityGroup;
import org.jclouds.cloudstack.options.ListSecurityGroupsOptions;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@code SecurityGroupClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "SecurityGroupClientLiveTest")
public class SecurityGroupClientLiveTest extends BaseCloudStackClientLiveTest {

   public void testCreateDestroySecurityGroup() throws Exception {
      SecurityGroup group = null;
      try {
         group = client.getSecurityGroupClient().createSecurityGroup(prefix);
         assertEquals(group.getName(), prefix);
         checkGroup(group);
      } finally {
         if (group != null) {
            client.getSecurityGroupClient().deleteSecurityGroup(group.getId());
            assertEquals(client.getSecurityGroupClient().getSecurityGroup(group.getId()), null);
         }
      }

   }

   public void testListSecurityGroups() throws Exception {
      Set<SecurityGroup> response = client.getSecurityGroupClient().listSecurityGroups();
      assert null != response;
      long groupCount = response.size();
      assertTrue(groupCount >= 0);
      for (SecurityGroup group : response) {
         SecurityGroup newDetails = Iterables.getOnlyElement(client.getSecurityGroupClient().listSecurityGroups(
               ListSecurityGroupsOptions.Builder.id(group.getId())));
         assertEquals(group, newDetails);
         checkGroup(group);
      }
   }

   protected void checkGroup(SecurityGroup group) {
      assertEquals(group, client.getSecurityGroupClient().getSecurityGroup(group.getId()));
      assert group.getId() > 0 : group;
      assert group.getName() != null : group;
      assert group.getAccount() != null : group;
      assert group.getDescription() != null : group;
      assert group.getDomain() != null : group;
      assert group.getDomainId() >= 0 : group;
      assert group.getIngressRules() != null : group;
   }

}
