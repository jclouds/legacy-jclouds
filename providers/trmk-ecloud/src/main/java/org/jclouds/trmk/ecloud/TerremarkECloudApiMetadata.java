package org.jclouds.trmk.ecloud;

import java.net.URI;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.apis.ApiType;
import org.jclouds.apis.BaseApiMetadata;

/**
 * Implementation of {@link ApiMetadata} for Terremark eCloud v2.8 API
 * 
 * @author Adrian Cole
 */
public class TerremarkECloudApiMetadata extends BaseApiMetadata {

   public TerremarkECloudApiMetadata() {
      this(builder()
            .id("trmk-ecloud")
            .type(ApiType.COMPUTE)
            .name("Terremark Enterprise Cloud v2.8 API")
            .identityName("Email")
            .credentialName("Password")
            .documentation(URI.create("http://support.theenterprisecloud.com/kb/default.asp?id=533&Lang=1&SID=")));
   }

   // below are so that we can reuse builders, toString, hashCode, etc.
   // we have to set concrete classes here, as our base class cannot be
   // concrete due to serviceLoader
   protected TerremarkECloudApiMetadata(Builder<?> builder) {
      super(builder);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {

      @Override
      public TerremarkECloudApiMetadata build() {
         return new TerremarkECloudApiMetadata(this);
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