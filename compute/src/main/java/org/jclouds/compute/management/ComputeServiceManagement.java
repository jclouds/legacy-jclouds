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

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.domain.Location;
import org.jclouds.management.JcloudsManagedBean;
import org.jclouds.management.functions.ToCompositeData;
import org.jclouds.management.functions.ToTabularData;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.TabularData;

public class ComputeServiceManagement implements ComputeServiceManagementMBean, JcloudsManagedBean {

   private final ComputeService service;
   private final ToCompositeData<Image> imageToComposite = ToCompositeData.from(Image.class);
   private final ToTabularData<Image> imageToTabular = ToTabularData.from(Image.class);
   private final ToTabularData<Hardware> hardwareToTabular = ToTabularData.from(Hardware.class);
   private final ToTabularData<Location> locationToTabular = ToTabularData.from(Location.class);
   private final ToCompositeData<ComputeMetadata> nodeToTComposite = ToCompositeData.from(ComputeMetadata.class);
   private final ToTabularData<ComputeMetadata> nodeToTabular = ToTabularData.from(ComputeMetadata.class);
   private final ToCompositeData<ExecResponse> execToComposite = ToCompositeData.from(ExecResponse.class);


   public ComputeServiceManagement(ComputeServiceContext context) {
      this.service = context.getComputeService();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TabularData listHardwareProfiles() throws OpenDataException {
      return hardwareToTabular.apply(service.listHardwareProfiles());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TabularData listImages() throws OpenDataException {
      return imageToTabular.apply(service.listImages());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CompositeData getImage(String id) throws OpenDataException {
      return imageToComposite.apply(service.getImage(id));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TabularData listNodes() throws OpenDataException {
      return nodeToTabular.apply(service.listNodes());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TabularData listAssignableLocations() throws OpenDataException {
      return locationToTabular.apply(service.listAssignableLocations());
   }


   /**
    * {@inheritDoc}
    */
   @Override
   public void resumeNode(String id) {
      service.resumeNode(id);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void suspendNode(String id) {
      service.suspendNode(id);
   }


   /**
    * destroy the node, given its id. If it is the only node in a tag set, the dependent resources
    * will also be destroyed.
    */
   @Override
   public void destroyNode(String id) {
      service.destroyNode(id);
   }


   /**
    * reboot the node, given its id.
    */
   @Override
   public void rebootNode(String id) {
      service.destroyNode(id);
   }

   /**
    * Find a node by its id.
    */
   @Override
   public CompositeData getNode(String id) throws OpenDataException {
      return nodeToTComposite.apply(service.getNodeMetadata(id));
   }

   /**
    * @see #runScriptOnNode(String, String, org.jclouds.compute.options.RunScriptOptions)
    */
   @Override
   public CompositeData runScriptOnNode(String id, String runScript) throws OpenDataException {
      return execToComposite.apply(service.runScriptOnNode(id, runScript));
   }

   /**
    * Returns the type of the MBean.
    *
    * @return
    */
   @Override
   public String getType() {
      return "compute";
   }

   /**
    * Returns the anme of the MBean.
    *
    * @return
    */
   @Override
   public String getName() {
      return service.getContext().unwrap().getName();
   }
}
