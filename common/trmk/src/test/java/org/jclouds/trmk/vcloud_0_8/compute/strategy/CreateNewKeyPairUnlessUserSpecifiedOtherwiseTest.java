/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.trmk.vcloud_0_8.compute.strategy;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.jclouds.trmk.vcloud_0_8.compute.options.TerremarkVCloudTemplateOptions.Builder.noKeyPair;
import static org.jclouds.trmk.vcloud_0_8.compute.options.TerremarkVCloudTemplateOptions.Builder.sshKeyFingerprint;
import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.jclouds.domain.Credentials;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.trmk.vcloud_0_8.compute.domain.OrgAndName;
import org.jclouds.trmk.vcloud_0_8.compute.functions.CreateUniqueKeyPair;
import org.jclouds.trmk.vcloud_0_8.compute.options.TerremarkVCloudTemplateOptions;
import org.jclouds.trmk.vcloud_0_8.domain.KeyPair;
import org.jclouds.trmk.vcloud_0_8.xml.KeyPairHandlerTest;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class CreateNewKeyPairUnlessUserSpecifiedOtherwiseTest {

   public void testExecuteWithDefaultOptionsAlreadyHasKey() throws SecurityException, NoSuchMethodException {
      // setup constants
      URI org = URI.create("org1");
      String identity = "identity";
      String group = "group";
      TerremarkVCloudTemplateOptions options = new TerremarkVCloudTemplateOptions();

      // create mocks
      CreateNewKeyPairUnlessUserSpecifiedOtherwise strategy = setupStrategy();
      LoginCredentials keyPairCredentials = LoginCredentials.builder().privateKey(KeyPairHandlerTest.keyPair.getPrivateKey()).build();

      // setup expectations
      expect(strategy.credentialStore.containsKey("group#group")).andReturn(true);
      expect(strategy.credentialStore.get("group#group")).andReturn(keyPairCredentials);

      // replay mocks
      replayStrategy(strategy);

      // run
      strategy.execute(org, group, identity, options);
      assertEquals(options.getSshKeyFingerprint(), KeyPairHandlerTest.keyPair.getFingerPrint());

      // verify mocks
      verifyStrategy(strategy);
   }

   public void testCreateNewKeyPairUnlessUserSpecifiedOtherwise_reusesKeyWhenToldTo() {
      // setup constants
      URI org = URI.create("org1");
      String identity = "identity";
      String group = "group";
      TerremarkVCloudTemplateOptions options = sshKeyFingerprint("fingerprintFromUser");

      // create mocks
      CreateNewKeyPairUnlessUserSpecifiedOtherwise strategy = setupStrategy();

      // setup expectations

      // replay mocks
      replayStrategy(strategy);

      // run
      strategy.execute(org, group, identity, options);
      assertEquals(options.getSshKeyFingerprint(), "fingerprintFromUser");

      // verify mocks
      verifyStrategy(strategy);
   }

   @Test(enabled = false)
   // TODO add any() mock as we are calling new on KeyPairCredentials inside the
   // strategy
   public void testCreateNewKeyPairUnlessUserSpecifiedOtherwise_createsNewKeyPairAndReturnsItsNameByDefault() {
      // setup constants
      URI org = URI.create("org1");
      String identity = "identity";
      String group = "group";
      String systemGeneratedFingerprint = "systemGeneratedKeyPairfinger";
      TerremarkVCloudTemplateOptions options = new TerremarkVCloudTemplateOptions();

      // create mocks
      CreateNewKeyPairUnlessUserSpecifiedOtherwise strategy = setupStrategy();
      LoginCredentials keyPairCredentials = LoginCredentials.builder().privateKey(KeyPairHandlerTest.keyPair.getPrivateKey()).build();
      KeyPair keyPair = createMock(KeyPair.class);

      // setup expectations
      expect(strategy.credentialStore.containsKey("group#group")).andReturn(false);
      expect(strategy.createUniqueKeyPair.apply(new OrgAndName(org, "group"))).andReturn(keyPair);
      expect(keyPair.getFingerPrint()).andReturn(KeyPairHandlerTest.keyPair.getFingerPrint()).atLeastOnce();
      expect(strategy.credentialStore.put("group#group", keyPairCredentials)).andReturn(null);

      // replay mocks
      replay(keyPair);
      replayStrategy(strategy);

      // run
      strategy.execute(org, group, identity, options);
      assertEquals(options.getSshKeyFingerprint(), systemGeneratedFingerprint);

      // verify mocks
      verify(keyPair);
      verifyStrategy(strategy);
   }

   public void testCreateNewKeyPairUnlessUserSpecifiedOtherwise_doesntCreateAKeyPairAndReturnsNullWhenToldNotTo() {
      // setup constants
      URI org = URI.create("org1");
      String identity = "identity";
      String group = "group";
      TerremarkVCloudTemplateOptions options = noKeyPair();

      // create mocks
      CreateNewKeyPairUnlessUserSpecifiedOtherwise strategy = setupStrategy();

      // setup expectations

      // replay mocks
      replayStrategy(strategy);

      // run
      strategy.execute(org, group, identity, options);
      assertEquals(options.getSshKeyFingerprint(), null);

      // verify mocks
      verifyStrategy(strategy);
   }

   private void verifyStrategy(CreateNewKeyPairUnlessUserSpecifiedOtherwise strategy) {
      verify(strategy.credentialStore);
      verify(strategy.createUniqueKeyPair);
   }

   @SuppressWarnings("unchecked")
   private CreateNewKeyPairUnlessUserSpecifiedOtherwise setupStrategy() {
      Map<String, Credentials> credentialStore = createMock(ConcurrentMap.class);
      CreateUniqueKeyPair createUniqueKeyPair = createMock(CreateUniqueKeyPair.class);

      return new CreateNewKeyPairUnlessUserSpecifiedOtherwise(credentialStore, createUniqueKeyPair);
   }

   private void replayStrategy(CreateNewKeyPairUnlessUserSpecifiedOtherwise strategy) {
      replay(strategy.credentialStore);
      replay(strategy.createUniqueKeyPair);
   }

}
