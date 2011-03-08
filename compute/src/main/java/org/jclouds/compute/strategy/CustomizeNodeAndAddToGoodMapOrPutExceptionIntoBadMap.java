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

package org.jclouds.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Throwables.getRootCause;
import static java.lang.String.format;
import static org.jclouds.compute.util.ComputeServiceUtils.findReachableSocketOnNode;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.compute.callables.RunScriptOnNode;
import org.jclouds.compute.config.CustomizationResponse;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.predicates.RetryIfSocketNotYetOpen;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.reference.ComputeServiceConstants.Timeouts;
import org.jclouds.logging.Logger;
import org.jclouds.scriptbuilder.domain.Statement;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Multimap;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * 
 * @author Adrian Cole
 */
public class CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap implements Callable<Void>,
         Function<NodeMetadata, Void> {
   public static interface Factory {
      Callable<Void> create(TemplateOptions options, NodeMetadata node, Set<NodeMetadata> goodNodes,
               Map<NodeMetadata, Exception> badNodes,
               Multimap<NodeMetadata, CustomizationResponse> customizationResponses);

      Function<NodeMetadata, Void> create(TemplateOptions options, Set<NodeMetadata> goodNodes,
               Map<NodeMetadata, Exception> badNodes,
               Multimap<NodeMetadata, CustomizationResponse> customizationResponses);
   }

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final Predicate<NodeMetadata> nodeRunning;
   private final InitializeRunScriptOnNodeOrPlaceInBadMap.Factory initScriptRunnerFactory;
   private final GetNodeMetadataStrategy getNode;
   private final RetryIfSocketNotYetOpen socketTester;
   private final Timeouts timeouts;

   @Nullable
   private final Statement statement;
   private final TemplateOptions options;
   private NodeMetadata node;
   private final Set<NodeMetadata> goodNodes;
   private final Map<NodeMetadata, Exception> badNodes;
   private final Multimap<NodeMetadata, CustomizationResponse> customizationResponses;

   private transient boolean tainted;

   @AssistedInject
   public CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap(
            @Named("NODE_RUNNING") Predicate<NodeMetadata> nodeRunning, GetNodeMetadataStrategy getNode,
            RetryIfSocketNotYetOpen socketTester, Timeouts timeouts,
            Function<TemplateOptions, Statement> templateOptionsToStatement,
            InitializeRunScriptOnNodeOrPlaceInBadMap.Factory initScriptRunnerFactory,
            @Assisted TemplateOptions options, @Assisted @Nullable NodeMetadata node,
            @Assisted Set<NodeMetadata> goodNodes, @Assisted Map<NodeMetadata, Exception> badNodes,
            @Assisted Multimap<NodeMetadata, CustomizationResponse> customizationResponses) {
      this.statement = checkNotNull(templateOptionsToStatement, "templateOptionsToStatement").apply(
               checkNotNull(options, "options"));
      this.nodeRunning = checkNotNull(nodeRunning, "nodeRunning");
      this.initScriptRunnerFactory = checkNotNull(initScriptRunnerFactory, "initScriptRunnerFactory");
      this.getNode = checkNotNull(getNode, "getNode");
      this.socketTester = checkNotNull(socketTester, "socketTester");
      this.timeouts = checkNotNull(timeouts, "timeouts");
      this.node = node;
      this.options = checkNotNull(options, "options");
      this.goodNodes = checkNotNull(goodNodes, "goodNodes");
      this.badNodes = checkNotNull(badNodes, "badNodes");
      this.customizationResponses = checkNotNull(customizationResponses, "customizationResponses");
   }

   @AssistedInject
   public CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap(
            @Named("NODE_RUNNING") Predicate<NodeMetadata> nodeRunning, GetNodeMetadataStrategy getNode,
            RetryIfSocketNotYetOpen socketTester, Timeouts timeouts,
            Function<TemplateOptions, Statement> templateOptionsToStatement,
            InitializeRunScriptOnNodeOrPlaceInBadMap.Factory initScriptRunnerFactory,
            @Assisted TemplateOptions options, @Assisted Set<NodeMetadata> goodNodes,
            @Assisted Map<NodeMetadata, Exception> badNodes,
            @Assisted Multimap<NodeMetadata, CustomizationResponse> customizationResponses) {
      this(nodeRunning, getNode, socketTester, timeouts, templateOptionsToStatement, initScriptRunnerFactory, options,
               null, goodNodes, badNodes, customizationResponses);
   }

   @Override
   public Void call() {
      checkState(!tainted, "this object is not designed to be reused: %s", toString());
      tainted = true;
      String originalId = node.getId();
      NodeMetadata originalNode = node;
      try {
         if (options.shouldBlockUntilRunning()) {
            if (nodeRunning.apply(node)) {
               node = getNode.getNode(originalId);
            } else {
               NodeMetadata nodeForState = getNode.getNode(originalId);
               NodeState state = nodeForState == null ? NodeState.TERMINATED : nodeForState.getState();
               if (state == NodeState.TERMINATED)
                  throw new IllegalStateException(format("node(%s) terminated before we could customize", originalId));
               else
                  throw new IllegalStateException(format(
                           "node(%s) didn't achieve the state running within %d seconds, final state: %s", originalId,
                           timeouts.nodeRunning / 1000, state));
            }
            if (node == null)
               throw new IllegalStateException(format("node %s terminated before applying options", originalId));
            if (statement != null) {
               RunScriptOnNode runner = initScriptRunnerFactory.create(node, statement, options, badNodes).call();
               if (runner != null) {
                  ExecResponse exec = runner.call();
                  customizationResponses.put(node, exec);
               }
            }
            if (options.getPort() > 0) {
               findReachableSocketOnNode(socketTester.seconds(options.getSeconds()), node, options.getPort());
            }
         }
         logger.debug("<< options applied node(%s)", originalId);
         goodNodes.add(node);
      } catch (Exception e) {
         logger.error(e, "<< problem applying options to node(%s): ", originalId, getRootCause(e).getMessage());
         badNodes.put(node == null ? originalNode : node, e);
      }
      return null;
   }

   @Override
   public Void apply(NodeMetadata input) {
      this.node = input;
      call();
      return null;
   }
}