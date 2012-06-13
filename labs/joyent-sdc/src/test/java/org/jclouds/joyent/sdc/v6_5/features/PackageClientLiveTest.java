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
package org.jclouds.joyent.sdc.v6_5.features;

import static org.testng.Assert.assertEquals;

import java.util.Set;

import org.jclouds.joyent.sdc.v6_5.domain.Package;
import org.jclouds.joyent.sdc.v6_5.internal.BaseSDCClientLiveTest;
import org.testng.annotations.Test;

/**
 * @author Gerald Pereira
 */
@Test(groups = "live", testName = "PackageClientLiveTest")
public class PackageClientLiveTest extends BaseSDCClientLiveTest {

   @Test
   public void testListAndGetPackages() throws Exception {
      for (String datacenterId : sdcContext.getApi().getConfiguredDatacenters()) {
         PackageClient client = sdcContext.getApi().getPackageClientForDatacenter(datacenterId);
         Set<Package> response = client.listPackages();
         assert null != response;
         for (Package pkg : response) {
            Package newDetails = client.getPackage(pkg.getName());
            assertEquals(newDetails.getName(), pkg.getName());
            assertEquals(newDetails.getMemorySizeMb(), pkg.getMemorySizeMb());
            assertEquals(newDetails.getDiskSizeGb(), pkg.getDiskSizeGb());
            assertEquals(newDetails.getSwapSizeMb(), pkg.getSwapSizeMb());
            assertEquals(newDetails.isDefault(), pkg.isDefault());
         }
      }
   }
}
