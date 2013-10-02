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
package org.jclouds.providers.config;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Properties;
import java.util.Set;

import javax.inject.Singleton;

import org.jclouds.Context;
import org.jclouds.domain.Credentials;
import org.jclouds.location.Iso3166;
import org.jclouds.location.Provider;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.rest.annotations.Api;
import org.jclouds.rest.annotations.ApiVersion;
import org.jclouds.rest.annotations.BuildVersion;

import com.google.common.base.Supplier;
import com.google.common.reflect.TypeToken;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

/**
 * Binds data inside {@link ProviderMetadata} to types with scopes qualified with annotations in the
 * {@code org.jclouds.location} and {@code org.jclouds.rest.annotations} packages. It also binds the
 * properties specified in {@link ProviderMetadata#getDefaultProperties()} {@link ProviderMetadata}
 * explicitly. Finally, it bind the context type so that it can be looked up later.
 * 
 * 
 * @author Adrian Cole
 */
public class BindProviderMetadataContextAndCredentials extends AbstractModule {

   private final ProviderMetadata providerMetadata;
   private final Supplier<Credentials> creds;

   public BindProviderMetadataContextAndCredentials(ProviderMetadata providerMetadata, Supplier<Credentials> creds) {
      this.providerMetadata = checkNotNull(providerMetadata, "providerMetadata");
      this.creds = checkNotNull(creds, "creds");
   }

   @Override
   protected void configure() {
      bind(ProviderMetadata.class).toInstance(providerMetadata);
      Properties toBind = new Properties();
      toBind.putAll(providerMetadata.getApiMetadata().getDefaultProperties());
      toBind.putAll(providerMetadata.getDefaultProperties());
      Names.bindProperties(binder(), toBind);
      bind(new TypeLiteral<Supplier<Credentials>>(){}).annotatedWith(Provider.class).toInstance(creds);
      bindConstant().annotatedWith(Provider.class).to(providerMetadata.getId());
      bind(new TypeLiteral<Set<String>>() {
      }).annotatedWith(Iso3166.class).toInstance(providerMetadata.getIso3166Codes());
      bindConstant().annotatedWith(Api.class).to(providerMetadata.getApiMetadata().getId());
      bindConstant().annotatedWith(ApiVersion.class).to(providerMetadata.getApiMetadata().getVersion());
      // nullable
      bind(String.class).annotatedWith(BuildVersion.class).toProvider(
               com.google.inject.util.Providers.of(providerMetadata.getApiMetadata().getBuildVersion().orNull()));
      bind(new TypeLiteral<TypeToken<? extends Context>>() {
      }).annotatedWith(Provider.class).toInstance(providerMetadata.getApiMetadata().getContext());
   }

   @Provides
   @Provider
   @Singleton
   protected Context backend(Injector i, @Provider TypeToken<? extends Context> backendType) {
      return Context.class.cast(i.getInstance(Key.get(TypeLiteral.get(backendType.getType()))));
   }

}
