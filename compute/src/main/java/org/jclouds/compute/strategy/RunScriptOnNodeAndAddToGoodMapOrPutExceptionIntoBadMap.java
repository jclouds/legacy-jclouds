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

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.compute.callables.RunScriptOnNode;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;

import com.google.common.base.Objects;
import com.google.inject.assistedinject.AssistedInject;

/**
 * 
 * @author Adrian Cole
 */
public class RunScriptOnNodeAndAddToGoodMapOrPutExceptionIntoBadMap implements Callable<ExecResponse> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   private final RunScriptOnNode runScriptOnNode;
   private final Map<NodeMetadata, Exception> badNodes;
   private final Map<NodeMetadata, ExecResponse> goodNodes;

   private transient boolean tainted;

   @AssistedInject
   public RunScriptOnNodeAndAddToGoodMapOrPutExceptionIntoBadMap(RunScriptOnNode runScriptOnNode,
            Map<NodeMetadata, ExecResponse> goodNodes, Map<NodeMetadata, Exception> badNodes) {
      this.runScriptOnNode = checkNotNull(runScriptOnNode, "runScriptOnNode");
      this.badNodes = checkNotNull(badNodes, "badNodes");
      this.goodNodes = checkNotNull(goodNodes, "goodNodes");
   }

   @Override
   public ExecResponse call() {
      checkState(runScriptOnNode != null, "runScriptOnNode must be set");
      checkState(!tainted, "this object is not designed to be reused: %s", toString());
      tainted = true;
      try {
         ExecResponse exec = runScriptOnNode.call();
         logger.trace("<< script output for node(%s): %s", runScriptOnNode.getNode().getId(), exec);
         logger.debug("<< options applied node(%s)", runScriptOnNode.getNode().getId());
         goodNodes.put(runScriptOnNode.getNode(), exec);
         return exec;
      } catch (Exception e) {
         logger.error(e, "<< problem applying options to node(%s): ", runScriptOnNode.getNode().getId(),
                  getRootCause(e).getMessage());
         badNodes.put(runScriptOnNode.getNode(), e);
      }
      return null;
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).add("runScriptOnNode", runScriptOnNode).add("goodNodes", goodNodes).add(
               "badNodes", badNodes).toString();
   }

}