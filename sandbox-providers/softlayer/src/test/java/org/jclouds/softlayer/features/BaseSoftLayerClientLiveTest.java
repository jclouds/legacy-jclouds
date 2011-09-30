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
package org.jclouds.softlayer.features;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.RestContext;
import org.jclouds.softlayer.SoftLayerAsyncClient;
import org.jclouds.softlayer.SoftLayerClient;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Tests behavior of {@code SoftLayerClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live")
public class BaseSoftLayerClientLiveTest {

   protected RestContext<SoftLayerClient, SoftLayerAsyncClient> context;
   protected ComputeServiceContext computeContext;

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      String identity = checkNotNull(System.getProperty("test.softlayer.identity"), "test.softlayer.identity");
      String credential = checkNotNull(System.getProperty("test.softlayer.credential"), "test.softlayer.credential");

      computeContext = new ComputeServiceContextFactory().createContext("softlayer", identity, credential,
            ImmutableSet.<Module> of(new Log4JLoggingModule(), new SshjSshClientModule()));
      context = computeContext.getProviderSpecificContext();

   }

   @AfterGroups(groups = "live")
   protected void tearDown() {
      if (context != null)
         context.close();
   }

}
