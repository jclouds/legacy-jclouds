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

package org.jclouds.blobstore;

import java.util.Properties;

import org.jclouds.rest.RestContextBuilder;

import com.google.inject.Module;

/**
 * @author Adrian Cole
 */
public abstract class BlobStoreContextBuilder<S, A> extends RestContextBuilder<S, A> {

   @Override
   public BlobStoreContextBuilder<S, A> withModules(Iterable<Module> modules) {
      return (BlobStoreContextBuilder<S, A>) super.withModules(modules);
   }

   public BlobStoreContextBuilder(Class<S> syncClientType, Class<A> asyncClientType) {
      this(syncClientType, asyncClientType, new Properties());
   }

   public BlobStoreContextBuilder(Class<S> syncClientType, Class<A> asyncClientType, Properties properties) {
      super(syncClientType, asyncClientType, properties);

   }

   public BlobStoreContext buildBlobStoreContext() {
      return buildInjector().getInstance(BlobStoreContext.class);
   }
}