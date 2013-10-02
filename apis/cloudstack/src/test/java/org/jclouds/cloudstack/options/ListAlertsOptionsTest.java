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

import static org.jclouds.cloudstack.options.ListAlertsOptions.Builder.id;
import static org.jclouds.cloudstack.options.ListAlertsOptions.Builder.keyword;
import static org.jclouds.cloudstack.options.ListAlertsOptions.Builder.type;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests behavior of {@code ListAlertsOptions}
 * 
 * @author Richard Downer
 */
@Test(groups = "unit")
public class ListAlertsOptionsTest {

   public void testId() {
      ListAlertsOptions options = new ListAlertsOptions().id("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("id"));
   }

   public void testIdStatic() {
      ListAlertsOptions options = id("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("id"));
   }

   public void testKeyword() {
      ListAlertsOptions options = new ListAlertsOptions().keyword("fred");
      assertEquals(ImmutableList.of("fred"), options.buildQueryParameters().get("keyword"));
   }

   public void testKeywordStatic() {
      ListAlertsOptions options = keyword("fred");
      assertEquals(ImmutableList.of("fred"), options.buildQueryParameters().get("keyword"));
   }

   public void testType() {
      ListAlertsOptions options = new ListAlertsOptions().type("42");
      assertEquals(ImmutableList.of("42"), options.buildQueryParameters().get("type"));
   }

   public void testTypeStatic() {
      ListAlertsOptions options = type("42");
      assertEquals(ImmutableList.of("42"), options.buildQueryParameters().get("type"));
   }
}
