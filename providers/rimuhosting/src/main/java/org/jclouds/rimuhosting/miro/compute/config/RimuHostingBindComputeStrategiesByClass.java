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
package org.jclouds.rimuhosting.miro.compute.config;

import org.jclouds.compute.config.BindComputeStrategiesByClass;
import org.jclouds.compute.strategy.CreateNodeWithGroupEncodedIntoName;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.compute.strategy.ResumeNodeStrategy;
import org.jclouds.compute.strategy.SuspendNodeStrategy;
import org.jclouds.rimuhosting.miro.compute.strategy.RimuHostingCreateNodeWithGroupEncodedIntoName;
import org.jclouds.rimuhosting.miro.compute.strategy.RimuHostingDestroyNodeStrategy;
import org.jclouds.rimuhosting.miro.compute.strategy.RimuHostingGetNodeMetadataStrategy;
import org.jclouds.rimuhosting.miro.compute.strategy.RimuHostingLifeCycleStrategy;
import org.jclouds.rimuhosting.miro.compute.strategy.RimuHostingListNodesStrategy;

public class RimuHostingBindComputeStrategiesByClass extends BindComputeStrategiesByClass {
   @Override
   protected Class<? extends CreateNodeWithGroupEncodedIntoName> defineAddNodeWithTagStrategy() {
      return RimuHostingCreateNodeWithGroupEncodedIntoName.class;
   }

   @Override
   protected Class<? extends DestroyNodeStrategy> defineDestroyNodeStrategy() {
      return RimuHostingDestroyNodeStrategy.class;
   }

   @Override
   protected Class<? extends GetNodeMetadataStrategy> defineGetNodeMetadataStrategy() {
      return RimuHostingGetNodeMetadataStrategy.class;
   }

   @Override
   protected Class<? extends ListNodesStrategy> defineListNodesStrategy() {
      return RimuHostingListNodesStrategy.class;
   }

   @Override
   protected Class<? extends RebootNodeStrategy> defineRebootNodeStrategy() {
      return RimuHostingLifeCycleStrategy.class;
   }

   @Override
   protected Class<? extends ResumeNodeStrategy> defineStartNodeStrategy() {
      return RimuHostingLifeCycleStrategy.class;
   }

   @Override
   protected Class<? extends SuspendNodeStrategy> defineStopNodeStrategy() {
      return RimuHostingLifeCycleStrategy.class;
   }
}