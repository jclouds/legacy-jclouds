/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.deltacloud.config;

import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.deltacloud.DeltacloudAsyncClient;
import org.jclouds.deltacloud.DeltacloudClient;
import org.jclouds.deltacloud.collections.DeltacloudCollection;
import org.jclouds.deltacloud.collections.Images;
import org.jclouds.deltacloud.collections.Instances;
import org.jclouds.deltacloud.collections.Realms;
import org.jclouds.deltacloud.handlers.DeltacloudErrorHandler;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;
import org.jclouds.rest.suppliers.RetryOnTimeOutButNotOnAuthorizationExceptionSupplier;

import com.google.common.base.Supplier;
import com.google.inject.Provides;

/**
 * Configures the deltacloud connection.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
@ConfiguresRestClient
public class DeltacloudRestClientModule extends RestClientModule<DeltacloudClient, DeltacloudAsyncClient> {

   public DeltacloudRestClientModule() {
      super(DeltacloudClient.class, DeltacloudAsyncClient.class);
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(DeltacloudErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(DeltacloudErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(DeltacloudErrorHandler.class);
   }

   @Override
   protected void bindRetryHandlers() {
      // TODO
   }

   protected AtomicReference<AuthorizationException> authException = new AtomicReference<AuthorizationException>();

   @Provides
   @Singleton
   protected Supplier<Map<DeltacloudCollection, URI>> provideCollections(
         @Named(PROPERTY_SESSION_INTERVAL) long seconds, final DeltacloudClient client) {
      return new RetryOnTimeOutButNotOnAuthorizationExceptionSupplier<Map<DeltacloudCollection, URI>>(authException,
            seconds, new Supplier<Map<DeltacloudCollection, URI>>() {
               @Override
               public Map<DeltacloudCollection, URI> get() {
                  return client.getCollections();
               }
            });
   }

   /**
    * since the supplier is memoized, and there are no objects created here, this doesn't need to be
    * singleton.
    */
   @Provides
   @Images
   protected URI provideImageCollection(Supplier<Map<DeltacloudCollection, URI>> collectionSupplier) {
      return collectionSupplier.get().get(DeltacloudCollection.IMAGES);
   }

   /**
    * since the supplier is memoized, and there are no objects created here, this doesn't need to be
    * singleton.
    */
   @Provides
   @Instances
   protected URI provideInstanceCollection(Supplier<Map<DeltacloudCollection, URI>> collectionSupplier) {
      return collectionSupplier.get().get(DeltacloudCollection.INSTANCES);
   }

   /**
    * since the supplier is memoized, and there are no objects created here, this doesn't need to be
    * singleton.
    */
   @Provides
   @Realms
   protected URI provideRealmCollection(Supplier<Map<DeltacloudCollection, URI>> collectionSupplier) {
      return collectionSupplier.get().get(DeltacloudCollection.REALMS);
   }
}
