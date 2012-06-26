package org.jclouds.nodepool.config;

import org.jclouds.compute.config.JCloudsNativeComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.domain.Location;
import org.jclouds.nodepool.NodePoolComputeServiceAdapter;
import org.jclouds.nodepool.internal.JsonNodeMetadataStore;
import org.jclouds.nodepool.internal.NodeMetadataStore;

public class NodePoolComputeServiceContextModule extends JCloudsNativeComputeServiceAdapterContextModule {

   public NodePoolComputeServiceContextModule() {
      super(NodePoolComputeServiceAdapter.class);
   }
   
   @Override
   protected void configure() {
      super.configure();
      bind(NodeMetadataStore.class).to(JsonNodeMetadataStore.class);
      install(new LocationsFromComputeServiceAdapterModule<NodeMetadata, Hardware, Image, Location>() {
      });
   }

}
