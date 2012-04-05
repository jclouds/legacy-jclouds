package org.jclouds.trmk.vcloud_0_8.internal;

import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.trmk.vcloud_0_8.reference.TerremarkConstants.PROPERTY_TERREMARK_EXTENSION_NAME;
import static org.jclouds.trmk.vcloud_0_8.reference.TerremarkConstants.PROPERTY_TERREMARK_EXTENSION_NS;
import static org.jclouds.trmk.vcloud_0_8.reference.TerremarkConstants.PROPERTY_TERREMARK_EXTENSION_VERSION;
import static org.jclouds.trmk.vcloud_0_8.reference.VCloudConstants.PROPERTY_VCLOUD_DEFAULT_FENCEMODE;
import static org.jclouds.trmk.vcloud_0_8.reference.VCloudConstants.PROPERTY_VCLOUD_TIMEOUT_TASK_COMPLETED;
import static org.jclouds.trmk.vcloud_0_8.reference.VCloudConstants.PROPERTY_VCLOUD_VERSION_SCHEMA;
import static org.jclouds.trmk.vcloud_0_8.reference.VCloudConstants.PROPERTY_VCLOUD_XML_NAMESPACE;
import static org.jclouds.trmk.vcloud_0_8.reference.VCloudConstants.PROPERTY_VCLOUD_XML_SCHEMA;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.internal.BaseComputeServiceApiMetadata;
import org.jclouds.trmk.vcloud_0_8.TerremarkVCloudAsyncClient;
import org.jclouds.trmk.vcloud_0_8.TerremarkVCloudClient;

/**
 * Implementation of {@link ApiMetadata} for Amazon's TerremarkVCloud api.
 * 
 * <h3>note</h3>
 * <p/>
 * This class allows overriding of types {@code S}(client) and {@code A}
 * (asyncClient), so that children can add additional methods not declared here,
 * such as new features from AWS.
 * <p/>
 * 
 * As this is a popular api, we also allow overrides for type {@code C}
 * (context). This allows subtypes to add in new feature groups or extensions,
 * not present in the base api. For example, you could make a subtype for
 * context, that exposes admin operations.
 * 
 * @author Adrian Cole
 */
public abstract class TerremarkVCloudApiMetadata<S extends TerremarkVCloudClient, A extends TerremarkVCloudAsyncClient, M extends TerremarkVCloudApiMetadata<S, A, M>>
      extends BaseComputeServiceApiMetadata<S, A, ComputeServiceContext<S, A>, M> {

   protected TerremarkVCloudApiMetadata(Builder<?, ?, ?> builder) {
      super(builder);
   }

   protected static Properties defaultProperties() {
      Properties properties = BaseComputeServiceApiMetadata.Builder.defaultProperties();
      properties.setProperty(PROPERTY_VCLOUD_VERSION_SCHEMA, "0.8");
      properties.setProperty(PROPERTY_SESSION_INTERVAL, 8 * 60 + "");
      properties.setProperty(PROPERTY_VCLOUD_XML_SCHEMA, "http://vcloud.safesecureweb.com/ns/vcloud.xsd");
      properties.setProperty(PROPERTY_VCLOUD_DEFAULT_FENCEMODE, "allowInOut");
      properties.setProperty(PROPERTY_TERREMARK_EXTENSION_NS, String.format("urn:tmrk:${%s}-${%s}",
            PROPERTY_TERREMARK_EXTENSION_NAME, PROPERTY_TERREMARK_EXTENSION_VERSION));
      properties.setProperty(PROPERTY_VCLOUD_XML_NAMESPACE,
            String.format("http://www.vmware.com/vcloud/v${%s}", PROPERTY_VCLOUD_VERSION_SCHEMA));
      properties.setProperty("jclouds.dns_name_length_min", "1");
      properties.setProperty("jclouds.dns_name_length_max", "15");
      // terremark can sometimes block extremely long times
      properties.setProperty(PROPERTY_VCLOUD_TIMEOUT_TASK_COMPLETED, TimeUnit.MINUTES.toMillis(20) + "");
      return properties;
   }

   public static abstract class Builder<S extends TerremarkVCloudClient, A extends TerremarkVCloudAsyncClient, M extends TerremarkVCloudApiMetadata<S, A, M>>
         extends BaseComputeServiceApiMetadata.Builder<S, A, ComputeServiceContext<S, A>, M> {

      protected Builder(Class<S> syncClient, Class<A> asyncClient) {
         id("vcloud-common")
         .identityName("Email")
         .credentialName("Password")
         .version("0.8")
         .defaultProperties(TerremarkVCloudApiMetadata.defaultProperties())
         .javaApi(syncClient, asyncClient);
      }

      @Override
      public Builder<S, A, M> fromApiMetadata(M in) {
         super.fromApiMetadata(in);
         return this;
      }

   }

}