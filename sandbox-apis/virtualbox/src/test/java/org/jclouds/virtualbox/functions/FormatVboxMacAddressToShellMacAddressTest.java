/*
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

package org.jclouds.virtualbox.functions;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

/**
 * Transform a string representation of a mac address in a shell readable mac
 * address
 * 
 * @author Andrea Turli
 */
@Test(groups = "unit")
public class FormatVboxMacAddressToShellMacAddressTest {

   private static final boolean isOSX = true;
   private static final String macAddressWith00 = "0800271A9806";
   private static final String macAddressWith0c = "0811271A9806";
   private static final String macAddressWithC0 = "8011271A9806";
   private static final String macAddressWithout0 = "189827A32F31";

   @Test
   public void testFormatMacAddress() {
      assertEquals(
            new FormatVboxMacAddressToShellMacAddress(!isOSX)
                  .apply(macAddressWith00),
            "08:00:27:1a:98:06");
      assertEquals(
            new FormatVboxMacAddressToShellMacAddress(isOSX)
                  .apply(macAddressWith00),
            "8:0:27:1a:98:6");

      assertEquals(
            new FormatVboxMacAddressToShellMacAddress(!isOSX)
                  .apply(macAddressWith0c),
            "08:11:27:1a:98:06");
      assertEquals(
            new FormatVboxMacAddressToShellMacAddress(isOSX)
                  .apply(macAddressWith0c),
            "8:11:27:1a:98:6");

      assertEquals(
            new FormatVboxMacAddressToShellMacAddress(!isOSX)
                  .apply(macAddressWithC0),
            "80:11:27:1a:98:06");
      assertEquals(
            new FormatVboxMacAddressToShellMacAddress(isOSX)
                  .apply(macAddressWithC0),
            "80:11:27:1a:98:6");

      assertEquals(
            new FormatVboxMacAddressToShellMacAddress(!isOSX)
                  .apply(macAddressWithout0),
            "18:98:27:a3:2f:31");
      assertEquals(
            new FormatVboxMacAddressToShellMacAddress(isOSX)
                  .apply(macAddressWithout0),
            "18:98:27:a3:2f:31");
   }
}
