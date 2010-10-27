package org.jclouds.compute.config;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.StandaloneComputeServiceContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.domain.Location;

import com.google.common.base.Function;
import com.google.inject.TypeLiteral;

public class JCloudsNativeStandaloneComputeServiceContextModule extends
      StandaloneComputeServiceContextModule<NodeMetadata, Hardware, Image, Location> {
   private final Class<? extends ComputeServiceAdapter<NodeMetadata, Hardware, Image, Location>> adapter;

   public JCloudsNativeStandaloneComputeServiceContextModule(
         Class<? extends ComputeServiceAdapter<NodeMetadata, Hardware, Image, Location>> adapter) {
      this.adapter = adapter;
   }

   /**
    * This binds the converters to {@link IdentityFunction} as that ensure the same value is
    * returned.
    */
   @SuppressWarnings({ "unchecked", "rawtypes" })
   @Override
   protected void configure() {
      bind(new TypeLiteral<ComputeServiceAdapter<NodeMetadata, Hardware, Image, Location>>() {
      }).to(adapter);
      bind(IdentityFunction.class).toInstance(IdentityFunction.INSTANCE);
      bind(new TypeLiteral<Function<NodeMetadata, NodeMetadata>>() {
      }).to((Class) StandaloneComputeServiceContextModule.IdentityFunction.class);
      bind(new TypeLiteral<Function<Image, Image>>() {
      }).to((Class) StandaloneComputeServiceContextModule.IdentityFunction.class);
      bind(new TypeLiteral<Function<Hardware, Hardware>>() {
      }).to((Class) StandaloneComputeServiceContextModule.IdentityFunction.class);
      bind(new TypeLiteral<Function<Location, Location>>() {
      }).to((Class) StandaloneComputeServiceContextModule.IdentityFunction.class);
      super.configure();
   }

}