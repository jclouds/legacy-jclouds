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
package org.jclouds.elasticstack;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.IOException;
import java.util.Set;
import java.util.logging.Logger;

import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.elasticstack.domain.ClaimType;
import org.jclouds.elasticstack.domain.CreateDriveRequest;
import org.jclouds.elasticstack.domain.DriveData;
import org.jclouds.elasticstack.domain.DriveInfo;
import org.jclouds.elasticstack.domain.DriveStatus;
import org.jclouds.elasticstack.domain.IDEDevice;
import org.jclouds.elasticstack.domain.ImageConversionType;
import org.jclouds.elasticstack.domain.Model;
import org.jclouds.elasticstack.domain.Server;
import org.jclouds.elasticstack.domain.ServerInfo;
import org.jclouds.elasticstack.domain.ServerStatus;
import org.jclouds.elasticstack.predicates.DriveClaimed;
import org.jclouds.elasticstack.util.Servers;
import org.jclouds.io.Payloads;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.ssh.SshClient;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.jclouds.util.Strings2;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.net.HostAndPort;
import com.google.gson.Gson;
import com.google.inject.Guice;

/**
 * Tests behavior of {@code ElasticStackClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "ElasticStackClientLiveTest")
public class ElasticStackClientLiveTest extends BaseComputeServiceContextLiveTest {

   public ElasticStackClientLiveTest() {
      provider = "elasticstack";
   }

   protected long driveSize = 1 * 1024 * 1024 * 1024l;
   protected int maxDriveImageTime = 360;
   protected String vncPassword = "Il0veVNC";
   protected ElasticStackClient client;
   protected Predicate<HostAndPort> socketTester;
   protected Predicate<DriveInfo> driveNotClaimed;
   protected String imageId;

   @Override
   @BeforeClass(groups = { "integration", "live" })
   public void setupContext() {
      super.setupContext();
      imageId = view.getComputeService().templateBuilder().build().getImage().getId();
         
      client = view.utils().injector().getInstance(ElasticStackClient.class);
      driveNotClaimed = retry(Predicates.not(new DriveClaimed(client)), maxDriveImageTime, 1, SECONDS);
      SocketOpen socketOpen = context.utils().injector().getInstance(SocketOpen.class);
      socketTester = retry(socketOpen, maxDriveImageTime, 1, SECONDS);
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
         assert !"".equals(driveUUID) : driveUUID;
         assert client.getDriveInfo(driveUUID) != null : driveUUID;
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

      DriveInfo drive2 = client.setDriveData(drive.getUuid(), new DriveData.Builder().claimType(ClaimType.SHARED).name(
               "rediculous").readers(ImmutableSet.of("ffffffff-ffff-ffff-ffff-ffffffffffff")).tags(
               ImmutableSet.of("networking", "security", "gateway")).userMetadata(ImmutableMap.of("foo", "bar"))
               .build());

      assertNotNull(drive2.getUuid(), drive.getUuid());
      assertEquals(drive2.getName(), "rediculous");
      assertEquals(drive2.getClaimType(), ClaimType.SHARED);
      assertEquals(drive2.getReaders(), ImmutableSet.of("ffffffff-ffff-ffff-ffff-ffffffffffff"));
      assertEquals(drive2.getTags(), ImmutableSet.of("networking", "security", "gateway"));
      assertEquals(drive2.getUserMetadata(), ImmutableMap.of("foo", "bar"));
      drive = drive2;
   }

   protected ServerInfo server;

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
      assertEquals(server.getDevices(), ImmutableMap.of("ide:0:0", new IDEDevice.Builder(0, 0).uuid(drive.getUuid())
               .build()));
      assertEquals(server.getBootDeviceIds(), ImmutableSet.of("ide:0:0"));
      assertEquals(server.getNics().get(0).getDhcp(), server.getVnc().getIp());
      assertEquals(server.getNics().get(0).getModel(), Model.E1000);
      assertEquals(server.getStatus(), ServerStatus.ACTIVE);
   }

   @Test(dependsOnMethods = "testCreateAndStartServer")
   public void testConnectivity() throws Exception {
      HostAndPort vncsocket = HostAndPort.fromParts(server.getVnc().getIp(), 5900);
      Logger.getAnonymousLogger().info("awaiting vnc: " + vncsocket);
      assert socketTester.apply(vncsocket) : server;
      HostAndPort sshsocket = HostAndPort.fromParts(server.getNics().get(0).getDhcp(), 22);
      Logger.getAnonymousLogger().info("awaiting ssh: " + sshsocket);
      assert socketTester.apply(sshsocket) : server;
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

      ServerInfo server2 = client.setServerConfiguration(server.getUuid(), Server.Builder.fromServer(server).name(
               "rediculous").tags(ImmutableSet.of("networking", "security", "gateway")).userMetadata(
               ImmutableMap.of("foo", "bar")).build());

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

   protected void doConnectViaSsh(Server server, LoginCredentials creds) throws IOException {
      SshClient ssh = Guice.createInjector(new SshjSshClientModule()).getInstance(SshClient.Factory.class).create(
               HostAndPort.fromParts(server.getVnc().getIp(), 22), creds);
      try {
         ssh.connect();
         ExecResponse hello = ssh.exec("echo hello");
         assertEquals(hello.getOutput().trim(), "hello");
         System.err.println(ssh.exec("df -k").getOutput());
         System.err.println(ssh.exec("mount").getOutput());
         System.err.println(ssh.exec("uname -a").getOutput());
      } finally {
         if (ssh != null)
            ssh.disconnect();
      }
   }

   @AfterGroups(groups = "live")
   @Override
   protected void tearDownContext() {
      try {
         client.destroyServer(server.getUuid());
      } catch (Exception e) {
         // no need to check null or anything as we swallow all
      }
      try {
         client.destroyDrive(drive.getUuid());
      } catch (Exception e) {

      }
      super.tearDownContext();
   }

   private DriveInfo drive2;
   private DriveInfo drive3;

   public void testWeCanReadAndWriteToDrive() throws IOException {
      drive2 = client.createDrive(new CreateDriveRequest.Builder().name(prefix + "2").size(1 * 1024 * 1024l).build());
      client.writeDrive(drive2.getUuid(), Payloads.newStringPayload("foo"));
      assertEquals(Strings2.toString(client.readDrive(drive2.getUuid(), 0, 3)), "foo");
   }

   @Test(dependsOnMethods = "testWeCanReadAndWriteToDrive")
   public void testWeCopyADriveContentsViaGzip() throws IOException {
      try {
         drive3 = client
                  .createDrive(new CreateDriveRequest.Builder().name(prefix + "3").size(1 * 1024 * 1024l).build());
         System.err.println("before image; drive 2" + client.getDriveInfo(drive2.getUuid()));
         System.err.println("before image; drive 3" + client.getDriveInfo(drive3.getUuid()));
         client.imageDrive(drive2.getUuid(), drive3.getUuid());
         assert driveNotClaimed.apply(drive3) : client.getDriveInfo(drive3.getUuid());
         assert driveNotClaimed.apply(drive2) : client.getDriveInfo(drive2.getUuid());
         System.err.println("after image; drive 2" + client.getDriveInfo(drive2.getUuid()));
         System.err.println("after image; drive 3" + client.getDriveInfo(drive3.getUuid()));
         assertEquals(Strings2.toString(client.readDrive(drive3.getUuid(), 0, 3)), "foo");
      } finally {
         client.destroyDrive(drive2.getUuid());
         client.destroyDrive(drive3.getUuid());
      }
   }

   protected LoginCredentials getSshCredentials(Server server) {
      return LoginCredentials.builder().user("toor").password(server.getVnc().getPassword()).build();
   }

   protected void prepareDrive() {
      System.err.println("before prepare" + client.getDriveInfo(drive.getUuid()));
      client.imageDrive(imageId, drive.getUuid(), ImageConversionType.GUNZIP);
      assert driveNotClaimed.apply(drive) : client.getDriveInfo(drive.getUuid());
      System.err.println("after prepare" + client.getDriveInfo(drive.getUuid()));
   }

}
