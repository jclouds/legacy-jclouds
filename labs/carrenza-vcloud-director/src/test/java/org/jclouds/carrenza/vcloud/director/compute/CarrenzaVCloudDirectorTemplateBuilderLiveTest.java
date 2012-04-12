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
package org.jclouds.carrenza.vcloud.director.compute;

import org.testng.annotations.Test;

/**
 * 
 * @author danikov
 */
@Test(groups = "live", testName = "CarrenzaVCloudDirectorTemplateBuilderLiveTest")
public class CarrenzaVCloudDirectorTemplateBuilderLiveTest {
//      BaseTemplateBuilderLiveTest<VCloudDirectorClient, VCloudDirectorAsyncClient, VCloudDirectorContext> {
//
//   public CarrenzaVCloudDirectorTemplateBuilderLiveTest() {
//      provider = "carrenza-vcloud-director";
//   }
//
//   @Override
//   protected Predicate<OsFamilyVersion64Bit> defineUnsupportedOperatingSystems() {
//      return new Predicate<OsFamilyVersion64Bit>() {
//
//         @Override
//         public boolean apply(OsFamilyVersion64Bit input) {
//            switch (input.family) {
//            case UBUNTU:
//               return !input.version.equals("") || !input.is64Bit;
//            default:
//               return true;
//            }
//         }
//
//      };
//   }
//
//   @Override
//   public void testDefaultTemplateBuilder() throws IOException { 
//      Template defaultTemplate = context.getComputeService().templateBuilder().build();
//      assertEquals(defaultTemplate.getImage().getOperatingSystem().getVersion(), "");
//      assertEquals(defaultTemplate.getImage().getOperatingSystem().is64Bit(), true);
//      assertEquals(defaultTemplate.getImage().getOperatingSystem().getFamily(), OsFamily.UBUNTU);
//      assertEquals(getCores(defaultTemplate.getHardware()), 1.0d);
//   }
//
//   @Override
//   protected Set<String> getIso3166Codes() {
//      return ImmutableSet.<String> of("GB-LND");
//   }
}
