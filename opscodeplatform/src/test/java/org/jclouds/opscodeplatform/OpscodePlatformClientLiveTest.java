/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.opscodeplatform;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import org.jclouds.chef.BaseChefClientLiveTest;
import org.jclouds.chef.ChefClient;
import org.jclouds.chef.config.ChefParserModule;
import org.jclouds.http.HttpResponseException;
import org.jclouds.json.Json;
import org.jclouds.json.config.GsonModule;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.opscodeplatform.domain.Organization;
import org.jclouds.opscodeplatform.domain.User;
import org.jclouds.rest.HttpClient;
import org.jclouds.rest.RestContextFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
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

   @Override
   @BeforeClass(groups = { "live" })
   public void setupClient() throws IOException {
      validator = checkNotNull(System.getProperty("jclouds.test.validator"), "jclouds.test.validator");
      orgname = Iterables.get(Splitter.on('-').split(validator), 0);
      String validatorKey = System.getProperty("jclouds.test.validator.key");
      if (validatorKey == null || validatorKey.equals(""))
         validatorKey = System.getProperty("user.home") + "/.chef/" + orgname + "-validator.pem";
      user = checkNotNull(System.getProperty("jclouds.test.identity"), "jclouds.test.identity");
      String keyfile = System.getProperty("jclouds.test.credential");
      if (keyfile == null || keyfile.equals(""))
         keyfile = System.getProperty("user.home") + "/.chef/" + user + ".pem";

      validatorConnection = createConnection(validator, Files.toString(new File(validatorKey), Charsets.UTF_8));
      adminConnection = createConnection(user, Files.toString(new File(keyfile), Charsets.UTF_8));
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
      return adminConnection.getApi().getChefClientForOrganization(orgname);
   }

   @Override
   protected ChefClient getValidatorConnection() {
      return validatorConnection.getApi().getChefClientForOrganization(orgname);
   }

   @Override
   protected ChefClient getClientConnection() {
      return clientConnection.getApi().getChefClientForOrganization(orgname);
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
      if (org != null)
         adminConnection.getApi().deleteOrganization(PREFIX);
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

   // http://tickets.corp.opscode.com/browse/PL-524
   @Test(expectedExceptions = HttpResponseException.class)
   public void testListOrganizations() throws Exception {
      Set<String> orgs = adminConnection.getApi().listOrganizations();
      assertNotNull(orgs);
   }

   // http://tickets.corp.opscode.com/browse/PL-524
   @Test(expectedExceptions = HttpResponseException.class)
   public void testCreateOrganization() throws Exception {
      adminConnection.getApi().deleteOrganization(PREFIX);
      adminConnection.getApi().createOrganization(new Organization(PREFIX));
      org = adminConnection.getApi().getOrganization(PREFIX);
      assertNotNull(org);
      assertEquals(org.getName(), PREFIX);
      assertEquals(org.getClientname(), PREFIX + "-validator");
   }

   // http://tickets.corp.opscode.com/browse/PL-524
   @Test(expectedExceptions = HttpResponseException.class)
   public void testOrganizationExists() throws Exception {
      assertNotNull(adminConnection.getApi().organizationExists(orgname));
   }

   @Test(enabled = false, dependsOnMethods = "testCreateOrganization")
   public void testUpdateOrganization() throws Exception {
      Organization org = adminConnection.getApi().getOrganization(PREFIX);
      adminConnection.getApi().updateOrganization(org);
   }

   // http://tickets.corp.opscode.com/browse/PL-524
   @Test(expectedExceptions = HttpResponseException.class)
   public void testGetOrganization() throws Exception {
      adminConnection.getApi().getOrganization(orgname);
   }

   // http://tickets.corp.opscode.com/browse/PL-524
   @Test(expectedExceptions = HttpResponseException.class)
   public void testListUsers() throws Exception {
      Set<String> orgs = adminConnection.getApi().listUsers();
      assertNotNull(orgs);
   }

   // http://tickets.corp.opscode.com/browse/PL-524
   @Test(expectedExceptions = HttpResponseException.class)
   public void testCreateUser() throws Exception {
      adminConnection.getApi().deleteUser(PREFIX);
      adminConnection.getApi().createUser(new User(PREFIX));
      orgUser = adminConnection.getApi().getUser(PREFIX);
      assertNotNull(orgUser);
      assertEquals(orgUser.getUsername(), PREFIX);
      assertNotNull(orgUser.getPrivateKey());
   }

   // http://tickets.corp.opscode.com/browse/PL-524
   @Test(expectedExceptions = HttpResponseException.class)
   public void testUserExists() throws Exception {
      assertNotNull(adminConnection.getApi().userExists(user));
   }

   // http://tickets.corp.opscode.com/browse/PL-524
   @Test(expectedExceptions = HttpResponseException.class)
   public void testGetUser() throws Exception {
      adminConnection.getApi().getUser(user);
   }

   @Test(enabled = false, dependsOnMethods = "testCreateUser")
   public void testUpdateUser() throws Exception {
      User user = adminConnection.getApi().getUser(PREFIX);
      adminConnection.getApi().updateUser(user);
   }

   @Test(expectedExceptions = HttpResponseException.class)
   public void testGetOrganizationFailsForValidationKey() throws Exception {
      validatorConnection.getApi().getOrganization(orgname);
   }

   @Test(dependsOnMethods = "testGenerateKeyForClient", expectedExceptions = HttpResponseException.class)
   public void testGetOrganizationFailsForClient() throws Exception {
      clientConnection.getApi().getOrganization(orgname);
   }
}
