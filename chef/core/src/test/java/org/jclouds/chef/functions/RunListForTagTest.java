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

import org.jclouds.chef.ChefClient;
import org.jclouds.chef.config.ChefParserModule;
import org.jclouds.chef.domain.Client;
import org.jclouds.chef.domain.DatabagItem;
import org.jclouds.json.Json;
import org.jclouds.json.config.GsonModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "chef.RunListForTagTest")
public class RunListForTagTest {
   Injector injector = Guice.createInjector(new ChefParserModule(), new GsonModule());
   Json json = injector.getInstance(Json.class);

   @Test(expectedExceptions = IllegalStateException.class)
   public void testWhenNoDatabagItem() throws IOException {
      ChefClient chefClient = createMock(ChefClient.class);
      Client client = createMock(Client.class);

      RunListForTag fn = new RunListForTag("jclouds", chefClient, json);

      expect(chefClient.getDatabagItem("jclouds", "foo")).andReturn(null);

      replay(client);
      replay(chefClient);

      fn.apply("foo");

      verify(client);
      verify(chefClient);
   }

   @Test
   public void testOneRecipe() throws IOException {
      ChefClient chefClient = createMock(ChefClient.class);
      Client client = createMock(Client.class);

      RunListForTag fn = new RunListForTag("jclouds", chefClient, json);

      expect(chefClient.getDatabagItem("jclouds", "foo")).andReturn(
               new DatabagItem("foo", "{\"run_list\":[\"recipe[apache2]\"]}"));

      replay(client);
      replay(chefClient);

      assertEquals(fn.apply("foo"), ImmutableList.of("recipe[apache2]"));

      verify(client);
      verify(chefClient);
   }

   @Test
   public void testTwoRecipes() throws IOException {
      ChefClient chefClient = createMock(ChefClient.class);
      Client client = createMock(Client.class);

      RunListForTag fn = new RunListForTag("jclouds", chefClient, json);

      expect(chefClient.getDatabagItem("jclouds", "foo")).andReturn(
               new DatabagItem("foo", "{\"run_list\":[\"recipe[apache2]\",\"recipe[mysql]\"]}"));

      replay(client);
      replay(chefClient);

      assertEquals(fn.apply("foo"), ImmutableList.of("recipe[apache2]", "recipe[mysql]"));

      verify(client);
      verify(chefClient);
   }

}
