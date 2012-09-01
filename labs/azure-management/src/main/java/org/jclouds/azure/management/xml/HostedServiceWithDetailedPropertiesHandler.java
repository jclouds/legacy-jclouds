package org.jclouds.azure.management.xml;

import javax.inject.Inject;

import org.jclouds.azure.management.domain.HostedServiceWithDetailedProperties;

/**
 * 
 * @author Adrian Cole
 */
public class HostedServiceWithDetailedPropertiesHandler extends HostedServiceHandler {

   @Inject
   protected HostedServiceWithDetailedPropertiesHandler(
            DetailedHostedServicePropertiesHandler hostedServicePropertiesHandler) {
      super(hostedServicePropertiesHandler);
   }

   @Override
   protected HostedServiceWithDetailedProperties.Builder<?> builder() {
      return HostedServiceWithDetailedProperties.builder();
   }

   @Override
   public HostedServiceWithDetailedProperties getResult() {
      try {
         return HostedServiceWithDetailedProperties.class.cast(builder.build());
      } finally {
         builder = builder();
      }
   }
}
