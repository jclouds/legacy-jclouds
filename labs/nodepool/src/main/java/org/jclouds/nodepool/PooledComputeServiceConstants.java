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
 * Constants for the {@link org.jclouds.nodepool.PooledComputeService}.
 */
public interface PooledComputeServiceConstants {

   /**
    * Property to set the name of the backing group used for pooled nodes.
    */
   public static final String NODEPOOL_BACKING_GROUP_PROPERTY = "jclouds.nodepool.backing-group";

   /**
    * Property to set the template that will be used to create the nodes in the pool.
    */
   public static final String NODEPOOL_BACKING_TEMPLATE_PROPERTY = "jclouds.nodepool.backing-template";

   /**
    * Property to set the maximum size of the pool. Set this to {@literal -1} to have an unlimited
    * pool size.
    */
   public static final String NODEPOOL_MAX_SIZE_PROPERTY = "jclouds.nodepool.max-size";

   /**
    * Property to set the minimum (initial) size of the pool.
    */
   public static final String NODEPOOL_MIN_SIZE_PROPERTY = "jclouds.nodepool.min-size";

   /**
    * Property to set the pool behaviour to remove destroyed nodes rather than returning them to the
    * pool for re-use.
    */
   public static final String NODEPOOL_REMOVE_DESTROYED_PROPERTY = "jclouds.nodepool.remove-destroyed";

   /**
    * The string used to describe nodes that have no group assigned to them.
    */
   public static final String UNASSIGNED = "unassigned";

   /**
    * NOTE not used, intended to be user metadata entry key for assigned group name
    */
   public static final String NODEPOOL_GROUP_PROPERTY = "jclouds.pool.group";
}