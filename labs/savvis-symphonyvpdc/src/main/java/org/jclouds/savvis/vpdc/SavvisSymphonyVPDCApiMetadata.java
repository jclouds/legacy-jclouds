package org.jclouds.savvis.vpdc;

import java.net.URI;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.apis.ApiType;
import org.jclouds.apis.BaseApiMetadata;

/**
 * Implementation of {@link ApiMetadata} for the Savvis Symphony VPDC API
 * 
 * @author Adrian Cole
 */
public class SavvisSymphonyVPDCApiMetadata extends BaseApiMetadata {

   public SavvisSymphonyVPDCApiMetadata() {
      this(builder()
            .id("savvis-symphonyvpdc")
            .type(ApiType.COMPUTE)
            .name("Savvis Symphony VPDC API")
            .identityName("Username")
            .credentialName("Password")
            .documentation(URI.create("https://api.savvis.net/doc/spec/api/index.html")));
   }

   // below are so that we can reuse builders, toString, hashCode, etc.
   // we have to set concrete classes here, as our base class cannot be
   // concrete due to serviceLoader
   protected SavvisSymphonyVPDCApiMetadata(Builder<?> builder) {
      super(builder);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {

      @Override
      public SavvisSymphonyVPDCApiMetadata build() {
         return new SavvisSymphonyVPDCApiMetadata(this);
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