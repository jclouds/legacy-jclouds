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

import java.util.Properties;

import org.jclouds.rest.RestContextBuilder;

import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * @author Adrian Cole
 */
public abstract class BlobStoreContextBuilder<A, S> extends RestContextBuilder<A, S> {

   @Override
   public BlobStoreContextBuilder<A, S> withModules(Module... modules) {
      return (BlobStoreContextBuilder<A, S>) super.withModules(modules);
   }

   public BlobStoreContextBuilder(String providerName,TypeLiteral<A> asyncClientType, TypeLiteral<S> syncClientType) {
      this(providerName, asyncClientType, syncClientType, new Properties());
   }

   public BlobStoreContextBuilder(String providerName,TypeLiteral<A> asyncClientType, TypeLiteral<S> syncClientType,
            Properties properties) {
      super(providerName, asyncClientType, syncClientType, properties);

   }

   public BlobStoreContext buildBlobStoreContext() {
      return buildInjector().getInstance(BlobStoreContext.class);
   }
}