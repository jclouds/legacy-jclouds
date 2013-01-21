/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.sts.options;

import static org.jclouds.sts.options.TemporaryCredentialsOptions.Builder.serialNumber;
import static org.jclouds.sts.options.TemporaryCredentialsOptions.Builder.durationSeconds;
import static org.jclouds.sts.options.TemporaryCredentialsOptions.Builder.tokenCode;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "TemporaryCredentialsOptionsTest")
public class TemporaryCredentialsOptionsTest {

   public void testSerialNumber() {
      TemporaryCredentialsOptions options = new TemporaryCredentialsOptions().serialNumber("YourMFADeviceSerialNumber");
      assertEquals(ImmutableSet.of("YourMFADeviceSerialNumber"), options.buildFormParameters().get("SerialNumber"));
   }

   public void testSerialNumberStatic() {
      TemporaryCredentialsOptions options = serialNumber("YourMFADeviceSerialNumber");
      assertEquals(ImmutableSet.of("YourMFADeviceSerialNumber"), options.buildFormParameters().get("SerialNumber"));
   }

   public void testDurationSeconds() {
      TemporaryCredentialsOptions options = new TemporaryCredentialsOptions().durationSeconds(3600);
      assertEquals(ImmutableSet.of("3600"), options.buildFormParameters().get("DurationSeconds"));
   }

   public void testDurationSecondsStatic() {
      TemporaryCredentialsOptions options = durationSeconds(3600);
      assertEquals(ImmutableSet.of("3600"), options.buildFormParameters().get("DurationSeconds"));
   }

   public void testTokenCode() {
      TemporaryCredentialsOptions options = new TemporaryCredentialsOptions().tokenCode("123456");
      assertEquals(ImmutableSet.of("123456"), options.buildFormParameters().get("TokenCode"));
   }

   public void testTokenCodeStatic() {
      TemporaryCredentialsOptions options = tokenCode("123456");
      assertEquals(ImmutableSet.of("123456"), options.buildFormParameters().get("TokenCode"));
   }

}
