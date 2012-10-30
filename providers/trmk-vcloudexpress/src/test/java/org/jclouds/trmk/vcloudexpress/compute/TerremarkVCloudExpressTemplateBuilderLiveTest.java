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
package org.jclouds.trmk.vcloudexpress.compute;

import static org.jclouds.compute.util.ComputeServiceUtils.getCores;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Set;

import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.OsFamilyVersion64Bit;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.internal.BaseTemplateBuilderLiveTest;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "TerremarkVCloudExpressTemplateBuilderLiveTest")
public class TerremarkVCloudExpressTemplateBuilderLiveTest extends BaseTemplateBuilderLiveTest {

   public TerremarkVCloudExpressTemplateBuilderLiveTest() {
      provider = "trmk-vcloudexpress";
   }

   @Override
   protected Predicate<OsFamilyVersion64Bit> defineUnsupportedOperatingSystems() {
      return Predicates.not(new Predicate<OsFamilyVersion64Bit>() {
         @Override
         public boolean apply(OsFamilyVersion64Bit input) {
            switch (input.family) {
            case RHEL:
               return input.version.equals("") || input.version.equals("5.0");
            case CENTOS:
               return input.version.equals("") || input.version.equals("5.0");
            case UBUNTU:
               return input.version.equals("") || input.version.matches("9.[10][04]") || input.version.equals("10.04");
            case WINDOWS:
               return input.version.equals("") || input.version.equals("2003") || input.version.equals("2008");
            default:
               return false;
            }
         }
      });
   }

   @Test
   public void testDefaultTemplateBuilder() throws IOException {
      Template defaultTemplate = view.getComputeService().templateBuilder().build();
      assertEquals(defaultTemplate.getImage().getOperatingSystem().getVersion(), "10.04");
      assertEquals(defaultTemplate.getImage().getOperatingSystem().is64Bit(), true);
      assertEquals(defaultTemplate.getImage().getOperatingSystem().getFamily(), OsFamily.UBUNTU);
      assertEquals(getCores(defaultTemplate.getHardware()), 1.0d);
   }

   @Override
   protected Set<String> getIso3166Codes() {
      return ImmutableSet.<String> of("US-FL");
   }
}
