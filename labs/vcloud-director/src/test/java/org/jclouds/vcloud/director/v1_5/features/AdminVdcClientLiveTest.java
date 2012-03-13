/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 *(Link.builder().regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless(Link.builder().required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.vcloud.director.v1_5.features;

import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_REQ_LIVE;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.REF_REQ_LIVE;
import static org.testng.Assert.assertNotNull;

import org.jclouds.vcloud.director.v1_5.domain.AdminVdc;
import org.jclouds.vcloud.director.v1_5.domain.Checks;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorClientLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@link NetworkClient}
 * 
 * @author danikov
 */
@Test(groups = { "live", "admin", "vdc" }, singleThreaded = true, testName = "AdminVdcClientLiveTest")
public class AdminVdcClientLiveTest extends BaseVCloudDirectorClientLiveTest {
   
   public static final String VDC = "admin vdc";
 
   /*
    * Convenience reference to API client.
    */
   protected AdminVdcClient vdcClient;
    
   @Override
   @BeforeClass(inheritGroups = true)
   public void setupRequiredClients() {
      vdcClient = context.getApi().getAdminVdcClient();
   }

   @Test(testName = "GET /admin/vdc/{id}", enabled = false)
   public void testGetNetwork() {
      // required for testing
      assertNotNull(vdcURI, String.format(REF_REQ_LIVE, VDC));
       
      AdminVdc vdc = vdcClient.getVdc(toAdminUri(vdcURI));
      assertNotNull(vdc, String.format(OBJ_REQ_LIVE, VDC));
       
      // parent type
      Checks.checkAdminVdc(vdc);
   }
}
