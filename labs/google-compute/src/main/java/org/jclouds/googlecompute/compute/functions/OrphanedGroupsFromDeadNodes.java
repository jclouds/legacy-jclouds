/*
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

package org.jclouds.googlecompute.compute.functions;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import org.jclouds.compute.domain.NodeMetadata;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Set;

/**
 * @author David Alves
 */
@Singleton
public class OrphanedGroupsFromDeadNodes implements Function<Set<? extends NodeMetadata>, Set<String>> {

   private final Predicate<String> isOrphanedGroupPredicate;

   @Inject
   public OrphanedGroupsFromDeadNodes(Predicate<String> isOrphanedGroupPredicate) {
      this.isOrphanedGroupPredicate = isOrphanedGroupPredicate;
   }


   @Override
   public Set<String> apply(Set<? extends NodeMetadata> deadNodes) {
      Set<String> groups = Sets.newLinkedHashSet();
      for (NodeMetadata deadNode : deadNodes) {
         groups.add(deadNode.getGroup());
      }
      Set<String> orphanedGroups = Sets.newLinkedHashSet();
      for (String group : groups) {
         if (isOrphanedGroupPredicate.apply(group)) {
            orphanedGroups.add(group);
         }
      }
      return orphanedGroups;
   }


}
