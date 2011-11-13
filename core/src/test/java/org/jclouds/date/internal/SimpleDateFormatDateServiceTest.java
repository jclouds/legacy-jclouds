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

import java.util.Date;

import static org.testng.AssertJUnit.assertEquals;

@Test(groups = "unit", testName = "SimpleDateFormatDateServiceTest")
public class SimpleDateFormatDateServiceTest {
   // TODO: this test has to work when a machine is not in GMT timezone
   @Test(enabled = false)
   public void testCorrectHandlingOfMillis() {
      Date date = new SimpleDateFormatDateService().iso8601DateParse("2011-11-07T11:19:13.38225Z");
      assertEquals("Mon Nov 07 11:19:13 GMT 2011", date.toString());
   }

   // TODO: this test has to work when a machine is not in GMT timezone
   @Test(enabled = false)
   public void testCorrectHandlingOfMillisWithNoTimezone() {
      Date date = new SimpleDateFormatDateService().iso8601DateParse("2009-02-03T05:26:32.612278");
      assertEquals("Tue Feb 03 05:26:32 GMT 2009", date.toString());
   }
}
