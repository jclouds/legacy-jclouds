/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.aws.config;

import java.net.URI;
import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.aws.config.ProvidersViaAPI.ProvidesZoneAndRegionClientModule;
import org.jclouds.http.RequiresHttp;
import org.jclouds.location.Region;
import org.jclouds.location.Zone;
import org.jclouds.rest.ConfiguresRestClient;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
@RequiresHttp
public class WithZonesFormSigningRestClientModule<S, A> extends FormSigningRestClientModule<S, A> {

   public WithZonesFormSigningRestClientModule(Class<S> syncClientType, Class<A> asyncClientType,
         Map<Class<?>, Class<?>> delegates) {
      super(syncClientType, asyncClientType, delegates);
   }

   public WithZonesFormSigningRestClientModule(Class<S> syncClientType, Class<A> asyncClientType) {
      super(syncClientType, asyncClientType);
   }

   protected void bindZonesToProvider() {
      install(new ProvidesZoneAndRegionClientModule());
      bindZonesToProvider(ProvidersViaAPI.RegionIdToZoneId.class);
   }

   protected void bindZonesToProvider(Class<? extends javax.inject.Provider<Map<String, String>>> providerClass) {
      bind(new TypeLiteral<Map<String, String>>() {
      }).annotatedWith(Zone.class).toProvider(providerClass).in(Scopes.SINGLETON);
   }

   @Override
   protected void configure() {
      super.configure();
      bindZonesToProvider();
   }

   @Provides
   @Singleton
   @Zone
   protected Map<String, URI> provideZones(@Region final Map<String, URI> regionToEndpoint,
         @Zone Map<String, String> availabilityZoneToRegion) {
      return Maps.transformValues(availabilityZoneToRegion, new Function<String, URI>() {

         @Override
         public URI apply(String from) {

            return regionToEndpoint.get(from);
         }

      });
   }

}