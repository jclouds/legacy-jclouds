package org.jclouds.ninefold.compute;

import static org.jclouds.compute.config.ComputeServiceProperties.TEMPLATE;

import java.net.URI;
import java.util.Properties;

import org.jclouds.cloudstack.CloudStackApiMetadata;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

/**
 * Implementation of {@link org.jclouds.types.ProviderMetadata} for Ninefold
 * Compute. 
 * @author Adrian Cole
 */
public class NinefoldComputeProviderMetadata extends BaseProviderMetadata {

   /** The serialVersionUID */
   private static final long serialVersionUID = -4496340915519024L;

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

   public static Properties defaultProperties() {
      Properties properties = new Properties();
      // https://ninefold.com/support/display/SPT/Ubuntu+10.04+64+Bit+Micro+Server+with+CHEF
      properties.setProperty(TEMPLATE, "imageNameMatches=.*Micro.*,osFamily=UBUNTU,osVersionMatches=1[012].[01][04],loginUser=user:Password01,authenticateSudo=true");
      return properties;
   }

   public static class Builder
         extends
         BaseProviderMetadata.Builder {

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
            ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }

   }
}
