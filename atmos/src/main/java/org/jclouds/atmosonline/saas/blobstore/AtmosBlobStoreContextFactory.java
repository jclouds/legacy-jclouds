/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.atmosonline.saas.blobstore;

import java.net.URI;
import java.util.Properties;

import org.jclouds.atmosonline.saas.AtmosStorageClient;
import org.jclouds.atmosonline.saas.AtmosStoragePropertiesBuilder;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.logging.jdk.config.JDKLoggingModule;

import com.google.inject.Module;

/**
 * Creates {@link AtmosBlobStoreContext} instances based on the most commonly requested arguments.
 * <p/>
 * Note that Threadsafe objects will be bound as singletons to the Injector or Context provided.
 * <p/>
 * <p/>
 * If no <code>Module</code>s are specified, the default {@link JDKLoggingModule logging} and
 * {@link JavaUrlHttpCommandExecutorServiceModule http transports} will be installed.
 * 
 * @author Adrian Cole
 * @see AtmosBlobStoreContext
 */
public class AtmosBlobStoreContextFactory {
   public static BlobStoreContext<AtmosStorageClient> createContext(Properties properties,
            Module... modules) {
      return new AtmosBlobStoreContextBuilder(new AtmosStoragePropertiesBuilder(properties).build())
               .withModules(modules).buildContext();
   }

   public static BlobStoreContext<AtmosStorageClient> createContext(String uid, String key,
            Module... modules) {
      return new AtmosBlobStoreContextBuilder(new AtmosStoragePropertiesBuilder(uid, key).build())
               .withModules(modules).buildContext();
   }

   public static BlobStoreContext<AtmosStorageClient> createContext(URI endpoint, String uid,
            String key, Module... modules) {
      return new AtmosBlobStoreContextBuilder(new AtmosStoragePropertiesBuilder(uid, key)
               .withEndpoint(endpoint).build()).withModules(modules).buildContext();
   }
}
