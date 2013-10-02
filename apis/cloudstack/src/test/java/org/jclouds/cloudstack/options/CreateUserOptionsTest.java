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

import static org.jclouds.cloudstack.options.CreateUserOptions.Builder.domainId;
import static org.jclouds.cloudstack.options.CreateUserOptions.Builder.timezone;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code CreateUserOptions}
 *
 * @author Andrei Savu
 */
@Test(groups = "unit")
public class CreateUserOptionsTest {

   public void testDomainId() {
      CreateUserOptions options = new CreateUserOptions().domainId("6");
      assertEquals(ImmutableSet.of("6"), options.buildQueryParameters().get("domainid"));
   }

   public void testDomainIdStatic() {
      CreateUserOptions options = domainId("6");
      assertEquals(ImmutableSet.of("6"), options.buildQueryParameters().get("domainid"));
   }

   public void testTimezone() {
      CreateUserOptions options = new CreateUserOptions().timezone("something");
      assertEquals(ImmutableSet.of("something"), options.buildQueryParameters().get("timezone"));
   }

   public void testTimezoneStatic() {
      CreateUserOptions options = timezone("something");
      assertEquals(ImmutableSet.of("something"), options.buildQueryParameters().get("timezone"));
   }
}
