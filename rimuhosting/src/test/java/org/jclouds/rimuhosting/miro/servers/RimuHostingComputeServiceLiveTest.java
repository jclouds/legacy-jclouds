/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.rimuhosting.miro.servers;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertNotNull;

import org.jclouds.compute.domain.CreateServerResponse;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Profile;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rimuhosting.miro.RimuHostingClient;
import org.jclouds.rimuhosting.miro.RimuHostingContextBuilder;
import org.jclouds.rimuhosting.miro.RimuHostingPropertiesBuilder;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.inject.Injector;

/**
 * @author Ivan Meredith
 */
@Test(groups = "live", sequential = true, testName = "rimuhosting.RimuHostingServerServiceLiveTest")
public class RimuHostingComputeServiceLiveTest {
   RimuHostingClient rhClient;
   RimuHostingComputeService rhServerService;

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      String account = "ddd";
      String key = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");
      Injector injector = new RimuHostingContextBuilder(new RimuHostingPropertiesBuilder(account,
               key).relaxSSLHostname().build()).withModules(new Log4JLoggingModule())
               .buildInjector();

      rhClient = injector.getInstance(RimuHostingClient.class);
      rhServerService = injector.getInstance(RimuHostingComputeService.class);
   }

   @Test
   public void testServerCreate() {
      CreateServerResponse server = rhServerService.createServer("test.com", Profile.SMALLEST,
               Image.CENTOS_53);
      assertNotNull(rhClient.getInstance(Long.valueOf(server.getId())));
      rhServerService.destroyServer(server.getId());
   }
}
