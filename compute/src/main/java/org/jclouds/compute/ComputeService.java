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
    * The get sizes command shows you the options including virtual cpu count, memory, and disks.
    * cpu count is not a portable quantity across clouds, as they are measured differently. However,
    * it is a good indicator of relative speed within a cloud. memory is measured in megabytes and
    * disks in gigabytes.
    * 
    * @retun a map of sizes by ID, conceding that in some clouds the "id" is not used.
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
    * The get locations command returns all the valid locations for nodes. A location has a scope,
    * which is typically region or zone. A region is a general area, like eu-west, where a zone is
    * similar to a datacenter. If a location has a parent, that implies it is within that location.
    * For example a location can be a rack, whose parent is likely to be a zone.
    */
   Map<String, ? extends Location> getLocations();

   /**
    * create and run nodes in the specified tagset. If resources needed are currently available for
    * this tag, they will be reused. Otherwise, they will be created. Inbound port 22 will always be
    * opened up.
    * 
    * @param tag
    *           - common identifier to group nodes by, cannot contain hyphens
    * @param count
    *           - how many to fire up.
    * @param template
    *           - how to configure the nodes
    * 
    */
   NodeSet runNodesWithTag(String tag, int count, Template template);

   /**
    * destroy the node. If it is the only node in a tag set, the dependent resources will also be
    * destroyed.
    */
   void destroyNode(ComputeMetadata node);

   /**
    * destroy the nodes identified by this tag.
    */
   void destroyNodesWithTag(String tag);

   /**
    * Find a node by its id
    */
   NodeMetadata getNodeMetadata(ComputeMetadata node);

   /**
    * get all nodes matching the tag.
    * 
    * @param tag
    */
   NodeSet getNodesWithTag(String tag);

}
