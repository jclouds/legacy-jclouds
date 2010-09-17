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

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.jclouds.chef.config.ChefParserModule;
import org.jclouds.chef.domain.CookbookVersion;
import org.jclouds.json.Json;
import org.jclouds.json.config.GsonModule;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.HttpClient;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Files;
import com.google.inject.Guice;
import com.google.inject.Module;

/**
 * Tests behavior of {@code ChefClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "chef.ChefClientLiveTest")
public class ChefClientLiveTest extends BaseChefClientLiveTest {

   private ChefContext validatorConnection;
   private ChefContext clientConnection;
   private ChefContext adminConnection;

   private String validator;

   @Override
   @BeforeClass(groups = { "live" })
   public void setupClient() throws IOException {
      endpoint = checkNotNull(System.getProperty("jclouds.test.endpoint"), "jclouds.test.endpoint");
      validator = System.getProperty("jclouds.test.validator");
      if (validator == null || validator.equals(""))
         validator = "chef-validator";
      String validatorKey = System.getProperty("jclouds.test.validator.key");
      if (validatorKey == null || validatorKey.equals(""))
         validatorKey = System.getProperty("user.home") + "/.chef/validation.pem";
      user = checkNotNull(System.getProperty("jclouds.test.identity"), "jclouds.test.identity");
      String keyfile = System.getProperty("jclouds.test.credential");
      if (keyfile == null || keyfile.equals(""))
         keyfile = System.getProperty("user.home") + "/.chef/" + user + ".pem";
      validatorConnection = createConnection(validator, Files.toString(new File(validatorKey), Charsets.UTF_8));
      adminConnection = createConnection(user, Files.toString(new File(keyfile), Charsets.UTF_8));
      json = Guice.createInjector(new GsonModule(), new ChefParserModule()).getInstance(Json.class);
   }

   private ChefContext createConnection(String identity, String key) throws IOException {
      Properties props = new Properties();
      props.setProperty("chef.endpoint", endpoint);
      return new ChefContextFactory().createContext(identity, key, ImmutableSet.<Module> of(new Log4JLoggingModule()),
               props);
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

   @Test
   public void testListCookbookVersionsWithChefService() throws Exception {
      Iterable<? extends CookbookVersion> cookbooks = adminConnection.getChefService().listCookbookVersions();
      assertNotNull(cookbooks);
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
