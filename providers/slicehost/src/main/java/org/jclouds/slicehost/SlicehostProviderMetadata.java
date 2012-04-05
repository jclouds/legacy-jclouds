package org.jclouds.slicehost;

import java.net.URI;
import java.util.Properties;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

/**
 * Implementation of {@link org.jclouds.types.ProviderMetadata} for Slicehost
 * 
 * @author Adrian Cole
 */
public class SlicehostProviderMetadata extends BaseProviderMetadata<SlicehostClient, SlicehostAsyncClient, ComputeServiceContext<SlicehostClient, SlicehostAsyncClient>, SlicehostApiMetadata> {
   
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

   protected static Properties defaultProperties() {
      Properties properties = new Properties();
      return properties;
   }
   
   public static class Builder extends BaseProviderMetadata.Builder<SlicehostClient, SlicehostAsyncClient, ComputeServiceContext<SlicehostClient, SlicehostAsyncClient>, SlicehostApiMetadata> {

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
            ProviderMetadata<SlicehostClient, SlicehostAsyncClient, ComputeServiceContext<SlicehostClient, SlicehostAsyncClient>, SlicehostApiMetadata> in) {
         super.fromProviderMetadata(in);
         return this;
      }
   }
}