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
package org.jclouds.opsource.servers.internal;

import java.util.Properties;

import org.jclouds.compute.BaseVersionedServiceLiveTest;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.opsource.servers.OpSourceServersAsyncClient;
import org.jclouds.opsource.servers.OpSourceServersClient;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Tests behavior of {@link OpSourceServersClient} and acts as parent for other
 * client live tests.
 * 
 * @author Adrian Cole
 */
@Test(groups = "live")
public abstract class BaseOpSourceServersClientLiveTest extends BaseVersionedServiceLiveTest {

   protected BaseOpSourceServersClientLiveTest() {
      provider = "opsource-servers";
   }

   protected RestContext<OpSourceServersClient, OpSourceServersAsyncClient> context;

   @BeforeClass(groups = { "live" })
   public void setupContext() {
      setupCredentials();
      Properties overrides = setupProperties();

      context = new RestContextFactory().createContext(provider, identity, credential,
            ImmutableSet.<Module> of(new Log4JLoggingModule(), new SshjSshClientModule()), overrides);
   }

   protected void tearDown() {
      if (context != null)
         context.close();
   }
}
