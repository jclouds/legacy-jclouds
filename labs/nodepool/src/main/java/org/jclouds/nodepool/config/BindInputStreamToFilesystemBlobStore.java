/*
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.nodepool.config;

import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.filesystem.reference.FilesystemConstants;
import org.jclouds.lifecycle.Closer;
import org.jclouds.nodepool.Backend;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.inject.Exposed;
import com.google.inject.Module;
import com.google.inject.Provides;

public class BindInputStreamToFilesystemBlobStore extends BindJcloudsModules {

   @Override
   protected void configure() {
   }

   @Provides
   @Singleton
   @Exposed
   protected Supplier<Map<String, InputStream>> provideInputStreamMapFromBlobStore(Supplier<BlobStoreContext> in,
         @Named(NodePoolProperties.METADATA_CONTAINER) final String container) {
      return Suppliers.memoize(Suppliers.compose(new Function<BlobStoreContext, Map<String, InputStream>>() {

         @Override
         public Map<String, InputStream> apply(BlobStoreContext input) {
            input.getBlobStore().createContainerInLocation(null, container);
            return input.createInputStreamMap(container);
         }

      }, in));
   }

   @Provides
   @Singleton
   protected Supplier<BlobStoreContext> makeBlobStoreContext(@Named(NodePoolProperties.BASEDIR) final String basedir,
         @Backend final Set<Module> modules, final Closer closer) {
      final Properties overrides = new Properties();
      overrides.setProperty(FilesystemConstants.PROPERTY_BASEDIR, basedir);
      return Suppliers.memoize(new Supplier<BlobStoreContext>() {

         @Override
         public BlobStoreContext get() {
            // GAE alert!
            new File(basedir).mkdirs();
            BlobStoreContext returnVal = ContextBuilder.newBuilder("filesystem")
                                                       .overrides(overrides)
                                                       .modules(modules) 
                                                       .buildView(BlobStoreContext.class);
            closer.addToClose(returnVal);
            return returnVal;
         }
         
      });
   }
}
