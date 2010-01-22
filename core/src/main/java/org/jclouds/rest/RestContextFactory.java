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

import javax.annotation.Nullable;
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
public abstract class RestContextFactory<T, B extends RestContextBuilder<?, ?>> {
   private final static Properties NO_PROPERTIES = new Properties();
   private final Properties properties;

   /**
    * Initializes with the default properties built-in to jclouds. This is typically stored in the
    * classpath resource {@code filename}
    * 
    * @param filename
    *           name of the properties file to initialize from
    * @throws IOException
    *            if the default properties file cannot be loaded
    * @see #getPropertiesFromResource
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
      properties.putAll(System.getProperties());
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

   public T createContext(String hint, Properties overrides) {
      return createContext(hint, null, null, ImmutableSet.<Module> of(), overrides);
   }

   public T createContext(String hint, Iterable<? extends Module> modules, Properties overrides) {
      return createContext(hint, null, null, modules, overrides);
   }

   public T createContext(String hint, @Nullable String account, @Nullable String key,
            Iterable<? extends Module> modules) {
      return createContext(hint, account, key, modules, new Properties());
   }

   /**
    * Creates a new remote context.
    * 
    * @param hint
    * @param account
    *           nullable, if credentials are present in the overrides
    * @param key
    *           nullable, if credentials are present in the overrides
    * @param modules
    *           Configuration you'd like to pass to the context. Ex. ImmutableSet.<Module>of(new
    *           ExecutorServiceModule(myexecutor))
    * @param overrides
    *           properties to override defaults with.
    * @return initialized context ready for use
    */
   @SuppressWarnings("unchecked")
   public T createContext(String hint, @Nullable String account, @Nullable String key,
            Iterable<? extends Module> modules, Properties overrides) {
      checkNotNull(hint, "hint");
      checkNotNull(modules, "modules");
      checkNotNull(overrides, "overrides");
      String propertiesBuilderKey = String.format("%s.propertiesbuilder", hint);
      String propertiesBuilderClassName = checkNotNull(
               properties.getProperty(propertiesBuilderKey), hint + " service not supported");

      String contextBuilderKey = String.format("%s.contextbuilder", hint);
      String contextBuilderClassName = checkNotNull(properties.getProperty(contextBuilderKey),
               contextBuilderKey);

      String endpointKey = String.format("%s.endpoint", hint);
      String endpoint = properties.getProperty(endpointKey);
      try {
         Class<HttpPropertiesBuilder> propertiesBuilderClass = (Class<HttpPropertiesBuilder>) Class
                  .forName(propertiesBuilderClassName);
         Class<B> contextBuilderClass = (Class<B>) Class.forName(contextBuilderClassName);
         HttpPropertiesBuilder builder = propertiesBuilderClass.getConstructor(Properties.class)
                  .newInstance(overrides);
         if (key != null)
            builder.withCredentials(account, key);
         if (endpoint != null)
            builder.withEndpoint(URI.create(endpoint));
         B contextBuilder = (B) contextBuilderClass.getConstructor(Properties.class).newInstance(
                  builder.build()).withModules(Iterables.toArray(modules, Module.class));
         return build(contextBuilder);
      } catch (Exception e) {
         throw new RuntimeException("error instantiating " + contextBuilderClassName, e);
      }
   }

   protected abstract T build(B contextBuilder);
}
