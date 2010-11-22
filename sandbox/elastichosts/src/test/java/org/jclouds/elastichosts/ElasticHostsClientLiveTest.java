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

package org.jclouds.elastichosts;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.rest.RestContextFactory.contextSpec;
import static org.jclouds.rest.RestContextFactory.createContext;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import org.jclouds.Constants;
import org.jclouds.elastichosts.domain.ClaimType;
import org.jclouds.elastichosts.domain.CreateDriveRequest;
import org.jclouds.elastichosts.domain.DriveData;
import org.jclouds.elastichosts.domain.DriveInfo;
import org.jclouds.io.Payloads;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.RestContext;
import org.jclouds.util.Utils;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Module;

/**
 * Tests behavior of {@code ElasticHostsClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "elastichosts.ElasticHostsClientLiveTest")
public class ElasticHostsClientLiveTest {

   private ElasticHostsClient client;
   private RestContext<ElasticHostsClient, ElasticHostsAsyncClient> context;

   protected String provider = "elastichosts";
   protected String identity;
   protected String credential;
   protected String endpoint;
   protected String apiversion;

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

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      setupCredentials();
      Properties overrides = setupProperties();
      context = createContext(
            contextSpec(provider, endpoint, "1.0", identity, credential, ElasticHostsClient.class,
                  ElasticHostsAsyncClient.class, (Class) ElasticHostsPropertiesBuilder.class,
                  (Class) ElasticHostsContextBuilder.class, ImmutableSet.<Module> of(new Log4JLoggingModule())),
            overrides);

      client = context.getApi();
   }

   @AfterGroups(groups = "live")
   void tearDown() {
      if (context != null)
         context.close();
   }

   @Test
   public void testListDrives() throws Exception {
      Set<String> drives = client.listDrives();
      assertNotNull(drives);
   }

   @Test
   public void testListDriveInfo() throws Exception {
      Set<DriveInfo> drives = client.listDriveInfo();
      assertNotNull(drives);
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

   @Test
   public void testGetDrive() throws Exception {
      for (String driveUUID : client.listDrives()) {
         assertNotNull(client.getDriveInfo(driveUUID));
      }
      for (String driveUUID : client.listStandardDrives()) {
         assertNotNull(client.getDriveInfo(driveUUID));
      }
   }

   private String prefix = System.getProperty("user.name") + ".test";
   private DriveInfo info;
   private DriveInfo info2;

   @Test
   public void testCreate() throws Exception {
      try {
         findAndDestroyDrive(prefix);
      } catch (Exception e) {

      }

      info = client.createDrive(new CreateDriveRequest.Builder().name(prefix).size(1024 * 1024l).build());
      assertNotNull(info.getUuid());
      assertEquals(info.getName(), prefix);
      assertEquals(info.getSize(), 1024 * 1024l);
      assertEquals(info, client.getDriveInfo(info.getUuid()));

   }

   @Test(dependsOnMethods = "testCreate")
   public void testWeCanReadAndWriteToDrive() throws IOException {
      client.writeDrive(info.getUuid(), Payloads.newStringPayload("foo"));
      assertEquals(Utils.toStringAndClose(client.readDrive(info.getUuid()).getInput()), "foo");
   }

   @Test(dependsOnMethods = "testWeCanReadAndWriteToDrive")
   public void testWeCopyADriveContentsViaGzip() throws IOException {
      try {
         findAndDestroyDrive(prefix + "2");
      } catch (Exception e) {

      }
      try {
         info2 = client.createDrive(new CreateDriveRequest.Builder().name(prefix + "2").size(1024 * 1024l).build());
         client.imageDrive(info.getUuid(), info2.getUuid());

         // TODO block until complete
         System.err.println("state " + client.getDriveInfo(info2.getUuid()));
         assertEquals(Utils.toStringAndClose(client.readDrive(info2.getUuid()).getInput()), "foo");
      } finally {
         findAndDestroyDrive(prefix + "2");
      }

   }

   @Test(dependsOnMethods = "testCreate")
   public void testSetDriveData() throws Exception {

      DriveInfo info2 = client.setDriveData(
            info.getUuid(),
            new DriveData.Builder().claimType(ClaimType.SHARED).name("rediculous")
                  .readers(ImmutableSet.of("ffffffff-ffff-ffff-ffff-ffffffffffff"))
                  .tags(ImmutableSet.of("tag1", "tag2")).userMetadata(ImmutableMap.of("foo", "bar")).build());

      assertNotNull(info2.getUuid(), info.getUuid());
      assertEquals(info.getName(), "rediculous");
      assertEquals(info.getClaimType(), ClaimType.SHARED);
      assertEquals(info.getReaders(), ImmutableSet.of("ffffffff-ffff-ffff-ffff-ffffffffffff"));
      assertEquals(info.getTags(), ImmutableSet.of("tag1", "tag2"));
      assertEquals(info.getUserMetadata(), ImmutableMap.of("foo", "bar"));
   }

   @Test(dependsOnMethods = "testSetDriveData")
   public void testDestroyDrive() throws Exception {

      findAndDestroyDrive(prefix);
      assertEquals(client.getDriveInfo(info.getUuid()), null);

   }

   protected void findAndDestroyDrive(final String prefix) {
      DriveInfo drive = Iterables.find(client.listDriveInfo(), new Predicate<DriveInfo>() {

         @Override
         public boolean apply(DriveInfo input) {
            return input.getName().equals(prefix);
         }

      });
      client.destroyDrive(drive.getUuid());
   }

}
