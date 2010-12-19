/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.elasticstack;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.jclouds.Constants;
import org.jclouds.domain.Credentials;
import org.jclouds.elasticstack.domain.ClaimType;
import org.jclouds.elasticstack.domain.CreateDriveRequest;
import org.jclouds.elasticstack.domain.DriveData;
import org.jclouds.elasticstack.domain.DriveInfo;
import org.jclouds.elasticstack.domain.DriveStatus;
import org.jclouds.elasticstack.domain.IDEDevice;
import org.jclouds.elasticstack.domain.Model;
import org.jclouds.elasticstack.domain.Server;
import org.jclouds.elasticstack.domain.ServerInfo;
import org.jclouds.elasticstack.domain.ServerStatus;
import org.jclouds.elasticstack.predicates.DriveClaimed;
import org.jclouds.elasticstack.util.Servers;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.InetSocketAddressConnect;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.ssh.ExecResponse;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Module;

/**
 * Tests behavior of {@code CommonElasticStackClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true)
public abstract class CommonElasticStackClientLiveTest<S extends CommonElasticStackClient, A extends CommonElasticStackAsyncClient> {

   protected long driveSize = 1 * 1024 * 1024 * 1024l;
   protected int maxDriveImageTime = 120;
   protected String vncPassword = "Il0veVNC";
   protected S client;
   protected RestContext<S, A> context;
   protected Predicate<IPSocket> socketTester;

   protected String provider = "elasticstack";
   protected String identity;
   protected String credential;
   protected String endpoint;
   protected String apiversion;
   protected RetryablePredicate<DriveInfo> driveNotClaimed;

   protected void setupCredentials() {
      identity = checkNotNull(System.getProperty("test." + provider + ".identity"), "test." + provider + ".identity");
      credential = System.getProperty("test." + provider + ".credential");
      endpoint = System.getProperty("test." + provider + ".endpoint");
      apiversion = System.getProperty("test." + provider + ".apiversion");
   }

   protected Properties setupProperties() {
      Properties overrides = new Properties();
      overrides.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "true");
      overrides.setProperty(Constants.PROPERTY_RELAX_HOSTNAME, "true");
      overrides.setProperty(provider + ".identity", identity);
      if (credential != null)
         overrides.setProperty(provider + ".credential", credential);
      if (endpoint != null)
         overrides.setProperty(provider + ".endpoint", endpoint);
      if (apiversion != null)
         overrides.setProperty(provider + ".apiversion", apiversion);
      return overrides;
   }

   @BeforeGroups(groups = "live")
   public void setupClient() {
      setupCredentials();
      Properties overrides = setupProperties();
      context = new RestContextFactory().createContext(provider, ImmutableSet.<Module> of(new Log4JLoggingModule()),
            overrides);

      client = context.getApi();
      driveNotClaimed = new RetryablePredicate<DriveInfo>(Predicates.not(new DriveClaimed(client)), maxDriveImageTime,
            1, TimeUnit.SECONDS);
      socketTester = new RetryablePredicate<IPSocket>(new InetSocketAddressConnect(), maxDriveImageTime, 1,
            TimeUnit.SECONDS);
   }

   @Test
   public void testListServers() throws Exception {
      Set<String> servers = client.listServers();
      assertNotNull(servers);
   }

   @Test
   public void testListServerInfo() throws Exception {
      Set<? extends ServerInfo> servers = client.listServerInfo();
      assertNotNull(servers);
   }

   @Test
   public void testGetServer() throws Exception {
      for (String serverUUID : client.listServers()) {
         assert !"".equals(serverUUID);
         assertNotNull(client.getServerInfo(serverUUID));
      }
   }

   @Test
   public void testListDrives() throws Exception {
      Set<String> drives = client.listDrives();
      assertNotNull(drives);
   }

   @Test
   public void testListDriveInfo() throws Exception {
      Set<? extends DriveInfo> drives = client.listDriveInfo();
      assertNotNull(drives);
   }

   @Test
   public void testGetDrive() throws Exception {
      for (String driveUUID : client.listDrives()) {
         assert !"".equals(driveUUID);
         assertNotNull(client.getDriveInfo(driveUUID));
      }
   }

   protected String prefix = System.getProperty("user.name") + ".test";
   protected DriveInfo drive;

   @Test
   public void testCreateDrive() throws Exception {
      drive = client.createDrive(new CreateDriveRequest.Builder().name(prefix).size(driveSize).build());
      checkCreatedDrive();

      DriveInfo newInfo = client.getDriveInfo(drive.getUuid());
      checkDriveMatchesGet(newInfo);

   }

   protected void checkDriveMatchesGet(DriveInfo newInfo) {
      assertEquals(newInfo.getUuid(), drive.getUuid());
   }

   protected void checkCreatedDrive() {
      assertNotNull(drive.getUuid());
      assertNotNull(drive.getUser());
      assertEquals(drive.getName(), prefix);
      assertEquals(drive.getSize(), driveSize);
      assertEquals(drive.getStatus(), DriveStatus.ACTIVE);
      // for some reason, these occasionally return as 4096,1
      // assertEquals(info.getReadBytes(), 0l);
      // assertEquals(info.getWriteBytes(), 0l);
      // assertEquals(info.getReadRequests(), 0l);
      // assertEquals(info.getWriteRequests(), 0l);
      assertEquals(drive.getEncryptionCipher(), "aes-xts-plain");
   }

   @Test(dependsOnMethods = "testCreateDrive")
   public void testSetDriveData() throws Exception {

      DriveInfo drive2 = client.setDriveData(
            drive.getUuid(),
            new DriveData.Builder().claimType(ClaimType.SHARED).name("rediculous")
                  .readers(ImmutableSet.of("ffffffff-ffff-ffff-ffff-ffffffffffff"))
                  .tags(ImmutableSet.of("networking", "security", "gateway"))
                  .userMetadata(ImmutableMap.of("foo", "bar")).build());

      assertNotNull(drive2.getUuid(), drive.getUuid());
      assertEquals(drive2.getName(), "rediculous");
      assertEquals(drive2.getClaimType(), ClaimType.SHARED);
      assertEquals(drive2.getReaders(), ImmutableSet.of("ffffffff-ffff-ffff-ffff-ffffffffffff"));
      assertEquals(drive2.getTags(), ImmutableSet.of("networking", "security", "gateway"));
      assertEquals(drive2.getUserMetadata(), ImmutableMap.of("foo", "bar"));
      drive = drive2;
   }

   protected ServerInfo server;

   protected abstract void prepareDrive();

   @Test(dependsOnMethods = "testSetDriveData")
   public void testCreateAndStartServer() throws Exception {
      Logger.getAnonymousLogger().info("preparing drive");
      prepareDrive();

      Server serverRequest = Servers.small(prefix, drive.getUuid(), vncPassword).build();

      Logger.getAnonymousLogger().info("starting server");
      server = client.createServer(serverRequest);
      client.startServer(server.getUuid());
      server = client.getServerInfo(server.getUuid());
      checkStartedServer();

      Server newInfo = client.getServerInfo(server.getUuid());
      checkServerMatchesGet(newInfo);

   }

   protected void checkServerMatchesGet(Server newInfo) {
      assertEquals(newInfo.getUuid(), server.getUuid());
   }

   protected void checkStartedServer() {
      System.out.println(new Gson().toJson(server));
      assertNotNull(server.getUuid());
      assertNotNull(server.getUser());
      assertEquals(server.getName(), prefix);
      assertEquals(server.isPersistent(), true);
      assertEquals(server.getDevices(),
            ImmutableMap.of("ide:0:0", new IDEDevice.Builder(0, 0).uuid(drive.getUuid()).build()));
      assertEquals(server.getBootDeviceIds(), ImmutableSet.of("ide:0:0"));
      assertEquals(server.getNics().get(0).getDhcp(), server.getVnc().getIp());
      assertEquals(server.getNics().get(0).getModel(), Model.E1000);
      assertEquals(server.getStatus(), ServerStatus.ACTIVE);
   }

   @Test(dependsOnMethods = "testCreateAndStartServer")
   public void testConnectivity() throws Exception {
      Logger.getAnonymousLogger().info("awaiting vnc");
      assert socketTester.apply(new IPSocket(server.getVnc().getIp(), 5900)) : server;
      Logger.getAnonymousLogger().info("awaiting ssh");
      assert socketTester.apply(new IPSocket(server.getNics().get(0).getDhcp(), 22)) : server;
      doConnectViaSsh(server, getSshCredentials(server));
   }

   @Test(dependsOnMethods = "testConnectivity")
   public void testLifeCycle() throws Exception {
      client.stopServer(server.getUuid());
      assertEquals(client.getServerInfo(server.getUuid()).getStatus(), ServerStatus.STOPPED);

      client.startServer(server.getUuid());
      assertEquals(client.getServerInfo(server.getUuid()).getStatus(), ServerStatus.ACTIVE);

      client.resetServer(server.getUuid());
      assertEquals(client.getServerInfo(server.getUuid()).getStatus(), ServerStatus.ACTIVE);

      client.shutdownServer(server.getUuid());
      // behavior on shutdown depends on how your server OS is set up to respond to an ACPI power
      // button signal
      assert (client.getServerInfo(server.getUuid()).getStatus() == ServerStatus.ACTIVE || client.getServerInfo(
            server.getUuid()).getStatus() == ServerStatus.STOPPED);
   }

   @Test(dependsOnMethods = "testLifeCycle")
   public void testSetServerConfiguration() throws Exception {
      client.stopServer(server.getUuid());
      assertEquals(client.getServerInfo(server.getUuid()).getStatus(), ServerStatus.STOPPED);

      ServerInfo server2 = client.setServerConfiguration(
            server.getUuid(),
            Server.Builder.fromServer(server).name("rediculous")
                  .tags(ImmutableSet.of("networking", "security", "gateway"))
                  .userMetadata(ImmutableMap.of("foo", "bar")).build());

      assertNotNull(server2.getUuid(), server.getUuid());
      assertEquals(server2.getName(), "rediculous");
      checkTagsAndMetadata(server2);
      server = server2;
   }

   protected void checkTagsAndMetadata(ServerInfo server2) {
      assertEquals(server2.getTags(), ImmutableSet.of("networking", "security", "gateway"));
      assertEquals(server2.getUserMetadata(), ImmutableMap.of("foo", "bar"));
   }

   @Test(dependsOnMethods = "testSetServerConfiguration")
   public void testDestroyServer() throws Exception {
      client.destroyServer(server.getUuid());
      assertEquals(client.getServerInfo(server.getUuid()), null);
   }

   @Test(dependsOnMethods = "testDestroyServer")
   public void testDestroyDrive() throws Exception {
      client.destroyDrive(drive.getUuid());
      assertEquals(client.getDriveInfo(drive.getUuid()), null);
   }

   protected void doConnectViaSsh(Server server, Credentials creds) throws IOException {
      SshClient ssh = Guice.createInjector(new JschSshClientModule()).getInstance(SshClient.Factory.class)
            .create(new IPSocket(server.getVnc().getIp(), 22), creds);
      try {
         ssh.connect();
         ExecResponse hello = ssh.exec("echo hello");
         assertEquals(hello.getOutput().trim(), "hello");
      } finally {
         if (ssh != null)
            ssh.disconnect();
      }
   }

   protected abstract Credentials getSshCredentials(Server server);

   @AfterGroups(groups = "live")
   protected void tearDown() {
      try {
         client.destroyServer(server.getUuid());
      } catch (Exception e) {
         // no need to check null or anything as we swallow all
      }
      try {
         client.destroyDrive(drive.getUuid());
      } catch (Exception e) {

      }
      if (context != null)
         context.close();
   }
}
