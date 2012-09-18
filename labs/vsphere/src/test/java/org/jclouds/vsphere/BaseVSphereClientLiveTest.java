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

package org.jclouds.vsphere;

import javax.inject.Inject;

import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.compute.strategy.PrioritizeCredentialsFromTemplate;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.inject.Module;
import com.vmware.vim25.mo.ServiceInstance;

/**
 * Tests behavior of {@code VSphereClient}
 * 
 * @author Andrea Turli
 */
@Test(groups = "live", testName = "BaseVSphereClientLiveTest")
public class BaseVSphereClientLiveTest extends BaseComputeServiceContextLiveTest {

   public BaseVSphereClientLiveTest() {
      provider = "vsphere";
   }

   @Inject
   protected Supplier<ServiceInstance> client;
   
   @Inject
   protected PrioritizeCredentialsFromTemplate prioritizeCredentialsFromTemplate;

   @Inject
   void eagerlyStartManager(Supplier<ServiceInstance> client) {
      this.client = client;
   }
   
   @Override
   @BeforeClass(groups = { "integration", "live" })
   public void setupContext() {
      super.setupContext();
      view.utils().injector().injectMembers(this);
   }
   
   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }
   
   
   @AfterClass(groups = "live")
   protected void tearDown() throws Exception {
      super.tearDownContext();
      if (view != null)
         view.close();
   }
}
