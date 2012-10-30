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
package org.jclouds.vcloud.compute;

import org.jclouds.compute.internal.BaseComputeServiceLiveTest;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.Test;

import com.google.inject.Module;

/**
 * 
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", enabled = true, singleThreaded = true)
public class VCloudComputeServiceLiveTest extends BaseComputeServiceLiveTest {

   public VCloudComputeServiceLiveTest() {
      provider = "vcloud";
   }

   @Override
   public void setServiceDefaults() {
      // extremely short names needed so that we don't get errors relating to
      // guestCustomization.computerName being too long
      group = "vcd";
   }

   @Override
   public void testOptionToNotBlock() {
      // start call has to block until deploy
   }

   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }

}
