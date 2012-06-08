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

import java.io.Closeable;
import java.io.IOException;

import org.jclouds.compute.ComputeService;
import org.jclouds.nodepool.config.NodePoolComputeServiceProperties;
import org.jclouds.nodepool.internal.EagerNodePoolComputeService;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.ImplementedBy;

/**
 * A {@link ComputeService} wrapper that uses a pool of pre-loaded nodes to speed up creation times.
 * 
 * This interface extends the ComputeService with a backing pool of nodes, configured during
 * construction. The {@link #startPool()} and {@link #close()} methods are used to create and
 * destroy the pool and its associated nodes.
 * 
 * @author Andrew Kennedy
 * @author Gustavo Morozowski
 * @author David Alves
 * 
 * @see <a href="https://github.com/jclouds/jclouds/wiki/NodePool-Notes">NodePool Notes</a>
 * @see NodePoolComputeServiceProperties
 * @since 1.5.0
 */
@ImplementedBy(EagerNodePoolComputeService.class)
public interface NodePoolComputeService extends ComputeService, Closeable {

   /**
    * Starts the pool, may or may not start the actual nodes, depending on the implementation, i.e.
    * the returned Set may be empty.
    */
   ListenableFuture<Void> startPool();

   /**
    * Returns true of the pool has been started by calling the {@link #startPool()} method.
    */
   boolean isStarted();

   /**
    * Returns the number of ready (pre-allocated) nodes in the pool.
    */
   int ready();

   /**
    * Returns the current size of the pool (nodes allocated on the backing compute service)
    */
   int size();

   /**
    * Returns the maximum amout of node the pool will allocate in the backing compute service.
    */
   int maxSize();

   /**
    * Close the pool and destroy all associated nodes.
    */
   void close() throws IOException;

}