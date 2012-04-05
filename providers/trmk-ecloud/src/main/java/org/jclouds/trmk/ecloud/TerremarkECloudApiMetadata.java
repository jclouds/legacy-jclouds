package org.jclouds.trmk.ecloud;

import static org.jclouds.trmk.vcloud_0_8.reference.TerremarkConstants.PROPERTY_TERREMARK_EXTENSION_NAME;
import static org.jclouds.trmk.vcloud_0_8.reference.TerremarkConstants.PROPERTY_TERREMARK_EXTENSION_VERSION;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.trmk.vcloud_0_8.internal.TerremarkVCloudApiMetadata;

import com.google.common.reflect.TypeToken;

/**
 * Implementation of {@link ApiMetadata} for Terremark eCloud v2.8 API
 * 
 * @author Adrian Cole
 */
public class TerremarkECloudApiMetadata extends
      TerremarkVCloudApiMetadata<TerremarkECloudClient, TerremarkECloudAsyncClient, TerremarkECloudApiMetadata> {

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

   protected static Properties defaultProperties() {
      Properties properties = TerremarkVCloudApiMetadata.defaultProperties();
      properties.setProperty(PROPERTY_TERREMARK_EXTENSION_NAME, "eCloudExtensions");
      properties.setProperty(PROPERTY_TERREMARK_EXTENSION_VERSION, "2.8");
      return properties;
   }

   public static class Builder
         extends
         TerremarkVCloudApiMetadata.Builder<TerremarkECloudClient, TerremarkECloudAsyncClient, TerremarkECloudApiMetadata> {

      protected Builder() {
         super(TerremarkECloudClient.class, TerremarkECloudAsyncClient.class);
         id("trmk-ecloud")
         .name("Terremark Enterprise Cloud v2.8 API")
         .version("0.8b-ext2.8")
         .defaultProperties(TerremarkECloudApiMetadata.defaultProperties())
         .defaultEndpoint("https://services.enterprisecloud.terremark.com/api")
         .contextBuilder(TypeToken.of(TerremarkECloudContextBuilder.class))
         .documentation(URI.create("http://support.theenterprisecloud.com/kb/default.asp?id=533&Lang=1&SID="));
      }

      @Override
      public TerremarkECloudApiMetadata build() {
         return new TerremarkECloudApiMetadata(this);
      }

      @Override
      public Builder fromApiMetadata(TerremarkECloudApiMetadata in) {
         super.fromApiMetadata(in);
         return this;
      }
   }

}