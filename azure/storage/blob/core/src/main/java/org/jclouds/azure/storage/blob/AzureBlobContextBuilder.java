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
package org.jclouds.azure.storage.blob;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

import org.jclouds.azure.storage.blob.config.AzureBlobContextModule;
import org.jclouds.azure.storage.blob.config.AzureBlobRestClientModule;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.logging.jdk.config.JDKLoggingModule;
import org.jclouds.rest.RestContextBuilder;

import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Creates {@link AzureBlobContext} or {@link Injector} instances based on the most commonly
 * requested arguments.
 * <p/>
 * Note that Threadsafe objects will be bound as singletons to the Injector or Context provided.
 * <p/>
 * <p/>
 * If no <code>Module</code>s are specified, the default {@link JDKLoggingModule logging} and
 * {@link JavaUrlHttpCommandExecutorServiceModule http transports} will be installed.
 * 
 * @author Adrian Cole
 * @see AzureBlobClient
 */
public class AzureBlobContextBuilder extends RestContextBuilder<AzureBlobClient> {
   private static final TypeLiteral<AzureBlobClient> connectionType = new TypeLiteral<AzureBlobClient>() {
   };

   public AzureBlobContextBuilder(Properties properties) {
      super(connectionType, properties);
   }

   @Override
   protected void addContextModule(List<Module> modules) {
      modules.add(new AzureBlobContextModule());
   }

   @Override
   protected void addClientModule(List<Module> modules) {
      modules.add(new AzureBlobRestClientModule());
   }

   // below is to cast the builder to the correct type so that chained builder methods end correctly

   @Override
   public AzureBlobContextBuilder withExecutorService(ExecutorService service) {
      return (AzureBlobContextBuilder) super.withExecutorService(service);
   }

   @Override
   public AzureBlobContextBuilder withModules(Module... modules) {
      return (AzureBlobContextBuilder) super.withModules(modules);
   }

}
