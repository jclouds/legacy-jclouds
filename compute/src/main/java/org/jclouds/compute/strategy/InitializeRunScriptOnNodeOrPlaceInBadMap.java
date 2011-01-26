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

import java.util.Map;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.jclouds.compute.callables.RunScriptOnNode;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.scriptbuilder.domain.Statement;

import com.google.common.base.Objects;
import com.google.inject.assistedinject.Assisted;

/**
 * 
 * @author Adrian Cole
 */
public class InitializeRunScriptOnNodeOrPlaceInBadMap implements Callable<RunScriptOnNode> {

   public interface Factory {
      Callable<RunScriptOnNode> create(NodeMetadata node, Statement script, RunScriptOptions options,
               Map<NodeMetadata, Exception> badNodes);
   }

   private final RunScriptOnNode.Factory runScriptOnNodeFactory;
   private final Statement script;
   private final Map<NodeMetadata, Exception> badNodes;
   private final NodeMetadata node;
   private final RunScriptOptions options;

   @Inject
   InitializeRunScriptOnNodeOrPlaceInBadMap(RunScriptOnNode.Factory runScriptOnNodeFactory,
            @Assisted NodeMetadata node, @Assisted Statement script, @Assisted RunScriptOptions options,
            @Assisted Map<NodeMetadata, Exception> badNodes) {
      this.runScriptOnNodeFactory = checkNotNull(runScriptOnNodeFactory, "runScriptOnNodeFactory");
      this.script = checkNotNull(script, "script");
      this.badNodes = checkNotNull(badNodes, "badNodes");
      this.node = checkNotNull(node, "node");
      this.options = checkNotNull(options, "options");
   }

   @Override
   public RunScriptOnNode call() throws Exception {
      try {
         return runScriptOnNodeFactory.create(node, script, options).init();
      } catch (Exception e) {
         badNodes.put(node, e);
         return null;
      }
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).add("node", node).add("options", options).toString();
   }
}