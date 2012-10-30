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
package org.jclouds.trmk.vcloud_0_8.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;
import java.net.UnknownHostException;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.trmk.vcloud_0_8.domain.NetworkExtendedInfo;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code NetworkExtendedInfoHandler}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "NetworkExtendedInfoHandlerTest")
public class NetworkExtendedInfoHandlerTest extends BaseHandlerTest {

   public void test1() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/TerremarkNetwork.xml");

      NetworkExtendedInfo result = factory.create(injector.getInstance(NetworkExtendedInfoHandler.class)).parse(is);
      assertEquals(result, new NetworkExtendedInfo("XXXXX", URI
               .create("https://services.enterprisecloud.terremark.com/api/v0.8b-ext2.5/extensions/network/XXXXX"),
               "10.X.X.0/27", "209.X.X.X", "10.X.X.0", "10.X.X.X", "10.X.X.1", NetworkExtendedInfo.Type.DMZ, "279",
               "10.X.X.0/27 (DMZ_279)"));
   }
}
