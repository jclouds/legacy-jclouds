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
package org.jclouds.compute.strategy;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.jclouds.compute.config.CustomizationResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.functions.TemplateOptionsToStatement;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.util.OpenSocketFinder;
import org.jclouds.scriptbuilder.domain.Statement;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.Atomics;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapTest")
public class CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapTest {

   public void testBreakOnIllegalStateExceptionDuringPollNode() {
      InitializeRunScriptOnNodeOrPlaceInBadMap.Factory initScriptRunnerFactory = createMock(InitializeRunScriptOnNodeOrPlaceInBadMap.Factory.class);
      OpenSocketFinder openSocketFinder = createMock(OpenSocketFinder.class);
      Function<TemplateOptions, Statement> templateOptionsToStatement = new TemplateOptionsToStatement();
      @SuppressWarnings("unused")
      Statement statement = null;
      TemplateOptions options = new TemplateOptions();
      Set<NodeMetadata> goodNodes = Sets.newLinkedHashSet();
      Map<NodeMetadata, Exception> badNodes = Maps.newLinkedHashMap();
      Multimap<NodeMetadata, CustomizationResponse> customizationResponses = LinkedHashMultimap.create();

      final NodeMetadata pendingNode = new NodeMetadataBuilder().ids("id").status(Status.PENDING).build();

      // node always stays pending
      Function<AtomicReference<NodeMetadata>, AtomicReference<NodeMetadata>> pollNodeRunning = new Function<AtomicReference<NodeMetadata>, AtomicReference<NodeMetadata>>() {

         @Override
         public AtomicReference<NodeMetadata> apply(AtomicReference<NodeMetadata> node) {
            Assert.assertEquals(node.get(), pendingNode);
            throw new IllegalStateException("bad state!");
         }

      };

      // replay mocks
      replay(initScriptRunnerFactory, openSocketFinder);
      // run
      AtomicReference<NodeMetadata> atomicNode = Atomics.newReference(pendingNode);
      new CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap(pollNodeRunning, openSocketFinder,
            templateOptionsToStatement, initScriptRunnerFactory, options, atomicNode, goodNodes, badNodes,
            customizationResponses).apply(atomicNode);

      assertEquals(goodNodes.size(), 0);
      assertEquals(badNodes.keySet(), ImmutableSet.of(pendingNode));
      assertEquals(badNodes.get(pendingNode).getMessage(), "bad state!");
      assertEquals(customizationResponses.size(), 0);

      // verify mocks
      verify(initScriptRunnerFactory, openSocketFinder);
   }

   public void testBreakGraceWhenNodeSocketFailsToOpen() {
      int portTimeoutSecs = 2;
      InitializeRunScriptOnNodeOrPlaceInBadMap.Factory initScriptRunnerFactory = createMock(InitializeRunScriptOnNodeOrPlaceInBadMap.Factory.class);
      OpenSocketFinder openSocketFinder = createMock(OpenSocketFinder.class);
      Function<TemplateOptions, Statement> templateOptionsToStatement = new TemplateOptionsToStatement();
      TemplateOptions options = new TemplateOptions().blockOnPort(22, portTimeoutSecs);
      Set<NodeMetadata> goodNodes = Sets.newLinkedHashSet();
      Map<NodeMetadata, Exception> badNodes = Maps.newLinkedHashMap();
      Multimap<NodeMetadata, CustomizationResponse> customizationResponses = LinkedHashMultimap.create();

      final NodeMetadata pendingNode = new NodeMetadataBuilder().ids("id").status(Status.PENDING).build();
      final NodeMetadata runningNode = new NodeMetadataBuilder().ids("id").status(Status.RUNNING).build();

      expect(openSocketFinder.findOpenSocketOnNode(runningNode, 22, portTimeoutSecs, TimeUnit.SECONDS)).andThrow(
            new NoSuchElementException("could not connect to any ip address port")).once();

      Function<AtomicReference<NodeMetadata>, AtomicReference<NodeMetadata>> pollNodeRunning = new Function<AtomicReference<NodeMetadata>, AtomicReference<NodeMetadata>>() {

         @Override
         public AtomicReference<NodeMetadata> apply(AtomicReference<NodeMetadata> node) {
            Assert.assertEquals(node.get(), pendingNode);
            node.set(runningNode);
            return node;
         }

      };

      // replay mocks
      replay(initScriptRunnerFactory, openSocketFinder);

      // run
      AtomicReference<NodeMetadata> atomicNode = Atomics.newReference(pendingNode);
      new CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap(pollNodeRunning, openSocketFinder,
            templateOptionsToStatement, initScriptRunnerFactory, options, atomicNode, goodNodes, badNodes,
            customizationResponses).apply(atomicNode);

      assertEquals(goodNodes.size(), 0);
      assertEquals(badNodes.keySet(), ImmutableSet.of(pendingNode));
      badNodes.get(pendingNode).printStackTrace();
      assertEquals(badNodes.get(pendingNode).getMessage(), "could not connect to any ip address port");
      assertEquals(customizationResponses.size(), 0);

      // verify mocks
      verify(initScriptRunnerFactory, openSocketFinder);
   }
}
