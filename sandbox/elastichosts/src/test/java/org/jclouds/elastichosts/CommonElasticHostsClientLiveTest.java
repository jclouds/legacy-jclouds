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
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Properties;
import java.util.Set;

import org.jclouds.Constants;
import org.jclouds.elastichosts.domain.ClaimType;
import org.jclouds.elastichosts.domain.CreateDriveRequest;
import org.jclouds.elastichosts.domain.DriveData;
import org.jclouds.elastichosts.domain.DriveInfo;
import org.jclouds.elastichosts.domain.DriveStatus;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.RestContextFactory;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Tests behavior of {@code CommonElasticHostsClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true)
public abstract class CommonElasticHostsClientLiveTest<S extends CommonElasticHostsClient, A extends CommonElasticHostsAsyncClient> {

   protected S client;
   protected RestContext<S, A> context;

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

   @BeforeGroups(groups = "live")
   public void setupClient() {
      setupCredentials();
      Properties overrides = setupProperties();
      context = new RestContextFactory().createContext(provider, ImmutableSet.<Module> of(new Log4JLoggingModule()),
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
   protected DriveInfo info;

   @Test
   public void testCreate() throws Exception {
      info = client.createDrive(new CreateDriveRequest.Builder().name(prefix).size(4 * 1024 * 1024l).build());
      checkCreatedDrive();

      DriveInfo newInfo = client.getDriveInfo(info.getUuid());
      checkDriveMatchesGet(newInfo);

   }

   protected void checkDriveMatchesGet(DriveInfo newInfo) {
      assertEquals(newInfo.getUuid(), info.getUuid());
   }

   protected void checkCreatedDrive() {
      assertNotNull(info.getUuid());
      assertNotNull(info.getUser());
      assertEquals(info.getName(), prefix);
      assertEquals(info.getSize(), 4 * 1024 * 1024l);
      assertEquals(info.getStatus(), DriveStatus.ACTIVE);
      // for some reason, these occasionally return as 4096,1
      // assertEquals(info.getReadBytes(), 0l);
      // assertEquals(info.getWriteBytes(), 0l);
      // assertEquals(info.getReadRequests(), 0l);
      // assertEquals(info.getWriteRequests(), 0l);
      assertEquals(info.getEncryptionCipher(), "aes-xts-plain");
   }

   @Test(dependsOnMethods = "testCreate")
   public void testSetDriveData() throws Exception {

      DriveInfo info2 = client.setDriveData(
            info.getUuid(),
            new DriveData.Builder().claimType(ClaimType.SHARED).name("rediculous")
                  .readers(ImmutableSet.of("ffffffff-ffff-ffff-ffff-ffffffffffff"))
                  .tags(ImmutableSet.of("networking", "security", "gateway"))
                  .userMetadata(ImmutableMap.of("foo", "bar")).build());

      assertNotNull(info2.getUuid(), info.getUuid());
      assertEquals(info2.getName(), "rediculous");
      assertEquals(info2.getClaimType(), ClaimType.SHARED);
      assertEquals(info2.getReaders(), ImmutableSet.of("ffffffff-ffff-ffff-ffff-ffffffffffff"));
      assertEquals(info2.getTags(), ImmutableSet.of("networking", "security", "gateway"));
      assertEquals(info2.getUserMetadata(), ImmutableMap.of("foo", "bar"));
      info = info2;
   }

   @Test(dependsOnMethods = "testSetDriveData")
   public void testDestroyDrive() throws Exception {
      client.destroyDrive(info.getUuid());
      assertEquals(client.getDriveInfo(info.getUuid()), null);
   }

}
