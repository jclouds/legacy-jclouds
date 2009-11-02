package org.jclouds.atmosonline.saas.config;

import java.net.URI;

import org.jclouds.atmosonline.saas.AtmosStorage;
import org.jclouds.atmosonline.saas.AtmosStorageClient;
import org.jclouds.atmosonline.saas.internal.StubAtmosStorageClient;
import org.jclouds.blobstore.integration.config.StubBlobStoreModule;
import org.jclouds.rest.ConfiguresRestClient;

import com.google.inject.AbstractModule;

/**
 * adds a stub alternative to invoking AtmosStorage
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
public class AtmosStorageStubClientModule extends AbstractModule {

   protected void configure() {
      install(new StubBlobStoreModule());
      bind(AtmosStorageClient.class).to(StubAtmosStorageClient.class).asEagerSingleton();
      bind(URI.class).annotatedWith(AtmosStorage.class).toInstance(URI.create("https://localhost/azurestub"));
   }
}