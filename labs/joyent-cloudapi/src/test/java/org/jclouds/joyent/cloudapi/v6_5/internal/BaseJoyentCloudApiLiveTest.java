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
package org.jclouds.joyent.cloudapi.v6_5.internal;

import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.joyent.cloudapi.v6_5.JoyentCloudApi;
import org.jclouds.joyent.cloudapi.v6_5.JoyentCloudAsyncApi;
import org.jclouds.rest.RestContext;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.inject.Module;

/**
 * Tests behavior of {@code JoyentCloudApi}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live")
public class BaseJoyentCloudApiLiveTest extends BaseComputeServiceContextLiveTest {

   public BaseJoyentCloudApiLiveTest() {
      provider = "joyent-cloudapi";
   }

   protected RestContext<JoyentCloudApi, JoyentCloudAsyncApi> cloudApiContext;

   @BeforeGroups(groups = { "integration", "live" })
   @Override
   public void setupContext() {
      super.setupContext();
      cloudApiContext = view.unwrap();
   }

   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }
}
