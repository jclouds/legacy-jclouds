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

import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.Iterables.all;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.size;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.compute.predicates.NodePredicates.TERMINATED;
import static org.jclouds.compute.predicates.NodePredicates.parentLocationId;
import static org.jclouds.compute.predicates.NodePredicates.withTag;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.vcloud.terremark.compute.domain.OrgAndName;

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

   @Inject
   CleanupOrphanKeys(Function<NodeMetadata, OrgAndName> nodeToOrgAndName, DeleteKeyPair deleteKeyPair,
         ListNodesStrategy listNodes) {
      this.nodeToOrgAndName = nodeToOrgAndName;
      this.deleteKeyPair = deleteKeyPair;
      this.listNodes = listNodes;
   }

   public void execute(Iterable<? extends NodeMetadata> deadOnes) {
      Iterable<OrgAndName> orgTags = filter(transform(deadOnes, nodeToOrgAndName), notNull());
      for (OrgAndName orgTag : orgTags) {
         Iterable<? extends NodeMetadata> nodesInOrg = listNodes.listDetailsOnNodesMatching(parentLocationId(orgTag
               .getOrg().toASCIIString()));
         Iterable<? extends NodeMetadata> nodesWithTag = filter(nodesInOrg, withTag(orgTag.getName()));
         if (size(nodesWithTag) == 0 || all(nodesWithTag, TERMINATED))
            deleteKeyPair.execute(orgTag);
      }
   }

}