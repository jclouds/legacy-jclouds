/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.trmk.vcloud_0_8.compute.strategy;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.net.URI;
import java.util.concurrent.ConcurrentMap;

import org.jclouds.trmk.vcloud_0_8.TerremarkVCloudClient;
import org.jclouds.trmk.vcloud_0_8.compute.domain.KeyPairCredentials;
import org.jclouds.trmk.vcloud_0_8.compute.domain.OrgAndName;
import org.jclouds.trmk.vcloud_0_8.compute.strategy.DeleteKeyPair;
import org.jclouds.trmk.vcloud_0_8.domain.KeyPair;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class DeleteKeyPairTest {

   public void testWhenNoKeyPairsInOrg() {
      URI org = URI.create("org1");

      // setup constants
      OrgAndName orgTag = new OrgAndName(org, "tag");

      // create mocks
      DeleteKeyPair strategy = setupStrategy();

      // setup expectations
      expect(strategy.terremarkClient.listKeyPairsInOrg(orgTag.getOrg())).andReturn(ImmutableSet.<KeyPair> of());

      // replay mocks
      replayStrategy(strategy);

      // run
      strategy.execute(orgTag);

      // verify mocks
      verifyStrategy(strategy);
   }

   public void testWhenKeyPairMatches() {
      URI org = URI.create("org1");

      // setup constants
      OrgAndName orgTag = new OrgAndName(org, "tag");

      // create mocks
      DeleteKeyPair strategy = setupStrategy();
      KeyPair keyPair = createMock(KeyPair.class);

      // setup expectations
      expect(strategy.terremarkClient.listKeyPairsInOrg(orgTag.getOrg())).andReturn(ImmutableSet.<KeyPair> of(keyPair));
      expect(keyPair.getName()).andReturn("jclouds#" + orgTag.getName() + "#123").atLeastOnce();
      expect(keyPair.getId()).andReturn(URI.create("1245"));
      strategy.terremarkClient.deleteKeyPair(URI.create("1245"));
      expect(strategy.credentialsMap.remove(orgTag)).andReturn(null);

      // replay mocks
      replay(keyPair);
      replayStrategy(strategy);

      // run
      strategy.execute(orgTag);

      // verify mocks
      verify(keyPair);
      verifyStrategy(strategy);
   }

   public void testWhenKeyPairDoesntMatch() {
      URI org = URI.create("org1");

      // setup constants
      OrgAndName orgTag = new OrgAndName(org, "tag");

      // create mocks
      DeleteKeyPair strategy = setupStrategy();
      KeyPair keyPair = createMock(KeyPair.class);

      // setup expectations
      expect(strategy.terremarkClient.listKeyPairsInOrg(orgTag.getOrg())).andReturn(ImmutableSet.<KeyPair> of(keyPair));
      expect(keyPair.getName()).andReturn("kclouds#" + orgTag.getName() + "-123");

      // replay mocks
      replay(keyPair);
      replayStrategy(strategy);

      // run
      strategy.execute(orgTag);

      // verify mocks
      verify(keyPair);
      verifyStrategy(strategy);
   }

   private void verifyStrategy(DeleteKeyPair strategy) {
      verify(strategy.credentialsMap);
      verify(strategy.terremarkClient);
   }

   @SuppressWarnings("unchecked")
   private DeleteKeyPair setupStrategy() {
      ConcurrentMap<OrgAndName, KeyPairCredentials> credentialsMap = createMock(ConcurrentMap.class);
      TerremarkVCloudClient terremarkClient = createMock(TerremarkVCloudClient.class);

      return new DeleteKeyPair(terremarkClient, credentialsMap);
   }

   private void replayStrategy(DeleteKeyPair strategy) {
      replay(strategy.credentialsMap);
      replay(strategy.terremarkClient);
   }

}
