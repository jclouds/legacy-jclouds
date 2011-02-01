/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.ibmdev.compute.config;

import org.jclouds.compute.config.BindComputeStrategiesByClass;
import org.jclouds.compute.strategy.CreateNodeWithGroupEncodedIntoName;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.compute.strategy.ResumeNodeStrategy;
import org.jclouds.compute.strategy.SuspendNodeStrategy;
import org.jclouds.ibmdev.compute.strategy.IBMDeveloperCloudCreateNodeWithGroupEncodedIntoName;
import org.jclouds.ibmdev.compute.strategy.IBMDeveloperCloudDestroyNodeStrategy;
import org.jclouds.ibmdev.compute.strategy.IBMDeveloperCloudGetNodeMetadataStrategy;
import org.jclouds.ibmdev.compute.strategy.IBMDeveloperCloudLifeCycleStrategy;
import org.jclouds.ibmdev.compute.strategy.IBMDeveloperCloudListNodesStrategy;

/**
 * @author Adrian Cole
 */
public class IBMDeveloperCloudBindComputeStrategiesByClass extends BindComputeStrategiesByClass {

   @Override
   protected Class<? extends CreateNodeWithGroupEncodedIntoName> defineAddNodeWithTagStrategy() {
      return IBMDeveloperCloudCreateNodeWithGroupEncodedIntoName.class;
   }

   @Override
   protected Class<? extends DestroyNodeStrategy> defineDestroyNodeStrategy() {
      return IBMDeveloperCloudDestroyNodeStrategy.class;
   }

   @Override
   protected Class<? extends GetNodeMetadataStrategy> defineGetNodeMetadataStrategy() {
      return IBMDeveloperCloudGetNodeMetadataStrategy.class;
   }

   @Override
   protected Class<? extends ListNodesStrategy> defineListNodesStrategy() {
      return IBMDeveloperCloudListNodesStrategy.class;
   }

   @Override
   protected Class<? extends RebootNodeStrategy> defineRebootNodeStrategy() {
      return IBMDeveloperCloudLifeCycleStrategy.class;
   }

   @Override
   protected Class<? extends ResumeNodeStrategy> defineStartNodeStrategy() {
      return IBMDeveloperCloudLifeCycleStrategy.class;
   }

   @Override
   protected Class<? extends SuspendNodeStrategy> defineStopNodeStrategy() {
      return IBMDeveloperCloudLifeCycleStrategy.class;
   }

}