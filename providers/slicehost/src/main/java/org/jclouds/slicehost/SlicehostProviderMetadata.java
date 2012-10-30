package org.jclouds.slicehost;

import static org.jclouds.compute.config.ComputeServiceProperties.TEMPLATE;

import java.net.URI;
import java.util.Properties;

import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

/**
 * Implementation of {@link org.jclouds.types.ProviderMetadata} for Slicehost
 * 
 * @author Adrian Cole
 */
public class SlicehostProviderMetadata extends BaseProviderMetadata {
   
   /** The serialVersionUID */
   private static final long serialVersionUID = -5260230633956988685L;

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }
   
   public SlicehostProviderMetadata() {
      super(builder());
   }

   public SlicehostProviderMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = new Properties();
      properties.setProperty(TEMPLATE, "osFamily=UBUNTU,osVersionMatches=1[012].[01][04],os64Bit=true,osDescriptionMatches=^((?!MGC).)*$");
      return properties;
   }
   
   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder(){
          id("slicehost")
         .name("Slicehost")
         .apiMetadata(new SlicehostApiMetadata())
         .homepage(URI.create("http://www.slicehost.com"))
         .console(URI.create("https://manage.slicehost.com/"))
         .iso3166Codes("US-IL", "US-TX", "US-MO");
      }

      @Override
      public SlicehostProviderMetadata build() {
         return new SlicehostProviderMetadata(this);
      }
      
      @Override
      public Builder fromProviderMetadata(
            ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }
   }
}
