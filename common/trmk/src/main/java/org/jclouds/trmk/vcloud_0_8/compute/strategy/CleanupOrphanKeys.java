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

import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.Iterables.all;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.size;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.compute.predicates.NodePredicates.TERMINATED;
import static org.jclouds.compute.predicates.NodePredicates.inGroup;
import static org.jclouds.compute.predicates.NodePredicates.parentLocationId;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.domain.Credentials;
import org.jclouds.trmk.vcloud_0_8.compute.domain.OrgAndName;

import com.google.common.base.Function;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class CleanupOrphanKeys {
   final Function<NodeMetadata, OrgAndName> nodeToOrgAndName;
   final DeleteKeyPair deleteKeyPair;
   final ListNodesStrategy listNodes;
   final Map<String, Credentials> credentialStore;

   @Inject
   CleanupOrphanKeys(Function<NodeMetadata, OrgAndName> nodeToOrgAndName, DeleteKeyPair deleteKeyPair,
            Map<String, Credentials> credentialStore, ListNodesStrategy listNodes) {
      this.nodeToOrgAndName = nodeToOrgAndName;
      this.deleteKeyPair = deleteKeyPair;
      this.listNodes = listNodes;
      this.credentialStore = credentialStore;
   }

   public void execute(Iterable<? extends NodeMetadata> deadOnes) {
      for (NodeMetadata node : deadOnes){
         credentialStore.remove("node#" + node.getId());
      }
      Iterable<OrgAndName> orgGroups = filter(transform(deadOnes, nodeToOrgAndName), notNull());
      for (OrgAndName orgGroup : orgGroups) {
         Iterable<? extends NodeMetadata> nodesInOrg = listNodes.listDetailsOnNodesMatching(parentLocationId(orgGroup
                  .getOrg().toASCIIString()));
         Iterable<? extends NodeMetadata> nodesInGroup = filter(nodesInOrg, inGroup(orgGroup.getName()));
         if (size(nodesInGroup) == 0 || all(nodesInGroup, TERMINATED)){
            deleteKeyPair.execute(orgGroup);
         }
      }
   }

}
