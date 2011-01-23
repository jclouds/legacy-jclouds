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

import java.util.Map;
import java.util.concurrent.Callable;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.compute.callables.StartInitScriptOnNode;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.ssh.ExecResponse;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * 
 * @author Adrian Cole
 */
public class RunStatementOnNodeAndAddToGoodMapOrPutExceptionIntoBadMap implements Callable<Void> {
   public static interface Factory {
      Callable<Void> create(NodeMetadata node, Statement statement, RunScriptOptions options,
               Map<NodeMetadata, ExecResponse> goodNodes, Map<NodeMetadata, Exception> badNodes);

   }

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   // NOTE this is mutable
   protected NodeMetadata node;
   private final Map<NodeMetadata, Exception> badNodes;
   private final Map<NodeMetadata, ExecResponse> goodNodes;
   private final RunScriptOptions options;

   protected final Statement statement;
   private final ScriptInvokerForNodeAndStatement scriptInvokerForNodeAndStatement;
   private transient boolean tainted;

   @AssistedInject
   public RunStatementOnNodeAndAddToGoodMapOrPutExceptionIntoBadMap(
            ScriptInvokerForNodeAndStatement scriptInvokerForNodeAndStatement, @Assisted NodeMetadata node,
            @Assisted @Nullable Statement statement, @Assisted RunScriptOptions options,
            @Assisted Map<NodeMetadata, ExecResponse> goodNodes, @Assisted Map<NodeMetadata, Exception> badNodes) {
      this.statement = checkNotNull(statement, "statement");
      this.scriptInvokerForNodeAndStatement = checkNotNull(scriptInvokerForNodeAndStatement,
               "scriptInvokerForNodeAndStatement");
      this.node = node;
      this.badNodes = checkNotNull(badNodes, "badNodes");
      this.goodNodes = checkNotNull(goodNodes, "goodNodes");
      this.options = checkNotNull(options, "options");
   }

   @AssistedInject
   public RunStatementOnNodeAndAddToGoodMapOrPutExceptionIntoBadMap(
            ScriptInvokerForNodeAndStatement scriptInvokerForNodeAndStatement, @Assisted @Nullable Statement statement,
            @Assisted RunScriptOptions options, @Assisted Map<NodeMetadata, ExecResponse> goodNodes,
            @Assisted Map<NodeMetadata, Exception> badNodes) {
      this(scriptInvokerForNodeAndStatement, null, statement, options, goodNodes, badNodes);
   }

   @Override
   public Void call() {
      checkState(node != null, "node must be set");
      checkState(!tainted, "this object is not designed to be reused: %s", toString());
      tainted = true;
      try {
         StartInitScriptOnNode scriptInstructions = scriptInvokerForNodeAndStatement.create(node, statement, options);
         ExecResponse exec = scriptInstructions.call();
         logger.trace("<< script output for node(%s): %s", node.getId(), exec);
         logger.debug("<< options applied node(%s)", node.getId());
         goodNodes.put(node, exec);
      } catch (Exception e) {
         logger.error(e, "<< problem applying options to node(%s): ", node.getId(), getRootCause(e).getMessage());
         badNodes.put(node, e);
      }
      return null;
   }

}