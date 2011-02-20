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

import static org.jclouds.cloudstack.options.AssociateIPAddressOptions.Builder.account;
import static org.jclouds.cloudstack.options.AssociateIPAddressOptions.Builder.domainId;
import static org.jclouds.cloudstack.options.AssociateIPAddressOptions.Builder.networkId;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests behavior of {@code AssociateIPAddressOptions}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class AssociateIPAddressOptionsTest {

   public void testAccount() {
      AssociateIPAddressOptions options = new AssociateIPAddressOptions().account("account");
      assertEquals(ImmutableList.of("account"), options.buildQueryParameters().get("account"));
   }

   public void testAccountStatic() {
      AssociateIPAddressOptions options = account("account");
      assertEquals(ImmutableList.of("account"), options.buildQueryParameters().get("account"));
   }

   public void testDomainId() {
      AssociateIPAddressOptions options = new AssociateIPAddressOptions().domainId(6);
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("domainid"));
   }

   public void testDomainIdStatic() {
      AssociateIPAddressOptions options = domainId(6);
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("domainid"));
   }

   public void testNetworkId() {
      AssociateIPAddressOptions options = new AssociateIPAddressOptions().networkId(6);
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("networkid"));
   }

   public void testNetworkIdStatic() {
      AssociateIPAddressOptions options = networkId(6);
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("networkid"));
   }
}
