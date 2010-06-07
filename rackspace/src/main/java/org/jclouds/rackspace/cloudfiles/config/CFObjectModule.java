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
package org.jclouds.rackspace.cloudfiles.config;

import javax.inject.Inject;
import javax.inject.Provider;

import org.jclouds.blobstore.config.BlobStoreObjectModule;
import org.jclouds.encryption.EncryptionService;
import org.jclouds.rackspace.cloudfiles.domain.CFObject;
import org.jclouds.rackspace.cloudfiles.domain.MutableObjectInfoWithMetadata;
import org.jclouds.rackspace.cloudfiles.domain.internal.CFObjectImpl;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;

/**
 * Configures the domain object mappings needed for all CF implementations
 * 
 * @author Adrian Cole
 */
public class CFObjectModule extends AbstractModule {

   /**
    * explicit factories are created here as it has been shown that Assisted Inject is extremely
    * inefficient. http://code.google.com/p/google-guice/issues/detail?id=435
    */
   @Override
   protected void configure() {
      // for converters to work.
      install(new BlobStoreObjectModule());
      bind(CFObject.Factory.class).to(CFObjectFactory.class).in(Scopes.SINGLETON);
   }

   private static class CFObjectFactory implements CFObject.Factory {
      @Inject
      EncryptionService encryptionService;
      @Inject
      Provider<MutableObjectInfoWithMetadata> metadataProvider;

      public CFObject create(MutableObjectInfoWithMetadata metadata) {
         return new CFObjectImpl(encryptionService, metadata != null ? metadata : metadataProvider
                  .get());
      }
   }

   @Provides
   CFObject provideCFObject(CFObject.Factory factory) {
      return factory.create(null);
   }

}