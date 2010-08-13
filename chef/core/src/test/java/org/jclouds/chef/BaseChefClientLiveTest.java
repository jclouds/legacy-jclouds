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

package org.jclouds.chef;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.jclouds.chef.domain.ChecksumStatus;
import org.jclouds.chef.domain.Client;
import org.jclouds.chef.domain.CookbookVersion;
import org.jclouds.chef.domain.DatabagItem;
import org.jclouds.chef.domain.Node;
import org.jclouds.chef.domain.Resource;
import org.jclouds.chef.domain.Role;
import org.jclouds.chef.domain.SearchResult;
import org.jclouds.chef.domain.UploadSandbox;
import org.jclouds.crypto.CryptoStreams;
import org.jclouds.crypto.Pems;
import org.jclouds.io.InputSuppliers;
import org.jclouds.io.Payloads;
import org.jclouds.io.payloads.FilePayload;
import org.jclouds.json.Json;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.HttpClient;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.primitives.Bytes;

/**
 * Tests behavior of {@code ChefClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "chef.ChefClientLiveTest")
public abstract class BaseChefClientLiveTest {

   protected String clientKey;

   protected abstract void closeContexts();

   protected abstract void recreateClientConnection() throws IOException;

   protected abstract ChefClient getClientConnection();

   protected abstract ChefClient getValidatorConnection();

   protected abstract ChefClient getAdminConnection();

   protected abstract HttpClient getHttp();

   public abstract void setupClient() throws IOException;

   protected String endpoint;
   protected String user;
   private Node node;
   private Role role;
   protected Json json;
   protected DatabagItem databagItem;
   public static final String PREFIX = System.getProperty("user.name") + "-jcloudstest";

   public BaseChefClientLiveTest() {
      super();
   }

   public void testCreateNewCookbook() throws Exception {

      // define the file you want in the cookbook
      FilePayload content = Payloads.newFilePayload(new File(System.getProperty("user.dir"), "pom.xml"));
      content.setContentType("application/x-binary");

      // get an md5 so that you can see if the server already has it or not
      Payloads.calculateMD5(content);

      // Note that java collections cannot effectively do equals or hashcodes on
      // byte arrays,
      // so let's convert to a list of bytes.
      List<Byte> md5 = Bytes.asList(content.getContentMD5());

      // request an upload site for this file
      UploadSandbox site = getAdminConnection().getUploadSandboxForChecksums(ImmutableSet.of(md5));

      try {
         assert site.getChecksums().containsKey(md5) : md5 + " not in " + site.getChecksums();

         ChecksumStatus status = site.getChecksums().get(md5);
         if (status.needsUpload()) {
            getHttp().put(status.getUrl(), content);
         }

         getAdminConnection().commitSandbox(site.getSandboxId(), true);

      } catch (RuntimeException e) {
         getAdminConnection().commitSandbox(site.getSandboxId(), false);
      }

      // create a new cookbook
      CookbookVersion cookbook = new CookbookVersion("test3", "0.0.0");
      cookbook.getRootFiles().add(new Resource(content));

      // upload the cookbook to the remote server
      getAdminConnection().updateCookbook("test3", "0.0.0", cookbook);
   }

   @Test(dependsOnMethods = "testCreateClient")
   public void testGenerateKeyForClient() throws Exception {
      clientKey = Pems.pem(getClientConnection().generateKeyForClient(PREFIX).getPrivateKey());

      assertNotNull(clientKey);
      recreateClientConnection();
      getClientConnection().clientExists(PREFIX);
   }

   @Test(dependsOnMethods = "testCreateNewCookbook")
   public void testListCookbooks() throws Exception {
      for (String cookbook : getAdminConnection().listCookbooks())
         for (String version : getAdminConnection().getVersionsOfCookbook(cookbook)) {
            System.err.printf("%s/%s:%n", cookbook, version);
            CookbookVersion cookbookO = getAdminConnection().getCookbook(cookbook, version);
            for (Resource resource : ImmutableList.<Resource> builder().addAll(cookbookO.getDefinitions()).addAll(
                  cookbookO.getFiles()).addAll(cookbookO.getLibraries()).addAll(cookbookO.getSuppliers()).addAll(
                  cookbookO.getRecipes()).addAll(cookbookO.getResources()).addAll(cookbookO.getRootFiles()).addAll(
                  cookbookO.getTemplates()).build()) {
               try {
                  InputStream stream = getHttp().get(resource.getUrl());
                  byte[] md5 = CryptoStreams.md5(InputSuppliers.of(stream));
                  assertEquals(md5, resource.getChecksum());
               } catch (NullPointerException e) {
                  assert false : "resource not found: " + resource;
               }
               System.err.printf("resource %s ok%n", resource.getName());
            }
         }
   }

   @Test(dependsOnMethods = "testListCookbooks")
   public void testUpdateCookbook() throws Exception {
      for (String cookbook : getAdminConnection().listCookbooks())
         for (String version : getAdminConnection().getVersionsOfCookbook(cookbook)) {
            System.err.printf("%s/%s:%n", cookbook, version);
            CookbookVersion cook = getAdminConnection().getCookbook(cookbook, version);
            getAdminConnection().updateCookbook(cookbook, version, cook);
         }
   }

   @Test(dependsOnMethods = "testUpdateCookbook")
   public void testCreateCookbook() throws Exception {
      for (String cookbook : getAdminConnection().listCookbooks())
         for (String version : getAdminConnection().getVersionsOfCookbook(cookbook)) {
            System.err.printf("%s/%s:%n", cookbook, version);
            CookbookVersion cook = getAdminConnection().getCookbook(cookbook, version);
            getAdminConnection().deleteCookbook(cookbook, version);
            assert getAdminConnection().getCookbook(cookbook, version) == null : cookbook + version;
            getAdminConnection().updateCookbook(cookbook, version, cook);
         }
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testValidatorCannotListClients() throws Exception {
      for (String client : getValidatorConnection().listClients())
         assertNotNull(getValidatorConnection().getClient(client));
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testValidatorCannotDeleteClient() throws Exception {
      getValidatorConnection().deleteClient(PREFIX);
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testValidatorCannotCreateClient() throws Exception {
      getValidatorConnection().createClient(PREFIX);
   }

   @Test
   public void testCreateClient() throws Exception {
      getAdminConnection().deleteClient(PREFIX);

      clientKey = Pems.pem(getAdminConnection().createClient(PREFIX).getPrivateKey());

      recreateClientConnection();
      getClientConnection().clientExists(PREFIX);
      Set<String> clients = getAdminConnection().listClients();
      assert clients.contains(PREFIX) : String.format("client %s not in %s", PREFIX, clients);
      assertNotNull(getClientConnection().getClient(PREFIX));
   }

   @Test(dependsOnMethods = "testCreateClient")
   public void testClientExists() throws Exception {
      assertNotNull(getValidatorConnection().clientExists(PREFIX));
   }

   @Test
   public void testListNodes() throws Exception {
      Set<String> nodes = getAdminConnection().listNodes();
      assertNotNull(nodes);
   }

   @Test(dependsOnMethods = "testCreateRole")
   public void testCreateNode() throws Exception {
      getAdminConnection().deleteNode(PREFIX);
      getClientConnection().createNode(new Node(PREFIX, Collections.singleton("role[" + PREFIX + "]")));
      node = getAdminConnection().getNode(PREFIX);
      // TODO check recipes
      assertNotNull(node);
      Set<String> nodes = getAdminConnection().listNodes();
      assert nodes.contains(PREFIX) : String.format("node %s not in %s", PREFIX, nodes);
   }

   @Test(dependsOnMethods = "testCreateNode")
   public void testNodeExists() throws Exception {
      assertNotNull(getClientConnection().nodeExists(PREFIX));
   }

   @Test(dependsOnMethods = "testNodeExists")
   public void testUpdateNode() throws Exception {
      for (String nodename : getClientConnection().listNodes()) {
         Node node = getAdminConnection().getNode(nodename);
         getAdminConnection().updateNode(node);
      }
   }

   @Test
   public void testListRoles() throws Exception {
      Set<String> roles = getAdminConnection().listRoles();
      assertNotNull(roles);
   }

   @Test(dependsOnMethods = "testCreateClient")
   public void testCreateRole() throws Exception {
      getAdminConnection().deleteRole(PREFIX);
      getAdminConnection().createRole(new Role(PREFIX, Collections.singleton("recipe[java]")));
      role = getAdminConnection().getRole(PREFIX);
      assertNotNull(role);
      assertEquals(role.getName(), PREFIX);
      assertEquals(role.getRunList(), Collections.singleton("recipe[java]"));
   }

   @Test(dependsOnMethods = "testCreateRole")
   public void testRoleExists() throws Exception {
      assertNotNull(getClientConnection().roleExists(PREFIX));
   }

   @Test(dependsOnMethods = "testRoleExists")
   public void testUpdateRole() throws Exception {
      for (String rolename : getClientConnection().listRoles()) {
         Role role = getAdminConnection().getRole(rolename);
         getAdminConnection().updateRole(role);
      }
   }

   @Test
   public void testListDatabags() throws Exception {
      Set<String> databags = getAdminConnection().listDatabags();
      assertNotNull(databags);
   }

   @Test(dependsOnMethods = "testCreateClient")
   public void testCreateDatabag() throws Exception {
      getAdminConnection().deleteDatabag(PREFIX);
      getAdminConnection().createDatabag(PREFIX);
   }

   @Test(dependsOnMethods = "testCreateDatabag")
   public void testDatabagExists() throws Exception {
      assertNotNull(getClientConnection().databagExists(PREFIX));
   }

   @Test(dependsOnMethods = "testCreateDatabagItem")
   public void testListDatabagItems() throws Exception {
      Set<String> databagItems = getAdminConnection().listDatabagItems(PREFIX);
      assertNotNull(databagItems);
   }

   @Test(dependsOnMethods = { "testCreateDatabag", "testCreateRole" })
   public void testCreateDatabagItem() throws Exception {
      Properties config = new Properties();
      config.setProperty("foo", "bar");
      getAdminConnection().deleteDatabagItem(PREFIX, PREFIX);
      databagItem = getAdminConnection().createDatabagItem(PREFIX, new DatabagItem("config", json.toJson(config)));
      assertNotNull(databagItem);
      assertEquals(databagItem.getId(), "config");
      assertEquals(config, json.fromJson(databagItem.toString(), Properties.class));
   }

   @Test(dependsOnMethods = "testCreateDatabagItem")
   public void testDatabagItemExists() throws Exception {
      assertNotNull(getClientConnection().databagItemExists(PREFIX, PREFIX));
   }

   @Test(dependsOnMethods = "testDatabagItemExists")
   public void testUpdateDatabagItem() throws Exception {
      for (String databagItemId : getClientConnection().listDatabagItems(PREFIX)) {
         DatabagItem databagItem = getAdminConnection().getDatabagItem(PREFIX, databagItemId);
         getAdminConnection().updateDatabagItem(PREFIX, databagItem);
      }
   }

   @Test
   public void testListSearchIndexes() throws Exception {
      Set<String> indexes = getAdminConnection().listSearchIndexes();
      assertNotNull(indexes);
      assert indexes.contains("node") : indexes;
      assert indexes.contains("client") : indexes;
      assert indexes.contains("role") : indexes;
   }

   @Test
   public void testSearchNodes() throws Exception {
      SearchResult<? extends Node> results = getAdminConnection().searchNodes();
      assertNotNull(results);
   }

   @Test
   public void testSearchClients() throws Exception {
      SearchResult<? extends Client> results = getAdminConnection().searchClients();
      assertNotNull(results);
   }

   @Test
   public void testSearchRoles() throws Exception {
      SearchResult<? extends Role> results = getAdminConnection().searchRoles();
      assertNotNull(results);
   }

   @Test(dependsOnMethods = "testDatabagItemExists")
   public void testSearchDatabag() throws Exception {
      SearchResult<? extends DatabagItem> results = getAdminConnection().searchDatabag(PREFIX);
      assertNotNull(results);
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testSearchDatabagNotFound() throws Exception {
      SearchResult<? extends DatabagItem> results = getAdminConnection().searchDatabag("whoopie");
      assertNotNull(results);
   }

   @AfterClass(groups = { "live" })
   public void teardownClient() throws IOException {
      if (getValidatorConnection().clientExists(PREFIX))
         getValidatorConnection().deleteClient(PREFIX);
      if (getAdminConnection().nodeExists(PREFIX))
         getAdminConnection().deleteNode(PREFIX);
      if (getAdminConnection().roleExists(PREFIX))
         getAdminConnection().deleteRole(PREFIX);
      if (getAdminConnection().databagExists(PREFIX))
         getAdminConnection().deleteDatabag(PREFIX);
      closeContexts();
   }

}