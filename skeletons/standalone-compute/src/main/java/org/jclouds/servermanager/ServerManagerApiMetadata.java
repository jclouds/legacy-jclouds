package org.jclouds.servermanager;

import java.net.URI;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.internal.BaseComputeServiceApiMetadata;
import org.jclouds.servermanager.compute.ServerManagerComputeServiceContextBuilder;

import com.google.common.reflect.TypeToken;

/**
 * Implementation of {@link ApiMetadata} for an example of library integration (ServerManager)
 * 
 * @author Adrian Cole
 */
public class ServerManagerApiMetadata extends BaseComputeServiceApiMetadata<ServerManager, ServerManager, ComputeServiceContext<ServerManager, ServerManager>, ServerManagerApiMetadata> {
   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return Builder.class.cast(builder().fromApiMetadata(this));
   }

   public ServerManagerApiMetadata() {
      super(builder());
   }

   protected ServerManagerApiMetadata(Builder builder) {
      super(builder);
   }

   public static class Builder extends BaseComputeServiceApiMetadata.Builder<ServerManager, ServerManager, ComputeServiceContext<ServerManager, ServerManager>, ServerManagerApiMetadata> {

      protected Builder(){
         id("servermanager")
         .name("ServerManager API")
         .identityName("Unused")
         .defaultIdentity("foo")
         .defaultCredential("bar")
         .defaultEndpoint("http://demo")
         .context(new TypeToken<ComputeServiceContext<ServerManager, ServerManager>>(getClass()){
            private static final long serialVersionUID = 1L;
            })
         .javaApi(ServerManager.class, ServerManager.class)
         .documentation(URI.create("http://www.jclouds.org/documentation/userguide/compute"))
         .contextBuilder(TypeToken.of(ServerManagerComputeServiceContextBuilder.class));
      }

      @Override
      public ServerManagerApiMetadata build() {
         return new ServerManagerApiMetadata(this);
      }

   }
}