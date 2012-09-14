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

package org.jclouds.virtualbox.compute.extensions;

import java.util.concurrent.ExecutionException;

import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.extensions.internal.BaseImageExtensionLiveTest;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.Test;

import com.google.inject.Module;

@Test(groups = "live", singleThreaded = true, testName = "VirtualBoxImageExtensionLiveTest")
public class VirtualBoxImageExtensionLiveTest extends BaseImageExtensionLiveTest {

   @Override
   public void testDeleteImage() {
      // TODO
   }

   @Override
   public void testCreateImage() throws RunNodesException,
         InterruptedException, ExecutionException {
      // TODO
   }

   @Override
   public void testSpawnNodeFromImage() throws RunNodesException {
      // TODO
   }

   public VirtualBoxImageExtensionLiveTest() {
      provider = "virtualbox";
   }

   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }

}
