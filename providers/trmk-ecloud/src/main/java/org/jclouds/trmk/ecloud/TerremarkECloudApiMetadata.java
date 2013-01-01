package org.jclouds.trmk.ecloud;

import static org.jclouds.trmk.vcloud_0_8.reference.TerremarkConstants.PROPERTY_TERREMARK_EXTENSION_NAME;
import static org.jclouds.trmk.vcloud_0_8.reference.TerremarkConstants.PROPERTY_TERREMARK_EXTENSION_VERSION;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.rest.RestContext;
import org.jclouds.trmk.ecloud.compute.config.TerremarkECloudComputeServiceContextModule;
import org.jclouds.trmk.ecloud.config.TerremarkECloudRestClientModule;
import org.jclouds.trmk.vcloud_0_8.internal.TerremarkVCloudApiMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for Terremark eCloud v2.8 API
 * 
 * @author Adrian Cole
 */
public class TerremarkECloudApiMetadata extends TerremarkVCloudApiMetadata {

   public static final TypeToken<RestContext<TerremarkECloudClient, TerremarkECloudAsyncClient>> CONTEXT_TOKEN = new TypeToken<RestContext<TerremarkECloudClient, TerremarkECloudAsyncClient>>() {
      private static final long serialVersionUID = 1L;
   };
   
   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public TerremarkECloudApiMetadata() {
      this(new Builder());
   }

   protected TerremarkECloudApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = TerremarkVCloudApiMetadata.defaultProperties();
      properties.setProperty(PROPERTY_TERREMARK_EXTENSION_NAME, "eCloudExtensions");
      properties.setProperty(PROPERTY_TERREMARK_EXTENSION_VERSION, "2.8");
      return properties;
   }

   public static class Builder extends TerremarkVCloudApiMetadata.Builder<Builder> {

      protected Builder() {
         super(TerremarkECloudClient.class, TerremarkECloudAsyncClient.class);
         id("trmk-ecloud")
         .name("Terremark Enterprise Cloud v2.8 API")
         .version("0.8b-ext2.8")
         .defaultEndpoint("https://services.enterprisecloud.terremark.com/api")
         .documentation(URI.create("http://support.theenterprisecloud.com/kb/default.asp?id=533&Lang=1&SID="))
         .defaultProperties(TerremarkECloudApiMetadata.defaultProperties())
         .defaultModules(ImmutableSet.<Class<? extends Module>>of(TerremarkECloudRestClientModule.class, TerremarkECloudComputeServiceContextModule.class));
      }

      @Override
      public TerremarkECloudApiMetadata build() {
         return new TerremarkECloudApiMetadata(this);
      }
      
      @Override
      protected Builder self() {
         return this;
      }
   }
}
