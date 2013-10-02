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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Throwables.getRootCause;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.compute.callables.RunScriptOnNode;
import org.jclouds.compute.config.CustomizationResponse;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.util.OpenSocketFinder;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.logging.Logger;
import org.jclouds.scriptbuilder.domain.Statement;

import com.google.common.base.Function;
import com.google.common.collect.Multimap;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * 
 * @author Adrian Cole
 */
public class CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap implements Callable<Void>,
      Function<AtomicReference<NodeMetadata>, Void> {

   public static interface Factory {
      Callable<Void> create(TemplateOptions options, AtomicReference<NodeMetadata> node, Set<NodeMetadata> goodNodes,
            Map<NodeMetadata, Exception> badNodes, Multimap<NodeMetadata, CustomizationResponse> customizationResponses);

      Function<AtomicReference<NodeMetadata>, Void> create(TemplateOptions options, Set<NodeMetadata> goodNodes,
            Map<NodeMetadata, Exception> badNodes, Multimap<NodeMetadata, CustomizationResponse> customizationResponses);
   }

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final Function<AtomicReference<NodeMetadata>, AtomicReference<NodeMetadata>> pollNodeRunning;
   private final InitializeRunScriptOnNodeOrPlaceInBadMap.Factory initScriptRunnerFactory;
   private final OpenSocketFinder openSocketFinder;

   @Nullable
   private final Statement statement;
   private final TemplateOptions options;
   private AtomicReference<NodeMetadata> node;
   private final Set<NodeMetadata> goodNodes;
   private final Map<NodeMetadata, Exception> badNodes;
   private final Multimap<NodeMetadata, CustomizationResponse> customizationResponses;

   private transient boolean tainted;

   @AssistedInject
   public CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap(
         @Named(TIMEOUT_NODE_RUNNING) Function<AtomicReference<NodeMetadata>, AtomicReference<NodeMetadata>> pollNodeRunning,
         OpenSocketFinder openSocketFinder, Function<TemplateOptions, Statement> templateOptionsToStatement,
         InitializeRunScriptOnNodeOrPlaceInBadMap.Factory initScriptRunnerFactory, @Assisted TemplateOptions options,
         @Assisted AtomicReference<NodeMetadata> node, @Assisted Set<NodeMetadata> goodNodes,
         @Assisted Map<NodeMetadata, Exception> badNodes,
         @Assisted Multimap<NodeMetadata, CustomizationResponse> customizationResponses) {
      this.statement = checkNotNull(templateOptionsToStatement, "templateOptionsToStatement").apply(
            checkNotNull(options, "options"));
      this.pollNodeRunning = checkNotNull(pollNodeRunning, "pollNodeRunning");
      this.initScriptRunnerFactory = checkNotNull(initScriptRunnerFactory, "initScriptRunnerFactory");
      this.openSocketFinder = checkNotNull(openSocketFinder, "openSocketFinder");
      this.node = node;
      this.options = checkNotNull(options, "options");
      this.goodNodes = checkNotNull(goodNodes, "goodNodes");
      this.badNodes = checkNotNull(badNodes, "badNodes");
      this.customizationResponses = checkNotNull(customizationResponses, "customizationResponses");
   }

   @AssistedInject
   public CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap(
         @Named(TIMEOUT_NODE_RUNNING) Function<AtomicReference<NodeMetadata>, AtomicReference<NodeMetadata>> pollNodeRunning,
         GetNodeMetadataStrategy getNode, OpenSocketFinder openSocketFinder,
         Function<TemplateOptions, Statement> templateOptionsToStatement,
         InitializeRunScriptOnNodeOrPlaceInBadMap.Factory initScriptRunnerFactory, @Assisted TemplateOptions options,
         @Assisted Set<NodeMetadata> goodNodes, @Assisted Map<NodeMetadata, Exception> badNodes,
         @Assisted Multimap<NodeMetadata, CustomizationResponse> customizationResponses) {
      this(pollNodeRunning, openSocketFinder, templateOptionsToStatement, initScriptRunnerFactory, options,
            new AtomicReference<NodeMetadata>(null), goodNodes, badNodes, customizationResponses);
   }

   @Override
   public Void call() {
      checkState(!tainted, "this object is not designed to be reused: %s", toString());
      tainted = true;
      String originalId = node.get().getId();
      try {
         if (options.shouldBlockUntilRunning()) {
            pollNodeRunning.apply(node);
            if (statement != null) {
               RunScriptOnNode runner = initScriptRunnerFactory.create(node.get(), statement, options, badNodes).call();
               if (runner != null) {
                  ExecResponse exec = runner.call();
                  customizationResponses.put(node.get(), exec);
               }
            }
            if (options.getPort() > 0) {
               openSocketFinder.findOpenSocketOnNode(node.get(), options.getPort(), options.getSeconds(),
                     TimeUnit.SECONDS);
            }
         }
         logger.debug("<< customized node(%s)", originalId);
         goodNodes.add(node.get());
      } catch (Exception e) {
         logger.error(e, "<< problem customizing node(%s): ", originalId, getRootCause(e).getMessage());
         badNodes.put(node.get(), e);
      }
      return null;
   }

   @Override
   public Void apply(AtomicReference<NodeMetadata> input) {
      this.node = input;
      call();
      return null;
   }
}
