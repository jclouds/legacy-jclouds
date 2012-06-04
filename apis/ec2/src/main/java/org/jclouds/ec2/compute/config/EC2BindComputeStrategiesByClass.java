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
package org.jclouds.ec2.compute.config;

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
import org.jclouds.ec2.compute.strategy.EC2CreateNodesInGroupThenAddToSet;
import org.jclouds.ec2.compute.strategy.EC2DestroyNodeStrategy;
import org.jclouds.ec2.compute.strategy.EC2GetImageStrategy;
import org.jclouds.ec2.compute.strategy.EC2GetNodeMetadataStrategy;
import org.jclouds.ec2.compute.strategy.EC2ListNodesStrategy;
import org.jclouds.ec2.compute.strategy.EC2RebootNodeStrategy;
import org.jclouds.ec2.compute.strategy.EC2ResumeNodeStrategy;
import org.jclouds.ec2.compute.strategy.EC2SuspendNodeStrategy;

/**
 * @author Adrian Cole
 */
public class EC2BindComputeStrategiesByClass extends BindComputeStrategiesByClass {
   @Override
   protected Class<? extends CreateNodesInGroupThenAddToSet> defineRunNodesAndAddToSetStrategy() {
      return EC2CreateNodesInGroupThenAddToSet.class;
   }

   /**
    * not needed, as {@link EC2CreateNodesInGroupThenAddToSet} is used and is already set-based.
    */
   @Override
   protected Class<? extends CreateNodeWithGroupEncodedIntoName> defineAddNodeWithTagStrategy() {
      return null;
   }

   /**
    * not needed, as {@link EC2CreateNodesInGroupThenAddToSet} is used and is already set-based.
    */
   @Override
   protected void bindAddNodeWithTagStrategy(Class<? extends CreateNodeWithGroupEncodedIntoName> clazz) {
   }

   @Override
   protected Class<? extends DestroyNodeStrategy> defineDestroyNodeStrategy() {
      return EC2DestroyNodeStrategy.class;
   }

   @Override
   protected Class<? extends GetNodeMetadataStrategy> defineGetNodeMetadataStrategy() {
      return EC2GetNodeMetadataStrategy.class;
   }

   @Override
   protected Class<? extends GetImageStrategy> defineGetImageStrategy() {
      return EC2GetImageStrategy.class;
   }

   @Override
   protected Class<? extends ListNodesStrategy> defineListNodesStrategy() {
      return EC2ListNodesStrategy.class;
   }

   @Override
   protected Class<? extends RebootNodeStrategy> defineRebootNodeStrategy() {
      return EC2RebootNodeStrategy.class;
   }

   @Override
   protected Class<? extends ResumeNodeStrategy> defineStartNodeStrategy() {
      return EC2ResumeNodeStrategy.class;
   }

   @Override
   protected Class<? extends SuspendNodeStrategy> defineStopNodeStrategy() {
      return EC2SuspendNodeStrategy.class;
   }

}
