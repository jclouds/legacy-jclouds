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
import org.jclouds.virtualbox.statements.ScanNetworkWithPing;
import org.testng.annotations.Test;

/**
 * @author Andrea Turli
 */
@Test(groups = "unit")
public class ScanNetworkWithPingTest {

   private static final String network = "192.168.1.1";
   private static final String networkWithoutLastNumber = "192.168.1";

   public void testGetIPAdressFromMacAddressUnix() {
      ScanNetworkWithPing statement = new ScanNetworkWithPing(network);
      assertEquals(statement.render(OsFamily.UNIX), "for i in {1..254} ; do ping -c 1 -t 1 " + networkWithoutLastNumber + ".$i & done");

   }

}
