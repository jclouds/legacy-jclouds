package org.jclouds.oauth.v2.config;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.reflect.TypeToken;
import com.google.inject.Provides;
import org.jclouds.oauth.v2.OAuthApi;
import org.jclouds.oauth.v2.OAuthAsyncApi;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;

import javax.inject.Singleton;
import java.net.URI;

/**
 * OAuth module to when accessing OAuth stand-alone.
 *
 * @author David Alves
 */
@ConfiguresRestClient
public class OAuthRestClientModule extends RestClientModule<OAuthApi, OAuthAsyncApi> {

   public OAuthRestClientModule() {
      super(TypeToken.class.cast(TypeToken.of(OAuthApi.class)), TypeToken.class.cast(TypeToken.of(OAuthAsyncApi
              .class)));
   }

   @Provides
   @Singleton
   @Authentication
   protected Supplier<URI> provideAuthenticationEndpoint(ProviderMetadata providerMetadata) {
      return Suppliers.ofInstance(URI.create(providerMetadata.getEndpoint()));
   }

}
