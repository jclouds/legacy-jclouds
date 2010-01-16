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
package org.jclouds.rest;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.net.URI;
import java.util.Properties;

import javax.inject.Inject;

import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpPropertiesBuilder;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.io.Resources;
import com.google.inject.Module;

/**
 * Helper class to instantiate {@code RestContext} instances. "blobstore.properties"
 * 
 * @author Adrian Cole
 */
public class RestContextFactory<T extends RestContext<?, ?>, B extends RestContextBuilder<?, ?>> {
   private final static Properties NO_PROPERTIES = new Properties();
   private final Properties properties;

   /**
    * Initializes with the default properties built-in to jclouds. This is typically stored in the
    * classpath resource {@code filename}
    * 
    * @parma filename name of the properties file to initialize from
    * @throws IOException
    *            if the default properties file cannot be loaded
    * @see #init
    */
   public RestContextFactory(String filename) throws IOException {
      this(getPropertiesFromResource(filename));
   }

   /**
    * Loads the default properties that define the {@code RestContext} objects. <h3>properties file
    * format</h3>
    * 
    * Two properties are needed per context:
    * <ul>
    * <li>tag.contextbuilder=classname extends RestContextBuilder</li>
    * <li>tag.propertiesbuilder=classname extends HttpPropertiesBuilder</li>
    * </ul>
    * Ex.
    * 
    * <pre>
    * azureblob.contextbuilder=org.jclouds.azure.storage.blob.blobstore.AzureRestContextBuilder
    * azureblob.propertiesbuilder=org.jclouds.azure.storage.blob.AzureBlobPropertiesBuilder
    * </pre>
    * 
    * @param filename
    *           name of file to load from in the resource path
    * @return properties object with these items loaded for each tag
    * @throws IOException
    *            if {@code filename} cannot load.
    */
   static Properties getPropertiesFromResource(String filename) throws IOException {
      Properties properties = new Properties();
      properties.load(Resources.newInputStreamSupplier(Resources.getResource(filename)).getInput());
      return properties;
   }

   /**
    * Initializes the {@code RestContext} definitions from the specified properties.
    */
   @Inject
   public RestContextFactory(Properties properties) {
      this.properties = properties;
   }

   public T createContext(URI blobStore, Iterable<? extends Module> modules, Properties overrides) {
      return createContext(blobStore, Credentials.parse(blobStore), modules, overrides);
   }

   public T createContext(URI blobStore) {
      return createContext(blobStore, ImmutableSet.<Module> of(), NO_PROPERTIES);
   }

   public T createContext(URI blobStore, Credentials creds, Iterable<? extends Module> modules,
            Properties overrides) {
      return createContext(checkNotNull(blobStore.getHost(), "host"), checkNotNull(creds.account,
               "account"), checkNotNull(creds.key, "key"), checkNotNull(modules, "modules"),
               checkNotNull(overrides, "overrides"));
   }

   public T createContext(URI blobStore, Credentials creds) {
      return createContext(blobStore, creds, ImmutableSet.<Module> of(), NO_PROPERTIES);
   }

   public T createContext(String hint, String account, String key) {
      return createContext(hint, account, key, ImmutableSet.<Module> of(), NO_PROPERTIES);
   }

   /**
    * 
    * FIXME Comment this // ImmutableSet.<Module>of(new ExecutorServiceModule(myexecutor))
    * 
    * @param hint
    * @param account
    * @param key
    * @param modules
    * @param overrides
    * @return
    */
   @SuppressWarnings("unchecked")
   public T createContext(String hint, String account, String key,
            Iterable<? extends Module> modules, Properties overrides) {
      checkNotNull(hint, "hint");
      checkNotNull(account, "account");
      checkNotNull(key, "key");
      checkNotNull(modules, "modules");
      checkNotNull(overrides, "overrides");
      String propertiesBuilderKey = String.format("%s.propertiesbuilder", hint);
      String propertiesBuilderClassName = checkNotNull(
               properties.getProperty(propertiesBuilderKey), hint + " service not supported");

      String contextBuilderKey = String.format("%s.contextbuilder", hint);
      String contextBuilderClassName = checkNotNull(properties.getProperty(contextBuilderKey),
               contextBuilderKey);

      try {
         Class<HttpPropertiesBuilder> propertiesBuilderClass = (Class<HttpPropertiesBuilder>) Class
                  .forName(propertiesBuilderClassName);
         Class<B> contextBuilderClass = (Class<B>) Class.forName(contextBuilderClassName);
         HttpPropertiesBuilder builder = propertiesBuilderClass.getConstructor(Properties.class)
                  .newInstance(overrides).withCredentials(account, key);
         return (T) contextBuilderClass.getConstructor(Properties.class).newInstance(
                  builder.build()).withModules(Iterables.toArray(modules, Module.class))
                  .buildContext();
      } catch (Exception e) {
         throw new RuntimeException("error instantiating " + contextBuilderClassName, e);
      }
   }
}
