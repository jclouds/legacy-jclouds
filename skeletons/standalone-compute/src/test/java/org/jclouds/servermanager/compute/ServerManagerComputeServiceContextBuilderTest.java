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

package org.jclouds.servermanager.compute;

import static org.testng.Assert.assertEquals;

import java.util.Properties;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.StandaloneComputeServiceContextSpec;
import org.jclouds.rest.RestContext;
import org.jclouds.servermanager.Datacenter;
import org.jclouds.servermanager.Hardware;
import org.jclouds.servermanager.Image;
import org.jclouds.servermanager.Server;
import org.jclouds.servermanager.ServerManager;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Test(groups = "unit")
public class ServerManagerComputeServiceContextBuilderTest {

   @Test
   public void testCanBuildDirectly() {
      ComputeServiceContext context = new ServerManagerComputeServiceContextBuilder(new Properties())
               .buildComputeServiceContext();
      context.close();
   }

   @Test
   public void testCanBuildWithContextSpec() {
      ComputeServiceContext context = new ComputeServiceContextFactory()
               .createContext(new StandaloneComputeServiceContextSpec<ServerManager, Server, Hardware, Image, Datacenter>(
                        "servermanager", "http://host", "1", "", "identity", "credential", ServerManager.class,
                        ServerManagerComputeServiceContextBuilder.class, ImmutableSet.<Module> of()));

      context.close();
   }

   @Test
   public void testCanBuildWithRestProperties() {
      Properties restProperties = new Properties();
      restProperties.setProperty("servermanager.contextbuilder", ServerManagerComputeServiceContextBuilder.class
               .getName());
      restProperties.setProperty("servermanager.endpoint", "http://host");
      restProperties.setProperty("servermanager.apiversion", "1");

      ComputeServiceContext context = new ComputeServiceContextFactory(restProperties).createContext("servermanager",
               "identity", "credential");

      context.close();
   }

   @Test
   public void testProviderSpecificContextIsCorrectType() {
      ComputeServiceContext context = new ServerManagerComputeServiceContextBuilder(new Properties())
               .buildComputeServiceContext();
      RestContext<ServerManager, ServerManager> providerContext = context.getProviderSpecificContext();

      assertEquals(providerContext.getApi().getClass(), ServerManager.class);

      context.close();
   }
}
