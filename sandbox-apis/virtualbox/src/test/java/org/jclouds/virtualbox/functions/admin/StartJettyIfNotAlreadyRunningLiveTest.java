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

package org.jclouds.virtualbox.functions.admin;

import static org.testng.Assert.assertEquals;

import org.eclipse.jetty.server.Server;
import org.jclouds.virtualbox.BaseVirtualBoxClientLiveTest;
import org.testng.annotations.Test;

/**
 * @author Andrea Turli
 */
@Test(groups = "live", singleThreaded = true, testName = "StartJettyIfNotAlreadyRunningLiveTest")
public class StartJettyIfNotAlreadyRunningLiveTest extends BaseVirtualBoxClientLiveTest {

   private String basebaseResource = ".";
   private String port = "8080";

   @Test
   public void testLaunchJettyServer() throws Exception {
      Server server = new StartJettyIfNotAlreadyRunning(port).apply(basebaseResource);
      server.stop();
      assertEquals(server.getState(), server.STOPPED);
   }

   @Test
   public void testLaunchingSameJettyServer() throws Exception {
      Server server = new StartJettyIfNotAlreadyRunning(port).apply(basebaseResource);
      assertEquals(server.getState(), server.STARTED);
      Server sameServer = new StartJettyIfNotAlreadyRunning(port).apply(basebaseResource);
      sameServer.stop();
   }

}
