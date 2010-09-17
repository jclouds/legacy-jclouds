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

package org.jclouds.opscodeplatform;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import org.jclouds.chef.BaseChefClientLiveTest;
import org.jclouds.chef.ChefClient;
import org.jclouds.chef.config.ChefParserModule;
import org.jclouds.crypto.Pems;
import org.jclouds.json.Json;
import org.jclouds.json.config.GsonModule;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.opscodeplatform.domain.Organization;
import org.jclouds.opscodeplatform.domain.User;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.HttpClient;
import org.jclouds.rest.RestContextFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Files;
import com.google.inject.Guice;
import com.google.inject.Module;

/**
 * Tests behavior of {@code OpscodePlatformClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "chef.OpscodePlatformClientLiveTest")
public class OpscodePlatformClientLiveTest extends BaseChefClientLiveTest {

   private OpscodePlatformContext validatorConnection;
   private OpscodePlatformContext clientConnection;
   private OpscodePlatformContext adminConnection;

   private String validator;
   private String adminKey;
   private String validatorKey;

   @Override
   @BeforeClass(groups = { "live" })
   public void setupClient() throws IOException {
      validator = checkNotNull(System.getProperty("jclouds.test.validator"), "jclouds.test.validator");
      orgname = validator.substring(0, validator.lastIndexOf('-'));
      String validatorKeyFile = System.getProperty("jclouds.test.validator.key");
      if (validatorKeyFile == null || validatorKeyFile.equals(""))
         validatorKeyFile = System.getProperty("user.home") + "/.chef/" + orgname + "-validator.pem";
      user = checkNotNull(System.getProperty("jclouds.test.identity"), "jclouds.test.identity");
      String keyfile = System.getProperty("jclouds.test.credential");
      if (keyfile == null || keyfile.equals(""))
         keyfile = System.getProperty("user.home") + "/.chef/" + user + ".pem";

      validatorKey = Files.toString(new File(validatorKeyFile), Charsets.UTF_8);
      adminKey = Files.toString(new File(keyfile), Charsets.UTF_8);

      validatorConnection = createConnection(validator, validatorKey);
      adminConnection = createConnection(user, adminKey);
      json = Guice.createInjector(new GsonModule(), new ChefParserModule()).getInstance(Json.class);
   }

   private OpscodePlatformContext createConnection(String identity, String key) throws IOException {
      Properties props = new Properties();
      return (OpscodePlatformContext) new RestContextFactory()
               .<OpscodePlatformClient, OpscodePlatformAsyncClient> createContext("opscodeplatform", identity, key,
                        ImmutableSet.<Module> of(new Log4JLoggingModule()), props);
   }

   @Override
   protected HttpClient getHttp() {
      return adminConnection.utils().http();
   }

   @Override
   protected ChefClient getAdminConnection() {
      return createChefClient(user, adminKey);
   }

   private ChefClient createChefClient(String user, String adminKey) {
      Properties props = new Properties();
      props.setProperty("chef.endpoint", "https://api.opscode.com/organizations/" + orgname);
      return (ChefClient) new RestContextFactory().createContext("chef", user, adminKey,
               ImmutableSet.<Module> of(new Log4JLoggingModule()), props).getApi();
   }

   @Override
   protected ChefClient getValidatorConnection() {
      return createChefClient(validator, validatorKey);
   }

   @Override
   protected ChefClient getClientConnection() {
      return createChefClient(PREFIX, clientKey);
   }

   @Override
   protected void recreateClientConnection() throws IOException {
      if (clientConnection != null)
         clientConnection.close();
      clientConnection = createConnection(PREFIX, clientKey);
   }

   @Override
   protected void closeContexts() {
      if (orgUser != null)
         adminConnection.getApi().deleteUser(PREFIX);
      if (createdOrgname != null)
         adminConnection.getApi().deleteOrganization(createdOrgname);
      if (clientConnection != null)
         clientConnection.close();
      if (validatorConnection != null)
         validatorConnection.close();
      if (adminConnection != null)
         adminConnection.close();
   }

   private String orgname;
   private Organization org;
   private User orgUser;
   private String createdOrgname;

   @Test(expectedExceptions = AuthorizationException.class)
   public void testListOrganizations() throws Exception {
      Set<String> orgs = adminConnection.getApi().listOrganizations();
      assertNotNull(orgs);
   }

   /**
    * this test only works when you have a super user not yet supported in the official api
    */
   @Test(enabled = false, expectedExceptions = AuthorizationException.class)
   public void testCreateOrganization() throws Exception {
      createdOrgname = orgname + 1;
      adminConnection.getApi().deleteOrganization(createdOrgname);
      org = adminConnection.getApi().createOrganization(new Organization(createdOrgname, Organization.Type.BUSINESS));
      assertNotNull(org);
      assertNull(org.getName());
      assertNull(org.getFullName());
      assertEquals(org.getClientname(), createdOrgname + "-validator");
      assertNull(org.getOrgType());
      assertNotNull(org.getPrivateKey());
      OpscodePlatformContext connection = null;
      try {
         connection = createConnection(org.getClientname(), Pems.pem(org.getPrivateKey()));
      } finally {
         if (connection != null)
            connection.close();
      }
   }

   public void testOrganizationExists() throws Exception {
      assertNotNull(adminConnection.getApi().organizationExists(orgname));
   }

   @Test(enabled = false, dependsOnMethods = "testCreateOrganization", expectedExceptions = AuthorizationException.class)
   public void testUpdateOrganization() throws Exception {
      Organization org = adminConnection.getApi().getOrganization(createdOrgname);
      adminConnection.getApi().updateOrganization(org);
   }

   public void testGetOrganization() throws Exception {
      adminConnection.getApi().getOrganization(orgname);
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testListUsers() throws Exception {
      Set<String> orgs = adminConnection.getApi().listUsers();
      assertNotNull(orgs);
   }

   // @Test(expectedExceptions = HttpResponseException.class)
   @Test(enabled = false, expectedExceptions = AuthorizationException.class)
   public void testCreateUser() throws Exception {
      adminConnection.getApi().deleteUser(PREFIX);
      adminConnection.getApi().createUser(new User(PREFIX));
      orgUser = adminConnection.getApi().getUser(PREFIX);
      assertNotNull(orgUser);
      assertEquals(orgUser.getUsername(), PREFIX);
      assertNotNull(orgUser.getPrivateKey());
   }

   public void testUserExists() throws Exception {
      assertNotNull(adminConnection.getApi().userExists(user));
   }

   public void testGetUser() throws Exception {
      adminConnection.getApi().getUser(user);
   }

   // disabled while create user fails
   @Test(dependsOnMethods = "testCreateUser", enabled = false)
   public void testUpdateUser() throws Exception {
      User user = adminConnection.getApi().getUser(PREFIX);
      adminConnection.getApi().updateUser(user);
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testGetOrganizationFailsForValidationKey() throws Exception {
      validatorConnection.getApi().getOrganization(orgname);
   }

   @Test(dependsOnMethods = "testGenerateKeyForClient", expectedExceptions = AuthorizationException.class)
   public void testGetOrganizationFailsForClient() throws Exception {
      clientConnection.getApi().getOrganization(orgname);
   }
}
