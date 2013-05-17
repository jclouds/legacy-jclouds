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
package org.jclouds.cloudstack.options;

import static org.jclouds.cloudstack.options.CreateAccountOptions.Builder.account;
import static org.jclouds.cloudstack.options.CreateAccountOptions.Builder.domainId;
import static org.jclouds.cloudstack.options.CreateAccountOptions.Builder.networkDomain;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code CreateAccountOptions}
 *
 * @author Andrei Savu
 */
@Test(groups = "unit")
public class CreateAccountOptionsTest {

   public void testNetworkDomain() {
      CreateAccountOptions options = new CreateAccountOptions().networkDomain("net");
      assertEquals(ImmutableSet.of("net"), options.buildQueryParameters().get("networkdomain"));
   }

   public void testNetworkDomainStatic() {
      CreateAccountOptions options = networkDomain("net");
      assertEquals(ImmutableSet.of("net"), options.buildQueryParameters().get("networkdomain"));
   }

   public void testAccount() {
      CreateAccountOptions options = new CreateAccountOptions().account("accountName");
      assertEquals(ImmutableSet.of("accountName"), options.buildQueryParameters().get("account"));
   }

   public void testAccountStatic() {
      CreateAccountOptions options = account("accountName");
      assertEquals(ImmutableSet.of("accountName"), options.buildQueryParameters().get("account"));
   }

   public void testAccountDomain() {
      CreateAccountOptions options = new CreateAccountOptions().domainId("6");
      assertEquals(ImmutableSet.of("6"), options.buildQueryParameters().get("domainid"));
   }

   public void testDomainIdStatic() {
      CreateAccountOptions options = domainId("6");
      assertEquals(ImmutableSet.of("6"), options.buildQueryParameters().get("domainid"));
   }
}
