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

package org.jclouds.ec2.compute;

import static org.jclouds.compute.util.ComputeServiceUtils.getCores;
import static org.testng.Assert.assertEquals;

import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(enabled = false, groups = "live", sequential = true)
public class NovaComputeServiceLiveTestDisabled extends EC2ComputeServiceLiveTest {

   public NovaComputeServiceLiveTestDisabled() {
      provider = "nova";
   }

   @Override
   protected void assertDefaultWorks() {
      Template defaultTemplate = client.templateBuilder().build();
      assert (defaultTemplate.getImage().getProviderId().startsWith("ami-")) : defaultTemplate;
      assertEquals(defaultTemplate.getImage().getOperatingSystem().getVersion(), "10.04");
      assertEquals(defaultTemplate.getImage().getOperatingSystem().is64Bit(), true);
      assertEquals(defaultTemplate.getImage().getOperatingSystem().getFamily(), OsFamily.UBUNTU);
      assertEquals(defaultTemplate.getImage().getUserMetadata().get("rootDeviceType"), "instance-store");
      assertEquals(defaultTemplate.getLocation().getId(), "nova");
      assertEquals(getCores(defaultTemplate.getHardware()), 1.0d);
   }

}
