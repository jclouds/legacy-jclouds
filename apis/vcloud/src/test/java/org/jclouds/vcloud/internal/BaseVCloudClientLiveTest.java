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
package org.jclouds.vcloud.internal;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.jclouds.vcloud.VCloudApiMetadata;
import org.jclouds.vcloud.VCloudClient;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", enabled = true, singleThreaded = true)
public abstract class BaseVCloudClientLiveTest extends BaseComputeServiceContextLiveTest {

   // username is too long for name constraints
   protected String prefix = "vcd";

   protected ComputeService client;

   public BaseVCloudClientLiveTest() {
      provider = "vcloud";
   }

   protected VCloudClient getVCloudApi() {
      return VCloudClient.class.cast(view.unwrap(VCloudApiMetadata.CONTEXT_TOKEN).getApi());
   }

   @Override
   @BeforeClass(groups = { "integration", "live" })
   public void setupContext() {
      super.setupContext();
      client = view.getComputeService();
   }

   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }
}
