/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.atmosonline.saas.options;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests behavior of {@code ListOptions}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "emcsaas.ListOptionsTest")
public class ListOptionsTest {

   public void testToken() {
      ListOptions options = new ListOptions().token("a");
      assertEquals(ImmutableList.of("a"), options.buildRequestHeaders().get("x-emc-token"));
   }
   
   public void testLimit() {
      int limit = 1;
      ListOptions options = new ListOptions().limit(limit);
      assertEquals(ImmutableList.of("1"), options.buildRequestHeaders().get("x-emc-limit"));
   }

   public void testTokenStatic() {
      ListOptions options = ListOptions.Builder.token("a");
      assertEquals(ImmutableList.of("a"), options.buildRequestHeaders().get("x-emc-token"));
   }

   public void testLimitStatic() {
      int limit = 1;
      ListOptions options = ListOptions.Builder.limit(limit);
      assertEquals(ImmutableList.of("1"), options.buildRequestHeaders().get("x-emc-limit"));
   }
}
