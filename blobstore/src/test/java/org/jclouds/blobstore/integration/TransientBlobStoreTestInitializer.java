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

package org.jclouds.blobstore.integration;

import java.io.IOException;

import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.BlobStoreContextFactory;
import org.jclouds.blobstore.integration.internal.BaseTestInitializer;

import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
public class TransientBlobStoreTestInitializer extends BaseTestInitializer {

   @Override
   protected BlobStoreContext createLiveContext(Module configurationModule, String url, String app,
            String identity, String key) throws IOException {
      return createStubContext();
   }

   @Override
   protected BlobStoreContext createStubContext() throws IOException {
      return new BlobStoreContextFactory().createContext("transient", "foo", "bar");
   }

}