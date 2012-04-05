package org.jclouds.rest;
import static com.google.common.base.Preconditions.checkArgument;

import java.util.NoSuchElementException;

import org.jclouds.providers.ProviderMetadata;
import org.jclouds.rest.internal.ContextBuilder;

/**
 * @author Adrian Cole
 */
public class RestContextBuilder<S, A, C extends RestContext<S, A>, M extends RestApiMetadata<S, A, C, M>>
      extends ContextBuilder<S, A, C, M> {
   
   /**
    * looks up a provider or api with the given id
    * 
    * @param providerOrApi
    *           id of the provider or api
    * @return means to build a context to that provider
    * @throws NoSuchElementException
    *            if the id was not configured.
    * @throws IllegalArgumentException
    *            if the api or provider isn't assignable from RestContext
    */
   public static RestContextBuilder<?, ?, ?, ?> newBuilder(String providerOrApi) throws NoSuchElementException {
      ContextBuilder<?, ?, ?, ?> builder = ContextBuilder.newBuilder(providerOrApi);
      checkArgument(builder instanceof RestContextBuilder,
            "type of providerOrApi[%s] is not RestContextBuilder: %s", providerOrApi, builder);
      return RestContextBuilder.class.cast(builder);
   }

   public RestContextBuilder(ProviderMetadata<S, A, C, M> providerMetadata) {
      super(providerMetadata);
   }

   public RestContextBuilder(M apiMetadata) {
      super(apiMetadata);
   }
}