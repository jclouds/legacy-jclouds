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
package org.jclouds.aws.s3.blobstore;

import static org.jclouds.aws.s3.reference.S3Constants.PROPERTY_S3_METADATA_PREFIX;
import static org.jclouds.aws.s3.reference.S3Constants.PROPERTY_S3_RETRY;
import static org.jclouds.aws.s3.reference.S3Constants.PROPERTY_S3_TIMEOUT;
import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_BLOBSTORE_RETRY;
import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX;

import java.util.List;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;

import org.jclouds.aws.s3.S3AsyncClient;
import org.jclouds.aws.s3.S3Client;
import org.jclouds.aws.s3.blobstore.config.S3BlobStoreContextModule;
import org.jclouds.aws.s3.config.S3RestClientModule;
import org.jclouds.blobstore.BlobStoreContextBuilder;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.logging.jdk.config.JDKLoggingModule;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Creates {@link S3BlobStoreContext} or {@link Injector} instances based on the most commonly
 * requested arguments.
 * <p/>
 * Note that Threadsafe objects will be bound as singletons to the Injector or Context provided.
 * <p/>
 * <p/>
 * If no <code>Module</code>s are specified, the default {@link JDKLoggingModule logging} and
 * {@link JavaUrlHttpCommandExecutorServiceModule http transports} will be installed.
 * 
 * @author Adrian Cole, Andrew Newdigate
 * @see S3BlobStoreContext
 */
public class S3BlobStoreContextBuilder extends BlobStoreContextBuilder<S3AsyncClient, S3Client> {

   public S3BlobStoreContextBuilder(Properties props) {
      super(new TypeLiteral<S3AsyncClient>() {
      }, new TypeLiteral<S3Client>() {
      }, convert(props));
   }

   private static Properties convert(Properties props) {
      for (Entry<String, String> entry : ImmutableMap.of(PROPERTY_S3_METADATA_PREFIX,
               PROPERTY_USER_METADATA_PREFIX, PROPERTY_S3_RETRY, PROPERTY_BLOBSTORE_RETRY,
               PROPERTY_S3_TIMEOUT, PROPERTY_USER_METADATA_PREFIX).entrySet()) {
         if (props.containsKey(entry.getKey()))
            props.setProperty(entry.getValue(), props.getProperty(entry.getKey()));
      }
      return props;
   }

   @Override
   public S3BlobStoreContextBuilder withExecutorService(ExecutorService service) {
      return (S3BlobStoreContextBuilder) super.withExecutorService(service);
   }

   @Override
   public S3BlobStoreContextBuilder withModules(Module... modules) {
      return (S3BlobStoreContextBuilder) super.withModules(modules);
   }

   @Override
   protected void addContextModule(List<Module> modules) {
      modules.add(new S3BlobStoreContextModule());
   }

   @Override
   protected void addClientModule(List<Module> modules) {
      modules.add(new S3RestClientModule());
   }
}
