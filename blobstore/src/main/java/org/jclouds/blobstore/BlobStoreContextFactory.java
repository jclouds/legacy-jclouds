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
package org.jclouds.blobstore;

import java.io.IOException;
import java.util.Properties;

import javax.inject.Inject;

import org.jclouds.rest.RestContextFactory;

/**
 * Helper class to instantiate {@code BlobStoreContext} instances.
 * 
 * @author Adrian Cole
 */
public class BlobStoreContextFactory extends
         RestContextFactory<BlobStoreContext, BlobStoreContextBuilder<?, ?>> {

   /**
    * Initializes with the default properties built-in to jclouds. This is typically stored in the
    * classpath resource {@code blobstore.properties}
    * 
    * @throws IOException
    *            if the default properties file cannot be loaded
    * @see #init
    */
   public BlobStoreContextFactory() throws IOException {
      super("blobstore.properties");
   }

   /**
    * 
    * Initializes the {@code BlobStoreContext) definitions from the specified properties.
    */
   @Inject
   public BlobStoreContextFactory(Properties properties) {
      super(properties);
   }

   @Override
   protected BlobStoreContext build(BlobStoreContextBuilder<?, ?> contextBuilder) {
      return contextBuilder.buildBlobStoreContext();
   }
}
