/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.trmk.vcloud_0_8.compute.config;

import org.jclouds.compute.config.BindComputeStrategiesByClass;
import org.jclouds.compute.strategy.CreateNodeWithGroupEncodedIntoName;
import org.jclouds.compute.strategy.CreateNodesInGroupThenAddToSet;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetImageStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.compute.strategy.ResumeNodeStrategy;
import org.jclouds.compute.strategy.SuspendNodeStrategy;
import org.jclouds.trmk.vcloud_0_8.compute.strategy.StartVAppWithGroupEncodedIntoName;
import org.jclouds.trmk.vcloud_0_8.compute.strategy.TerremarkEncodeTagIntoNameRunNodesAndAddToSetStrategy;
import org.jclouds.trmk.vcloud_0_8.compute.strategy.TerremarkVCloudDestroyNodeStrategy;
import org.jclouds.trmk.vcloud_0_8.compute.strategy.TerremarkVCloudGetImageStrategy;
import org.jclouds.trmk.vcloud_0_8.compute.strategy.TerremarkVCloudGetNodeMetadataStrategy;
import org.jclouds.trmk.vcloud_0_8.compute.strategy.TerremarkVCloudLifeCycleStrategy;
import org.jclouds.trmk.vcloud_0_8.compute.strategy.TerremarkVCloudListNodesStrategy;

/**
 * @author Adrian Cole
 */
public class TerremarkBindComputeStrategiesByClass extends BindComputeStrategiesByClass {

   @Override
   protected Class<? extends DestroyNodeStrategy> defineDestroyNodeStrategy() {
      return TerremarkVCloudDestroyNodeStrategy.class;
   }

   @Override
   protected Class<? extends GetNodeMetadataStrategy> defineGetNodeMetadataStrategy() {
      return TerremarkVCloudGetNodeMetadataStrategy.class;
   }

   @Override
   protected Class<? extends GetImageStrategy> defineGetImageStrategy() {
      return TerremarkVCloudGetImageStrategy.class;
   }

   @Override
   protected Class<? extends ListNodesStrategy> defineListNodesStrategy() {
      return TerremarkVCloudListNodesStrategy.class;
   }

   @Override
   protected Class<? extends RebootNodeStrategy> defineRebootNodeStrategy() {
      return TerremarkVCloudLifeCycleStrategy.class;
   }

   @Override
   protected Class<? extends ResumeNodeStrategy> defineStartNodeStrategy() {
      return TerremarkVCloudLifeCycleStrategy.class;
   }

   @Override
   protected Class<? extends SuspendNodeStrategy> defineStopNodeStrategy() {
      return TerremarkVCloudLifeCycleStrategy.class;
   }

   @Override
   protected Class<? extends CreateNodesInGroupThenAddToSet> defineRunNodesAndAddToSetStrategy() {
      return TerremarkEncodeTagIntoNameRunNodesAndAddToSetStrategy.class;
   }

   @Override
   protected Class<? extends CreateNodeWithGroupEncodedIntoName> defineAddNodeWithTagStrategy() {
      return StartVAppWithGroupEncodedIntoName.class;
   }
}
