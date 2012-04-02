package org.jclouds.openstack.nova.v1_1;

import java.net.URI;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.apis.ApiType;
import org.jclouds.apis.BaseApiMetadata;

/**
 * Implementation of {@link ApiMetadata} for OpenStack Nova Diablo+
 * 
 * @author Adrian Cole
 */
public class NovaApiMetadata extends BaseApiMetadata {

   public NovaApiMetadata() {
      this(builder()
            .id("openstack-nova")
            .type(ApiType.COMPUTE)
            .name("OpenStack Nova Diablo+ API")
            .identityName("tenantId:user")
            .credentialName("password")
            .documentation(URI.create("http://api.openstack.org/")));
   }


   // below are so that we can reuse builders, toString, hashCode, etc.
   // we have to set concrete classes here, as our base class cannot be
   // concrete due to serviceLoader
   protected NovaApiMetadata(NovaApiMetadataBuilder<?> builder) {
      super(builder);
   }

   public static class NovaApiMetadataBuilder<B extends NovaApiMetadataBuilder<B>> extends Builder<B> {

      @Override
      public NovaApiMetadata build() {
         return new NovaApiMetadata(this);
      }
   }

   private static class NovaConcreteBuilder extends NovaApiMetadataBuilder<NovaConcreteBuilder> {

      @Override
      public NovaApiMetadata build() {
         return new NovaApiMetadata(this);
      }
   }

   private static NovaConcreteBuilder builder() {
      return new NovaConcreteBuilder();
   }

   @Override
   public NovaApiMetadataBuilder<?> toBuilder() {
      return builder().fromApiMetadata(this);
   }
}