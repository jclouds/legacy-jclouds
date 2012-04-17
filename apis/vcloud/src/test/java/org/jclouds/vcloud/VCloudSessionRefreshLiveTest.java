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
package org.jclouds.vcloud;

import org.jclouds.vcloud.internal.BaseVCloudClientLiveTest;
import org.testng.annotations.Test;

/**
 * Tests session refresh works
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true)
public class VCloudSessionRefreshLiveTest extends BaseVCloudClientLiveTest {

   private final static int timeOut = 40;

   @Test
   public void testSessionRefresh() throws Exception {
      VCloudClient connection = VCloudClient.class.cast(client.getContext().unwrap(VCloudApiMetadata.CONTEXT_TOKEN)
               .getApi());

      connection.getOrgClient().findOrgNamed(null);
      Thread.sleep(timeOut * 1000);
      connection.getOrgClient().findOrgNamed(null);
   }

}
