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
import static org.testng.Assert.*;

import com.google.common.base.Predicate;
import org.jclouds.glesys.GleSYSAsyncClient;
import org.jclouds.glesys.GleSYSClient;
import org.jclouds.glesys.domain.ServerDetails;
import org.jclouds.glesys.domain.ServerState;
import org.jclouds.glesys.domain.ServerStatus;
import org.jclouds.glesys.options.ServerStatusOptions;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

import java.util.concurrent.TimeUnit;

/**
 * Tests behavior of {@code GleSYSClient}
 * 
 * @author Adrian Cole, Adam Lowe
 */
@Test(groups = "live")
public class BaseGleSYSClientLiveTest {

   protected RestContext<GleSYSClient, GleSYSAsyncClient> context;

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      if (context == null) {
         String identity = checkNotNull(System.getProperty("test.glesys.identity"), "test.glesys.identity");
         String credential = checkNotNull(System.getProperty("test.glesys.credential"), "test.glesys.credential");
   
         context = new RestContextFactory().createContext("glesys", identity, credential,
               ImmutableSet.<Module> of(new Log4JLoggingModule(), new SshjSshClientModule()));
      }
   }

   @AfterGroups(groups = "live")
   protected void tearDown() {
      if (context != null) {
         context.close();
         context = null;
      }
   }

   protected void createDomain(String domain) {
      final DomainClient client = context.getApi().getDomainClient();
      int before = client.listDomains().size();
      client.addDomain(domain);
      RetryablePredicate<Integer> result = new RetryablePredicate<Integer>(
            new Predicate<Integer>() {
               public boolean apply(Integer value) {
                  return client.listDomains().size() == value;
               }
            }, 30, 1, TimeUnit.SECONDS);

      assertTrue(result.apply(before + 1));
   }


   protected ServerStatusChecker createServer(String hostName) {
      ServerClient client = context.getApi().getServerClient();
      ServerDetails testServer = client.createServer("Falkenberg", "OpenVZ", hostName, "Ubuntu 10.04 LTS 32-bit", 5, 512, 1, "password", 50);

      assertNotNull(testServer.getId());
      assertEquals(testServer.getHostname(), hostName);
      assertFalse(testServer.getIps().isEmpty());

      ServerStatusChecker runningServerCounter = new ServerStatusChecker(client, testServer.getId(), 180, 10, TimeUnit.SECONDS);

      assertTrue(runningServerCounter.apply(ServerState.RUNNING));
      return runningServerCounter;
   }

   public static class ServerStatusChecker extends RetryablePredicate<ServerState> {
      private final String serverId;
      public String getServerId() {
         return serverId;
      }
      public ServerStatusChecker(final ServerClient client, final String serverId, long maxWait, long period, TimeUnit unit) {
         super(new Predicate<ServerState>() {

            public boolean apply(ServerState value) {
               ServerStatus status = client.getServerStatus(serverId, ServerStatusOptions.Builder.state());
               return status.getState() == value;
            }

         }, maxWait, period, unit);
         this.serverId = serverId;
      }
   }
}
