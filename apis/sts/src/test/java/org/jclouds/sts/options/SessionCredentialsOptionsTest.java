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

import static org.jclouds.sts.options.SessionCredentialsOptions.Builder.serialNumber;
import static org.jclouds.sts.options.SessionCredentialsOptions.Builder.durationSeconds;
import static org.jclouds.sts.options.SessionCredentialsOptions.Builder.tokenCode;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "SessionCredentialsOptionsTest")
public class SessionCredentialsOptionsTest {

   public void testSerialNumber() {
      SessionCredentialsOptions options = new SessionCredentialsOptions().serialNumber("YourMFADeviceSerialNumber");
      assertEquals(ImmutableSet.of("YourMFADeviceSerialNumber"), options.buildFormParameters().get("SerialNumber"));
   }

   public void testSerialNumberStatic() {
      SessionCredentialsOptions options = serialNumber("YourMFADeviceSerialNumber");
      assertEquals(ImmutableSet.of("YourMFADeviceSerialNumber"), options.buildFormParameters().get("SerialNumber"));
   }

   public void testDurationSeconds() {
      SessionCredentialsOptions options = new SessionCredentialsOptions().durationSeconds(3600);
      assertEquals(ImmutableSet.of("3600"), options.buildFormParameters().get("DurationSeconds"));
   }

   public void testDurationSecondsStatic() {
      SessionCredentialsOptions options = durationSeconds(3600);
      assertEquals(ImmutableSet.of("3600"), options.buildFormParameters().get("DurationSeconds"));
   }

   public void testTokenCode() {
      SessionCredentialsOptions options = new SessionCredentialsOptions().tokenCode("123456");
      assertEquals(ImmutableSet.of("123456"), options.buildFormParameters().get("TokenCode"));
   }

   public void testTokenCodeStatic() {
      SessionCredentialsOptions options = tokenCode("123456");
      assertEquals(ImmutableSet.of("123456"), options.buildFormParameters().get("TokenCode"));
   }

}
