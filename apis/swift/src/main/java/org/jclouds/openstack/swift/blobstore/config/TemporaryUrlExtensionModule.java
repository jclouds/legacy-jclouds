/**
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
package org.jclouds.openstack.swift.blobstore.config;

import static org.jclouds.rest.config.BinderUtils.bindClientAndAsyncClient;

import org.jclouds.blobstore.BlobRequestSigner;
import org.jclouds.date.TimeStamp;
import org.jclouds.openstack.swift.CommonSwiftAsyncClient;
import org.jclouds.openstack.swift.SwiftAsyncClient;
import org.jclouds.openstack.swift.SwiftKeystoneAsyncClient;
import org.jclouds.openstack.swift.TemporaryUrlKey;
import org.jclouds.openstack.swift.blobstore.SwiftBlobSigner;
import org.jclouds.openstack.swift.extensions.KeystoneTemporaryUrlKeyAsyncApi;
import org.jclouds.openstack.swift.extensions.TemporaryUrlKeyApi;
import org.jclouds.openstack.swift.extensions.TemporaryUrlKeyAsyncApi;
import org.jclouds.openstack.swift.suppliers.ReturnOrFetchTemporaryUrlKey;

import com.google.common.base.Supplier;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * Isolates dependencies needed for {@link SwiftBlobSigner}
 *
 * @author Adrian Cole
 *
 */
public abstract class TemporaryUrlExtensionModule<A extends CommonSwiftAsyncClient> extends AbstractModule {

   public static class SwiftTemporaryUrlExtensionModule extends TemporaryUrlExtensionModule<SwiftAsyncClient> {

      @Override
      protected void bindRequestSigner() {
         bind(BlobRequestSigner.class).to(new TypeLiteral<SwiftBlobSigner<SwiftAsyncClient>>() {
         });
      }

   }

   /**
    * Ensures keystone auth is used instead of swift auth
    *
    */
   public static class SwiftKeystoneTemporaryUrlExtensionModule extends
         TemporaryUrlExtensionModule<SwiftKeystoneAsyncClient> {

      protected void bindTemporaryUrlKeyApi() {
         bindClientAndAsyncClient(binder(), TemporaryUrlKeyApi.class, KeystoneTemporaryUrlKeyAsyncApi.class);
      }

      @Override
      protected void bindRequestSigner() {
         bind(BlobRequestSigner.class).to(new TypeLiteral<SwiftBlobSigner<SwiftKeystoneAsyncClient>>() {
         });
      }

   }

   @Provides
   @TimeStamp
   protected Long unixEpochTimestampProvider() {
      return System.currentTimeMillis() / 1000; /* convert to seconds */
   }

   @Override
   protected void configure() {
      bindRequestSigner();
      bindTemporaryUrlKeyApi();
      bind(new TypeLiteral<Supplier<String>>() {
      }).annotatedWith(TemporaryUrlKey.class).to(ReturnOrFetchTemporaryUrlKey.class);
   }

   protected abstract void bindRequestSigner();

   protected void bindTemporaryUrlKeyApi() {
      bindClientAndAsyncClient(binder(), TemporaryUrlKeyApi.class, TemporaryUrlKeyAsyncApi.class);
   }

}
