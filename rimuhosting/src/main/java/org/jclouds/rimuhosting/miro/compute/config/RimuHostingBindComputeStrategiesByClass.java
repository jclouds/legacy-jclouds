package org.jclouds.rimuhosting.miro.compute.config;

import org.jclouds.compute.config.BindComputeStrategiesByClass;
import org.jclouds.compute.strategy.AddNodeWithTagStrategy;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.rimuhosting.miro.compute.strategy.RimuHostingAddNodeWithTagStrategy;
import org.jclouds.rimuhosting.miro.compute.strategy.RimuHostingDestroyNodeStrategy;
import org.jclouds.rimuhosting.miro.compute.strategy.RimuHostingGetNodeMetadataStrategy;
import org.jclouds.rimuhosting.miro.compute.strategy.RimuHostingListNodesStrategy;
import org.jclouds.rimuhosting.miro.compute.strategy.RimuHostingRebootNodeStrategy;

public class RimuHostingBindComputeStrategiesByClass extends BindComputeStrategiesByClass {
   @Override
   protected Class<? extends AddNodeWithTagStrategy> defineAddNodeWithTagStrategy() {
      return RimuHostingAddNodeWithTagStrategy.class;
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
      return RimuHostingRebootNodeStrategy.class;
   }
}