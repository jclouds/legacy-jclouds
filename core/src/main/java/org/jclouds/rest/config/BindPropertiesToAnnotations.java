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
package org.jclouds.rest.config;

import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.not;
import static com.google.common.base.Splitter.on;
import static com.google.common.collect.Iterables.filter;
import static org.jclouds.Constants.PROPERTY_API;
import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.Constants.PROPERTY_BUILD_VERSION;
import static org.jclouds.Constants.PROPERTY_CREDENTIAL;
import static org.jclouds.Constants.PROPERTY_IDENTITY;
import static org.jclouds.Constants.PROPERTY_ISO3166_CODES;
import static org.jclouds.Constants.PROPERTY_PROVIDER;
import static org.jclouds.Constants.PROPERTY_TIMEOUTS_PREFIX;

import java.util.Map;
import java.util.Set;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.config.ValueOfConfigurationKeyOrNull;
import org.jclouds.domain.Credentials;
import org.jclouds.internal.FilterStringsBoundToInjectorByName;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.Iso3166;
import org.jclouds.location.Provider;
import org.jclouds.rest.annotations.Api;
import org.jclouds.rest.annotations.ApiVersion;
import org.jclouds.rest.annotations.BuildVersion;
import org.jclouds.rest.annotations.Credential;
import org.jclouds.rest.annotations.Identity;
import org.jclouds.util.Maps2;
import org.nnsoft.guice.rocoto.configuration.ConfigurationModule;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

public class BindPropertiesToAnnotations extends ConfigurationModule {

   @Provides
   @Singleton
   @Named("TIMEOUTS")
   protected Map<String, Long> timeouts(Function<Predicate<String>, Map<String, String>> filterStringsBoundByName) {
      Map<String, String> stringBoundWithTimeoutPrefix = filterStringsBoundByName.apply(new Predicate<String>() {

         @Override
         public boolean apply(String input) {
            return input.startsWith(PROPERTY_TIMEOUTS_PREFIX);
         }

      });

      Map<String, Long> longsByName = Maps.transformValues(stringBoundWithTimeoutPrefix, new Function<String, Long>() {

         @Override
         public Long apply(String input) {
            return Long.valueOf(String.valueOf(input));
         }

      });
      return Maps2.transformKeys(longsByName, new Function<String, String>() {

         @Override
         public String apply(String input) {
            return input.replaceFirst(PROPERTY_TIMEOUTS_PREFIX, "");
         }

      });

   }

   @Provides
   @Singleton
   @Provider
   protected String bindProvider(@Named(PROPERTY_PROVIDER) String in){
      return in;
   }
   
   @Provides
   @Singleton
   @Iso3166
   protected Set<String> bindIsoCodes(@Named(PROPERTY_ISO3166_CODES) String in){
      return  ImmutableSet.copyOf(filter(on(',').split( in),
            not(equalTo(""))));
   }
   
   @Provides
   @Singleton
   @Api
   protected String bindApi(@Named(PROPERTY_API) String in){
      return in;
   }
   
   @Provides
   @Singleton
   @ApiVersion
   protected String bindApiVersion(@Named(PROPERTY_API_VERSION) String in){
      return in;
   }
   
   @Provides
   @Singleton
   @BuildVersion
   protected String bindBuildVersion(@Named(PROPERTY_BUILD_VERSION) String in){
      return in;
   }
   
   @Provides
   @Singleton
   @Identity
   protected String bindIdentity(@Named(PROPERTY_IDENTITY) String in){
      return in;
   }
   
   @Provides
   @Singleton
   @Credential
   @Nullable
   protected String bindCredential(ValueOfConfigurationKeyOrNull config){
      return config.apply(PROPERTY_CREDENTIAL);
   }
   
   @Provides
   @Singleton
   @Provider
   protected Credentials bindProviderCredentials(@Identity String identity, @Nullable @Credential String credential){
      return new Credentials(identity, credential);
   }

   @Override
   protected void bindConfigurations() {
      bind(new TypeLiteral<Function<Predicate<String>, Map<String, String>>>() {
      }).to(FilterStringsBoundToInjectorByName.class);
   }
  
}