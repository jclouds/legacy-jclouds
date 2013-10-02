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

import static org.jclouds.cloudstack.options.ListAccountsOptions.Builder.accountInDomain;
import static org.jclouds.cloudstack.options.ListAccountsOptions.Builder.cleanupRequired;
import static org.jclouds.cloudstack.options.ListAccountsOptions.Builder.domainId;
import static org.jclouds.cloudstack.options.ListAccountsOptions.Builder.id;
import static org.jclouds.cloudstack.options.ListAccountsOptions.Builder.recursive;
import static org.jclouds.cloudstack.options.ListAccountsOptions.Builder.state;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests behavior of {@code ListAccountsOptions}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ListAccountsOptionsTest {

   public void testAccountInDomainId() {
      ListAccountsOptions options = new ListAccountsOptions().accountInDomain("adrian", "6");
      assertEquals(ImmutableList.of("adrian"), options.buildQueryParameters().get("account"));
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("domainid"));
   }

   public void testAccountInDomainIdStatic() {
      ListAccountsOptions options = accountInDomain("adrian", "6");
      assertEquals(ImmutableList.of("adrian"), options.buildQueryParameters().get("account"));
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("domainid"));
   }

   public void testName() {
      ListAccountsOptions options = new ListAccountsOptions().id("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("id"));
   }

   public void testNameStatic() {
      ListAccountsOptions options = id("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("id"));
   }

   public void testRecursive() {
      ListAccountsOptions options = new ListAccountsOptions().recursive(true);
      assertEquals(ImmutableList.of("true"), options.buildQueryParameters().get("isrecursive"));
   }

   public void testRecursiveStatic() {
      ListAccountsOptions options = recursive(true);
      assertEquals(ImmutableList.of("true"), options.buildQueryParameters().get("isrecursive"));
   }

   public void testState() {
      ListAccountsOptions options = new ListAccountsOptions().state("state");
      assertEquals(ImmutableList.of("state"), options.buildQueryParameters().get("state"));
   }

   public void testStateStatic() {
      ListAccountsOptions options = state("state");
      assertEquals(ImmutableList.of("state"), options.buildQueryParameters().get("state"));
   }

   public void testCleanupRequired() {
      ListAccountsOptions options = new ListAccountsOptions().cleanupRequired(true);
      assertEquals(ImmutableList.of("true"), options.buildQueryParameters().get("iscleanuprequired"));
   }

   public void testCleanupRequiredStatic() {
      ListAccountsOptions options = cleanupRequired(true);
      assertEquals(ImmutableList.of("true"), options.buildQueryParameters().get("iscleanuprequired"));
   }

   public void testId() {
      ListAccountsOptions options = new ListAccountsOptions().id("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("id"));
   }

   public void testDomainId() {
      ListAccountsOptions options = new ListAccountsOptions().domainId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("domainid"));
   }

   public void testIdStatic() {
      ListAccountsOptions options = id("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("id"));
   }

   public void testDomainIdStatic() {
      ListAccountsOptions options = domainId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("domainid"));
   }
}
