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
import static org.testng.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.jclouds.chef.domain.User;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.RestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

/**
 * Tests behavior of {@code OpscodePlatformClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "chef.OpscodePlatformClientLiveTest")
public class OpscodePlatformClientLiveTest {

   private RestContext<OpscodePlatformClient, OpscodePlatformAsyncClient> validatorConnection;
   private RestContext<OpscodePlatformClient, OpscodePlatformAsyncClient> clientConnection;

   private String orgname;
   private String clientKey;

   public static final String PREFIX = System.getProperty("user.name") + "-jcloudstest";

   @BeforeClass(groups = { "live" })
   public void setupClient() throws IOException {
      orgname = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
      String keyfile = System.getProperty("jclouds.test.key");
      if (keyfile == null || keyfile.equals(""))
         keyfile = "/etc/chef/validation.pem";
      validatorConnection = createConnection(orgname + "-validator", Files.toString(new File(
               keyfile), Charsets.UTF_8));
   }

   private RestContext<OpscodePlatformClient, OpscodePlatformAsyncClient> createConnection(
            String identity, String key) throws IOException {
      return OpscodePlatformContextFactory.createContext(identity, key, new Log4JLoggingModule());
   }

   @Test
   public void testListClientsInOrg() throws Exception {
      Set<String> clients = validatorConnection.getApi().getChefClientForOrg(orgname).listClients();
      assertNotNull(clients);
      assert clients.contains(orgname + "-validator");
   }

   @Test(dependsOnMethods = "testListClientsInOrg")
   public void testCreateClientInOrg() throws Exception {
      validatorConnection.getApi().getChefClientForOrg(orgname).deleteClient(PREFIX);
      clientKey = validatorConnection.getApi().getChefClientForOrg(orgname).createClient(PREFIX);
      assertNotNull(clientKey);
      System.out.println(clientKey);
      clientConnection = createConnection(PREFIX, clientKey);
      clientConnection.getApi().getChefClientForOrg(orgname).clientExists(PREFIX);
   }

   @Test(dependsOnMethods = "testCreateClientInOrg")
   public void testGenerateKeyForClientInOrg() throws Exception {
      clientKey = validatorConnection.getApi().getChefClientForOrg(orgname).generateKeyForClient(
               PREFIX);
      assertNotNull(clientKey);
      clientConnection.close();
      clientConnection = createConnection(PREFIX, clientKey);
      clientConnection.getApi().getChefClientForOrg(orgname).clientExists(PREFIX);
   }

   @Test(dependsOnMethods = "testCreateClientInOrg")
   public void testClientExistsInOrg() throws Exception {
      assertNotNull(validatorConnection.getApi().getChefClientForOrg(orgname).clientExists(PREFIX));
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testGetOrgFailsForValidationKey() throws Exception {
      validatorConnection.getApi().getOrg(orgname);
   }

   @Test(dependsOnMethods = "testGenerateKeyForClientInOrg", expectedExceptions = AuthorizationException.class)
   public void testGetOrgFailsForClient() throws Exception {
      clientConnection.getApi().getOrg(orgname);
   }

   @Test(enabled = false)
   public void testGetUser() throws Exception {
      User user = validatorConnection.getApi().getUser(orgname);
      assertNotNull(user);
   }

   @Test(enabled = false)
   public void testCreateUser() throws Exception {
      // TODO
   }

   @Test(enabled = false)
   public void testUpdateUser() throws Exception {
      // TODO
   }

   @Test(enabled = false)
   public void testDeleteUser() throws Exception {
      // TODO
   }

   @Test(enabled = false)
   public void testCreateOrg() throws Exception {
      // TODO
   }

   @Test(enabled = false)
   public void testUpdateOrg() throws Exception {
      // TODO
   }

   @Test(enabled = false)
   public void testDeleteOrg() throws Exception {
      // TODO

   }

   @Test(dependsOnMethods = "testGenerateKeyForClientInOrg")
   public void testListCookbooksInOrg() throws Exception {
      System.err.println(clientConnection.getApi().getChefClientForOrg(orgname).listCookbooks());
   }

   @AfterClass(groups = { "live" })
   public void teardownClient() throws IOException {
      if (clientConnection != null)
         clientConnection.close();
      if (validatorConnection != null)
         validatorConnection.close();
   }
}
