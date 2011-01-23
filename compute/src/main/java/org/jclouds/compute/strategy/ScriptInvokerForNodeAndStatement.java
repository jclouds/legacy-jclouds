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

import javax.inject.Singleton;

import org.jclouds.compute.callables.StartInitScriptOnNode;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.scriptbuilder.domain.Statement;

import com.google.inject.Inject;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ScriptInvokerForNodeAndStatement {

   private final StartInitScriptOnNode.Factory initAndStartScriptOnNodeFactory;

   @Inject
   public ScriptInvokerForNodeAndStatement(StartInitScriptOnNode.Factory initAndStartScriptOnNodeFactory) {
      this.initAndStartScriptOnNodeFactory = checkNotNull(initAndStartScriptOnNodeFactory,
               "initAndStartScriptOnNodeFactory");
   }

   public StartInitScriptOnNode create(NodeMetadata node, Statement runScript, RunScriptOptions options) {
      checkNotNull(node, "node");
      checkNotNull(runScript, "runScript");
      checkNotNull(options, "options");
      return options.shouldBlockOnComplete() ? initAndStartScriptOnNodeFactory.blockOnComplete(node, options
               .getTaskName(), runScript, options.shouldRunAsRoot()) : initAndStartScriptOnNodeFactory
               .dontBlockOnComplete(node, options.getTaskName(), runScript, options.shouldRunAsRoot());
   }
}