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
import static org.jclouds.compute.predicates.NodePredicates.parentLocationId;

import java.net.URI;
import java.util.Map;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.domain.Credentials;
import org.jclouds.vcloud.terremark.compute.domain.OrgAndName;
import org.jclouds.vcloud.terremark.compute.functions.NodeMetadataToOrgAndName;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class CleanupOrphanKeysTest {

   public void testWhenNoDeletedNodes() {
      Iterable<? extends NodeMetadata> deadOnes = ImmutableSet.<NodeMetadata> of();
      // create mocks
      CleanupOrphanKeys strategy = setupStrategy();

      // setup expectations

      // replay mocks
      replayStrategy(strategy);

      // run
      strategy.execute(deadOnes);

      // verify mocks
      verifyStrategy(strategy);
   }

   public void testWhenDeletedNodesHaveNoTag() {
      // create mocks
      CleanupOrphanKeys strategy = setupStrategy();
      NodeMetadata nodeMetadata = createMock(NodeMetadata.class);
      Iterable<? extends NodeMetadata> deadOnes = ImmutableSet.<NodeMetadata> of(nodeMetadata);

      // setup expectations
      expect(strategy.nodeToOrgAndName.apply(nodeMetadata)).andReturn(null).atLeastOnce();
      expectCleanupCredentialStore(strategy, nodeMetadata);

      // replay mocks
      replay(nodeMetadata);
      replayStrategy(strategy);

      // run
      strategy.execute(deadOnes);

      // verify mocks
      verify(nodeMetadata);
      verifyStrategy(strategy);
   }

   public void testWhenStillRunningWithTag() {
      // create mocks
      CleanupOrphanKeys strategy = setupStrategy();
      NodeMetadata nodeMetadata = createMock(NodeMetadata.class);
      Iterable<? extends NodeMetadata> deadOnes = ImmutableSet.<NodeMetadata> of(nodeMetadata);
      OrgAndName orgTag = new OrgAndName(URI.create("location"), "group");

      // setup expectations
      expect(strategy.nodeToOrgAndName.apply(nodeMetadata)).andReturn(orgTag).atLeastOnce();
      expect((Object) strategy.listNodes.listDetailsOnNodesMatching(parentLocationId(orgTag.getOrg().toASCIIString())))
               .andReturn(ImmutableSet.of(nodeMetadata));
      expect(nodeMetadata.getGroup()).andReturn(orgTag.getName()).atLeastOnce();
      expect(nodeMetadata.getState()).andReturn(NodeState.RUNNING).atLeastOnce();
      expectCleanupCredentialStore(strategy, nodeMetadata);

      // replay mocks
      replay(nodeMetadata);
      replayStrategy(strategy);

      // run
      strategy.execute(deadOnes);

      // verify mocks
      verify(nodeMetadata);
      verifyStrategy(strategy);
   }

   public void testWhenTerminatedWithTag() {
      // create mocks
      CleanupOrphanKeys strategy = setupStrategy();
      NodeMetadata nodeMetadata = createMock(NodeMetadata.class);
      Iterable<? extends NodeMetadata> deadOnes = ImmutableSet.<NodeMetadata> of(nodeMetadata);
      OrgAndName orgTag = new OrgAndName(URI.create("location"), "group");

      // setup expectations
      expect(strategy.nodeToOrgAndName.apply(nodeMetadata)).andReturn(orgTag).atLeastOnce();
      expect((Object) strategy.listNodes.listDetailsOnNodesMatching(parentLocationId(orgTag.getOrg().toASCIIString())))
               .andReturn(ImmutableSet.of(nodeMetadata));
      expect(nodeMetadata.getGroup()).andReturn(orgTag.getName()).atLeastOnce();
      expect(nodeMetadata.getState()).andReturn(NodeState.TERMINATED).atLeastOnce();
      strategy.deleteKeyPair.execute(orgTag);
      expectCleanupCredentialStore(strategy, nodeMetadata);

      // replay mocks
      replay(nodeMetadata);
      replayStrategy(strategy);

      // run
      strategy.execute(deadOnes);

      // verify mocks
      verify(nodeMetadata);
      verifyStrategy(strategy);
   }

   private void expectCleanupCredentialStore(CleanupOrphanKeys strategy, NodeMetadata nodeMetadata) {
      expect("node#" + nodeMetadata.getId()).andReturn("1").times(2);
      expect(strategy.credentialStore.remove("node#1")).andReturn(null);
      expect(strategy.credentialStore.remove("node#1#adminPassword")).andReturn(null);
   }

   public void testWhenNoneLeftWithTag() {
      // create mocks
      CleanupOrphanKeys strategy = setupStrategy();
      NodeMetadata nodeMetadata = createMock(NodeMetadata.class);
      Iterable<? extends NodeMetadata> deadOnes = ImmutableSet.<NodeMetadata> of(nodeMetadata);
      OrgAndName orgTag = new OrgAndName(URI.create("location"), "group");

      // setup expectations
      expect(strategy.nodeToOrgAndName.apply(nodeMetadata)).andReturn(orgTag).atLeastOnce();
      expect((Object) strategy.listNodes.listDetailsOnNodesMatching(parentLocationId(orgTag.getOrg().toASCIIString())))
               .andReturn(ImmutableSet.of());
      strategy.deleteKeyPair.execute(orgTag);
      expectCleanupCredentialStore(strategy, nodeMetadata);

      // replay mocks
      replay(nodeMetadata);
      replayStrategy(strategy);

      // run
      strategy.execute(deadOnes);

      // verify mocks
      verify(nodeMetadata);
      verifyStrategy(strategy);
   }

   private void verifyStrategy(CleanupOrphanKeys strategy) {
      verify(strategy.nodeToOrgAndName);
      verify(strategy.deleteKeyPair);
      verify(strategy.listNodes);
      verify(strategy.credentialStore);
   }

   private CleanupOrphanKeys setupStrategy() {
      NodeMetadataToOrgAndName nodeToOrgAndName = createMock(NodeMetadataToOrgAndName.class);
      DeleteKeyPair deleteKeyPair = createMock(DeleteKeyPair.class);
      ListNodesStrategy listNodes = createMock(ListNodesStrategy.class);
      @SuppressWarnings("unchecked")
      Map<String, Credentials> credentialStore = createMock(Map.class);
      return new CleanupOrphanKeys(nodeToOrgAndName, deleteKeyPair, credentialStore, listNodes);
   }

   private void replayStrategy(CleanupOrphanKeys strategy) {
      replay(strategy.nodeToOrgAndName);
      replay(strategy.deleteKeyPair);
      replay(strategy.listNodes);
      replay(strategy.credentialStore);
   }

}
