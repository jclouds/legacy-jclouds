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
package org.jclouds.bluelock.vcloud.vcenterprise.compute;

import static org.jclouds.compute.util.ComputeServiceUtils.getCores;
import static org.testng.Assert.assertEquals;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.predicates.OperatingSystemPredicates;
import org.jclouds.vcloud.compute.VCloudComputeServiceLiveTest;
import org.testng.annotations.Test;

/**
 * 
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", enabled = true, singleThreaded = true, testName = "BluelockVCloudEnterpriseComputeServiceLiveTest")
public class BluelockVCloudEnterpriseComputeServiceLiveTest extends VCloudComputeServiceLiveTest {
   public BluelockVCloudEnterpriseComputeServiceLiveTest() {
      provider = "bluelock-vcloud-vcenterprise";
      // vcloud requires instantiate before deploy, which takes longer than 30 seconds
      nonBlockDurationSeconds = 300;
   }

   @Override
   public void setServiceDefaults() {
      group = "director";
   }

   @Test
   public void testTemplateBuilder() {
      Template defaultTemplate = client.templateBuilder().build();
      assertEquals(defaultTemplate.getImage().getOperatingSystem().is64Bit(), true);
      assert OperatingSystemPredicates.supportsApt().apply(defaultTemplate.getImage().getOperatingSystem());
      assertEquals(defaultTemplate.getImage().getOperatingSystem().getFamily(), OsFamily.UBUNTU);
      assertEquals(defaultTemplate.getImage().getOperatingSystem().getDescription(), "Ubuntu Linux (64-bit)");
      assert defaultTemplate.getLocation().getId() != null : defaultTemplate.getLocation();
      assertEquals(getCores(defaultTemplate.getHardware()), 1.0d);
      System.out.println(defaultTemplate.getHardware());
   }

   @Override
   protected Template buildTemplate(TemplateBuilder templateBuilder) {
      Template template = super.buildTemplate(templateBuilder);
      Image image = template.getImage();
      assert image.getDefaultCredentials().credential != null : image;
      return template;
   }

}
