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

import static org.jclouds.sts.options.AssumeRoleOptions.Builder.externalId;
import static org.jclouds.sts.options.AssumeRoleOptions.Builder.durationSeconds;
import static org.jclouds.sts.options.AssumeRoleOptions.Builder.policy;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "AssumeRoleOptionsTest")
public class AssumeRoleOptionsTest {

   public void testExternalId() {
      AssumeRoleOptions options = new AssumeRoleOptions().externalId("123ABC");
      assertEquals(ImmutableSet.of("123ABC"), options.buildFormParameters().get("ExternalId"));
   }

   public void testExternalIdStatic() {
      AssumeRoleOptions options = externalId("123ABC");
      assertEquals(ImmutableSet.of("123ABC"), options.buildFormParameters().get("ExternalId"));
   }

   public void testDurationSeconds() {
      AssumeRoleOptions options = new AssumeRoleOptions().durationSeconds(3600);
      assertEquals(ImmutableSet.of("3600"), options.buildFormParameters().get("DurationSeconds"));
   }

   public void testDurationSecondsStatic() {
      AssumeRoleOptions options = durationSeconds(3600);
      assertEquals(ImmutableSet.of("3600"), options.buildFormParameters().get("DurationSeconds"));
   }

   String policy = "{\"Statement\":[{\"Sid\":\"Stmt1\",\"Effect\":\"Allow\",\"Action\":\"s3:*\",\"Resource\":\"*\"}]}";

   public void testPolicy() {
      AssumeRoleOptions options = new AssumeRoleOptions().policy(policy);
      assertEquals(ImmutableSet.of(policy), options.buildFormParameters().get("Policy"));
   }

   public void testPolicyStatic() {
      AssumeRoleOptions options = policy(policy);
      assertEquals(ImmutableSet.of(policy), options.buildFormParameters().get("Policy"));
   }

}
