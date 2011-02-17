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