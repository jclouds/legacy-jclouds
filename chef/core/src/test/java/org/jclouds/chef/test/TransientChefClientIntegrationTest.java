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

package org.jclouds.chef.test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.IOException;
import java.util.Properties;

import org.jclouds.chef.BaseChefClientLiveTest;
import org.jclouds.chef.ChefClient;
import org.jclouds.chef.ChefContext;
import org.jclouds.chef.ChefContextFactory;
import org.jclouds.chef.config.ChefParserModule;
import org.jclouds.chef.domain.DatabagItem;
import org.jclouds.json.Json;
import org.jclouds.json.config.GsonModule;
import org.jclouds.rest.HttpClient;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.Guice;

/**
 * Tests behavior of {@code TransientChefClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "integration", testName = "chef.TransientChefClientIntegrationTest")
public class TransientChefClientIntegrationTest extends BaseChefClientLiveTest {
   public void testCreateDatabag1() throws Exception {
      getAdminConnection().deleteDatabag(PREFIX);
      getAdminConnection().createDatabag(PREFIX);
   }

   @Test(dependsOnMethods = "testCreateDatabag1")
   public void testDatabagExists1() throws Exception {
      assertNotNull(getClientConnection().databagExists(PREFIX));
   }

   @Test(dependsOnMethods = { "testCreateDatabag1"})
   public void testCreateDatabagItem1() throws Exception {
      Properties config = new Properties();
      config.setProperty("foo", "bar");
      getAdminConnection().deleteDatabagItem(PREFIX, PREFIX);
      databagItem = getAdminConnection().createDatabagItem(PREFIX, new DatabagItem("config", json.toJson(config)));
      assertNotNull(databagItem);
      assertEquals(databagItem.getId(), "config");
      assertEquals(config, json.fromJson(databagItem.toString(), Properties.class));
   }

   @Test(dependsOnMethods = "testCreateDatabagItem1")
   public void testDatabagItemExists1() throws Exception {
      assertNotNull(getClientConnection().databagItemExists(PREFIX, PREFIX));
   }

   @Test(dependsOnMethods = "testDatabagItemExists1")
   public void testUpdateDatabagItem1() throws Exception {
      for (String databagItemId : getClientConnection().listDatabagItems(PREFIX)) {
         DatabagItem databagItem = getAdminConnection().getDatabagItem(PREFIX, databagItemId);
         getAdminConnection().updateDatabagItem(PREFIX, databagItem);
      }
   }
   @Override
   @Test(enabled = false)
   public void testClientExists() throws Exception {
      super.testClientExists();
   }

   @Override
   public void testCreateClient() throws Exception {
   }

   @Override
   public void testCreateDatabag() throws Exception {
      super.testCreateDatabag();
   }

   @Override
   public void testCreateDatabagItem() throws Exception {
      super.testCreateDatabagItem();
   }

   @Override
   public void testDatabagExists() throws Exception {
      super.testDatabagExists();
   }

   @Override
   public void testDatabagItemExists() throws Exception {
      super.testDatabagItemExists();
   }

   @Override
   public void testListDatabagItems() throws Exception {
      super.testListDatabagItems();
   }

   @Override
   public void testListDatabags() throws Exception {
      super.testListDatabags();
   }

   @Override
   @Test(enabled = false)
   public void testCreateCookbook() throws Exception {
      super.testCreateCookbook();
   }

   @Override
   @Test(enabled = false)
   public void testCreateNewCookbook() throws Exception {
      super.testCreateNewCookbook();
   }

   @Override
   @Test(enabled = false)
   public void testCreateNode() throws Exception {
      super.testCreateNode();
   }

   @Override
   @Test(enabled = false)
   public void testCreateRole() throws Exception {
      super.testCreateRole();
   }

   @Override
   @Test(enabled = false)
   public void testGenerateKeyForClient() throws Exception {
      super.testGenerateKeyForClient();
   }

   @Override
   @Test(enabled = false)
   public void testListCookbooks() throws Exception {
      super.testListCookbooks();
   }

   @Override
   @Test(enabled = false)
   public void testListNodes() throws Exception {
      super.testListNodes();
   }

   @Override
   @Test(enabled = false)
   public void testListRoles() throws Exception {
      super.testListRoles();
   }

   @Override
   @Test(enabled = false)
   public void testListSearchIndexes() throws Exception {
      super.testListSearchIndexes();
   }

   @Override
   @Test(enabled = false)
   public void testNodeExists() throws Exception {
      super.testNodeExists();
   }

   @Override
   @Test(enabled = false)
   public void testRoleExists() throws Exception {
      super.testRoleExists();
   }

   @Override
   @Test(enabled = false)
   public void testSearchClients() throws Exception {
      super.testSearchClients();
   }

   @Override
   @Test(enabled = false)
   public void testSearchDatabag() throws Exception {
      super.testSearchDatabag();
   }

   @Override
   @Test(enabled = false)
   public void testSearchDatabagNotFound() throws Exception {
      super.testSearchDatabagNotFound();
   }

   @Override
   @Test(enabled = false)
   public void testSearchNodes() throws Exception {
      super.testSearchNodes();
   }

   @Override
   @Test(enabled = false)
   public void testSearchRoles() throws Exception {
      super.testSearchRoles();
   }

   @Override
   @Test(enabled = false)
   public void testUpdateCookbook() throws Exception {
      super.testUpdateCookbook();
   }

   @Override
   @Test(enabled = false)
   public void testUpdateDatabagItem() throws Exception {
      super.testUpdateDatabagItem();
   }

   @Override
   @Test(enabled = false)
   public void testUpdateNode() throws Exception {
      super.testUpdateNode();
   }

   @Override
   @Test(enabled = false)
   public void testUpdateRole() throws Exception {
      super.testUpdateRole();
   }

   @Override
   @Test(enabled = false)
   public void testValidatorCannotCreateClient() throws Exception {
      super.testValidatorCannotCreateClient();
   }

   @Override
   @Test(enabled = false)
   public void testValidatorCannotDeleteClient() throws Exception {
      super.testValidatorCannotDeleteClient();
   }

   @Override
   @Test(enabled = false)
   public void testValidatorCannotListClients() throws Exception {
      super.testValidatorCannotListClients();
   }

   private ChefContext validatorConnection;
   private ChefContext clientConnection;
   private ChefContext adminConnection;

   @Override
   @BeforeClass(groups = { "integration" })
   public void setupClient() throws IOException {
      // TODO make this nicer
      validatorConnection = adminConnection = clientConnection = createConnection("user", "userkey");
      json = Guice.createInjector(new GsonModule(), new ChefParserModule()).getInstance(Json.class);
   }

   @Override
   @AfterClass(groups = { "live" })
   public void teardownClient() throws IOException {
      // if (getValidatorConnection().clientExists(PREFIX))
      // getValidatorConnection().deleteClient(PREFIX);
      // if (getAdminConnection().nodeExists(PREFIX))
      // getAdminConnection().deleteNode(PREFIX);
      // if (getAdminConnection().roleExists(PREFIX))
      // getAdminConnection().deleteRole(PREFIX);
      if (getAdminConnection().databagExists(PREFIX))
         getAdminConnection().deleteDatabag(PREFIX);
      closeContexts();
   }

   private ChefContext createConnection(String identity, String key) throws IOException {
      return new ChefContextFactory().createContext("transientchef", identity, key);
   }

   @Override
   protected HttpClient getHttp() {
      return adminConnection.utils().http();
   }

   @Override
   protected ChefClient getAdminConnection() {
      return adminConnection.getApi();
   }

   @Override
   protected ChefClient getValidatorConnection() {
      return validatorConnection.getApi();
   }

   @Override
   protected ChefClient getClientConnection() {
      return clientConnection.getApi();
   }

   @Override
   protected void recreateClientConnection() throws IOException {
      if (clientConnection != null)
         clientConnection.close();
      clientConnection = createConnection(PREFIX, clientKey);
   }

   @Override
   protected void closeContexts() {
      if (clientConnection != null)
         clientConnection.close();
      if (validatorConnection != null)
         validatorConnection.close();
      if (adminConnection != null)
         adminConnection.close();
   }
}
