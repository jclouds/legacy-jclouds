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
package org.jclouds.providers;

import java.io.Closeable;
import java.net.URI;
import java.util.Properties;
import java.util.Set;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Optional;

/**
 * The ProviderMetadata interface allows jclouds to provide a plugin framework
 * for gathering cloud provider metadata.
 * 
 * @author Jeremy Whitlock <jwhitlock@apache.org>, Adrian Cole
 */
public interface ProviderMetadata<S, A, C extends Closeable, M extends ApiMetadata<S, A, C, M>> {
  
   /**
    * 
    * @author Adrian Cole
    * @since 1.5
    */
   public static interface Builder<S, A, C extends Closeable, M extends ApiMetadata<S, A, C, M>> {
      /**
       * @see ProviderMetadata#getId()
       */
      Builder<S, A, C, M> id(String id);

      /**
       * @see ProviderMetadata#getName()
       */
      Builder<S, A, C, M> name(String name);

      /**
       * @see ProviderMetadata#getApiMetadata()
       */
      Builder<S, A, C, M> apiMetadata(M api);

      /**
       * @see ProviderMetadata#getEndpoint()
       */
      Builder<S, A, C, M> endpoint(String endpoint);

      /**
       * @see ProviderMetadata#getDefaultProperties()
       */
      Builder<S, A, C, M> defaultProperties(Properties defaultProperties);

      /**
       * @see ProviderMetadata#getConsole()
       */
      Builder<S, A, C, M> console(@Nullable URI console);

      /**
       * @see ProviderMetadata#getHomepage()
       */
      Builder<S, A, C, M> homepage(@Nullable URI homepage);

      /**
       * @see ProviderMetadata#getLinkedServices()
       */
      Builder<S, A, C, M> linkedServices(Iterable<String> linkedServices);

      /**
       * @see ProviderMetadata#getLinkedServices()
       */
      Builder<S, A, C, M> linkedServices(String... linkedServices);

      /**
       * @see ProviderMetadata#getLinkedServices()
       */
      Builder<S, A, C, M> linkedService(String linkedService);

      /**
       * @see ProviderMetadata#getIso3166Code()
       */
      Builder<S, A, C, M> iso3166Codes(Iterable<String> iso3166Codes);

      /**
       * @see ProviderMetadata#getIso3166Code()
       */
      Builder<S, A, C, M> iso3166Codes(String... iso3166Codes);

      /**
       * @see ProviderMetadata#getIso3166Code()
       */
      Builder<S, A, C, M> iso3166Code(String iso3166Code);

      ProviderMetadata<S, A, C, M> build();

      Builder<S, A, C, M> fromProviderMetadata(ProviderMetadata<S, A, C, M> in);

   }

   /**
    * @see Builder
    * @since 1.5
    */
   Builder<S, A, C, M> toBuilder();

   /**
    * 
    * @return the provider's unique identifier (ex. aws-ec2, trystack-nova)
    */
   public String getId();

   /**
    * 
    * @return the name (display name) of the provider (ex. GoGrid)
    */
   public String getName();

   /**
    * 
    * @return the provider's api
    * @since 1.5
    */
   public M getApiMetadata();

   /**
    * @see ApiMetadata#getEndpoint
    * @return the url for the provider's api
    */
   public String getEndpoint();

   /**
    * Configuration Properties used when creating connections to this provider.
    * For example, location information, or default networking configuration.
    * 
    * @return properties used to create connections to this provider
    * @see ApiMetadata#getDefaultProperties
    */
   Properties getDefaultProperties();

   /**
    * 
    * @return the url for the provider's console, or absent if one doesn't exist
    */
   Optional<URI> getConsole();

   /**
    * 
    * @return the url for the provider's homepage, or absent if unknown
    */
   Optional<URI> getHomepage();

   /**
    * 
    * @return ids of all known {@link ProviderMetadata providers} which have the
    *         same account as this.
    */
   Set<String> getLinkedServices();

   /**
    * iso 3166 codes; ex. US-CA,US
    * 
    * @return all known region/location ISO 3166 codes
    */
   Set<String> getIso3166Codes();
}