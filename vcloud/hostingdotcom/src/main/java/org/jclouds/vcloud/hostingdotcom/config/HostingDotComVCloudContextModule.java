package org.jclouds.vcloud.hostingdotcom.config;

import java.net.URI;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeService;
import org.jclouds.lifecycle.Closer;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.RestContextImpl;
import org.jclouds.vcloud.endpoints.Org;
import org.jclouds.vcloud.hostingdotcom.HostingDotComVCloudAsyncClient;
import org.jclouds.vcloud.hostingdotcom.HostingDotComVCloudClient;
import org.jclouds.vcloud.hostingdotcom.compute.HostingDotComVCloudComputeService;
import org.jclouds.vcloud.reference.VCloudConstants;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * @author Adrian Cole
 */
public class HostingDotComVCloudContextModule extends AbstractModule {
   @Override
   protected void configure() {
      bind(ComputeService.class).to(HostingDotComVCloudComputeService.class);
   }

   @Provides
   @Singleton
   RestContext<HostingDotComVCloudAsyncClient, HostingDotComVCloudClient> provideContext(Closer closer,
            HostingDotComVCloudAsyncClient asynchApi, HostingDotComVCloudClient defaultApi,
            @Org URI endPoint, @Named(VCloudConstants.PROPERTY_VCLOUD_USER) String account) {
      return new RestContextImpl<HostingDotComVCloudAsyncClient, HostingDotComVCloudClient>(closer,
               asynchApi, defaultApi, endPoint, account);
   }

}