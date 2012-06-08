package org.jclouds.nodepool;

import static org.jclouds.nodepool.PooledComputeServiceConstants.NODEPOOL_BACKING_GROUP_PROPERTY;
import static org.jclouds.nodepool.PooledComputeServiceConstants.NODEPOOL_MAX_SIZE_PROPERTY;
import static org.jclouds.nodepool.PooledComputeServiceConstants.NODEPOOL_MIN_SIZE_PROPERTY;
import static org.jclouds.nodepool.PooledComputeServiceConstants.NODEPOOL_REMOVE_DESTROYED_PROPERTY;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.internal.BaseApiMetadata;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.rest.internal.BaseRestApiMetadata;

public class PooledApiMetadata extends BaseApiMetadata {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return Builder.class.cast(builder().fromApiMetadata(this));
   }

   public PooledApiMetadata() {
      super(builder());
   }

   protected PooledApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseRestApiMetadata.defaultProperties();
      properties.setProperty(NODEPOOL_BACKING_GROUP_PROPERTY, "node-pool");
      properties.setProperty(NODEPOOL_MAX_SIZE_PROPERTY, 10 + "");
      properties.setProperty(NODEPOOL_MIN_SIZE_PROPERTY, 5 + "");
      properties.setProperty(NODEPOOL_REMOVE_DESTROYED_PROPERTY, "false");
      return properties;
   }

   public static class Builder extends BaseApiMetadata.Builder {
      protected Builder() {
         id("pooled").name("node pool provider wrapper").identityName("Unused").defaultIdentity("pooled")
                  .defaultCredential("poll").defaultEndpoint("pooled")
                  .documentation(URI.create("http://www.jclouds.org/documentation/userguide/compute"))
                  .view(ComputeServiceContext.class).defaultProperties(PooledApiMetadata.defaultProperties());
      }

      @Override
      public PooledApiMetadata build() {
         return new PooledApiMetadata(this);
      }

   }

}
