package org.jclouds.rackspace.cloudfiles.config;

import java.net.MalformedURLException;
import java.net.URI;

import org.jclouds.cloud.ConfiguresCloudConnection;
import org.jclouds.http.HttpConstants;
import org.jclouds.http.RequiresHttp;
import org.jclouds.rackspace.Authentication;
import org.jclouds.rackspace.CDN;
import org.jclouds.rackspace.RackSpaceAuthentication;
import org.jclouds.rackspace.Storage;
import org.jclouds.rackspace.RackSpaceAuthentication.AuthenticationResponse;
import org.jclouds.rackspace.cloudfiles.CloudFilesConnection;
import org.jclouds.rackspace.cloudfiles.CloudFilesContext;
import org.jclouds.rackspace.cloudfiles.internal.GuiceCloudFilesContext;
import org.jclouds.rackspace.cloudfiles.reference.CloudFilesConstants;
import org.jclouds.rest.RestClientFactory;
import org.jclouds.rest.config.JaxrsModule;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * Configures the S3 connection, including logging and http transport.
 * 
 * @author Adrian Cole
 */
@ConfiguresCloudConnection
@RequiresHttp
public class RestCloudFilesConnectionModule extends AbstractModule {

   @Override
   protected void configure() {
      install(new JaxrsModule());
      bind(CloudFilesContext.class).to(GuiceCloudFilesContext.class);
      bindErrorHandlers();
      bindRetryHandlers();
   }

   @Provides
   @Singleton
   protected AuthenticationResponse provideAuthenticationResponse(
            @Authentication URI authenticationUri, RestClientFactory factory,
            @Named(CloudFilesConstants.PROPERTY_RACKSPACE_USER) String user,
            @Named(CloudFilesConstants.PROPERTY_RACKSPACE_KEY) String key) {
      return factory.create(authenticationUri, RackSpaceAuthentication.class).authenticate(user,
               key);
   }

   @Provides
   @Authentication
   protected String provideAuthenticationToken(@Authentication URI authenticationUri,
            RestClientFactory factory,
            @Named(CloudFilesConstants.PROPERTY_RACKSPACE_USER) String user,
            @Named(CloudFilesConstants.PROPERTY_RACKSPACE_KEY) String key) {
      return factory.create(authenticationUri, RackSpaceAuthentication.class).authenticate(user,
               key).getAuthToken();
   }

   @Provides
   @Singleton
   @Storage
   protected URI provideStorageUrl(AuthenticationResponse response) {
      return response.getStorageUrl();
   }

   @Provides
   @Singleton
   @CDN
   protected URI provideCDNUrl(AuthenticationResponse response) {
      return response.getCDNManagementUrl();
   }

   protected void bindErrorHandlers() {
      // TODO
   }

   protected void bindRetryHandlers() {
      // TODO retry on 401 by AuthenticateRequest.update()
   }

   @Singleton
   @Provides
   @Authentication
   protected URI provideAddress(@Named(HttpConstants.PROPERTY_HTTP_ADDRESS) String address,
            @Named(HttpConstants.PROPERTY_HTTP_PORT) int port,
            @Named(HttpConstants.PROPERTY_HTTP_SECURE) boolean isSecure)
            throws MalformedURLException {

      return URI.create(String.format("%1$s://%2$s:%3$s", isSecure ? "https" : "http", address,
               port));
   }

   @Provides
   @Singleton
   protected CloudFilesConnection provideConnection(@Storage URI authenticationUri,
            RestClientFactory factory) {
      return factory.create(authenticationUri, CloudFilesConnection.class);
   }

}