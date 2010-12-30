/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.vcloud.terremark.compute;

import static org.jclouds.compute.util.ComputeServiceUtils.getCores;
import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jclouds.compute.BaseTemplateBuilderLiveTest;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.os.OsFamilyVersion64Bit;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live")
public class TerremarkECloudTemplateBuilderLiveTest extends BaseTemplateBuilderLiveTest {

   public TerremarkECloudTemplateBuilderLiveTest() {
      provider = "trmk-ecloud";
   }

   @Override
   protected Predicate<OsFamilyVersion64Bit> defineUnsupportedOperatingSystems() {
      return new Predicate<OsFamilyVersion64Bit>() {

         @Override
         public boolean apply(OsFamilyVersion64Bit input) {
            return ((input.family == OsFamily.RHEL) || //
                  (input.family == OsFamily.CENTOS && !input.version.equals("5.5")) || //
                  (input.family == OsFamily.UBUNTU &&( !input.version.equals("10.04")&&!input.version.equals("8.04"))) || //
            (input.family == OsFamily.WINDOWS && (input.version.equals("2008 SP2") || input.version.equals("2008 R2"))));
         }

      };
   }

   @Test
   public void testDefaultTemplateBuilder() throws IOException {
      Template defaultTemplate = context.getComputeService().templateBuilder().build();
      assertEquals(defaultTemplate.getImage().getOperatingSystem().getVersion(), "5.5");
      assertEquals(defaultTemplate.getImage().getOperatingSystem().is64Bit(), true);
      assertEquals(defaultTemplate.getImage().getOperatingSystem().getFamily(), OsFamily.CENTOS);
      assertEquals(getCores(defaultTemplate.getHardware()), 1.0d);

   }

}