package org.jclouds.ninefold.compute;

import java.net.URI;
import java.util.Properties;

import org.jclouds.cloudstack.CloudStackApiMetadata;
import org.jclouds.cloudstack.CloudStackAsyncClient;
import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.CloudStackContext;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

/**
 * Implementation of {@link org.jclouds.types.ProviderMetadata} for Ninefold
 * Compute. 
 * @author Adrian Cole
 */
public class NinefoldComputeProviderMetadata
      extends
      BaseProviderMetadata<CloudStackClient, CloudStackAsyncClient, CloudStackContext, CloudStackApiMetadata> {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }

   public NinefoldComputeProviderMetadata() {
      super(builder());
   }

   public NinefoldComputeProviderMetadata(Builder builder) {
      super(builder);
   }

   protected static Properties defaultProperties() {
      Properties properties = new Properties();
      properties.setProperty("ninefold-compute.image-id", "1215");
      properties.setProperty("ninefold-compute.image.login-user", "user:Password01");
      properties.setProperty("ninefold-compute.image.authenticate-sudo", "true");
      return properties;
   }

   public static class Builder
         extends
         BaseProviderMetadata.Builder<CloudStackClient, CloudStackAsyncClient, CloudStackContext, CloudStackApiMetadata> {

      protected Builder() {
         id("ninefold-compute")
         .name("Ninefold Compute")
         .apiMetadata(new CloudStackApiMetadata().toBuilder().version("2.2.12").build())
         .homepage(URI.create("http://ninefold.com/virtual-servers/"))
         .console(URI.create("https://ninefold.com/portal/portal/login"))
         .iso3166Codes("AU-NSW")
         .endpoint("https://api.ninefold.com/compute/v1.0/")
         .defaultProperties(NinefoldComputeProviderMetadata.defaultProperties());
      }

      @Override
      public NinefoldComputeProviderMetadata build() {
         return new NinefoldComputeProviderMetadata(this);
      }

      @Override
      public Builder fromProviderMetadata(
            ProviderMetadata<CloudStackClient, CloudStackAsyncClient, CloudStackContext, CloudStackApiMetadata> in) {
         super.fromProviderMetadata(in);
         return this;
      }

   }
}