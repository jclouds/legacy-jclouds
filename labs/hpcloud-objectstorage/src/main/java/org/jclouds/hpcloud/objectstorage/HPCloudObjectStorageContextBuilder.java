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
package org.jclouds.hpcloud.objectstorage;

import java.util.List;
import java.util.Properties;

import org.jclouds.blobstore.BlobStoreContextBuilder;
import org.jclouds.hpcloud.objectstorage.blobstore.config.HPCloudObjectStorageBlobStoreContextModule;
import org.jclouds.hpcloud.objectstorage.config.HPCloudObjectStorageRestClientModule;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.logging.jdk.config.JDKLoggingModule;

import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Creates {@link HPCloudObjectStorageBlobStore} or {@link Injector} instances based on the most
 * commonly requested arguments.
 * <p/>
 * Note that Threadsafe objects will be bound as singletons to the Injector or Context provided.
 * <p/>
 * <p/>
 * If no <code>Module</code>s are specified, the default {@link JDKLoggingModule logging} and
 * {@link JavaUrlHttpCommandExecutorServiceModule http transports} will be installed.
 * 
 * @author Adrian Cole, Andrew Newdigate
 * @see HPCloudObjectStorageBlobStore
 */
public class HPCloudObjectStorageContextBuilder extends BlobStoreContextBuilder<HPCloudObjectStorageClient, HPCloudObjectStorageAsyncClient> {

   public HPCloudObjectStorageContextBuilder(Properties props) {
      super(HPCloudObjectStorageClient.class, HPCloudObjectStorageAsyncClient.class, props);
   }

   @Override
   protected void addContextModule(List<Module> modules) {
      modules.add(new HPCloudObjectStorageBlobStoreContextModule());
   }

   @Override
   protected void addClientModule(List<Module> modules) {
      modules.add(new HPCloudObjectStorageRestClientModule());
   }
}
