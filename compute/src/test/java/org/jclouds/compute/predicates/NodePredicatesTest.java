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
package org.jclouds.compute.predicates;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.NodeState;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;

/**
 * Tests possible uses of NodePredicates
 * 
 * @author Aled Sage, Adrian Cole
 */
@Test(singleThreaded = true, testName = "NodePredicatesTest")
public class NodePredicatesTest {

    @Test
    public void testNodeTags() {
       NodeMetadata node = new NodeMetadataBuilder()
             .id("id")
             .state(NodeState.RUNNING)
             .tags("one", "two")
             .build();

       Predicate<ComputeMetadata> hasOne = NodePredicates.hasTags("one");
       assertTrue(hasOne.apply(node));
       Predicate<ComputeMetadata> hasTwo = NodePredicates.hasTags("two");
       assertTrue(hasTwo.apply(node));
    }

    @Test
    public void testNodeTagsSuperset() {
       NodeMetadata node = new NodeMetadataBuilder()
             .id("id")
             .state(NodeState.RUNNING)
             .tags("one", "two")
             .build();

       Predicate<ComputeMetadata> hasTags = NodePredicates.hasTags("one", "two", "three");
       assertFalse(hasTags.apply(node));
    }

    @Test
    public void testNodeTagsAndMetadata() {
       NodeMetadata node = new NodeMetadataBuilder()
             .id("id")
             .state(NodeState.RUNNING)
             .tags("one", "two")
             .userMetadata(ImmutableMap.of("key", "value"))
             .build();

       Predicate<ComputeMetadata> hasTags = NodePredicates.hasTags("one", "two", "key");
       assertTrue(hasTags.apply(node));
    }

    @Test
    public void testNodeTagsAndMetadataSuperset() {
       NodeMetadata node = new NodeMetadataBuilder()
             .id("id")
             .state(NodeState.RUNNING)
             .tags("one", "two")
             .userMetadata(ImmutableMap.of("key", "value"))
             .build();

       Predicate<ComputeMetadata> hasTags = NodePredicates.hasTags("one", "two", "three", "key", "other");
       assertFalse(hasTags.apply(node));
    }

    @Test
    public void testNodeMetadata() {
       NodeMetadata node = new NodeMetadataBuilder()
             .id("id")
             .state(NodeState.RUNNING)
             .tags("one", "two")
             .userMetadata(ImmutableMap.of("key", "value"))
             .build();

       Predicate<ComputeMetadata> hasOneTwo = NodePredicates.hasMetadata(ImmutableMap.of("one", "", "two", ""));
       assertTrue(hasOneTwo.apply(node));
       Predicate<ComputeMetadata> hasOneKey = NodePredicates.hasMetadata(ImmutableMap.of("one", "", "key", "value"));
       assertTrue(hasOneKey.apply(node));
       Predicate<ComputeMetadata> hasTwoKey = NodePredicates.hasMetadata(ImmutableMap.of("two", "", "key", "value"));
       assertTrue(hasTwoKey.apply(node));
    }

    @Test
    public void testNodeMetadataSuperset() {
       NodeMetadata node = new NodeMetadataBuilder()
             .id("id")
             .state(NodeState.RUNNING)
             .tags("one", "two")
             .userMetadata(ImmutableMap.of("key", "value"))
             .build();

       Predicate<ComputeMetadata> hasMetadata = NodePredicates.hasMetadata(ImmutableMap.of("one", "", "two", "", "three", "", "key", "value", "other", "data"));
       assertFalse(hasMetadata.apply(node));
    }

    @Test
    public void testNodeMetadataEntry() {
       NodeMetadata node = new NodeMetadataBuilder()
             .id("id")
             .state(NodeState.RUNNING)
             .tags("one", "two")
             .userMetadata(ImmutableMap.of("key", "value"))
             .build();

       Predicate<ComputeMetadata> hasOne = NodePredicates.hasMetadataEntry("one", null);
       assertTrue(hasOne.apply(node));
       Predicate<ComputeMetadata> hasTwo = NodePredicates.hasMetadataEntry("two", "");
       assertTrue(hasTwo.apply(node));
       Predicate<ComputeMetadata> hasKey = NodePredicates.hasMetadataEntry("key", "value");
       assertTrue(hasKey.apply(node));
    }

    @Test
    public void testNodeMetadataEntryInvalid() {
       NodeMetadata node = new NodeMetadataBuilder()
             .id("id")
             .state(NodeState.RUNNING)
             .tags("one", "two")
             .userMetadata(ImmutableMap.of("key", "value"))
             .build();

       Predicate<ComputeMetadata> hasOne = NodePredicates.hasMetadataEntry("one", "two");
       assertFalse(hasOne.apply(node));
       Predicate<ComputeMetadata> hasTwo = NodePredicates.hasMetadataEntry("two", "two");
       assertFalse(hasTwo.apply(node));
       Predicate<ComputeMetadata> hasKeyNull = NodePredicates.hasMetadataEntry("key", null);
       assertFalse(hasKeyNull.apply(node));
       Predicate<ComputeMetadata> hasKeyDifferent = NodePredicates.hasMetadataEntry("key", "different");
       assertFalse(hasKeyDifferent.apply(node));
       Predicate<ComputeMetadata> hasOther = NodePredicates.hasMetadataEntry("other", "values");
       assertFalse(hasOther.apply(node));
    }
}
