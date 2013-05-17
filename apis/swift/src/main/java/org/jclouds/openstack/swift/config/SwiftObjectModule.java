/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.openstack.swift.config;

import javax.inject.Inject;
import javax.inject.Provider;

import org.jclouds.blobstore.config.BlobStoreObjectModule;
import org.jclouds.openstack.swift.domain.MutableObjectInfoWithMetadata;
import org.jclouds.openstack.swift.domain.SwiftObject;
import org.jclouds.openstack.swift.domain.internal.SwiftObjectImpl;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;

/**
 * Configures the domain object mappings needed for all CF implementations
 * 
 * @author Adrian Cole
 */
public class SwiftObjectModule extends AbstractModule {

   /**
    * explicit factories are created here as it has been shown that Assisted Inject is extremely
    * inefficient. http://code.google.com/p/google-guice/issues/detail?id=435
    */
   @Override
   protected void configure() {
      // for converters to work.
      install(new BlobStoreObjectModule());
      bind(SwiftObject.Factory.class).to(SwiftObjectFactory.class).in(Scopes.SINGLETON);
   }

   private static class SwiftObjectFactory implements SwiftObject.Factory {
      @Inject
      Provider<MutableObjectInfoWithMetadata> metadataProvider;

      public SwiftObject create(MutableObjectInfoWithMetadata metadata) {
         return new SwiftObjectImpl(metadata != null ? metadata : metadataProvider.get());
      }
   }

   @Provides
   SwiftObject provideSwiftObject(SwiftObject.Factory factory) {
      return factory.create(null);
   }

}
