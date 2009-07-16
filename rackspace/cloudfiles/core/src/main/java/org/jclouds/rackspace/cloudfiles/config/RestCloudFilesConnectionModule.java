package org.jclouds.rackspace.cloudfiles.config;

import java.net.URI;

import org.jclouds.cloud.ConfiguresCloudConnection;
import org.jclouds.http.RequiresHttp;
import org.jclouds.rackspace.Storage;
import org.jclouds.rackspace.cloudfiles.CloudFilesConnection;
import org.jclouds.rackspace.cloudfiles.CloudFilesContext;
import org.jclouds.rackspace.cloudfiles.internal.GuiceCloudFilesContext;
import org.jclouds.rest.RestClientFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

/**
 * Configures the Cloud Files connection, including logging and http transport.
 * 
 * @author Adrian Cole
 */
@ConfiguresCloudConnection
@RequiresHttp
public class RestCloudFilesConnectionModule extends AbstractModule {

   @Override
   protected void configure() {
      bind(CloudFilesContext.class).to(GuiceCloudFilesContext.class);
   }
   
   @Provides
   @Singleton
   protected CloudFilesConnection provideConnection(@Storage URI authenticationUri,
            RestClientFactory factory) {
      return factory.create(authenticationUri, CloudFilesConnection.class);
   }

}