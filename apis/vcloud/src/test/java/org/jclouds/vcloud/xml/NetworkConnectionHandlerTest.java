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
package org.jclouds.vcloud.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.UnknownHostException;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.vcloud.domain.NetworkConnection;
import org.jclouds.vcloud.domain.network.IpAddressAllocationMode;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code NetworkConnectionHandler}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "NetworkConnectionHandlerTest")
public class NetworkConnectionHandlerTest extends BaseHandlerTest {

   public void testDefault() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/networkconnection.xml");

      NetworkConnection result = factory.create(injector.getInstance(NetworkConnectionHandler.class)).parse(is);

      checkNetworkConnection(result);

   }

   @Test(enabled = false)
   public static void checkNetworkConnection(NetworkConnection result) {
      assertEquals(result.getNetwork(), "internet01");
      assertEquals(result.getNetworkConnectionIndex(), 0);
      assertEquals(result.getIpAddress(), "174.47.101.164");
      assertEquals(result.getExternalIpAddress(), null);
      assertEquals(result.isConnected(), true);
      assertEquals(result.getMACAddress(), "00:50:56:01:01:f2");
      assertEquals(result.getIpAddressAllocationMode(), IpAddressAllocationMode.POOL);
   }
}
