package org.jclouds.hpcloud.objectstorage;

import java.net.URI;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.apis.ApiType;
import org.jclouds.apis.BaseApiMetadata;

/**
 * Implementation of {@link ApiMetadata} for HP Cloud Object Storage API
 * 
 * @author Adrian Cole
 */
public class HPCloudObjectStorageApiMetadata extends BaseApiMetadata {

   public HPCloudObjectStorageApiMetadata() {
      this(builder()
            .id("hpcloud-objectstorage")
            .type(ApiType.BLOBSTORE)
            .name("HP Cloud Services Object Storage API")
            .identityName("tenantId:accessKey")
            .credentialName("secretKey")
            .documentation(URI.create("https://build.hpcloud.com/object-storage/api")));
   }

   // below are so that we can reuse builders, toString, hashCode, etc.
   // we have to set concrete classes here, as our base class cannot be
   // concrete due to serviceLoader
   protected HPCloudObjectStorageApiMetadata(ConcreteBuilder builder) {
      super(builder);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {

      @Override
      public HPCloudObjectStorageApiMetadata build() {
         return new HPCloudObjectStorageApiMetadata(this);
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