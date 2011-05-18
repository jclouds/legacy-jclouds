/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.ibm.smartcloud.compute;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Set;

import org.jclouds.compute.BaseTemplateBuilderLiveTest;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.OsFamilyVersion64Bit;
import org.jclouds.compute.domain.Template;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "IBMSmartCloudTemplateBuilderLiveTest")
public class IBMSmartCloudTemplateBuilderLiveTest extends BaseTemplateBuilderLiveTest {

   public IBMSmartCloudTemplateBuilderLiveTest() {
      provider = "ibm-smartcloud";
   }

   @Override
   protected Predicate<OsFamilyVersion64Bit> defineUnsupportedOperatingSystems() {
      return new Predicate<OsFamilyVersion64Bit>() {

         @Override
         public boolean apply(OsFamilyVersion64Bit input) {
            switch (input.family) {
               case SUSE:
                  return !input.version.equals("") && !input.version.equals("11") && !input.version.equals("11 SP1");
               case RHEL:
                  return !input.version.equals("") && !input.version.equals("5.4") && !input.version.equals("5.5");
               case WINDOWS:
                  return !input.version.equals("") && !(input.version.equals("2008 R2") && input.is64Bit)
                           && !input.version.equals("2008 R1") && !input.version.equals("2003 R2");
               default:
                  return true;
            }
         }

      };
   }

   @Override
   public void testDefaultTemplateBuilder() throws IOException {
      Template defaultTemplate = context.getComputeService().templateBuilder().build();
      assertEquals(defaultTemplate.getLocation().getIso3166Codes(), ImmutableSet.of("CA-ON"));
      assertEquals(defaultTemplate.getImage().getId(), "20015393");
      assertEquals(defaultTemplate.getHardware().getId(), "20015393/COP32.1/2048/60");
      assertEquals(defaultTemplate.getImage().getOperatingSystem().getVersion(), "11 SP1");
      assertEquals(defaultTemplate.getImage().getOperatingSystem().is64Bit(), false);
      assertEquals(defaultTemplate.getImage().getOperatingSystem().getFamily(), OsFamily.SUSE);
   }

   @Override
   protected Set<String> getIso3166Codes() {
      return ImmutableSet.of("US-NC", "DE-BW", "US-CO", "CA-ON", "JP-12", "SG");
   }
}
