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

package org.jclouds.compute.management;


import javax.management.openmbean.CompositeData;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.TabularData;


public interface ComputeServiceManagementMBean {

   /**
    * The list hardware profiles command shows you the options including virtual cpu count, memory,
    * and disks. cpu count is not a portable quantity across clouds, as they are measured
    * differently. However, it is a good indicator of relative speed within a cloud. memory is
    * measured in megabytes and disks in gigabytes.
    * <p/>
    * <h3>note</h3>
    * <p/>
    * This is a cached collection
    */
   TabularData listHardwareProfiles() throws OpenDataException;

   /**
    * Images define the operating system and metadata related to a node. In some clouds, Images are
    * bound to a specific region, and their identifiers are different across these regions. For this
    * reason, you should consider matching image requirements like operating system family with
    * TemplateBuilder as opposed to choosing an image explicitly.
    * <p/>
    * <h3>note</h3>
    * <p/>
    * This is a cached collection
    */
   TabularData listImages() throws OpenDataException;


   /**
    * all nodes available to the current user by id. If possible, the returned set will include
    * {@link org.jclouds.compute.domain.NodeMetadata} objects.
    */
   TabularData listNodes() throws OpenDataException;

   /**
    * The list locations command returns all the valid locations for nodes. A location has a scope,
    * which is typically region or zone. A region is a general area, like eu-west, where a zone is
    * similar to a datacenter. If a location has a parent, that implies it is within that location.
    * For example a location can be a rack, whose parent is likely to be a zone.
    * <p/>
    * <h3>note</h3>
    * <p/>
    * This is a cached collection
    */
   TabularData listAssignableLocations() throws OpenDataException;

   /**
    * Find an image by its id.
    * <p/>
    * <h3>note</h3>
    * <p/>
    * This is an uncached call to the backend service
    */
   CompositeData getImage(String id) throws OpenDataException;

   /**
    * Find a node by its id.
    */
   CompositeData getNode(String id) throws OpenDataException;


   /**
    * @see #runScriptOnNode(String, String, RunScriptOptions)
    */
   CompositeData runScriptOnNode(String id, String runScript) throws OpenDataException;


   /**
    * resume the node from {@link org.jclouds.compute.domain.NodeState#SUSPENDED suspended} state,
    * given its id.
    *
    * <h4>note</h4>
    *
    * affected nodes may not resume with the same IP address(es)
    */
   void resumeNode(String id);

   /**
    * suspend the node, given its id. This will result in
    * {@link org.jclouds.compute.domain.NodeState#SUSPENDED suspended} state.
    *
    * <h4>note</h4>
    *
    * affected nodes may not resume with the same IP address(es)
    *
    * @throws UnsupportedOperationException
    *            if the underlying provider doesn't support suspend/resume
    */
   void suspendNode(String id);


   /**
    * destroy the node, given its id. If it is the only node in a tag set, the dependent resources
    * will also be destroyed.
    */
   void destroyNode(String id);


   /**
    * reboot the node, given its id.
    */
   void rebootNode(String id);

}
