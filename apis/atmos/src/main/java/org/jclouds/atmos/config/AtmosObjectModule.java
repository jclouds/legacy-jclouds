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
package org.jclouds.atmos.config;

import org.jclouds.atmos.domain.AtmosObject;
import org.jclouds.atmos.domain.MutableContentMetadata;
import org.jclouds.blobstore.config.BlobStoreObjectModule;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

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
   }

   @Provides
   AtmosObject provideAtmosObject(AtmosObject.Factory factory) {
      return factory.create((MutableContentMetadata) null);
   }

}
