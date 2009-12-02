package org.jclouds.vcloud.hostingdotcom.config;

import java.net.URI;

import javax.inject.Singleton;

import org.jclouds.concurrent.internal.SyncProxy;
import org.jclouds.http.RequiresHttp;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestClientFactory;
import org.jclouds.vcloud.VCloudAsyncClient;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.config.VCloudRestClientModule;
import org.jclouds.vcloud.hostingdotcom.HostingDotComVCloudAsyncClient;
import org.jclouds.vcloud.hostingdotcom.HostingDotComVCloudClient;

import com.google.inject.Provides;

/**
 * Configures the VCloud authentication service connection, including logging and http transport.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
@ConfiguresRestClient
public class HostingDotComVCloudRestClientModule extends VCloudRestClientModule {
   @Provides
   @Singleton
   protected HostingDotComVCloudAsyncClient provideHostingDotComVCloudAsyncClient(VCloudAsyncClient in) {
      return (HostingDotComVCloudAsyncClient) in;
   }

   @Override
   protected VCloudAsyncClient provideAsyncClient(RestClientFactory factory) {
      return factory.create(HostingDotComVCloudAsyncClient.class);
   }

   @Provides
   @Singleton
   protected HostingDotComVCloudClient provideHostingDotComVCloudClient(VCloudClient in) {
      return (HostingDotComVCloudClient) in;
   }

   @Override
   public VCloudClient provideClient(VCloudAsyncClient client) throws IllegalArgumentException,
            SecurityException, NoSuchMethodException {
      return SyncProxy.create(HostingDotComVCloudClient.class, client);
   }
   @Override
   protected URI provideDefaultNetwork(VCloudAsyncClient client) {
      return URI.create("https://vcloud.safesecureweb.com/network/1990");
   }

}
