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
package org.jclouds.openstack.nova.live.novaclient;

import static org.jclouds.openstack.nova.live.PropertyHelper.setupKeyPair;
import static org.jclouds.openstack.nova.live.PropertyHelper.setupOverrides;
import static org.jclouds.openstack.nova.live.PropertyHelper.setupProperties;
import static org.jclouds.openstack.nova.options.CreateServerOptions.Builder.withFile;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.jclouds.http.HttpResponseException;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.net.IPSocket;
import org.jclouds.openstack.nova.NovaClient;
import org.jclouds.openstack.nova.domain.Server;
import org.jclouds.openstack.nova.domain.ServerStatus;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.ssh.SshClient;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * @author Victor Galkin
 */
@Test(groups = "live", sequential = true)
public class ServerCreateLiveTest {

   protected NovaClient client;
   protected SshClient.Factory sshFactory;
   @SuppressWarnings("unused")
   private Predicate<IPSocket> socketTester;
   protected String provider = "nova";

   Map<String, String> metadata = ImmutableMap.of("jclouds", "rackspace");
   Server server = null;
   Map<String, String> keyPair;

   @BeforeTest
   public void setupClient() throws IOException {
      Properties properties = setupOverrides(setupProperties(this.getClass()));

      Injector injector = new RestContextFactory().createContextBuilder(provider,
            ImmutableSet.<Module>of(new SLF4JLoggingModule(), new SshjSshClientModule()), properties)
            .buildInjector();

      client = injector.getInstance(NovaClient.class);

      sshFactory = injector.getInstance(SshClient.Factory.class);
      SocketOpen socketOpen = injector.getInstance(SocketOpen.class);
      socketTester = new RetryablePredicate<IPSocket>(socketOpen, 120, 1, TimeUnit.SECONDS);
      injector.injectMembers(socketOpen); // add logger

      keyPair = setupKeyPair(properties);
   }

   @Test(expectedExceptions = HttpResponseException.class, expectedExceptionsMessageRegExp = ".*Internal Server Error.*")
   public void testCreateServerWithUnknownImage() throws Exception {
      try {
         server = client.createServer("serverName", String.valueOf(88888888), "1", withFile("/etc/jclouds.txt",
               "rackspace".getBytes()).withMetadata(metadata));
      } catch (HttpResponseException e) {
         throw e;
      }
   }

   @Test(expectedExceptions = HttpResponseException.class, expectedExceptionsMessageRegExp = ".*Internal Server Error.*")
   public void testCreateServerWithUnknownFlavor() throws Exception {
      try {
         server = client.createServer("serverName", String.valueOf(13), "88888888", withFile("/etc/jclouds.txt",
               "rackspace".getBytes()).withMetadata(metadata));
      } catch (HttpResponseException e) {
         throw e;
      }
   }

   @AfterMethod
   public void after() {
      if (server != null) client.deleteServer(server.getId());
   }

   @Test(enabled = true)
   public void testCreateServer() throws Exception {
//      String imageRef = client.getImage(13).getURI().toASCIIString();
//      String flavorRef = client.getFlavor(1).getURI().toASCIIString();
//      String serverName = serverPrefix + "createserver" + new SecureRandom().nextInt();
//      Server server = client.createServer(serverName, imageRef, flavorRef, withFile("/etc/jclouds.txt",
//            "rackspace".getBytes()).withMetadata(metadata));
//
//      assertNotNull(server.getAdminPass());
//      assertEquals(server.getStatus(), ServerStatus.BUILD);
//      serverId = server.getId();
//      adminPass = server.getAdminPass();
//      blockUntilServerActive(serverId);
//      client.getServer(serverId).getAddresses().getPublicAddresses().iterator().next().getAddress();
   }

   @SuppressWarnings("unused")
   private void blockUntilServerActive(int serverId) throws InterruptedException {
      Server currentDetails;
      for (currentDetails = client.getServer(serverId); currentDetails.getStatus() != ServerStatus.ACTIVE; currentDetails = client
            .getServer(serverId)) {
         System.out.printf("blocking on status active%n%s%n", currentDetails);
         Thread.sleep(5 * 1000);
      }
   }
}
