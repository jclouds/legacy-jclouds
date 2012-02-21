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
package org.jclouds.compute.strategy;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.jclouds.compute.config.CustomizationResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.functions.TemplateOptionsToStatement;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.predicates.AtomicNodeRunning;
import org.jclouds.compute.predicates.RetryIfSocketNotYetOpen;
import org.jclouds.compute.reference.ComputeServiceConstants.Timeouts;
import org.jclouds.scriptbuilder.domain.Statement;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapTest {

   public void testBreakWhenNodeStillPending() {
      InitializeRunScriptOnNodeOrPlaceInBadMap.Factory initScriptRunnerFactory = createMock(InitializeRunScriptOnNodeOrPlaceInBadMap.Factory.class);
      RetryIfSocketNotYetOpen socketTester = createMock(RetryIfSocketNotYetOpen.class);
      Timeouts timeouts = new Timeouts();
      Function<TemplateOptions, Statement> templateOptionsToStatement = new TemplateOptionsToStatement();
      @SuppressWarnings("unused")
      Statement statement = null;
      TemplateOptions options = new TemplateOptions();
      Set<NodeMetadata> goodNodes = Sets.newLinkedHashSet();
      Map<NodeMetadata, Exception> badNodes = Maps.newLinkedHashMap();
      Multimap<NodeMetadata, CustomizationResponse> customizationResponses = LinkedHashMultimap.create();

      final NodeMetadata node = new NodeMetadataBuilder().ids("id").state(NodeState.PENDING).build();

      // node always stays pending
      GetNodeMetadataStrategy nodeRunning = new GetNodeMetadataStrategy(){

         @Override
         public NodeMetadata getNode(String input) {
            Assert.assertEquals(input, node.getId());
            return node;
         }
         
      };

      // replay mocks
      replay(initScriptRunnerFactory, socketTester);
      // run
      AtomicReference<NodeMetadata> atomicNode = new AtomicReference<NodeMetadata>(node);
      new CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap( new AtomicNodeRunning(nodeRunning),  socketTester, timeouts,
               templateOptionsToStatement, initScriptRunnerFactory, options, atomicNode, goodNodes, badNodes,
               customizationResponses).apply(atomicNode);
      
      assertEquals(goodNodes.size(), 0);
      assertEquals(badNodes.keySet(), ImmutableSet.of(node));
      assertEquals(badNodes.get(node).getMessage(),
               "node(id) didn't achieve the state running within 1200 seconds, so we couldn't customize; final state: PENDING");
      assertEquals(customizationResponses.size(), 0);

      // verify mocks
      verify(initScriptRunnerFactory, socketTester);
   }

   public void testBreakGraceFullyWhenNodeDied() {
      InitializeRunScriptOnNodeOrPlaceInBadMap.Factory initScriptRunnerFactory = createMock(InitializeRunScriptOnNodeOrPlaceInBadMap.Factory.class);
      RetryIfSocketNotYetOpen socketTester = createMock(RetryIfSocketNotYetOpen.class);
      Timeouts timeouts = new Timeouts();
      Function<TemplateOptions, Statement> templateOptionsToStatement = new TemplateOptionsToStatement();
      @SuppressWarnings("unused")
      Statement statement = null;
      TemplateOptions options = new TemplateOptions();
      Set<NodeMetadata> goodNodes = Sets.newLinkedHashSet();
      Map<NodeMetadata, Exception> badNodes = Maps.newLinkedHashMap();
      Multimap<NodeMetadata, CustomizationResponse> customizationResponses = LinkedHashMultimap.create();

      final NodeMetadata node = new NodeMetadataBuilder().ids("id").state(NodeState.PENDING).build();
      final NodeMetadata deadNnode = new NodeMetadataBuilder().ids("id").state(NodeState.TERMINATED).build();

      // node dies
      GetNodeMetadataStrategy nodeRunning = new GetNodeMetadataStrategy(){

         @Override
         public NodeMetadata getNode(String input) {
            Assert.assertEquals(input, node.getId());
            return deadNnode;
         }
         
      };

      // replay mocks
      replay(initScriptRunnerFactory, socketTester);
      // run
      AtomicReference<NodeMetadata> atomicNode = new AtomicReference<NodeMetadata>(node);
      new CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap( new AtomicNodeRunning(nodeRunning),  socketTester, timeouts,
               templateOptionsToStatement, initScriptRunnerFactory, options, atomicNode, goodNodes, badNodes,
               customizationResponses).apply(atomicNode);

      assertEquals(goodNodes.size(), 0);
      assertEquals(badNodes.keySet(), ImmutableSet.of(node));
      badNodes.get(node).printStackTrace();
      assertEquals(badNodes.get(node).getMessage(), "node(id) terminated before we could customize");
      assertEquals(customizationResponses.size(), 0);

      // verify mocks
      verify(initScriptRunnerFactory, socketTester);
   }
}
