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

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;

import java.util.Set;

import org.jclouds.joyent.sdc.v6_5.internal.BaseSDCClientLiveTest;
import org.testng.annotations.Test;

/**
 * @author Gerald Pereira
 */
@Test(groups = "live", testName = "PackageClientLiveTest")
public class PackageClientLiveTest extends BaseSDCClientLiveTest {

   public void testListPackages() {
      Set<org.jclouds.joyent.sdc.v6_5.domain.Package> packages = sdcContext.getApi().getPackageClient().listPackages();
      assertNotNull(packages);
      assertTrue(packages.size() > 0);
   }

   public void testGetPackage() {
      final String name = "Small 1GB";
      org.jclouds.joyent.sdc.v6_5.domain.Package packageSDC = sdcContext.getApi().getPackageClient().getPackage(name);
      assertNotNull(packageSDC);
      assertEquals(packageSDC.getName(), name);
   }
}
