/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.trmk.vcloud_0_8.internal;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.util.Predicates2.retry;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.ssh.SshClient.Factory;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.jclouds.trmk.vcloud_0_8.TerremarkVCloudClient;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.net.HostAndPort;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", enabled = true, singleThreaded = true)
public abstract class BaseTerremarkClientLiveTest extends BaseComputeServiceContextLiveTest {

   protected String prefix = System.getProperty("user.name");

   protected ComputeService client;

   public BaseTerremarkClientLiveTest() {
       provider = "trmk-ecloud";
   }

   protected Predicate<HostAndPort> socketTester;
   protected Factory sshFactory;
   protected TerremarkVCloudClient api;

   @Override
   @BeforeClass(groups = { "integration", "live" })
   public void setupContext() {
      super.setupContext();
      Injector injector = view.utils().injector();
      SocketOpen socketOpen = injector.getInstance(SocketOpen.class);
      socketTester = retry(socketOpen, 300, 1, SECONDS);
      sshFactory = injector.getInstance(Factory.class);
      api = injector.getInstance(TerremarkVCloudClient.class);
   }
   
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }

}
