package org.jclouds.nodepool.config;

import javax.annotation.Nullable;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.config.JCloudsNativeComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.nodepool.NodePoolApiMetadata;
import org.jclouds.nodepool.NodePoolComputeServiceAdapter;
import org.jclouds.nodepool.NodePoolComputeServiceContext;
import org.jclouds.nodepool.internal.JsonNodeMetadataStore;
import org.jclouds.nodepool.internal.NodeMetadataStore;

import com.google.common.base.Function;
import com.google.inject.TypeLiteral;

public class NodePoolComputeServiceContextModule extends JCloudsNativeComputeServiceAdapterContextModule {

   private static class NullCredentialsOverrider implements Function<Template, LoginCredentials> {

      @Override
      @Nullable
      public LoginCredentials apply(@Nullable Template input) {
         return null;
      }

   }

   public NodePoolComputeServiceContextModule() {
      super(NodePoolComputeServiceAdapter.class);
   }

   @Override
   protected void configure() {
      super.configure();
      bind(NodeMetadataStore.class).to(JsonNodeMetadataStore.class);
      bind(ApiMetadata.class).to(NodePoolApiMetadata.class);
      bind(ComputeServiceContext.class).to(NodePoolComputeServiceContext.class);
      install(new LocationsFromComputeServiceAdapterModule<NodeMetadata, Hardware, Image, Location>() {
      });
   }

   @Override
   protected void bindCredentialsOverriderFunction() {
      bind(new TypeLiteral<Function<Template, LoginCredentials>>() {
      }).to(NullCredentialsOverrider.class);
   }

}
