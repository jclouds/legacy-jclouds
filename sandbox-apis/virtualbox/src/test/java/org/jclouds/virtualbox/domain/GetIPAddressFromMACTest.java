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
package org.jclouds.virtualbox.domain;

import static org.testng.Assert.assertEquals;

import org.jclouds.scriptbuilder.domain.OsFamily;
import org.testng.annotations.Test;

/**
 * @author Andrea Turli
 */
@Test(groups = "unit")
public class GetIPAddressFromMACTest {
   
   private static final String macAddressWith00 = "0800271A9806";
   private static final String unixMacAddressWith00 =  "08:00:27:1a:98:06";
   private static final String osxMacAddressWith00 =   "8:0:27:1a:98:6";
   private static final String macAddressWith0c = "0811271A9806";
   private static final String macAddressWithC0 = "8011271A9806";
   private static final String macAddressWithout0 = "189827A32F31";

   public void testGetIPAdressFromMACUNIX() {
      GetIPAdressFromMAC getIPAdressFromMAC = new GetIPAdressFromMAC(macAddressWith00);
      assertEquals(getIPAdressFromMAC.render(OsFamily.UNIX), "arp -an | grep " + unixMacAddressWith00 + "\n");
   }

   public void testGetIPAdressFromMACOsX() {
      GetIPAdressFromMAC getIPAdressFromMAC = new GetIPAdressFromMAC(macAddressWith00);
      assertEquals(getIPAdressFromMAC.render(OsFamily.UNIX), "arp -an | grep " + osxMacAddressWith00 + "\n");
   }
}
