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
import java.security.PrivateKey;

import org.jclouds.chef.ChefClient;
import org.jclouds.chef.domain.Client;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "chef.ClientForTagTest")
public class ClientForTagTest {

   public void testWhenNoClientsInList() throws IOException {
      ChefClient chefClient = createMock(ChefClient.class);
      Client client = createMock(Client.class);
      PrivateKey privateKey = createMock(PrivateKey.class);

      ClientForTag fn = new ClientForTag(chefClient);

      expect(chefClient.listClients()).andReturn(ImmutableSet.<String> of());
      expect(chefClient.createClient("foo-validator-00")).andReturn(client);
      expect(client.getPrivateKey()).andReturn(privateKey);

      replay(client);
      replay(chefClient);

      Client compare = fn.apply("foo");
      assertEquals(compare.getClientname(), "foo-validator-00");
      assertEquals(compare.getName(), "foo-validator-00");
      assertEquals(compare.getPrivateKey(), privateKey);

      verify(client);
      verify(chefClient);
   }

   public void testWhenClientsInListAddsToEnd() throws IOException {
      ChefClient chefClient = createMock(ChefClient.class);
      Client client = createMock(Client.class);
      PrivateKey privateKey = createMock(PrivateKey.class);

      ClientForTag fn = new ClientForTag(chefClient);

      expect(chefClient.listClients()).andReturn(
               ImmutableSet.<String> of("foo-validator-00", "foo-validator-01", "foo-validator-02"));
      expect(chefClient.createClient("foo-validator-03")).andReturn(client);
      expect(client.getPrivateKey()).andReturn(privateKey);

      replay(client);
      replay(chefClient);

      Client compare = fn.apply("foo");
      assertEquals(compare.getClientname(), "foo-validator-03");
      assertEquals(compare.getName(), "foo-validator-03");
      assertEquals(compare.getPrivateKey(), privateKey);

      verify(client);
      verify(chefClient);
   }

   public void testWhenClientsInListReplacesMissing() throws IOException {
      ChefClient chefClient = createMock(ChefClient.class);
      Client client = createMock(Client.class);
      PrivateKey privateKey = createMock(PrivateKey.class);

      ClientForTag fn = new ClientForTag(chefClient);

      expect(chefClient.listClients()).andReturn(ImmutableSet.<String> of("foo-validator-00", "foo-validator-02"));
      expect(chefClient.createClient("foo-validator-01")).andReturn(client);
      expect(client.getPrivateKey()).andReturn(privateKey);

      replay(client);
      replay(chefClient);

      Client compare = fn.apply("foo");
      assertEquals(compare.getClientname(), "foo-validator-01");
      assertEquals(compare.getName(), "foo-validator-01");
      assertEquals(compare.getPrivateKey(), privateKey);

      verify(client);
      verify(chefClient);
   }
}
