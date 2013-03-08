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

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.easymock.EasyMock;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.internal.NodeMetadataImpl;
import org.jclouds.googlecompute.compute.predicates.AllNodesInGroupTerminated;
import org.testng.annotations.Test;

import java.util.Set;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

/**
 * @author David Alves
 */
public class OrphanedGroupsFromDeadNodesTest {

   private static class IdAndGroupOnlyNodeMetadata extends NodeMetadataImpl {

      public IdAndGroupOnlyNodeMetadata(String id, String group, Status status) {
         super(null, null, id, null, null, ImmutableMap.<String, String>of(), ImmutableSet.<String>of(), group, null,
                 null, null, status, null, 0, ImmutableSet.<String>of(), ImmutableSet.<String>of(), null, null);
      }
   }


   @Test
   public void testDetectsAllOrphanedGroupsWhenAllNodesTerminated() {

      Set<? extends NodeMetadata> deadNodesGroup1 = (Set) ImmutableSet.builder()
              .add(new IdAndGroupOnlyNodeMetadata("a", "1", NodeMetadata.Status.TERMINATED)).build();

      Set<? extends NodeMetadata> deadNodesGroup2 = (Set) ImmutableSet.builder()
              .add(new IdAndGroupOnlyNodeMetadata("b", "2", NodeMetadata.Status.TERMINATED)).build();

      Set<? extends NodeMetadata> allDeadNodes = Sets.union(deadNodesGroup1, deadNodesGroup2);

      ComputeService mock = createMock(ComputeService.class);
      expect(mock.listNodesDetailsMatching(EasyMock.<Predicate<ComputeMetadata>>anyObject()))
              .andReturn((Set) deadNodesGroup1).once();
      expect(mock.listNodesDetailsMatching(EasyMock.<Predicate<ComputeMetadata>>anyObject()))
              .andReturn((Set) deadNodesGroup2).once();

      replay(mock);

      OrphanedGroupsFromDeadNodes orphanedGroupsFromDeadNodes = new OrphanedGroupsFromDeadNodes(new
              AllNodesInGroupTerminated(mock));

      Set<String> orphanedGroups = orphanedGroupsFromDeadNodes.apply(allDeadNodes);

      assertSame(orphanedGroups.size(), 2);
      assertTrue(orphanedGroups.contains("1"));
      assertTrue(orphanedGroups.contains("2"));
   }

   @Test
   public void testDetectsAllOrphanedGroupsWhenSomeNodesTerminatedAndOtherMissing() {

      Set<? extends NodeMetadata> deadNodesGroup1 = (Set) ImmutableSet.builder()
              .add(new IdAndGroupOnlyNodeMetadata("a", "1", NodeMetadata.Status.TERMINATED)).build();

      Set<? extends NodeMetadata> deadNodesGroup2 = (Set) ImmutableSet.builder()
              .add(new IdAndGroupOnlyNodeMetadata("b", "2", NodeMetadata.Status.TERMINATED)).build();

      Set<? extends NodeMetadata> allDeadNodes = Sets.union(deadNodesGroup1, deadNodesGroup2);

      ComputeService mock = createMock(ComputeService.class);
      expect(mock.listNodesDetailsMatching(EasyMock.<Predicate<ComputeMetadata>>anyObject()))
              .andReturn((Set) deadNodesGroup1).once();
      expect(mock.listNodesDetailsMatching(EasyMock.<Predicate<ComputeMetadata>>anyObject()))
              .andReturn((Set) ImmutableSet.of()).once();

      replay(mock);

      OrphanedGroupsFromDeadNodes orphanedGroupsFromDeadNodes = new OrphanedGroupsFromDeadNodes(new
              AllNodesInGroupTerminated(mock));

      Set<String> orphanedGroups = orphanedGroupsFromDeadNodes.apply(allDeadNodes);

      assertSame(orphanedGroups.size(), 2);
      assertTrue(orphanedGroups.contains("1"));
      assertTrue(orphanedGroups.contains("2"));
   }

   @Test
   public void testDetectsAllOrphanedGroupsWhenSomeNodesAreAlive() {

      Set<? extends NodeMetadata> deadNodesGroup1 = (Set) ImmutableSet.builder()
              .add(new IdAndGroupOnlyNodeMetadata("a", "1", NodeMetadata.Status.TERMINATED)).build();

      Set<? extends NodeMetadata> deadNodesGroup2 = (Set) ImmutableSet.builder()
              .add(new IdAndGroupOnlyNodeMetadata("b", "2", NodeMetadata.Status.RUNNING)).build();

      Set<? extends NodeMetadata> allDeadNodes = Sets.union(deadNodesGroup1, deadNodesGroup2);

      ComputeService mock = createMock(ComputeService.class);
      expect(mock.listNodesDetailsMatching(EasyMock.<Predicate<ComputeMetadata>>anyObject()))
              .andReturn((Set) deadNodesGroup1).once();
      expect(mock.listNodesDetailsMatching(EasyMock.<Predicate<ComputeMetadata>>anyObject()))
              .andReturn((Set) deadNodesGroup2).once();

      replay(mock);

      OrphanedGroupsFromDeadNodes orphanedGroupsFromDeadNodes = new OrphanedGroupsFromDeadNodes(new
              AllNodesInGroupTerminated(mock));

      Set<String> orphanedGroups = orphanedGroupsFromDeadNodes.apply(allDeadNodes);

      assertSame(orphanedGroups.size(), 1);
      assertTrue(orphanedGroups.contains("1"));
   }
}
