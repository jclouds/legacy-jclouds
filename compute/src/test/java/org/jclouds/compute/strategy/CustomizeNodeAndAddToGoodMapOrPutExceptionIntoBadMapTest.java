/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.compute.strategy;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.util.Map;
import java.util.Set;

import org.jclouds.compute.config.CustomizationResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.functions.TemplateOptionsToStatement;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.predicates.RetryIfSocketNotYetOpen;
import org.jclouds.compute.reference.ComputeServiceConstants.Timeouts;
import org.jclouds.scriptbuilder.domain.Statement;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
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

   @SuppressWarnings("unchecked")
   public void testBreakWhenNodeStillPending() {
      Predicate<NodeMetadata> nodeRunning = createMock(Predicate.class);
      InitializeRunScriptOnNodeOrPlaceInBadMap.Factory initScriptRunnerFactory = createMock(InitializeRunScriptOnNodeOrPlaceInBadMap.Factory.class);
      GetNodeMetadataStrategy getNode = createMock(GetNodeMetadataStrategy.class);
      RetryIfSocketNotYetOpen socketTester = createMock(RetryIfSocketNotYetOpen.class);
      Timeouts timeouts = new Timeouts();
      Function<TemplateOptions, Statement> templateOptionsToStatement = new TemplateOptionsToStatement();
      @SuppressWarnings("unused")
      Statement statement = null;
      TemplateOptions options = new TemplateOptions();
      Set<NodeMetadata> goodNodes = Sets.newLinkedHashSet();
      Map<NodeMetadata, Exception> badNodes = Maps.newLinkedHashMap();
      Multimap<NodeMetadata, CustomizationResponse> customizationResponses = LinkedHashMultimap.create();

      NodeMetadata node = new NodeMetadataBuilder().ids("id").state(NodeState.PENDING).build();

      // node never reached running state
      expect(nodeRunning.apply(node)).andReturn(false);
      expect(getNode.getNode(node.getId())).andReturn(node);

      // replay mocks
      replay(nodeRunning);
      replay(initScriptRunnerFactory);
      replay(getNode);
      replay(socketTester);

      // run
      new CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap(nodeRunning, getNode, socketTester, timeouts,
               templateOptionsToStatement, initScriptRunnerFactory, options, node, goodNodes, badNodes,
               customizationResponses).apply(node);

      assertEquals(goodNodes.size(), 0);
      assertEquals(badNodes.keySet(), ImmutableSet.of(node));
      assertEquals(badNodes.get(node).getMessage(),
               "node(id) didn't achieve the state running within 1200 seconds, final state: PENDING");
      assertEquals(customizationResponses.size(), 0);

      // verify mocks
      verify(nodeRunning);
      verify(initScriptRunnerFactory);
      verify(getNode);
      verify(socketTester);
   }

   @SuppressWarnings("unchecked")
   public void testBreakGraceFullyWhenNodeDied() {
      Predicate<NodeMetadata> nodeRunning = createMock(Predicate.class);
      InitializeRunScriptOnNodeOrPlaceInBadMap.Factory initScriptRunnerFactory = createMock(InitializeRunScriptOnNodeOrPlaceInBadMap.Factory.class);
      GetNodeMetadataStrategy getNode = createMock(GetNodeMetadataStrategy.class);
      RetryIfSocketNotYetOpen socketTester = createMock(RetryIfSocketNotYetOpen.class);
      Timeouts timeouts = new Timeouts();
      Function<TemplateOptions, Statement> templateOptionsToStatement = new TemplateOptionsToStatement();
      @SuppressWarnings("unused")
      Statement statement = null;
      TemplateOptions options = new TemplateOptions();
      Set<NodeMetadata> goodNodes = Sets.newLinkedHashSet();
      Map<NodeMetadata, Exception> badNodes = Maps.newLinkedHashMap();
      Multimap<NodeMetadata, CustomizationResponse> customizationResponses = LinkedHashMultimap.create();

      NodeMetadata node = new NodeMetadataBuilder().ids("id").state(NodeState.PENDING).build();

      // node never reached running state
      expect(nodeRunning.apply(node)).andReturn(false);
      expect(getNode.getNode(node.getId())).andReturn(null);

      // replay mocks
      replay(nodeRunning);
      replay(initScriptRunnerFactory);
      replay(getNode);
      replay(socketTester);

      // run
      new CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap(nodeRunning, getNode, socketTester, timeouts,
               templateOptionsToStatement, initScriptRunnerFactory, options, node, goodNodes, badNodes,
               customizationResponses).apply(node);

      assertEquals(goodNodes.size(), 0);
      assertEquals(badNodes.keySet(), ImmutableSet.of(node));
      assertEquals(badNodes.get(node).getMessage(), "node(id) terminated before we could customize");
      assertEquals(customizationResponses.size(), 0);

      // verify mocks
      verify(nodeRunning);
      verify(initScriptRunnerFactory);
      verify(getNode);
      verify(socketTester);
   }
}
