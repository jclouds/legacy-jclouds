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
package org.jclouds.trmk.vcloud_0_8.internal;

import java.util.concurrent.TimeUnit;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.predicates.InetSocketAddressConnect;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.rest.RestContext;
import org.jclouds.ssh.SshClient.Factory;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.jclouds.trmk.vcloud_0_8.TerremarkVCloudAsyncClient;
import org.jclouds.trmk.vcloud_0_8.TerremarkVCloudClient;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.net.HostAndPort;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", enabled = true, singleThreaded = true)
public abstract class BaseTerremarkClientLiveTest<S extends TerremarkVCloudClient, A extends TerremarkVCloudAsyncClient> extends BaseComputeServiceContextLiveTest {

   protected String prefix = System.getProperty("user.name");

   protected ComputeService client;

   public BaseTerremarkClientLiveTest() {
       provider = "trmk-ecloud";
   }

   protected RetryablePredicate<HostAndPort> socketTester;
   protected Factory sshFactory;
   protected S connection;

   @SuppressWarnings("unchecked")
   @Override
   @BeforeClass(groups = { "integration", "live" })
   public void setupContext() {
      super.setupContext();
      Injector injector = view.utils().injector();
      socketTester = new RetryablePredicate<HostAndPort>(new InetSocketAddressConnect(), 300, 1, TimeUnit.SECONDS);
      sshFactory = injector.getInstance(Factory.class);
      connection = (S) RestContext.class.cast(view.unwrap()).getApi();
   }
   
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }

}
