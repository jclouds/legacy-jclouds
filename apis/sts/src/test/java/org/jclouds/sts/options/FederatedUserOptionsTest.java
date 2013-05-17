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
package org.jclouds.sts.options;

import static org.jclouds.sts.options.FederatedUserOptions.Builder.durationSeconds;
import static org.jclouds.sts.options.FederatedUserOptions.Builder.policy;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "FederatedUserOptionsTest")
public class FederatedUserOptionsTest {

   public void testDurationSeconds() {
      FederatedUserOptions options = new FederatedUserOptions().durationSeconds(3600);
      assertEquals(ImmutableSet.of("3600"), options.buildFormParameters().get("DurationSeconds"));
   }

   public void testDurationSecondsStatic() {
      FederatedUserOptions options = durationSeconds(3600);
      assertEquals(ImmutableSet.of("3600"), options.buildFormParameters().get("DurationSeconds"));
   }

   String policy = "{\"Statement\":[{\"Sid\":\"Stmt1\",\"Effect\":\"Allow\",\"Action\":\"s3:*\",\"Resource\":\"*\"}]}";

   public void testPolicy() {
      FederatedUserOptions options = new FederatedUserOptions().policy(policy);
      assertEquals(ImmutableSet.of(policy), options.buildFormParameters().get("Policy"));
   }

   public void testPolicyStatic() {
      FederatedUserOptions options = policy(policy);
      assertEquals(ImmutableSet.of(policy), options.buildFormParameters().get("Policy"));
   }

}
