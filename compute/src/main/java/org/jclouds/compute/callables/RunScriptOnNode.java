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
package org.jclouds.compute.callables;

import java.util.concurrent.Callable;

import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.scriptbuilder.domain.Statement;

import com.google.common.annotations.Beta;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.ImplementedBy;

/**
 * Separates out how one implements the ability to run a script on a node.
 * 
 * @author Adrian Cole
 */
@Beta
public interface RunScriptOnNode extends Callable<ExecResponse> {

   @ImplementedBy(org.jclouds.compute.config.BaseComputeServiceContextModule.RunScriptOnNodeFactoryImpl.class)
   public interface Factory {

      RunScriptOnNode create(NodeMetadata node, Statement script, RunScriptOptions options);

      ListenableFuture<ExecResponse> submit(NodeMetadata node, Statement script, RunScriptOptions options);
   }

   /**
    * Note that {@link #init} must be called first.
    */
   @Override
   ExecResponse call();

   /**
    * @return statement that will be executed
    */
   Statement getStatement();

   /**
    * verifies that the command can execute on the node. For example, if this is ssh, it may attempt
    * to find a reachable socket. If this is using an API, it may attempt to validate that
    * connection.
    */
   RunScriptOnNode init();

   /**
    * the node this command is being executed on.
    */
   NodeMetadata getNode();

}
