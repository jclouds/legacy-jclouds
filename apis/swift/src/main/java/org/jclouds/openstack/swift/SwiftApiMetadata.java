package org.jclouds.openstack.swift;

import java.net.URI;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.apis.ApiType;
import org.jclouds.apis.BaseApiMetadata;

/**
 * Implementation of {@link ApiMetadata} for OpenStack Swift Pre-Diablo
 * 
 * @author Adrian Cole
 */
public class SwiftApiMetadata extends BaseApiMetadata {

   public SwiftApiMetadata() {
      this(builder()
            .id("swift")
            .type(ApiType.BLOBSTORE)
            .name("OpenStack Swift Pre-Diablo API")
            .identityName("tenantId:user")
            .credentialName("password")
            .documentation(URI.create("http://api.openstack.org/")));
   }

   // below are so that we can reuse builders, toString, hashCode, etc.
   // we have to set concrete classes here, as our base class cannot be
   // concrete due to serviceLoader
   protected SwiftApiMetadata(ConcreteBuilder builder) {
      super(builder);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {

      @Override
      public SwiftApiMetadata build() {
         return new SwiftApiMetadata(this);
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