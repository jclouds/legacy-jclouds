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

import static org.jclouds.cloudstack.options.ListUsageRecordsOptions.Builder.accountId;
import static org.jclouds.cloudstack.options.ListUsageRecordsOptions.Builder.accountInDomain;
import static org.jclouds.cloudstack.options.ListUsageRecordsOptions.Builder.domainId;
import static org.jclouds.cloudstack.options.ListUsageRecordsOptions.Builder.keyword;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Options to the GlobalUsageClient.listUsageOptions() API call
 *
 * @author Richard Downer
 */
@Test(groups = "unit")
public class ListUsageRecordsOptionsTest {

   public void testAccountInDomain() {
      ListUsageRecordsOptions options = new ListUsageRecordsOptions().accountInDomain("fred", "42");
      assertEquals(ImmutableSet.of("fred"), options.buildQueryParameters().get("account"));
      assertEquals(ImmutableSet.of("42"), options.buildQueryParameters().get("domainid"));
   }

   public void testAccountInDomainStatic() {
      ListUsageRecordsOptions options = accountInDomain("fred", "42");
      assertEquals(ImmutableSet.of("fred"), options.buildQueryParameters().get("account"));
      assertEquals(ImmutableSet.of("42"), options.buildQueryParameters().get("domainid"));
   }

   public void testDomainId() {
      ListUsageRecordsOptions options = new ListUsageRecordsOptions().domainId("42");
      assertEquals(ImmutableSet.of("42"), options.buildQueryParameters().get("domainid"));
   }

   public void testDomainIdStatic() {
      ListUsageRecordsOptions options = domainId("42");
      assertEquals(ImmutableSet.of("42"), options.buildQueryParameters().get("domainid"));
   }

   public void testAccountId() {
      ListUsageRecordsOptions options = new ListUsageRecordsOptions().accountId("41");
      assertEquals(ImmutableSet.of("41"), options.buildQueryParameters().get("accountid"));
   }

   public void testAccountIdStatic() {
      ListUsageRecordsOptions options = accountId("41");
      assertEquals(ImmutableSet.of("41"), options.buildQueryParameters().get("accountid"));
   }

   public void testKeyword() {
      ListUsageRecordsOptions options = new ListUsageRecordsOptions().keyword("bob");
      assertEquals(ImmutableSet.of("bob"), options.buildQueryParameters().get("keyword"));
   }

   public void testKeywordStatic() {
      ListUsageRecordsOptions options = keyword("bob");
      assertEquals(ImmutableSet.of("bob"), options.buildQueryParameters().get("keyword"));
   }
}
