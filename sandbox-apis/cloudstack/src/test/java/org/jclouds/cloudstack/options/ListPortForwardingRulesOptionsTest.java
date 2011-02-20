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

package org.jclouds.cloudstack.options;

import static org.jclouds.cloudstack.options.ListPortForwardingRulesOptions.Builder.IPAddressId;
import static org.jclouds.cloudstack.options.ListPortForwardingRulesOptions.Builder.accountInDomain;
import static org.jclouds.cloudstack.options.ListPortForwardingRulesOptions.Builder.domainId;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests behavior of {@code ListPortForwardingRulesOptions}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ListPortForwardingRulesOptionsTest {

   public void testAccount() {
      ListPortForwardingRulesOptions options = new ListPortForwardingRulesOptions().accountInDomain("account", 6);
      assertEquals(ImmutableList.of("account"), options.buildQueryParameters().get("account"));
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("domainid"));
   }

   public void testAccountStatic() {
      ListPortForwardingRulesOptions options = accountInDomain("account", 6);
      assertEquals(ImmutableList.of("account"), options.buildQueryParameters().get("account"));
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("domainid"));
   }

   public void testName() {
      ListPortForwardingRulesOptions options = new ListPortForwardingRulesOptions().IPAddressId(9);
      assertEquals(ImmutableList.of("9"), options.buildQueryParameters().get("ipaddressid"));
   }

   public void testNameStatic() {
      ListPortForwardingRulesOptions options = IPAddressId(9);
      assertEquals(ImmutableList.of("9"), options.buildQueryParameters().get("ipaddressid"));
   }

   public void testDomainId() {
      ListPortForwardingRulesOptions options = new ListPortForwardingRulesOptions().domainId(6);
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("domainid"));
   }

   public void testDomainIdStatic() {
      ListPortForwardingRulesOptions options = domainId(6);
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("domainid"));
   }

}
