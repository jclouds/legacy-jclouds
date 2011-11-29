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
package org.jclouds.glesys.features;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.glesys.GleSYSAsyncClient;
import org.jclouds.glesys.GleSYSClient;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Tests behavior of {@code GleSYSClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live")
public class BaseGleSYSClientLiveTest {

   protected RestContext<GleSYSClient, GleSYSAsyncClient> context;

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      String identity = checkNotNull(System.getProperty("test.glesys.identity"), "test.glesys.identity");
      String credential = checkNotNull(System.getProperty("test.glesys.credential"), "test.glesys.credential");

      context = new RestContextFactory().createContext("glesys", identity, credential,
            ImmutableSet.<Module> of(new Log4JLoggingModule(), new SshjSshClientModule()));

   }

   @AfterGroups(groups = "live")
   protected void tearDown() {
      if (context != null)
         context.close();
   }

}
