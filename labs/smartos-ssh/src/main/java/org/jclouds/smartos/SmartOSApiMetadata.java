package org.jclouds.smartos;

import java.net.URI;

import org.jclouds.apis.internal.BaseApiMetadata;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.smartos.compute.config.SmartOSComputeServiceContextModule;
import org.jclouds.smartos.compute.config.SmartOSParserModule;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for SmartOS
 * 
 * @author Nigel Magnay
 */
public class SmartOSApiMetadata extends BaseApiMetadata {

   /** The serialVersionUID */
   private static final long serialVersionUID = 3606170564482119304L;

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return Builder.class.cast(builder().fromApiMetadata(this));
   }

   public SmartOSApiMetadata() {
      super(builder());
   }

   protected SmartOSApiMetadata(Builder builder) {
      super(builder);
   }

   public static class Builder extends BaseApiMetadata.Builder {

      protected Builder() {
         id("smartos-ssh")
         .name("SmartOS SSH API")
         .identityName("Username")
         .defaultIdentity("root")
         .defaultCredential("smartos")
         .defaultEndpoint("http://localhost")
         .documentation(URI.create("http://http://wiki.smartos.org/display/DOC/How+to+create+a+Virtual+Machine+in+SmartOS"))
         .view(ComputeServiceContext.class)
         .defaultModules(ImmutableSet.<Class<? extends Module>>builder()
                                     .add(SmartOSComputeServiceContextModule.class)
                                     .add(SmartOSParserModule.class).build());
      }

      @Override
      public SmartOSApiMetadata build() {
         return new SmartOSApiMetadata(this);
      }

   }
}
