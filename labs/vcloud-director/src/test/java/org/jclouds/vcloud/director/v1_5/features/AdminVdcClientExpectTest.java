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
package org.jclouds.vcloud.director.v1_5.features;

import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.admin.VCloudDirectorAdminClient;
import org.jclouds.vcloud.director.v1_5.domain.AdminVdc;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.features.admin.AdminVdcClient;
import org.jclouds.vcloud.director.v1_5.internal.VCloudDirectorAdminClientExpectTest;
import org.testng.annotations.Test;

/**
 * Test the {@link AdminVdcClient} by observing its side effects.
 * 
 * @author danikov
 */
@Test(groups = { "unit", "admin", "vdc" }, singleThreaded = true, testName = "AdminVdcClientExpectTest")
public class AdminVdcClientExpectTest extends VCloudDirectorAdminClientExpectTest {
   
   private Reference vdcRef = Reference.builder()
         .href(URI.create(endpoint + "???"))
         .build();
   
   @Test( enabled = false )
   public void testGetVdc() {
      VCloudDirectorAdminClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", "/admin/vdc/???")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/admin/vdc.xml", 
                  VCloudDirectorMediaType.ADMIN_VDC)
            .httpResponseBuilder().build());

      AdminVdc expected = adminVdc();

      assertEquals(client.getVdcClient().getVdc(vdcRef.getHref()), expected);
   }
   
   public static final AdminVdc adminVdc() {
      return AdminVdc.builder().fromVdc(VdcClientExpectTest.getVdc())
         
         .build();
   }
}
