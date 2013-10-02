/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.vcloud.features;

import static org.testng.Assert.assertNotNull;

import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.domain.Org;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VDC;
import org.jclouds.vcloud.internal.BaseVCloudApiLiveTest;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", enabled = true, singleThreaded = true, testName = "VAppApiLiveTest")
public class VAppApiLiveTest extends BaseVCloudApiLiveTest {

   @Test
   public void testGetVApp() throws Exception {
      Org org = getVCloudApi().getOrgApi().findOrgNamed(null);
      for (ReferenceType vdc : org.getVDCs().values()) {
         VDC response = getVCloudApi().getVDCApi().getVDC(vdc.getHref());
         for (ReferenceType item : response.getResourceEntities().values()) {
            if (item.getType().equals(VCloudMediaType.VAPP_XML)) {
               try {
                  VApp app = getVCloudApi().getVAppApi().getVApp(item.getHref());
                  assertNotNull(app);
               } catch (RuntimeException e) {

               }
            }
         }
      }
   }
}
