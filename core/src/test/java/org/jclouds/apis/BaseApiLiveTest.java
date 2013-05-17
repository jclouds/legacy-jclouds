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
package org.jclouds.apis;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.io.Closeables.closeQuietly;
import static org.jclouds.Constants.PROPERTY_RELAX_HOSTNAME;
import static org.jclouds.Constants.PROPERTY_TRUST_ALL_CERTS;

import java.io.Closeable;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.logging.Logger;

import org.jclouds.ContextBuilder;
import org.jclouds.logging.LoggingModules;
import org.jclouds.logging.config.LoggingModule;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.Providers;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
public abstract class BaseApiLiveTest<A extends Closeable> {
   protected String prefix = System.getProperty("user.name");
   protected String provider;

   protected volatile A api;

   protected String identity;
   protected String credential;
   protected String endpoint;
   protected String apiVersion;

   protected Properties setupProperties() {
      Properties overrides = new Properties();
      overrides.setProperty(PROPERTY_TRUST_ALL_CERTS, "true");
      overrides.setProperty(PROPERTY_RELAX_HOSTNAME, "true");
      identity = setIfTestSystemPropertyPresent(overrides,  provider + ".identity");
      credential = setIfTestSystemPropertyPresent(overrides,  provider + ".credential");
      endpoint = setIfTestSystemPropertyPresent(overrides,  provider + ".endpoint");
      apiVersion = setIfTestSystemPropertyPresent(overrides,  provider + ".api-version");
      setIfTestSystemPropertyPresent(overrides,  provider + ".build-version");
      return overrides;
   }

   protected String setIfTestSystemPropertyPresent(Properties overrides, String key) {
      if (System.getProperties().containsKey("test." + key)) {
         String val = System.getProperty("test." + key);
         overrides.setProperty(key, val);
         return val;
      }
      return null;
   }

   @BeforeClass(groups = { "integration", "live" })
   public void setup() {
      initialize();
   }

   protected void initialize() {
      closeQuietly(api);
      api = create(setupProperties(), setupModules());
   }

   protected Iterable<Module> setupModules() {
      return ImmutableSet.<Module> of(getLoggingModule());
   }

   protected LoggingModule getLoggingModule() {
      return LoggingModules.firstOrJDKLoggingModule();
   }

   /**
    * @see org.jclouds.providers.Providers#withId
    */
   protected ProviderMetadata createProviderMetadata() {
      try {
         return Providers.withId(provider);
      } catch (NoSuchElementException e) {
         return null;
      }
   }
   
   /**
    * @see org.jclouds.apis.Apis#withId
    */
   protected ApiMetadata createApiMetadata() {
      try {
         return Apis.withId(provider);
      } catch (NoSuchElementException e) {
         return null;
      }
   }

   @SuppressWarnings("serial")
   protected A create(Properties props, Iterable<Module> modules) {
      return newBuilder().modules(modules).overrides(props).buildApi(new TypeToken<A>(getClass()) {
      });
   }

   protected ContextBuilder newBuilder() {
      if (provider != null)
         try {
            return ContextBuilder.newBuilder(provider);
         } catch (NoSuchElementException e){
            Logger.getAnonymousLogger()
                  .warning("provider ["
                        + provider
                        + "] is not setup as META-INF/services/org.jclouds.apis.ApiMetadata or META-INF/services/org.jclouds.providers.ProviderMetadata");
         }

      ProviderMetadata pm = createProviderMetadata();

      ContextBuilder builder = pm != null ? ContextBuilder.newBuilder(pm) : ContextBuilder
            .newBuilder(ApiMetadata.class.cast(checkNotNull(createApiMetadata(),
                  "either createApiMetadata or createProviderMetadata must be overridden")));
      return builder;
   }
   
   @AfterClass(groups = { "integration", "live" })
   protected void tearDown() {
      closeQuietly(api);
   }
}
