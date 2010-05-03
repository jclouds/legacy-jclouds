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
package org.jclouds.rackspace.cloudfiles;

import java.net.URI;
import java.util.Properties;

import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.logging.jdk.config.JDKLoggingModule;

import com.google.inject.Module;

/**
 * Creates {@link CloudFilesBlobStoreContext} instances based on the most commonly requested
 * arguments.
 * <p/>
 * Note that Threadsafe objects will be bound as singletons to the Injector or Context provided.
 * <p/>
 * <p/>
 * If no <code>Module</code>s are specified, the default {@link JDKLoggingModule logging} and
 * {@link JavaUrlHttpCommandExecutorServiceModule http transports} will be installed.
 * 
 * @author Adrian Cole, Andrew Newdigate
 * @see CloudFilesBlobStoreContext
 */
public class CloudFilesContextFactory {
   public static BlobStoreContext createContext(Properties properties, Module... modules) {
      return new CloudFilesContextBuilder("cloudfiles", new CloudFilesPropertiesBuilder(properties)
               .build()).withModules(modules).buildBlobStoreContext();
   }

   public static BlobStoreContext createContext(String user, String key, Module... modules) {
      return new CloudFilesContextBuilder("cloudfiles", new CloudFilesPropertiesBuilder(user, key)
               .build()).withModules(modules).buildBlobStoreContext();
   }

   public static BlobStoreContext createContext(URI endpoint, String user, String key,
            Module... modules) {
      return new CloudFilesContextBuilder("cloudfiles", new CloudFilesPropertiesBuilder(user, key)
               .withEndpoint(endpoint).build()).withModules(modules).buildBlobStoreContext();
   }
}
