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

import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeSet;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.domain.Location;

/**
 * Provides portable access to launching compute instances.
 * 
 * @author Adrian Cole
 * @author Ivan Meredith
 */
public interface ComputeService {
   /**
    * Makes a new template builder for this service
    */
   TemplateBuilder templateBuilder();

   /**
    * all sizes available to the current user by id
    */
   Map<String, ? extends Size> getSizes();

   /**
    * all images available to the current user by id
    */
   Map<String, ? extends Image> getImages();

   /**
    * all nodes available to the current user by id. If possible, the returned set will include
    * {@link NodeMetadata} objects.
    */
   Map<String, ? extends ComputeMetadata> getNodes();

   /**
    * all nodes available to the current user by id
    */
   Map<String, ? extends Location> getLocations();

   /**
    * create and run nodes in the specified tagset. If resources needed are currently available for
    * this tag, they will be reused. Otherwise, they will be created. Inbound port 22 will always be
    * opened up.
    * 
    * @param tag
    *           - common identifier to group nodes by, cannot contain hyphens
    * @param maxNodes
    *           - how many to fire up.
    * @param template
    *           - how to configure the nodes
    * 
    */
   NodeSet runNodes(String tag, int maxNodes, Template template);

   /**
    * destroy the node. If it is the only node in a tag set, the dependent resources will also be
    * destroyed.
    */
   void destroyNode(ComputeMetadata node);

   /**
    * destroy the nodes identified by this tag.
    */
   void destroyNodes(String tag);

   /**
    * Find a node by its id
    */
   NodeMetadata getNodeMetadata(ComputeMetadata node);

   /**
    * get all nodes matching the tag.
    * 
    * @param tag
    */
   NodeSet getNodes(String tag);

}
