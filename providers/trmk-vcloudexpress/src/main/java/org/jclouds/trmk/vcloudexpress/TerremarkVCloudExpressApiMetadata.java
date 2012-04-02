package org.jclouds.trmk.vcloudexpress;

import java.net.URI;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.apis.ApiType;
import org.jclouds.apis.BaseApiMetadata;

/**
 * Implementation of {@link ApiMetadata} for the Terremark vCloud Express API
 * 
 * @author Adrian Cole
 */
public class TerremarkVCloudExpressApiMetadata extends BaseApiMetadata {

   public TerremarkVCloudExpressApiMetadata() {
      this(builder()
            .id("trmk-vcloudexpress")
            .type(ApiType.COMPUTE)
            .name("Terremark vCloud Express API")
            .identityName("Email")
            .credentialName("Password")
            .documentation(URI.create("https://community.vcloudexpress.terremark.com/en-us/product_docs/m/vcefiles/2342.aspx")));
   }

   // below are so that we can reuse builders, toString, hashCode, etc.
   // we have to set concrete classes here, as our base class cannot be
   // concrete due to serviceLoader
   protected TerremarkVCloudExpressApiMetadata(Builder<?> builder) {
      super(builder);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {

      @Override
      public TerremarkVCloudExpressApiMetadata build() {
         return new TerremarkVCloudExpressApiMetadata(this);
      }
   }

   public static ConcreteBuilder builder() {
      return new ConcreteBuilder();
   }

   @Override
   public ConcreteBuilder toBuilder() {
      return builder().fromApiMetadata(this);
   }

}