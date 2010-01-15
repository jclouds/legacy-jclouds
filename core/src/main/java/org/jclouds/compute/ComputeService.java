/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.compute;

import java.util.Map;
import java.util.Set;

import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.CreateNodeResponse;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Profile;
import org.jclouds.compute.domain.Size;

/**
 * Provides portable access to launching compute instances.
 * 
 * @author Adrian Cole
 * @author Ivan Meredith
 */
public interface ComputeService {

   /**
    * List all sizes available to the current user
    */
   Map<String, Size> getSizes();
   
   /**
    * List all nodes available to the current user
    */
   Set<ComputeMetadata> listNodes();


   /**
    * Create a new node given the name, size, and Image
    * 
    */
   CreateNodeResponse startNodeInLocation(String location, String name, Profile size, Image image);

   /**
    * destroy the node.
    */
   void destroyNode(ComputeMetadata node);

   /**
    * Find a node by its id
    */
   NodeMetadata getNodeMetadata(ComputeMetadata node);

}
