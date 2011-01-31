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

package org.jclouds.epc.compute;

import static org.jclouds.compute.util.ComputeServiceUtils.getCores;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Set;

import org.jclouds.compute.BaseTemplateBuilderLiveTest;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.os.OsFamilyVersion64Bit;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live")
public class EucalyptusPartnerCloudEucalyptusTemplateBuilderLiveTest extends BaseTemplateBuilderLiveTest {

   public EucalyptusPartnerCloudEucalyptusTemplateBuilderLiveTest() {
      provider = "eucalyptus-partnercloud-ec2";
   }

   @Override
   protected Predicate<OsFamilyVersion64Bit> defineUnsupportedOperatingSystems() {
      return new Predicate<OsFamilyVersion64Bit>() {

         @Override
         public boolean apply(OsFamilyVersion64Bit input) {
            switch (input.family) {
               case CENTOS:
                  return !(input.version.equals("") && input.is64Bit)
                           && !(input.version.equals("5.3") && input.is64Bit);
               case WINDOWS:
                  return !(input.version.equals("") && input.is64Bit)
                           && !(input.version.equals("2008") && input.is64Bit);
               default:
                  return true;
            }
         }

      };
   }

   @Test
   public void testDefaultTemplateBuilder() throws IOException {

      Template defaultTemplate = context.getComputeService().templateBuilder().build();
      assert (defaultTemplate.getImage().getProviderId().startsWith("emi-")) : defaultTemplate;
      assertEquals(defaultTemplate.getImage().getOperatingSystem().getVersion(), "5.3");
      assertEquals(defaultTemplate.getImage().getOperatingSystem().is64Bit(), true);
      assertEquals(defaultTemplate.getImage().getOperatingSystem().getFamily(), OsFamily.CENTOS);
      assertEquals(defaultTemplate.getImage().getUserMetadata().get("rootDeviceType"), "instance-store");
      assertEquals(defaultTemplate.getLocation().getId(), "kvm-cluster");
      assertEquals(getCores(defaultTemplate.getHardware()), 1.0d);

   }

   @Override
   protected Set<String> getIso3166Codes() {
      return ImmutableSet.<String> of("US-CA");
   }
}
