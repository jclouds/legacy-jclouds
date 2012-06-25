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

/**
 * NodePool statistics and status.
 * 
 * @author David Alves
 * 
 */
public class NodePoolStats {

   private final int currentSize;
   private final int idleNodes;
   private final int usedNodes;
   private final int maxNodes;
   private final int minNodes;

   NodePoolStats(int currentSize, int idleNodes, int usedNodes, int maxNodes, int minNodes) {
      this.currentSize = currentSize;
      this.idleNodes = idleNodes;
      this.usedNodes = usedNodes;
      this.maxNodes = maxNodes;
      this.minNodes = minNodes;
   }

   /**
    * The number of nodes currently allocated in the backend provider and in the pool.
    */
   public int currentSize() {
      return currentSize;
   }

   /**
    * The number of nodes in the pool not being used.
    */
   public int idleNodes() {
      return idleNodes;
   }

   /**
    * The number of nodes in the pool that are currently being used.
    */
   public int usedNodes() {
      return usedNodes;
   }

   /**
    * The maximum size the pool will reach.
    */
   public int maxNodes() {
      return maxNodes;
   }

   /**
    * The minimum size of the pool.
    */
   public int minNodes() {
      return minNodes;
   }

}
