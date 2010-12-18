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

import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.Constants;
import org.jclouds.elasticstack.domain.ClaimType;
import org.jclouds.elasticstack.domain.CreateDriveRequest;
import org.jclouds.elasticstack.domain.DriveData;
import org.jclouds.elasticstack.domain.DriveInfo;
import org.jclouds.elasticstack.domain.DriveStatus;
import org.jclouds.elasticstack.domain.IDEDevice;
import org.jclouds.elasticstack.domain.Model;
import org.jclouds.elasticstack.domain.NIC;
import org.jclouds.elasticstack.domain.Server;
import org.jclouds.elasticstack.domain.ServerInfo;
import org.jclouds.elasticstack.domain.ServerStatus;
import org.jclouds.elasticstack.domain.VNC;
import org.jclouds.elasticstack.predicates.DriveClaimed;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.RestContextFactory;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.inject.Module;

/**
 * Tests behavior of {@code CommonElasticStackClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true)
public abstract class CommonElasticStackClientLiveTest<S extends CommonElasticStackClient, A extends CommonElasticStackAsyncClient> {

   protected static final String VNC_PASSWORD = "XXXXXXXX";
   protected S client;
   protected RestContext<S, A> context;

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
      driveNotClaimed = new RetryablePredicate<DriveInfo>(Predicates.not(new DriveClaimed(client)), 30, 1,
            TimeUnit.SECONDS);
   }

   @AfterGroups(groups = "live")
   void tearDown() {
      if (context != null)
         context.close();
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
      drive = client.createDrive(new CreateDriveRequest.Builder().name(prefix).size(1 * 1024 * 1024 * 1024l).build());
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
      assertEquals(drive.getSize(), 1 * 1024 * 1024 * 1024l);
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
      prepareDrive();
      Server serverRequest = new Server.Builder().name(prefix).cpu(1000).mem(512).persistent(true)
            .devices(ImmutableMap.of("ide:0:0", new IDEDevice.Builder(0, 0).uuid(drive.getUuid()).build()))
            .bootDeviceIds(ImmutableSet.of("ide:0:0")).nics(ImmutableSet.of(new NIC.Builder().model(Model.E1000).

            build())).vnc(new VNC(null, VNC_PASSWORD, false)).build();

      server = client.createAndStartServer(serverRequest);
      checkCreatedServer();

      Server newInfo = client.getServerInfo(server.getUuid());
      checkServerMatchesGet(newInfo);

   }

   protected void checkServerMatchesGet(Server newInfo) {
      assertEquals(newInfo.getUuid(), server.getUuid());
   }

   protected void checkCreatedServer() {
      System.out.println(new Gson().toJson(server));
      assertNotNull(server.getUuid());
      assertNotNull(server.getUser());
      assertEquals(server.getName(), prefix);
      assertEquals(server.isPersistent(), true);
      assertEquals(server.getDevices(),
            ImmutableMap.of("ide:0:0", new IDEDevice.Builder(0, 0).uuid(drive.getUuid()).build()));
      assertEquals(server.getBootDeviceIds(), ImmutableSet.of("ide:0:0"));
      assertEquals(server.getNics(), ImmutableSet.of(new NIC.Builder()
            .model(Model.E1000)
            .block(
                  ImmutableList.of("tcp/43594", "tcp/5902", "udp/5060", "tcp/5900", "tcp/5901", "tcp/21", "tcp/22",
                        "tcp/23", "tcp/25", "tcp/110", "tcp/143", "tcp/43595")).build()));
      assertEquals(server.getStatus(), ServerStatus.ACTIVE);
   }
//TODO
//   @Test(dependsOnMethods = "testCreateAndStartServer")
//   public void testSetServerConfiguration() throws Exception {
//
//      ServerInfo server2 = client.setServerConfiguration(server.getUuid(), new Server.Builder().name("rediculous")
//            .tags(ImmutableSet.of("networking", "security", "gateway")).userMetadata(ImmutableMap.of("foo", "bar"))
//            .build());
//
//      assertNotNull(server2.getUuid(), server.getUuid());
//      assertEquals(server2.getName(), "rediculous");
//      assertEquals(server2.getTags(), ImmutableSet.of("networking", "security", "gateway"));
//      assertEquals(server2.getUserMetadata(), ImmutableMap.of("foo", "bar"));
//      server = server2;
//   }
// @Test(dependsOnMethods = "testSetServerConfiguration")

   @Test(dependsOnMethods = "testCreateAndStartServer")
   public void testLifeCycle() throws Exception {
      client.stopServer(server.getUuid());
      assertEquals(client.getServerInfo(server.getUuid()).getStatus(), ServerStatus.STOPPED);

      client.startServer(server.getUuid());
      assertEquals(client.getServerInfo(server.getUuid()).getStatus(), ServerStatus.ACTIVE);

      client.resetServer(server.getUuid());
      assertEquals(client.getServerInfo(server.getUuid()).getStatus(), ServerStatus.ACTIVE);

      client.shutdownServer(server.getUuid());
      assertEquals(client.getServerInfo(server.getUuid()).getStatus(), ServerStatus.STOPPED);
   }

   @Test(dependsOnMethods = "testLifeCycle")
   public void testDestroyServer() throws Exception {
      client.destroyServer(server.getUuid());
      assertEquals(client.getServerInfo(server.getUuid()), null);
   }

   @Test(dependsOnMethods = "testDestroyServer")
   public void testDestroyDrive() throws Exception {
      client.destroyDrive(drive.getUuid());
      assertEquals(client.getDriveInfo(drive.getUuid()), null);
   }

   @AfterTest
   public void cleanUp() {
      try {
         client.destroyServer(server.getUuid());
      } catch (Exception e) {
         // no need to check null or anything as we swallow all
      }
      try {
         client.destroyDrive(drive.getUuid());
      } catch (Exception e) {

      }
   }
}
