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
package org.jclouds.providers.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.Constants.PROPERTY_API;
import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.Constants.PROPERTY_BUILD_VERSION;
import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.Constants.PROPERTY_ISO3166_CODES;
import static org.jclouds.Constants.PROPERTY_PROVIDER;

import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.providers.AnonymousProviderMetadata;
import org.jclouds.providers.ProviderMetadata;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;

/**
 * Updates {@link ProviderMetadata} carrying over the input {@code Properties}, filtering out those which are typed fields in {@link ProviderMetadata} or {@link ApiMetadata}
 * 
 * @author Adrian Cole
 */
public class UpdateProviderMetadataFromProperties implements Function<Properties, ProviderMetadata> {
   private final ApiMetadata apiMetadata;
   private final Optional<ProviderMetadata> providerMetadata;

   public UpdateProviderMetadataFromProperties(ProviderMetadata providerMetadata) {
      this(checkNotNull(providerMetadata, "providerMetadata").getApiMetadata(), Optional.of(providerMetadata));
   }

   public UpdateProviderMetadataFromProperties(ApiMetadata apiMetadata) {
      this(checkNotNull(apiMetadata, "apiMetadata"), Optional.<ProviderMetadata> absent());
   }

   public UpdateProviderMetadataFromProperties(ApiMetadata apiMetadata, Optional<ProviderMetadata> providerMetadata) {
      this.apiMetadata = checkNotNull(apiMetadata, "apiMetadata");
      this.providerMetadata = checkNotNull(providerMetadata, "providerMetadata");
   }

   @Override
   public ProviderMetadata apply(Properties input) {
      Properties mutable = new Properties();
      mutable.putAll(input);
      ApiMetadata apiMetadata = this.apiMetadata.toBuilder()
                                    .name(getAndRemove(mutable, PROPERTY_API, this.apiMetadata.getName()))
                                    .version(getAndRemove(mutable, PROPERTY_API_VERSION, this.apiMetadata.getVersion()))
                                    .buildVersion(getAndRemove(mutable, PROPERTY_BUILD_VERSION, this.apiMetadata.getBuildVersion().orNull())).build();

      String endpoint = getAndRemove(mutable, PROPERTY_ENDPOINT, providerMetadata.isPresent() ? providerMetadata.get()
               .getEndpoint() : null);
      
      String providerId = getAndRemove(mutable, PROPERTY_PROVIDER, providerMetadata.isPresent() ? providerMetadata.get()
               .getId() : apiMetadata.getId());
      
      String isoCodes = getAndRemove(mutable, PROPERTY_ISO3166_CODES, providerMetadata.isPresent() ? Joiner.on(',').join(providerMetadata.get()
               .getIso3166Codes()) : "");
      
      ProviderMetadata providerMetadata = this.providerMetadata
               .or(AnonymousProviderMetadata.forApiWithEndpoint(apiMetadata, checkNotNull(endpoint, PROPERTY_ENDPOINT)))
               .toBuilder()
               .apiMetadata(apiMetadata)
               .id(providerId)
               .iso3166Codes(Splitter.on(',').omitEmptyStrings().split(isoCodes))
               .endpoint(endpoint).defaultProperties(mutable).build();

      return providerMetadata;
   }

   private static String getAndRemove(Properties expanded, String key, String defaultVal) {
      try {
         return expanded.getProperty(key, defaultVal);
      } finally {
         expanded.remove(key);
      }
   }

}
