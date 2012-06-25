/**
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
package org.jclouds.nodepool.internal;

import java.util.Set;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.options.TemplateOptions;

/**
 * Stores/Loads frontend {@link NodeMetadata} mappings.
 * 
 * @author David Alves
 * 
 */
public interface NodeMetadataStore {
   
   public static final String CONTAINER = "jclouds.nodepool.metadatastore.container";

   /**
    * Associates the provided user options and group with the provided backend {@link NodeMetadata},
    * then build a frontend version of node metadata that has some fields from the backend node such
    * as id, name or location, and some fields from the provided userOptions, such as userMetadata
    * or tags.
    * 
    * @param backendNode
    *           the backend node's {@link NodeMetadata}
    * @param userOptions
    *           the user provided options
    * @param userGroup
    *           the user selected group
    * @return a version of NodeMetadata that includes information from the backend node and form the
    *         user provided options and group.
    */
   public NodeMetadata store(NodeMetadata backendNode, TemplateOptions userOptions, String userGroup);

   /**
    * Removes the mapping from storage.
    * 
    * @param backendNodeId
    */
   public void deleteMapping(String backendNodeId);

   /**
    * Clears all mappings.
    */
   public void deleteAllMappings();

   /**
    * Loads the previously stored user {@link NodeMetadata} corresponding to the provided backend
    * {@link NodeMetadata}.
    * 
    * @param backendNode
    * 
    * @return the frontend {@link NodeMetadata} or null of this backend node has no mapping
    */
   public NodeMetadata load(NodeMetadata backendNode);

   /**
    * Loads frontend {@link NodeMetadata} for all provided backend nodes.
    * 
    * @param backendNodes
    * @return
    */
   public Set<NodeMetadata> loadAll(Set<NodeMetadata> backendNodes);
}
