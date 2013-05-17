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

import static org.jclouds.cloudstack.options.ListUsersOptions.Builder.accountInDomain;
import static org.jclouds.cloudstack.options.ListUsersOptions.Builder.accountType;
import static org.jclouds.cloudstack.options.ListUsersOptions.Builder.domainId;
import static org.jclouds.cloudstack.options.ListUsersOptions.Builder.id;
import static org.jclouds.cloudstack.options.ListUsersOptions.Builder.keyword;
import static org.jclouds.cloudstack.options.ListUsersOptions.Builder.page;
import static org.jclouds.cloudstack.options.ListUsersOptions.Builder.pageSize;
import static org.jclouds.cloudstack.options.ListUsersOptions.Builder.state;
import static org.jclouds.cloudstack.options.ListUsersOptions.Builder.userName;
import static org.testng.Assert.assertEquals;

import org.jclouds.cloudstack.domain.User;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code ListUsersOptions}
 *
 * @author Andrei Savu
 */
@Test(groups = "unit")
public class ListUsersOptionsTest {


   public void testId() {
      ListUsersOptions options = new ListUsersOptions().id("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("id"));
   }

   public void testIdStatic() {
      ListUsersOptions options = id("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("id"));
   }

   public void testUserName() {
      ListUsersOptions options = new ListUsersOptions().userName("andrei");
      assertEquals(ImmutableSet.of("andrei"), options.buildQueryParameters().get("name"));
   }

   public void testUserNameStatic() {
      ListUsersOptions options = userName("andrei");
      assertEquals(ImmutableSet.of("andrei"), options.buildQueryParameters().get("name"));
   }

   public void testState() {
      ListUsersOptions options = new ListUsersOptions().state(User.State.ENABLED);
      assertEquals(ImmutableList.of("enabled"), options.buildQueryParameters().get("state"));
   }

   public void testStateStatic() {
      ListUsersOptions options = state(User.State.ENABLED);
      assertEquals(ImmutableList.of("enabled"), options.buildQueryParameters().get("state"));
   }

   public void testAccountType() {
      ListUsersOptions options = new ListUsersOptions().accountType("user");
      assertEquals(ImmutableList.of("user"), options.buildQueryParameters().get("accounttype"));
   }

   public void testAccountTypeStatic() {
      ListUsersOptions options = accountType("user");
      assertEquals(ImmutableList.of("user"), options.buildQueryParameters().get("accounttype"));
   }

   public void testKeyword() {
      ListUsersOptions options = new ListUsersOptions().keyword("key");
      assertEquals(ImmutableList.of("key"), options.buildQueryParameters().get("keyword"));
   }

   public void testKeywordStatic() {
      ListUsersOptions options = keyword("key");
      assertEquals(ImmutableList.of("key"), options.buildQueryParameters().get("keyword"));
   }

   public void testPage() {
      ListUsersOptions options = new ListUsersOptions().page(6);
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("page"));
   }

   public void testPageStatic() {
      ListUsersOptions options = page(6);
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("page"));
   }

   public void testPageSize() {
      ListUsersOptions options = new ListUsersOptions().pageSize(6);
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("pagesize"));
   }

   public void testPageSizeStatic() {
      ListUsersOptions options = pageSize(6);
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("pagesize"));
   }

   public void testAccountInDomainId() {
      ListUsersOptions options = new ListUsersOptions().accountInDomain("adrian", "6");
      assertEquals(ImmutableList.of("adrian"), options.buildQueryParameters().get("account"));
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("domainid"));
   }

   public void testAccountInDomainIdStatic() {
      ListUsersOptions options = accountInDomain("adrian", "6");
      assertEquals(ImmutableList.of("adrian"), options.buildQueryParameters().get("account"));
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("domainid"));
   }

   public void testDomainIdStatic() {
      ListUsersOptions options = domainId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("domainid"));
   }
}
