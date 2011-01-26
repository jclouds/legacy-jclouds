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

package org.jclouds.compute;

import static org.jclouds.compute.util.ComputeServiceUtils.createExecutionErrorMessage;
import static org.jclouds.compute.util.ComputeServiceUtils.createNodeErrorMessage;

import java.util.Map;

import javax.annotation.Nullable;

import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.scriptbuilder.domain.Statement;

/**
 * 
 * @author Adrian Cole
 */
public class RunScriptOnNodesException extends Exception {

   /** The serialVersionUID */
   private static final long serialVersionUID = -2272965726680821281L;
   private final Statement runScript;
   private final RunScriptOptions options;
   private final Map<NodeMetadata, ExecResponse> successfulNodes;
   private final Map<? extends NodeMetadata, ? extends Throwable> failedNodes;
   private final Map<?, Exception> executionExceptions;

   public RunScriptOnNodesException(Statement runScript, @Nullable RunScriptOptions options,
            Map<NodeMetadata, ExecResponse> successfulNodes, Map<?, Exception> executionExceptions,
            Map<? extends NodeMetadata, ? extends Throwable> failedNodes) {
      super(String.format("error runScript on filtered nodes options(%s)%n%s%n%s", options,
               createExecutionErrorMessage(executionExceptions), createNodeErrorMessage(failedNodes)));
      this.runScript = runScript;
      this.options = options;
      this.successfulNodes = successfulNodes;
      this.failedNodes = failedNodes;
      this.executionExceptions = executionExceptions;
   }

   /**
    * @return Nodes that performed ssh without error
    */
   public Map<NodeMetadata, ExecResponse> getSuccessfulNodes() {
      return successfulNodes;
   }

   /**
    * 
    * @return Nodes that performed startup without error, but incurred problems applying options
    */
   public Map<?, ? extends Throwable> getExecutionErrors() {
      return executionExceptions;
   }

   /**
    * 
    * @return Nodes that performed startup without error, but incurred problems applying options
    */
   public Map<? extends NodeMetadata, ? extends Throwable> getNodeErrors() {
      return failedNodes;
   }

   public Statement getRunScript() {
      return runScript;
   }

   public RunScriptOptions getOptions() {
      return options;
   }

}
