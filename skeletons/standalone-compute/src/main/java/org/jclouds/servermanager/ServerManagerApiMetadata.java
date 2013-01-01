package org.jclouds.servermanager;

import java.net.URI;

import org.jclouds.apis.internal.BaseApiMetadata;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.servermanager.compute.config.ServerManagerComputeServiceContextModule;

/**
 * Implementation of {@link ApiMetadata} for an example of library integration (ServerManager)
 * 
 * @author Adrian Cole
 */
public class ServerManagerApiMetadata extends BaseApiMetadata {

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public ServerManagerApiMetadata() {
      super(new Builder());
   }

   protected ServerManagerApiMetadata(Builder builder) {
      super(builder);
   }

   public static class Builder extends BaseApiMetadata.Builder<Builder> {

      protected Builder(){
         id("servermanager")
         .name("ServerManager API")
         .identityName("Unused")
         .defaultIdentity("foo")
         .defaultCredential("bar")
         .defaultEndpoint("http://demo")
         .documentation(URI.create("http://www.jclouds.org/documentation/userguide/compute"))
         .view(ComputeServiceContext.class)
         .defaultModule(ServerManagerComputeServiceContextModule.class);
      }

      @Override
      public ServerManagerApiMetadata build() {
         return new ServerManagerApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
}
