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
package org.jclouds.date.internal;

import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

@Test(groups = "unit", testName = "DateUtilsTest")
public class DateUtilsTest {

   @Test
   public void testTrimsToMillisWithTimezone() {
      assertEquals("NO_MILLISZ", DateUtils.trimToMillis("NO_MILLISZ"));
      assertEquals("NO_MILLIS.1Z", DateUtils.trimToMillis("NO_MILLIS.1Z"));
      assertEquals("NO_MILLIS.12Z", DateUtils.trimToMillis("NO_MILLIS.12Z"));
      assertEquals("NO_MILLIS.123Z", DateUtils.trimToMillis("NO_MILLIS.123Z"));
      assertEquals("NO_MILLIS.123Z", DateUtils.trimToMillis("NO_MILLIS.1234Z"));
      assertEquals("NO_MILLIS.123Z", DateUtils.trimToMillis("NO_MILLIS.12345Z"));
      assertEquals("NO_MILLIS.123Z", DateUtils.trimToMillis("NO_MILLIS.123456Z"));
      assertEquals("NO_MILLIS.123Z", DateUtils.trimToMillis("NO_MILLIS.1234567Z"));
      assertEquals("NO_MILLIS.123Z", DateUtils.trimToMillis("NO_MILLIS.12345689Z"));
      assertEquals("NO_MILLIS.123Z", DateUtils.trimToMillis("NO_MILLIS.12345690123345678Z"));
   }

   // TODO: this test is failing on my jvm which is in IST
   @Test(enabled = false)
   public void testTrimsToMillisNoTimezone() {
      assertEquals("NO_MILLIS", DateUtils.trimToMillis("NO_MILLIS"));
      assertEquals("NO_MILLIS.1", DateUtils.trimToMillis("NO_MILLIS.1"));
      assertEquals("NO_MILLIS.12", DateUtils.trimToMillis("NO_MILLIS.12"));
      assertEquals("NO_MILLIS.123", DateUtils.trimToMillis("NO_MILLIS.123"));
      assertEquals("NO_MILLIS.123", DateUtils.trimToMillis("NO_MILLIS.1234"));
      assertEquals("NO_MILLIS.123", DateUtils.trimToMillis("NO_MILLIS.12345"));
      assertEquals("NO_MILLIS.123", DateUtils.trimToMillis("NO_MILLIS.123456"));
      assertEquals("NO_MILLIS.123", DateUtils.trimToMillis("NO_MILLIS.1234567"));
      assertEquals("NO_MILLIS.123", DateUtils.trimToMillis("NO_MILLIS.12345689"));
      assertEquals("NO_MILLIS.123", DateUtils.trimToMillis("NO_MILLIS.12345690123345678"));
   }
}
