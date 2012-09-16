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
package org.jclouds.glesys.internal;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.jclouds.glesys.domain.Server;
import org.jclouds.glesys.domain.ServerDetails;
import org.jclouds.glesys.domain.ServerSpec;
import org.jclouds.glesys.domain.ServerStatus;
import org.jclouds.glesys.features.DomainApi;
import org.jclouds.glesys.features.ServerApi;
import org.jclouds.glesys.options.DestroyServerOptions;
import org.jclouds.glesys.options.ServerStatusOptions;
import org.jclouds.predicates.RetryablePredicate;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
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
   protected String hostName = "test-server-jclouds";
   protected ServerStatusChecker serverStatusChecker;

   public BaseGleSYSApiWithAServerLiveTest() {
      provider = "glesys";
   }

   @BeforeGroups(groups = { "integration", "live" })
   @Override
   public final void setupContext() {
      assertNull(serverId, "This method should be called EXACTLY once per run");
      super.setupContext();
      serverStatusChecker = createServer(hostName);
      serverId = serverStatusChecker.getServerId();
   }

   @AfterGroups(groups = {"integration", "live"})
   @Override
   public final void tearDownContext() {
      gleContext.getApi().getServerApi().destroy(serverId, DestroyServerOptions.Builder.discardIp());
      super.tearDownContext();
   }

   protected void createDomain(String domain) {
      final DomainApi api = gleContext.getApi().getDomainApi();
      int before = api.list().size();
      api.create(domain);
      RetryablePredicate<Integer> result = new RetryablePredicate<Integer>(new Predicate<Integer>() {
         public boolean apply(Integer value) {
            return api.list().size() == value;
         }
      }, 30, 1, TimeUnit.SECONDS);

      assertTrue(result.apply(before + 1));
   }

   protected ServerStatusChecker createServer(String hostName) {
      ServerApi api = gleContext.getApi().getServerApi();

      ServerDetails testServer = api.createWithHostnameAndRootPassword(
            ServerSpec.builder().datacenter("Falkenberg").platform("OpenVZ").templateName("Ubuntu 10.04 LTS 32-bit")
                  .diskSizeGB(5).memorySizeMB(512).cpuCores(1).transferGB(50).build(), hostName, UUID.randomUUID()
                  .toString().replace("-",""));

      assertNotNull(testServer.getId());
      assertEquals(testServer.getHostname(), hostName);
      assertFalse(testServer.getIps().isEmpty());

      ServerStatusChecker runningServerCounter = new ServerStatusChecker(api, testServer.getId(), 180, 10,
            TimeUnit.SECONDS);

      assertTrue(runningServerCounter.apply(Server.State.RUNNING));
      return runningServerCounter;
   }

   public static class ServerStatusChecker extends RetryablePredicate<Server.State> {
      private final String serverId;

      public String getServerId() {
         return serverId;
      }

      public ServerStatusChecker(final ServerApi api, final String serverId, long maxWait, long period,
            TimeUnit unit) {
         super(new Predicate<Server.State>() {

            public boolean apply(Server.State value) {
               ServerStatus status = api.getStatus(serverId, ServerStatusOptions.Builder.state());
               return status.getState() == value;
            }

         }, maxWait, period, unit);
         this.serverId = serverId;
      }
   }
}
