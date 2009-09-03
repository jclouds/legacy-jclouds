package org.jclouds.azure.storage.config;

import java.net.MalformedURLException;
import java.net.URI;

import javax.annotation.Resource;

import org.jclouds.azure.storage.filters.SharedKeyAuthentication;
import org.jclouds.azure.storage.handlers.ParseAzureStorageErrorFromXmlContent;
import org.jclouds.cloud.ConfiguresCloudConnection;
import org.jclouds.http.HttpConstants;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.logging.Logger;
import org.jclouds.rest.config.JaxrsModule;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * Configures the AzureStorage connection, including logging and http transport.
 * 
 * @author Adrian Cole
 */
@ConfiguresCloudConnection
@RequiresHttp
public class RestAzureStorageConnectionModule extends AbstractModule {
   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   @Named(HttpConstants.PROPERTY_HTTP_ADDRESS)
   String address;
   @Inject
   @Named(HttpConstants.PROPERTY_HTTP_PORT)
   int port;
   @Inject
   @Named(HttpConstants.PROPERTY_HTTP_SECURE)
   boolean isSecure;

   @Override
   protected void configure() {
      install(new JaxrsModule());
      bind(SharedKeyAuthentication.class).in(Scopes.SINGLETON);
      bindErrorHandlers();
      bindRetryHandlers();
      requestInjection(this);
      logger
               .info("AzureStorage Context = %s://%s:%s", (isSecure ? "https" : "http"), address,
                        port);
   }

   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(
               ParseAzureStorageErrorFromXmlContent.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(
               ParseAzureStorageErrorFromXmlContent.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(
               ParseAzureStorageErrorFromXmlContent.class);
   }

   protected void bindRetryHandlers() {
   }

   @Singleton
   @Provides
   protected URI provideAddress(@Named(HttpConstants.PROPERTY_HTTP_ADDRESS) String address,
            @Named(HttpConstants.PROPERTY_HTTP_PORT) int port,
            @Named(HttpConstants.PROPERTY_HTTP_SECURE) boolean isSecure)
            throws MalformedURLException {

      return URI.create(String.format("%1$s://%2$s:%3$s", isSecure ? "https" : "http", address,
               port));
   }

}