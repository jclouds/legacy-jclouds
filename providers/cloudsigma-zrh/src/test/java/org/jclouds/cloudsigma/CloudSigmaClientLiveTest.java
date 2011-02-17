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

package org.jclouds.cloudsigma;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.jclouds.Constants;
import org.jclouds.cloudsigma.domain.ClaimType;
import org.jclouds.cloudsigma.domain.CreateDriveRequest;
import org.jclouds.cloudsigma.domain.DriveData;
import org.jclouds.cloudsigma.domain.DriveInfo;
import org.jclouds.cloudsigma.domain.DriveStatus;
import org.jclouds.cloudsigma.domain.DriveType;
import org.jclouds.cloudsigma.domain.IDEDevice;
import org.jclouds.cloudsigma.domain.Model;
import org.jclouds.cloudsigma.domain.ProfileInfo;
import org.jclouds.cloudsigma.domain.Server;
import org.jclouds.cloudsigma.domain.ServerInfo;
import org.jclouds.cloudsigma.domain.ServerStatus;
import org.jclouds.cloudsigma.domain.StaticIPInfo;
import org.jclouds.cloudsigma.domain.VLANInfo;
import org.jclouds.cloudsigma.options.CloneDriveOptions;
import org.jclouds.cloudsigma.predicates.DriveClaimed;
import org.jclouds.cloudsigma.util.Servers;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.domain.Credentials;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.InetSocketAddressConnect;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.rest.RestContext;
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
 * Tests behavior of {@code CloudSigmaClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true)
public class CloudSigmaClientLiveTest {
   protected long driveSize = 8 * 1024 * 1024 * 1024l;
   protected int maxDriveImageTime = 300;
   protected String vncPassword = "Il0veVNC";
   protected CloudSigmaClient client;
   protected RestContext<CloudSigmaClient, CloudSigmaAsyncClient> context;
   protected Predicate<IPSocket> socketTester;

   protected String provider = "cloudsigma-zrh";
   protected String identity;
   protected String credential;
   protected String endpoint;
   protected String apiversion;
   protected Predicate<DriveInfo> driveNotClaimed;

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
      context = new ComputeServiceContextFactory().createContext(provider,
            ImmutableSet.<Module> of(new Log4JLoggingModule()), overrides).getProviderSpecificContext();

      client = context.getApi();
      driveNotClaimed = new RetryablePredicate<DriveInfo>(Predicates.not(new DriveClaimed(client)), maxDriveImageTime,
            1, TimeUnit.SECONDS);
      socketTester = new RetryablePredicate<IPSocket>(new InetSocketAddressConnect(), maxDriveImageTime, 1,
            TimeUnit.SECONDS);
   }

   @Test
   public void testGetProfileInfo() throws Exception {
      ProfileInfo profile = client.getProfileInfo();
      assertNotNull(profile);
   }

   @Test
   public void testListVLANs() throws Exception {
      Set<String> vlans = client.listVLANs();
      assertNotNull(vlans);
   }

   @Test
   public void testListVLANInfo() throws Exception {
      Set<? extends VLANInfo> vlans = client.listVLANInfo();
      assertNotNull(vlans);
   }

   @Test
   public void testGetVLAN() throws Exception {
      for (String vlanUUID : client.listVLANs()) {
         assert !"".equals(vlanUUID);
         assertNotNull(client.getVLANInfo(vlanUUID));
      }
   }

   @Test
   public void testListStaticIPs() throws Exception {
      Set<String> ips = client.listStaticIPs();
      assertNotNull(ips);
   }

   @Test
   public void testListStaticIPInfo() throws Exception {
      Set<? extends StaticIPInfo> ips = client.listStaticIPInfo();
      assertNotNull(ips);
   }

   @Test
   public void testGetStaticIP() throws Exception {
      for (String ipUUID : client.listStaticIPs()) {
         assert !"".equals(ipUUID);
         assertNotNull(client.getStaticIPInfo(ipUUID));
      }
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
      for (String driveUUID : client.listStandardDrives()) {
         assert !"".equals(driveUUID);
         DriveInfo drive = client.getDriveInfo(driveUUID);
         assertNotNull(drive);
         assert !drive.getType().equals(DriveType.UNRECOGNIZED) : drive;
         if (drive.getType() == DriveType.DISK && drive.getDriveType().contains("preinstalled"))
            System.out.println(drive.getName());
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
      assertEquals(newInfo.getType(), DriveType.DISK);
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
      assertEquals(drive.getType(), null);
   }

   @Test(dependsOnMethods = "testCreateDrive")
   public void testSetDriveData() throws Exception {

      DriveInfo drive2 = client.setDriveData(
            drive.getUuid(),
            new DriveData.Builder().claimType(ClaimType.SHARED).name("rediculous")
                  .readers(ImmutableSet.of("ffffffff-ffff-ffff-ffff-ffffffffffff"))
                  .use(ImmutableSet.of("networking", "security", "gateway")).build());

      assertNotNull(drive2.getUuid(), drive.getUuid());
      assertEquals(drive2.getName(), "rediculous");
      assertEquals(drive2.getClaimType(), ClaimType.SHARED);
      assertEquals(drive2.getReaders(), ImmutableSet.of("ffffffff-ffff-ffff-ffff-ffffffffffff"));
      assertEquals(drive2.getUse(), ImmutableSet.of("networking", "security", "gateway"));
      drive = drive2;
   }

   @Test
   public void testCreateAndDestroyVLAN() throws Exception {
      VLANInfo vlan = client.createVLAN(prefix);
      String id = vlan.getUuid();
      try {
         vlan = client.getVLANInfo(vlan.getUuid());
         assertEquals(vlan.getName(), prefix);

         vlan = client.renameVLAN(vlan.getUuid(), prefix + "2");
         // test that rename didn't work :)
         assertEquals(vlan.getName(), prefix);
         vlan = client.getVLANInfo(vlan.getUuid());
         assertEquals(vlan.getName(), prefix);
      } finally {
         client.destroyVLAN(id);
      }
   }

   @Test
   public void testCreateAndDestroyStaticIP() throws Exception {
      StaticIPInfo ip = client.createStaticIP();
      String id = ip.getAddress();
      try {
         ip = client.getStaticIPInfo(ip.getAddress());
         assertNotNull(ip);
         Logger.getAnonymousLogger().info("preparing drive");
         prepareDrive();

         Server serverRequest = Servers.smallWithStaticIP(prefix, drive.getUuid(), vncPassword, ip.getAddress())
               .build();

         Logger.getAnonymousLogger().info("starting server");
         server = client.createServer(serverRequest);
         assertEquals(server.getNics().get(0).getDhcp(), ip.getAddress());
      } finally {
         client.destroyServer(server.getUuid());
         client.destroyDrive(drive.getUuid());
         client.destroyStaticIP(id);
      }
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
                  .use(ImmutableSet.of("networking", "security", "gateway")).build());

      assertNotNull(server2.getUuid(), server.getUuid());
      assertEquals(server2.getName(), "rediculous");
      checkUse(server2);
      server = server2;
   }

   protected void checkUse(ServerInfo server2) {
      // bug where use aren't updated
      assertEquals(server2.getUse(), ImmutableSet.<String> of());
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
         System.err.println(ssh.exec("df -k").getOutput());
         System.err.println(ssh.exec("mount").getOutput());
         System.err.println(ssh.exec("uname -a").getOutput());
      } finally {
         if (ssh != null)
            ssh.disconnect();
      }
   }

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

   @Test
   public void testListStandardDrives() throws Exception {
      Set<String> drives = client.listStandardDrives();
      assertNotNull(drives);
   }

   @Test
   public void testListStandardCds() throws Exception {
      Set<String> drives = client.listStandardCds();
      assertNotNull(drives);
   }

   @Test
   public void testListStandardImages() throws Exception {
      Set<String> drives = client.listStandardImages();
      assertNotNull(drives);
   }

   protected Credentials getSshCredentials(Server server) {
      return new Credentials("cloudsigma", "cloudsigma");
   }

   protected void prepareDrive() {
      client.destroyDrive(drive.getUuid());
      drive = client.cloneDrive("6a9cd9c2-4814-4953-8e86-f8ee6a3e57d5", drive.getName(),
            new CloneDriveOptions().size(driveSize));
      assert driveNotClaimed.apply(drive) : client.getDriveInfo(drive.getUuid());
      System.err.println("after prepare" + client.getDriveInfo(drive.getUuid()));
   }

}
