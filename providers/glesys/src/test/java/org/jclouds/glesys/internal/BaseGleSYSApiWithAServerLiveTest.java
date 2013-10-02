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
package org.jclouds.glesys.internal;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.jclouds.glesys.domain.Server;
import org.jclouds.glesys.domain.Server.State;
import org.jclouds.glesys.domain.ServerDetails;
import org.jclouds.glesys.domain.ServerSpec;
import org.jclouds.glesys.domain.ServerStatus;
import org.jclouds.glesys.features.DomainApi;
import org.jclouds.glesys.features.ServerApi;
import org.jclouds.glesys.options.DestroyServerOptions;
import org.jclouds.glesys.options.ServerStatusOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;

/**
 * Tests behavior of {@code GleSYSApi}
 * 
 * @author Adrian Cole, Adam Lowe
 */
@Test(groups = "live", singleThreaded = true)
public class BaseGleSYSApiWithAServerLiveTest extends BaseGleSYSApiLiveTest {
   protected String serverId;
   protected Predicate<State> serverStatusChecker;

   public BaseGleSYSApiWithAServerLiveTest() {
      provider = "glesys";
   }

   @BeforeClass(groups = { "integration", "live" })
   @Override
   public void setup() {
      assertNull(serverId, "This method should be called EXACTLY once per run");
      super.setup();
      serverStatusChecker = createServer(hostName);
   }

   @AfterClass(groups = { "integration", "live" })
   @Override
   public void tearDown() {
      api.getServerApi().destroy(serverId, DestroyServerOptions.Builder.discardIp());
      super.tearDown();
   }

   protected void createDomain(String domain) {
      final DomainApi domainApi = api.getDomainApi();
      int before = domainApi.list().size();
      domainApi.create(domain);
      Predicate<Integer> result = retry(new Predicate<Integer>() {
         public boolean apply(Integer value) {
            return domainApi.list().size() == value.intValue();
         }
      }, 30, 1, SECONDS);
      assertTrue(result.apply(before + 1));
   }

   protected Predicate<State> createServer(String hostName) {
      final ServerApi serverApi = api.getServerApi();

      ServerDetails testServer = serverApi.createWithHostnameAndRootPassword(
            ServerSpec.builder().datacenter("Falkenberg").platform("OpenVZ").templateName("Ubuntu 10.04 LTS 32-bit")
                  .diskSizeGB(5).memorySizeMB(512).cpuCores(1).transferGB(50).build(), hostName, UUID.randomUUID()
                  .toString().replace("-",""));

      assertNotNull(testServer.getId());
      assertEquals(testServer.getHostname(), hostName);
      assertFalse(testServer.getIps().isEmpty());

      Predicate<State> statusChecker = statusChecker(serverApi, testServer.getId());
      assertTrue(statusChecker.apply(Server.State.RUNNING));
      serverId = testServer.getId();
      return statusChecker;
   }

   protected Predicate<State> statusChecker(final ServerApi api, final String serverId) {
     return retry(new Predicate<Server.State>() {

         public boolean apply(Server.State value) {
            ServerStatus status = api.getStatus(serverId, ServerStatusOptions.Builder.state());
            return status.getState() == value;
         }

      }, 300, 10, TimeUnit.SECONDS);
   }
}
