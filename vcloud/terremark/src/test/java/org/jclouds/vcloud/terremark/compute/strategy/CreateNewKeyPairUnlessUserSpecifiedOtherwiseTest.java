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

package org.jclouds.vcloud.terremark.compute.strategy;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.jclouds.vcloud.terremark.compute.options.TerremarkVCloudTemplateOptions.Builder.noKeyPair;
import static org.jclouds.vcloud.terremark.compute.options.TerremarkVCloudTemplateOptions.Builder.sshKeyFingerprint;
import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.concurrent.ConcurrentMap;

import org.jclouds.vcloud.terremark.compute.domain.KeyPairCredentials;
import org.jclouds.vcloud.terremark.compute.domain.OrgAndName;
import org.jclouds.vcloud.terremark.compute.functions.CreateUniqueKeyPair;
import org.jclouds.vcloud.terremark.compute.options.TerremarkVCloudTemplateOptions;
import org.jclouds.vcloud.terremark.domain.KeyPair;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "terremark.CreateNewKeyPairUnlessUserSpecifiedOtherwiseTest")
public class CreateNewKeyPairUnlessUserSpecifiedOtherwiseTest {

   public void testExecuteWithDefaultOptionsAlreadyHasKey() throws SecurityException, NoSuchMethodException {
      // setup constants
      URI org = URI.create("org1");
      String identity = "identity";
      String tag = "tag";
      OrgAndName orgAndName = new OrgAndName(org, "tag");
      String systemGeneratedFingerprint = "systemGeneratedKeyPairfinger";
      TerremarkVCloudTemplateOptions options = new TerremarkVCloudTemplateOptions();

      // create mocks
      CreateNewKeyPairUnlessUserSpecifiedOtherwise strategy = setupStrategy();
      KeyPairCredentials keyPairCredentials = createMock(KeyPairCredentials.class);
      KeyPair keyPair = createMock(KeyPair.class);

      // setup expectations
      expect(strategy.credentialsMap.containsKey(orgAndName)).andReturn(true);
      expect(strategy.credentialsMap.get(orgAndName)).andReturn(keyPairCredentials);
      expect(keyPairCredentials.getKeyPair()).andReturn(keyPair);
      expect(keyPair.getFingerPrint()).andReturn(systemGeneratedFingerprint).atLeastOnce();

      // replay mocks
      replay(keyPair);
      replay(keyPairCredentials);
      replayStrategy(strategy);

      // run
      strategy.execute(org, tag, identity, options);
      assertEquals(options.getSshKeyFingerprint(), "systemGeneratedKeyPairfinger");

      // verify mocks
      verify(keyPair);
      verify(keyPairCredentials);
      verifyStrategy(strategy);
   }

   public void testCreateNewKeyPairUnlessUserSpecifiedOtherwise_reusesKeyWhenToldTo() {
      // setup constants
      URI org = URI.create("org1");
      String identity = "identity";
      String tag = "tag";
      TerremarkVCloudTemplateOptions options = sshKeyFingerprint("fingerprintFromUser");

      // create mocks
      CreateNewKeyPairUnlessUserSpecifiedOtherwise strategy = setupStrategy();

      // setup expectations

      // replay mocks
      replayStrategy(strategy);

      // run
      strategy.execute(org, tag, identity, options);
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
      String tag = "tag";
      String systemGeneratedFingerprint = "systemGeneratedKeyPairfinger";
      String privateKey = "privateKey";
      TerremarkVCloudTemplateOptions options = new TerremarkVCloudTemplateOptions();
      OrgAndName orgAndName = new OrgAndName(org, "tag");

      // create mocks
      CreateNewKeyPairUnlessUserSpecifiedOtherwise strategy = setupStrategy();
      KeyPairCredentials keyPairCredentials = createMock(KeyPairCredentials.class);
      KeyPair keyPair = createMock(KeyPair.class);

      // setup expectations
      expect(strategy.credentialsMap.containsKey(orgAndName)).andReturn(false);
      expect(strategy.createUniqueKeyPair.apply(orgAndName)).andReturn(keyPair);
      expect(keyPair.getFingerPrint()).andReturn(systemGeneratedFingerprint).atLeastOnce();
      expect(keyPair.getPrivateKey()).andReturn(privateKey).atLeastOnce();
      expect(strategy.credentialsMap.put(orgAndName, keyPairCredentials)).andReturn(null);

      // replay mocks
      replay(keyPair);
      replayStrategy(strategy);

      // run
      strategy.execute(org, tag, identity, options);
      assertEquals(options.getSshKeyFingerprint(), systemGeneratedFingerprint);

      // verify mocks
      verify(keyPair);
      verifyStrategy(strategy);
   }

   public void testCreateNewKeyPairUnlessUserSpecifiedOtherwise_doesntCreateAKeyPairAndReturnsNullWhenToldNotTo() {
      // setup constants
      URI org = URI.create("org1");
      String identity = "identity";
      String tag = "tag";
      TerremarkVCloudTemplateOptions options = noKeyPair();

      // create mocks
      CreateNewKeyPairUnlessUserSpecifiedOtherwise strategy = setupStrategy();
      KeyPair keyPair = createMock(KeyPair.class);

      // setup expectations

      // replay mocks
      replay(keyPair);
      replayStrategy(strategy);

      // run
      strategy.execute(org, tag, identity, options);
      assertEquals(options.getSshKeyFingerprint(), null);

      // verify mocks
      verify(keyPair);
      verifyStrategy(strategy);
   }

   private void verifyStrategy(CreateNewKeyPairUnlessUserSpecifiedOtherwise strategy) {
      verify(strategy.credentialsMap);
      verify(strategy.createUniqueKeyPair);
   }

   @SuppressWarnings("unchecked")
   private CreateNewKeyPairUnlessUserSpecifiedOtherwise setupStrategy() {
      ConcurrentMap<OrgAndName, KeyPairCredentials> credentialsMap = createMock(ConcurrentMap.class);
      CreateUniqueKeyPair createUniqueKeyPair = createMock(CreateUniqueKeyPair.class);

      return new CreateNewKeyPairUnlessUserSpecifiedOtherwise(credentialsMap, createUniqueKeyPair);
   }

   private void replayStrategy(CreateNewKeyPairUnlessUserSpecifiedOtherwise strategy) {
      replay(strategy.credentialsMap);
      replay(strategy.createUniqueKeyPair);
   }

}
