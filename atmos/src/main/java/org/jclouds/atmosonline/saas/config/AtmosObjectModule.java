/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.atmosonline.saas.config;

import javax.inject.Inject;
import javax.inject.Provider;

import org.jclouds.atmosonline.saas.domain.AtmosObject;
import org.jclouds.atmosonline.saas.domain.MutableContentMetadata;
import org.jclouds.atmosonline.saas.domain.SystemMetadata;
import org.jclouds.atmosonline.saas.domain.UserMetadata;
import org.jclouds.atmosonline.saas.domain.internal.AtmosObjectImpl;
import org.jclouds.blobstore.config.BlobStoreObjectModule;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;

/**
 * Configures the domain object mappings needed for all Atmos implementations
 * 
 * @author Adrian Cole
 */
public class AtmosObjectModule extends AbstractModule {

   /**
    * explicit factories are created here as it has been shown that Assisted Inject is extremely
    * inefficient. http://code.google.com/p/google-guice/issues/detail?id=435
    */
   @Override
   protected void configure() {
      // for converters
      install(new BlobStoreObjectModule());
      bind(AtmosObject.Factory.class).to(AtmosObjectFactory.class).in(Scopes.SINGLETON);
   }

   private static class AtmosObjectFactory implements AtmosObject.Factory {

      @Inject
      Provider<MutableContentMetadata> metadataProvider;

      public AtmosObject create(MutableContentMetadata contentMetadata) {
         return new AtmosObjectImpl(contentMetadata != null ? contentMetadata : metadataProvider
                  .get());
      }

      public AtmosObject create(SystemMetadata systemMetadata, UserMetadata userMetadata) {
         return new AtmosObjectImpl(metadataProvider.get(), systemMetadata, userMetadata);
      }

      public AtmosObject create(MutableContentMetadata contentMetadata,
               SystemMetadata systemMetadata, UserMetadata userMetadata) {
         return new AtmosObjectImpl(contentMetadata, systemMetadata, userMetadata);
      }
   }

   @Provides
   AtmosObject provideAtmosObject(AtmosObject.Factory factory) {
      return factory.create((MutableContentMetadata) null);
   }

}