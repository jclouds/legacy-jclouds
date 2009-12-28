/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.compute;

import java.util.SortedSet;

import org.jclouds.compute.domain.CreateNodeResponse;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeIdentity;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Size;

/**
 * 
 * @author Ivan Meredith
 * @author Adrian Cole
 */
public interface NodeService {

   /**
    * List all nodes available to the current user
    */
   SortedSet<NodeIdentity> listNode();

   /**
    * Find all nodes matching the specified name
    */
   SortedSet<NodeIdentity> getNodeByName(String name);

   /**
    * Create a new node given the name, image, and size.
    * 
    */
   CreateNodeResponse createNode(String name, Image image, Size size);

   /**
    * destroy the node.
    */
   void destroyNode(String id);

   /**
    * Find a node by its id
    */
   NodeMetadata getNodeMetadata(String id);

}
