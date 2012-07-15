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
package org.jclouds.joyent.cloudapi.v6_5.features;

import static org.testng.Assert.assertEquals;

import java.util.Set;

import org.jclouds.joyent.cloudapi.v6_5.domain.Package;
import org.jclouds.joyent.cloudapi.v6_5.features.PackageApi;
import org.jclouds.joyent.cloudapi.v6_5.internal.BaseJoyentCloudApiLiveTest;
import org.testng.annotations.Test;

/**
 * @author Gerald Pereira
 */
@Test(groups = "live", testName = "PackageApiLiveTest")
public class PackageApiLiveTest extends BaseJoyentCloudApiLiveTest {

   @Test
   public void testListAndGetPackages() throws Exception {
      for (String datacenterId : cloudApiContext.getApi().getConfiguredDatacenters()) {
         PackageApi api = cloudApiContext.getApi().getPackageApiForDatacenter(datacenterId);
         Set<Package> response = api.list();
         assert null != response;
         for (Package pkg : response) {
            Package newDetails = api.get(pkg.getName());
            assertEquals(newDetails.getName(), pkg.getName());
            assertEquals(newDetails.getMemorySizeMb(), pkg.getMemorySizeMb());
            assertEquals(newDetails.getDiskSizeGb(), pkg.getDiskSizeGb());
            assertEquals(newDetails.getSwapSizeMb(), pkg.getSwapSizeMb());
            assertEquals(newDetails.isDefault(), pkg.isDefault());
         }
      }
   }
}
