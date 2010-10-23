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

package org.jclouds.slicehost.compute.config;

import org.jclouds.compute.config.BindComputeStrategiesByClass;
import org.jclouds.compute.strategy.AddNodeWithTagStrategy;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.slicehost.compute.strategy.SlicehostAddNodeWithTagStrategy;
import org.jclouds.slicehost.compute.strategy.SlicehostDestroyNodeStrategy;
import org.jclouds.slicehost.compute.strategy.SlicehostGetNodeMetadataStrategy;
import org.jclouds.slicehost.compute.strategy.SlicehostListNodesStrategy;
import org.jclouds.slicehost.compute.strategy.SlicehostRebootNodeStrategy;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class SlicehostBindComputeStrategiesByClass extends BindComputeStrategiesByClass {

   @Override
   protected Class<? extends AddNodeWithTagStrategy> defineAddNodeWithTagStrategy() {
      return SlicehostAddNodeWithTagStrategy.class;
   }

   @Override
   protected Class<? extends DestroyNodeStrategy> defineDestroyNodeStrategy() {
      return SlicehostDestroyNodeStrategy.class;
   }

   @Override
   protected Class<? extends GetNodeMetadataStrategy> defineGetNodeMetadataStrategy() {
      return SlicehostGetNodeMetadataStrategy.class;
   }

   @Override
   protected Class<? extends ListNodesStrategy> defineListNodesStrategy() {
      return SlicehostListNodesStrategy.class;
   }

   @Override
   protected Class<? extends RebootNodeStrategy> defineRebootNodeStrategy() {
      return SlicehostRebootNodeStrategy.class;
   }
}