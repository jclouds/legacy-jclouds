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
package org.jclouds.libvirt.compute;

import static org.jclouds.compute.util.ComputeServiceUtils.getCores;
import static org.testng.Assert.assertEquals;

import java.util.Properties;

import org.jclouds.compute.BaseComputeServiceLiveTest;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.rest.RestContext;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.libvirt.Connect;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", enabled = true, sequential = true)
public class LibvirtComputeServiceLiveTest extends BaseComputeServiceLiveTest {
   public LibvirtComputeServiceLiveTest() {
      provider = "libvirt";
   }

   @Override
   protected Properties getRestProperties() {
      Properties restProperties = new Properties();
      restProperties.setProperty("libvirt.contextbuilder", LibvirtComputeServiceContextBuilder.class.getName());
      restProperties.setProperty("libvirt.propertiesbuilder", LibvirtPropertiesBuilder.class.getName());
      restProperties.setProperty("libvirt.endpoint", "test:///default");
      return restProperties;
   }

   @Test
   public void testTemplateBuilder() {
      Template defaultTemplate = client.templateBuilder().build();
      assertEquals(defaultTemplate.getImage().getOperatingSystem().is64Bit(), true);
      assertEquals(defaultTemplate.getImage().getOperatingSystem().getVersion(), "5.3");
      assertEquals(defaultTemplate.getImage().getOperatingSystem().getFamily(), OsFamily.CENTOS);
      assertEquals(defaultTemplate.getLocation().getId(), "1");
      assertEquals(getCores(defaultTemplate.getHardware()), 0.5d);
   }

   @Override
   protected JschSshClientModule getSshModule() {
      return new JschSshClientModule();
   }

   public void testAssignability() throws Exception {
      @SuppressWarnings("unused")
      RestContext<Connect, Connect> goGridContext = new ComputeServiceContextFactory().createContext(provider,
            identity, credential).getProviderSpecificContext();
   }
}
