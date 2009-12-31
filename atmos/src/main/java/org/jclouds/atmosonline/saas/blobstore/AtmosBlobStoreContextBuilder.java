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
package org.jclouds.atmosonline.saas.blobstore;

import static org.jclouds.atmosonline.saas.reference.AtmosStorageConstants.PROPERTY_EMCSAAS_RETRY;
import static org.jclouds.atmosonline.saas.reference.AtmosStorageConstants.PROPERTY_EMCSAAS_TIMEOUT;
import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_BLOBSTORE_RETRY;
import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX;

import java.util.List;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;

import org.jclouds.atmosonline.saas.AtmosStorageAsyncClient;
import org.jclouds.atmosonline.saas.AtmosStorageClient;
import org.jclouds.atmosonline.saas.blobstore.config.AtmosBlobStoreContextModule;
import org.jclouds.atmosonline.saas.config.AtmosStorageRestClientModule;
import org.jclouds.blobstore.BlobStoreContextBuilder;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.logging.jdk.config.JDKLoggingModule;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Creates {@link AtmosBlobStoreContext} or {@link Injector} instances based on the most commonly
 * requested arguments.
 * <p/>
 * Note that Threadsafe objects will be bound as singletons to the Injector or Context provided.
 * <p/>
 * <p/>
 * If no <code>Module</code>s are specified, the default {@link JDKLoggingModule logging} and
 * {@link JavaUrlHttpCommandExecutorServiceModule http transports} will be installed.
 * 
 * @author Adrian Cole, Andrew Newdigate
 * @see AtmosBlobStoreContext
 */
public class AtmosBlobStoreContextBuilder extends
         BlobStoreContextBuilder<AtmosStorageAsyncClient, AtmosStorageClient> {

   public AtmosBlobStoreContextBuilder(Properties props) {
      super(new TypeLiteral<AtmosStorageAsyncClient>() {
      }, new TypeLiteral<AtmosStorageClient>() {
      }, convert(props));
   }

   private static Properties convert(Properties props) {
      for (Entry<String, String> entry : ImmutableMap.of(PROPERTY_EMCSAAS_RETRY,
               PROPERTY_BLOBSTORE_RETRY, PROPERTY_EMCSAAS_TIMEOUT, PROPERTY_USER_METADATA_PREFIX)
               .entrySet()) {
         if (props.containsKey(entry.getKey()))
            props.setProperty(entry.getValue(), props.getProperty(entry.getKey()));
      }
      return props;
   }

   @Override
   public AtmosBlobStoreContextBuilder withExecutorService(ExecutorService service) {
      return (AtmosBlobStoreContextBuilder) super.withExecutorService(service);
   }

   @Override
   public AtmosBlobStoreContextBuilder withModules(Module... modules) {
      return (AtmosBlobStoreContextBuilder) super.withModules(modules);
   }

   @Override
   protected void addContextModule(List<Module> modules) {
      modules.add(new AtmosBlobStoreContextModule());
   }

   @Override
   protected void addClientModule(List<Module> modules) {
      modules.add(new AtmosStorageRestClientModule());
   }
}
