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
package org.jclouds.servermanager.compute;

import static org.jclouds.compute.util.ComputeServiceUtils.getCores;
import static org.testng.Assert.assertEquals;

import java.util.Properties;

import org.jclouds.compute.BaseComputeServiceLiveTest;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.rest.RestContext;
import org.jclouds.servermanager.ServerManager;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", enabled = true, sequential = true)
public class ServerManagerComputeServiceLiveTest extends BaseComputeServiceLiveTest {
   public ServerManagerComputeServiceLiveTest() {
      provider = "servermanager";
   }

   @Override
   protected Properties setupRestProperties() {
      Properties restProperties = new Properties();
      restProperties.setProperty("servermanager.contextbuilder",
            ServerManagerComputeServiceContextBuilder.class.getName());
      restProperties.setProperty("servermanager.endpoint", "http://host");
      restProperties.setProperty("servermanager.apiversion", "1");
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
      RestContext<ServerManager, ServerManager> goGridContext = new ComputeServiceContextFactory().createContext(
            provider, identity, credential).getProviderSpecificContext();
   }
}
