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
package org.jclouds.vcloud.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.internal.BaseVCloudClientLiveTest;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", enabled = true, singleThreaded = true, testName = "OrgClientLiveTest")
public class OrgClientLiveTest extends BaseVCloudClientLiveTest {

   @Test
   public void testListOrgs() throws Exception {
      for (ReferenceType response : getVCloudApi().getOrgClient().listOrgs().values()) {
         assertNotNull(response);
         assertNotNull(response.getName());
         assertNotNull(response.getHref());
         assertEquals(getVCloudApi().getOrgClient().getOrg(response.getHref()).getName(), response.getName());
         assertEquals(getVCloudApi().getOrgClient().findOrgNamed(response.getName()).getName(), response.getName());
      }
   }
}
