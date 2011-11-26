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

package org.jclouds.virtualbox.functions;

import static org.jclouds.virtualbox.experiment.TestUtils.computeServiceForLocalhostAndGuest;
import static org.testng.Assert.assertFalse;

import java.util.List;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.domain.Credentials;
import org.jclouds.virtualbox.BaseVirtualBoxClientLiveTest;
import org.testng.annotations.Test;

/**
 * @author Andrea Turli
 */
@Test(groups = "live", singleThreaded = true, testName = "RetrieveActiveBridgedInterfacesLiveTest")
public class RetrieveActiveBridgedInterfacesLiveTest extends
      BaseVirtualBoxClientLiveTest {

   private String guestId = "guest";
   private String hostId = "host";


   @Test
   public void retrieveAvailableBridgedInterfaceInfoTest() {
      ComputeServiceContext localHostContext = computeServiceForLocalhostAndGuest(
            hostId, "localhost", guestId, "localhost", new Credentials("toor",
                  "password"));
      List<String> bridgedInterface = new RetrieveActiveBridgedInterfaces(localHostContext).apply(hostId);
      assertFalse(bridgedInterface.isEmpty());   
   }

}