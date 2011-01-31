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

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.StandaloneComputeServiceContextSpec;
import org.jclouds.servermanager.Datacenter;
import org.jclouds.servermanager.Hardware;
import org.jclouds.servermanager.Image;
import org.jclouds.servermanager.Server;
import org.jclouds.servermanager.ServerManager;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live")
public class ServerManagerExperimentLiveTest {
   protected String provider = "servermanager";
   protected String identity;
   protected String credential;
   protected String endpoint;
   protected String apiversion;

   @BeforeClass
   protected void setupCredentials() {
      identity = checkNotNull(System.getProperty("test." + provider + ".identity"), "test." + provider + ".identity");
      credential = System.getProperty("test." + provider + ".credential");
      endpoint = System.getProperty("test." + provider + ".endpoint");
      apiversion = System.getProperty("test." + provider + ".apiversion");
   }

   @Test
   public void testAndExperiment() {
      ComputeServiceContext context = null;
      try {
         context = new ComputeServiceContextFactory()
                  .createContext(new StandaloneComputeServiceContextSpec<ServerManager, Server, Hardware, Image, Datacenter>(
                           "servermanager", endpoint, apiversion, "", identity, credential, ServerManager.class,
                           ServerManagerComputeServiceContextBuilder.class, ImmutableSet.<Module> of()));

         context.getComputeService().listNodes();

      } finally {
         if (context != null)
            context.close();
      }
   }

}