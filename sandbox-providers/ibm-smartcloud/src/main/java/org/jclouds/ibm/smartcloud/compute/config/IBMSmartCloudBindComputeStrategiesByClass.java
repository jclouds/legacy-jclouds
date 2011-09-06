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
package org.jclouds.ibm.smartcloud.compute.config;

import org.jclouds.compute.config.BindComputeStrategiesByClass;
import org.jclouds.compute.strategy.CreateNodeWithGroupEncodedIntoName;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.compute.strategy.ResumeNodeStrategy;
import org.jclouds.compute.strategy.SuspendNodeStrategy;
import org.jclouds.ibm.smartcloud.compute.strategy.IBMSmartCloudCreateNodeWithGroupEncodedIntoName;
import org.jclouds.ibm.smartcloud.compute.strategy.IBMSmartCloudDestroyNodeStrategy;
import org.jclouds.ibm.smartcloud.compute.strategy.IBMSmartCloudGetNodeMetadataStrategy;
import org.jclouds.ibm.smartcloud.compute.strategy.IBMSmartCloudLifeCycleStrategy;
import org.jclouds.ibm.smartcloud.compute.strategy.IBMSmartCloudListNodesStrategy;

/**
 * @author Adrian Cole
 */
public class IBMSmartCloudBindComputeStrategiesByClass extends BindComputeStrategiesByClass {

   @Override
   protected Class<? extends CreateNodeWithGroupEncodedIntoName> defineAddNodeWithTagStrategy() {
      return IBMSmartCloudCreateNodeWithGroupEncodedIntoName.class;
   }

   @Override
   protected Class<? extends DestroyNodeStrategy> defineDestroyNodeStrategy() {
      return IBMSmartCloudDestroyNodeStrategy.class;
   }

   @Override
   protected Class<? extends GetNodeMetadataStrategy> defineGetNodeMetadataStrategy() {
      return IBMSmartCloudGetNodeMetadataStrategy.class;
   }

   @Override
   protected Class<? extends ListNodesStrategy> defineListNodesStrategy() {
      return IBMSmartCloudListNodesStrategy.class;
   }

   @Override
   protected Class<? extends RebootNodeStrategy> defineRebootNodeStrategy() {
      return IBMSmartCloudLifeCycleStrategy.class;
   }

   @Override
   protected Class<? extends ResumeNodeStrategy> defineStartNodeStrategy() {
      return IBMSmartCloudLifeCycleStrategy.class;
   }

   @Override
   protected Class<? extends SuspendNodeStrategy> defineStopNodeStrategy() {
      return IBMSmartCloudLifeCycleStrategy.class;
   }

}
