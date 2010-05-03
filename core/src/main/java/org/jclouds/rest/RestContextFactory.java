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

import org.jclouds.PropertiesBuilder;
import org.jclouds.domain.Credentials;
import org.jclouds.util.Utils;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.io.Resources;
import com.google.inject.Module;
import com.google.inject.ProvisionException;

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
   public static Properties getPropertiesFromResource(String filename) throws IOException {
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

   public T createContext(URI contextUri, Iterable<? extends Module> modules, Properties overrides) {
      return createContext(contextUri, Credentials.parse(contextUri), modules, overrides);
   }

   public T createContext(URI contextUri) {
      return createContext(contextUri, ImmutableSet.<Module> of(), NO_PROPERTIES);
   }

   public T createContext(URI contextUri, Credentials creds, Iterable<? extends Module> modules,
            Properties overrides) {
      return createContext(checkNotNull(contextUri.getHost(), "host"), checkNotNull(creds.account,
               "account"), checkNotNull(creds.key, "key"), checkNotNull(modules, "modules"),
               checkNotNull(overrides, "overrides"));
   }

   public T createContext(URI contextUri, Credentials creds) {
      return createContext(contextUri, creds, ImmutableSet.<Module> of(), NO_PROPERTIES);
   }

   public T createContext(String provider, String account, String key) {
      return createContext(provider, account, key, ImmutableSet.<Module> of(), NO_PROPERTIES);
   }

   public T createContext(String provider, Properties overrides) {
      return createContext(provider, null, null, ImmutableSet.<Module> of(), overrides);
   }

   public T createContext(String provider, Iterable<? extends Module> modules, Properties overrides) {
      return createContext(provider, null, null, modules, overrides);
   }

   public T createContext(String provider, @Nullable String account, @Nullable String key,
            Iterable<? extends Module> modules) {
      return createContext(provider, account, key, modules, new Properties());
   }

   /**
    * Creates a new remote context.
    * 
    * @param provider
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
   public T createContext(String provider, @Nullable String account, @Nullable String key,
            Iterable<? extends Module> modules, Properties overrides) {
      checkNotNull(provider, "provider");
      checkNotNull(modules, "modules");
      checkNotNull(overrides, "overrides");
      String propertiesBuilderKey = String.format("%s.propertiesbuilder", provider);
      String propertiesBuilderClassName = checkNotNull(
               properties.getProperty(propertiesBuilderKey), provider + " service not supported");

      String contextBuilderKey = String.format("%s.contextbuilder", provider);
      String contextBuilderClassName = checkNotNull(properties.getProperty(contextBuilderKey),
               contextBuilderKey);

      String endpointKey = String.format("%s.endpoint", provider);
      String endpoint = properties.getProperty(endpointKey);
      try {
         Class<PropertiesBuilder> propertiesBuilderClass = (Class<PropertiesBuilder>) Class
                  .forName(propertiesBuilderClassName);
         Class<B> contextBuilderClass = (Class<B>) Class.forName(contextBuilderClassName);
         PropertiesBuilder builder = propertiesBuilderClass.getConstructor(Properties.class)
                  .newInstance(overrides);
         if (key != null)
            builder.withCredentials(account, key);
         if (endpoint != null)
            builder.withEndpoint(URI.create(endpoint));
         B contextBuilder = (B) contextBuilderClass.getConstructor(String.class, Properties.class)
                  .newInstance(provider, builder.build()).withModules(
                           Iterables.toArray(modules, Module.class));
         return build(contextBuilder);
      } catch (ProvisionException e) {
         Throwable throwable = Utils.firstRootCauseOrOriginalException(e);
         Throwables.propagate(throwable);
         assert false : "exception should have propogated " + e;
         return null;
      } catch (Exception e) {
         Throwables.propagate(Throwables.getRootCause(e));
         assert false : "exception should have propogated " + e;
         return null;
      }
   }

   /**
    * Hook so that you can specify how to create an object using the contextBuilder produced with
    * this factory.
    */
   protected abstract T build(B contextBuilder);
}
