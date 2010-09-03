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

package org.jclouds.chef.functions;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.security.PrivateKey;
import java.util.List;

import org.jclouds.chef.config.ChefParserModule;
import org.jclouds.chef.domain.Client;
import org.jclouds.chef.statements.InstallChefGems;
import org.jclouds.crypto.PemsTest;
import org.jclouds.json.Json;
import org.jclouds.json.config.GsonModule;
import org.jclouds.scriptbuilder.domain.Statement;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.CharStreams;
import com.google.common.io.Resources;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "chef.TagToBootScriptTest")
public class TagToBootScriptTest {

   Injector injector = Guice.createInjector(new ChefParserModule(), new GsonModule());
   Json json = injector.getInstance(Json.class);
   Statement installChefGems = new InstallChefGems();

   @Test(expectedExceptions = IllegalStateException.class)
   public void testMustHaveClients() {
      TagToBootScript fn = new TagToBootScript(URI.create("http://localhost:4000"), json, ImmutableMap
               .<String, Client> of(), ImmutableMap.<String, List<String>> of("foo", ImmutableList
               .of("recipe[apache2]")), installChefGems);
      fn.apply("foo");
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testMustHaveRunScripts() {
      TagToBootScript fn = new TagToBootScript(URI.create("http://localhost:4000"), json, ImmutableMap
               .<String, Client> of("foo", createMock(Client.class)), ImmutableMap.<String, List<String>> of(), installChefGems);
      fn.apply("foo");
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testMustHaveRunScriptsValue() {
      TagToBootScript fn = new TagToBootScript(URI.create("http://localhost:4000"), json, ImmutableMap
               .<String, Client> of("foo", createMock(Client.class)), ImmutableMap.<String, List<String>> of("foo",
               ImmutableList.<String> of()), installChefGems);
      fn.apply("foo");
   }

   public void testOneRecipe() throws IOException {
      Client client = createMock(Client.class);
      PrivateKey privateKey = createMock(PrivateKey.class);

      TagToBootScript fn = new TagToBootScript(URI.create("http://localhost:4000"), json, ImmutableMap
               .<String, Client> of("foo", client), ImmutableMap.<String, List<String>> of("foo", ImmutableList
               .<String> of("recipe[apache2]")), installChefGems);

      expect(client.getClientname()).andReturn("fooclient").atLeastOnce();
      expect(client.getPrivateKey()).andReturn(privateKey).atLeastOnce();
      expect(privateKey.getEncoded()).andReturn(PemsTest.PRIVATE_KEY.getBytes());

      replay(client);
      replay(privateKey);

      assertEquals(fn.apply("foo").getRawContent(), CharStreams.toString(Resources.newReaderSupplier(Resources
               .getResource("one-recipe.sh"), Charsets.UTF_8)));

      verify(client);
      verify(privateKey);
   }
}
