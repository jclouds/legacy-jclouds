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

import java.net.URI;
import java.util.Set;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.javax.annotation.Nullable;

/**
 * The ProviderMetadata interface allows jclouds to provide a plugin framework
 * for gathering cloud provider metadata.
 * 
 * @author Jeremy Whitlock <jwhitlock@apache.org>
 */
public interface ProviderMetadata {
   /**
    * @see ApiMetadata#BLOBSTORE_TYPE
    */
   @Deprecated
   public static final String BLOBSTORE_TYPE = "blobstore";
   /**
    * @see ApiMetadata#COMPUTE_TYPE
    */
   @Deprecated
   public static final String COMPUTE_TYPE = "compute";
   /**
    * @see ApiMetadata#LOADBALANCER_TYPE
    */
   @Deprecated
   public static final String LOADBALANCER_TYPE = "loadbalancer";
   /**
    * @see ApiMetadata#TABLE_TYPE
    */
   @Deprecated
   public static final String TABLE_TYPE = "table";
   /**
    * @see ApiMetadata#QUEUE_TYPE
    */
   @Deprecated
   public static final String QUEUE_TYPE = "queue";
   /**
    * @see ApiMetadata#MONITOR_TYPE
    */
   @Deprecated
   public static final String MONITOR_TYPE = "monitor";

   /**
    * 
    * @author Adrian Cole
    * @since 1.5
    */
   public static interface Builder<B extends Builder<B>> {
      /**
       * @see ProviderMetadata#getId()
       */
      B id(String id);

      /**
       * @see ProviderMetadata#getName()
       */
      B name(String name);

      /**
       * @see ProviderMetadata#getApi()
       */
      B api(ApiMetadata api);

      /**
       * @see ProviderMetadata#getConsole()
       */
      B console(@Nullable URI console);

      /**
       * @see ProviderMetadata#getHomepage()
       */
      B homepage(@Nullable URI homepage);

      /**
       * @see ProviderMetadata#getLinkedServices()
       */
      B linkedServices(Iterable<String> linkedServices);

      /**
       * @see ProviderMetadata#getLinkedServices()
       */
      B linkedServices(String... linkedServices);

      /**
       * @see ProviderMetadata#getLinkedServices()
       */
      B linkedService(String linkedService);

      /**
       * @see ProviderMetadata#getIso3166Code()
       */
      B iso3166Codes(Iterable<String> iso3166Codes);

      /**
       * @see ProviderMetadata#getIso3166Code()
       */
      B iso3166Codes(String... iso3166Codes);

      /**
       * @see ProviderMetadata#getIso3166Code()
       */
      B iso3166Code(String iso3166Code);

      ProviderMetadata build();

      B fromProviderMetadata(ProviderMetadata in);
   }

   /**
    * @see Builder
    * @since 1.5
    */
   Builder<?> toBuilder();

   /**
    * 
    * @return the provider's unique identifier
    */
   public String getId();

   /**
    * 
    * @return the name (display name) of the provider
    */
   public String getName();

   /**
    * 
    * @see #getApi()
    * @see ApiMetadata#getType
    */
   @Deprecated
   public String getType();

   /**
    * 
    * @return the provider's api
    * @since 1.5
    */
   public ApiMetadata getApi();

   /**
    * 
    * @see #getApi()
    * @see ApiMetadata#getIdentityName
    */
   @Deprecated
   public String getIdentityName();

   /**
    * 
    * @see #getApi()
    * @see ApiMetadata#getCredentialName
    */
   @Deprecated
   public String getCredentialName();

   /**
    * 
    * @see #getApi()
    * @see ApiMetadata#getDocumentation
    */
   @Deprecated
   public URI getApiDocumentation();

   /**
    * 
    * @return the url for the provider's console, or null if one doesn't exist
    */
   @Nullable
   public URI getConsole();

   /**
    * 
    * @return the url for the provider's homepage
    */
   public URI getHomepage();

   /**
    * 
    * @return all known services linked to the same account on this provider
    */
   public Set<String> getLinkedServices();

   /**
    * 
    * @return all known region/location ISO 3166 codes
    */
   public Set<String> getIso3166Codes();
}