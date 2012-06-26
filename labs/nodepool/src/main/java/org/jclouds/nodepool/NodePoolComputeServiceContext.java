/*
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.nodepool;

import javax.inject.Singleton;

import org.jclouds.Context;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.Utils;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.location.Provider;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;

@Singleton
public class NodePoolComputeServiceContext extends ComputeServiceContextImpl {

   private final NodePoolComputeServiceAdapter adapter;

   @Inject
   public NodePoolComputeServiceContext(@Provider Context backend, @Provider TypeToken<? extends Context> backendType,
         ComputeService computeService, Utils utils, NodePoolComputeServiceAdapter adapter) {
      super(backend, backendType, computeService, utils);
      this.adapter = adapter;
   }

   public NodePoolStats getPoolStats() {
      return new NodePoolStats(adapter.currentSize(), adapter.idleNodes(), adapter.usedNodes(), adapter.maxNodes(),
            adapter.minNodes());
   }
}
