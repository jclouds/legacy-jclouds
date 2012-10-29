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
package org.jclouds.nodepool.config;

/**
 * Constants for the {@link org.jclouds.nodepool.NodePoolComputeService}.
 */
public interface NodePoolProperties {

   /**
    * Property to set the name of the backend group used for pooled nodes.
    */
   public static final String BACKEND_GROUP = "jclouds.nodepool.backend-group";

   /**
    * Property to set the {@link AdminAccess} that will be installed in the nodes pre-frontend
    * allocation.
    * 
    * @see AdminAccessBuilderSpec for details on the format
    */
   public static final String POOL_ADMIN_ACCESS = "jclouds.nodepool.admin-access";

   /**
    * Property to set the provider or api of backend the pool
    */
   public static final String BACKEND_PROVIDER = "jclouds.nodepool.backend-provider";

   /**
    * Property to set the modules the backend will use for ssh and logging, comma delimited
    */
   public static final String BACKEND_MODULES = "jclouds.nodepool.backend-modules";

   /**
    * Property to set the basedir where metadata will be stored
    */
   public static final String BASEDIR = "jclouds.nodepool.basedir";

   /**
    * Property to set the container where metadata will be stored
    */
   public static final String METADATA_CONTAINER = "jclouds.nodepool.metadata-container";

   /**
    * Property to set the maximum size of the pool. Set this to {@literal -1} to have an unlimited
    * pool size.
    */
   public static final String MAX_SIZE = "jclouds.nodepool.max-size";

   /**
    * Property to set the minimum (initial) size of the pool.
    */
   public static final String MIN_SIZE = "jclouds.nodepool.min-size";

   /**
    * Property to set the pool behaviour to remove destroyed nodes rather than returning them to the
    * pool for re-use.
    */
   public static final String REMOVE_DESTROYED = "jclouds.nodepool.remove-destroyed";

}
