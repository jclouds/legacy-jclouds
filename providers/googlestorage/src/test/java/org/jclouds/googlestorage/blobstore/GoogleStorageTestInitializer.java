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

package org.jclouds.googlestorage.blobstore;

import java.io.IOException;

import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.BlobStoreContextFactory;
import org.jclouds.blobstore.integration.TransientBlobStoreTestInitializer;
import org.jclouds.blobstore.integration.internal.BaseBlobStoreIntegrationTest;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
public class GoogleStorageTestInitializer extends TransientBlobStoreTestInitializer {

   public GoogleStorageTestInitializer() {
      provider = "googlestorage";
      BaseBlobStoreIntegrationTest.SANITY_CHECK_RETURNED_BUCKET_NAME = true;
   }

   @Override
   protected BlobStoreContext createLiveContext(Module configurationModule, String endpoint, String apiversion,
            String app, String identity, String credential) throws IOException {
      return new BlobStoreContextFactory().createContext(provider, ImmutableSet.of(configurationModule,
               new Log4JLoggingModule()), setupProperties(endpoint, apiversion, identity, credential));
   }

}
