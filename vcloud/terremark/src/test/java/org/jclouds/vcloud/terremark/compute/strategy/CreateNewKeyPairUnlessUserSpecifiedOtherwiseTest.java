/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import java.util.concurrent.ConcurrentMap;

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

   public void testExecuteWithDefaultOptionsAlreadyHasKey()
         throws SecurityException, NoSuchMethodException {
      // setup constants
      String org = "org1";
      String tag = "tag";
      OrgAndName orgAndName = new OrgAndName("org1", "tag");
      String systemGeneratedFingerprint = "systemGeneratedKeyPairfinger";
      TerremarkVCloudTemplateOptions options = new TerremarkVCloudTemplateOptions();

      // create mocks
      CreateNewKeyPairUnlessUserSpecifiedOtherwise strategy = setupStrategy();
      KeyPair keyPair = createMock(KeyPair.class);

      // setup expectations
      expect(strategy.credentialsMap.containsKey(orgAndName)).andReturn(true);
      expect(strategy.credentialsMap.get(orgAndName)).andReturn(keyPair);
      expect(keyPair.getFingerPrint()).andReturn(systemGeneratedFingerprint)
            .atLeastOnce();
      
      // replay mocks
      replay(keyPair);
      replayStrategy(strategy);

      // run
      strategy.execute(org, tag, options);
      assertEquals(options.getSshKeyFingerprint(),
            "systemGeneratedKeyPairfinger");

      // verify mocks
      verify(keyPair);
      verifyStrategy(strategy);
   }

   public void testCreateNewKeyPairUnlessUserSpecifiedOtherwise_reusesKeyWhenToldTo() {
      // setup constants
      String org = "org1";
      String tag = "tag";
      TerremarkVCloudTemplateOptions options = sshKeyFingerprint("fingerprintFromUser");

      // create mocks
      CreateNewKeyPairUnlessUserSpecifiedOtherwise strategy = setupStrategy();
      KeyPair keyPair = createMock(KeyPair.class);

      // setup expectations

      // replay mocks
      replay(keyPair);
      replayStrategy(strategy);

      // run
      strategy.execute(org, tag, options);
      assertEquals(options.getSshKeyFingerprint(), "fingerprintFromUser");

      // verify mocks
      verify(keyPair);
      verifyStrategy(strategy);
   }

   public void testCreateNewKeyPairUnlessUserSpecifiedOtherwise_createsNewKeyPairAndReturnsItsNameByDefault() {
      // setup constants
      String org = "org1";
      String tag = "tag";
      String systemGeneratedFingerprint = "systemGeneratedKeyPairfinger";
      TerremarkVCloudTemplateOptions options = new TerremarkVCloudTemplateOptions();
      OrgAndName orgAndName = new OrgAndName("org1", "tag");

      // create mocks
      CreateNewKeyPairUnlessUserSpecifiedOtherwise strategy = setupStrategy();
      KeyPair keyPair = createMock(KeyPair.class);

      // setup expectations
      expect(strategy.credentialsMap.containsKey(orgAndName)).andReturn(false);
      expect(strategy.createUniqueKeyPair.apply(orgAndName)).andReturn(keyPair);
      expect(keyPair.getFingerPrint()).andReturn(systemGeneratedFingerprint)
            .atLeastOnce();
      expect(strategy.credentialsMap.put(orgAndName, keyPair)).andReturn(null);

      // replay mocks
      replay(keyPair);
      replayStrategy(strategy);

      // run
      strategy.execute(org, tag, options);
      assertEquals(options.getSshKeyFingerprint(), systemGeneratedFingerprint);

      // verify mocks
      verify(keyPair);
      verifyStrategy(strategy);
   }

   public void testCreateNewKeyPairUnlessUserSpecifiedOtherwise_doesntCreateAKeyPairAndReturnsNullWhenToldNotTo() {
      // setup constants
      String org = "org1";
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
      strategy.execute(org, tag, options);
      assertEquals(options.getSshKeyFingerprint(), null);

      // verify mocks
      verify(keyPair);
      verifyStrategy(strategy);
   }

   private void verifyStrategy(
         CreateNewKeyPairUnlessUserSpecifiedOtherwise strategy) {
      verify(strategy.credentialsMap);
      verify(strategy.createUniqueKeyPair);
   }

   @SuppressWarnings("unchecked")
   private CreateNewKeyPairUnlessUserSpecifiedOtherwise setupStrategy() {
      ConcurrentMap<OrgAndName, KeyPair> credentialsMap = createMock(ConcurrentMap.class);
      CreateUniqueKeyPair createUniqueKeyPair = createMock(CreateUniqueKeyPair.class);

      return new CreateNewKeyPairUnlessUserSpecifiedOtherwise(credentialsMap,
            createUniqueKeyPair);
   }

   private void replayStrategy(
         CreateNewKeyPairUnlessUserSpecifiedOtherwise strategy) {
      replay(strategy.credentialsMap);
      replay(strategy.createUniqueKeyPair);
   }

}
