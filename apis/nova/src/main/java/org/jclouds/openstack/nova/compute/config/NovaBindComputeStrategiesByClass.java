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
package org.jclouds.openstack.nova.compute.config;

import org.jclouds.compute.config.BindComputeStrategiesByClass;
import org.jclouds.compute.strategy.CreateNodeWithGroupEncodedIntoName;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.compute.strategy.ResumeNodeStrategy;
import org.jclouds.compute.strategy.SuspendNodeStrategy;
import org.jclouds.openstack.nova.compute.strategy.NovaCreateNodeWithGroupEncodedIntoName;
import org.jclouds.openstack.nova.compute.strategy.NovaDestroyNodeStrategy;
import org.jclouds.openstack.nova.compute.strategy.NovaGetNodeMetadataStrategy;
import org.jclouds.openstack.nova.compute.strategy.NovaListNodesStrategy;
import org.jclouds.openstack.nova.compute.strategy.NovaLifeCycleStrategy;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class NovaBindComputeStrategiesByClass extends BindComputeStrategiesByClass {

   @Override
   protected Class<? extends CreateNodeWithGroupEncodedIntoName> defineAddNodeWithTagStrategy() {
      return NovaCreateNodeWithGroupEncodedIntoName.class;
   }

   @Override
   protected Class<? extends DestroyNodeStrategy> defineDestroyNodeStrategy() {
      return NovaDestroyNodeStrategy.class;
   }

   @Override
   protected Class<? extends GetNodeMetadataStrategy> defineGetNodeMetadataStrategy() {
      return NovaGetNodeMetadataStrategy.class;
   }

   @Override
   protected Class<? extends ListNodesStrategy> defineListNodesStrategy() {
      return NovaListNodesStrategy.class;
   }

   @Override
   protected Class<? extends RebootNodeStrategy> defineRebootNodeStrategy() {
      return NovaLifeCycleStrategy.class;
   }

   @Override
   protected Class<? extends ResumeNodeStrategy> defineStartNodeStrategy() {
      return NovaLifeCycleStrategy.class;
   }

   @Override
   protected Class<? extends SuspendNodeStrategy> defineStopNodeStrategy() {
      return NovaLifeCycleStrategy.class;
   }
}
