/*
 * *
 *  * Licensed to jclouds, Inc. (jclouds) under one or more
 *  * contributor license agreements.  See the NOTICE file
 *  * distributed with this work for additional information
 *  * regarding copyright ownership.  jclouds licenses this file
 *  * to you under the Apache License, Version 2.0 (the
 *  * "License"); you may not use this file except in compliance
 *  * with the License.  You may obtain a copy of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */

package org.jclouds.virtualbox.compute;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import org.jclouds.compute.BaseComputeServiceLiveTest;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.StandaloneComputeServiceContextSpec;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.jclouds.virtualbox.VirtualBox;
import org.jclouds.virtualbox.VirtualBoxContextBuilder;
import org.jclouds.virtualbox.domain.Host;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.VirtualBoxManager;

import java.util.Properties;

import static com.google.common.base.Preconditions.checkNotNull;

@Test(groups = "live")
public class VirtualBoxComputeServiceLiveTest extends BaseComputeServiceLiveTest {

   public VirtualBoxComputeServiceLiveTest() {
      provider = "virtualbox";
   }

   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }

   @Override
   protected Properties setupRestProperties() {
      Properties restProperties = new Properties();
      restProperties.setProperty("virtualbox.contextbuilder", VirtualBoxContextBuilder.class.getName());
      restProperties.setProperty("virtualbox.endpoint", "http://localhost:18083/");
      restProperties.setProperty("virtualbox.apiversion", "4.1.2r73507");
      return restProperties;
   }

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
                 .createContext(new StandaloneComputeServiceContextSpec<VirtualBoxManager, IMachine, IMachine, IMachine, Host>(
                         "virtualbox", endpoint, apiversion, "", identity, credential, VirtualBoxManager.class,
                         VirtualBoxContextBuilder.class, ImmutableSet.<Module>of()));

         context.getComputeService().listNodes();

      } finally {
         if (context != null)
            context.close();
      }
   }
}
