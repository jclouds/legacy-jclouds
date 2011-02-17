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

package org.jclouds.rest.internal;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.lifecycle.Closer;
import org.jclouds.location.Iso3166;
import org.jclouds.location.Provider;
import org.jclouds.logging.Logger;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.Utils;
import org.jclouds.rest.annotations.ApiVersion;
import org.jclouds.rest.annotations.Identity;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;

/**
 * @author Adrian Cole
 */
@Singleton
public class RestContextImpl<S, A> implements RestContext<S, A> {

   @Resource
   private Logger logger = Logger.NULL;
   private final A asyncApi;
   private final S syncApi;
   private final Closer closer;
   private final URI endpoint;
   private final String identity;
   private final String provider;
   private final String apiVersion;
   private final Utils utils;
   private final Map<String, Credentials> credentialStore;
   private final Set<String> iso3166Codes;

   @Inject
   protected RestContextImpl(Closer closer, Map<String, Credentials> credentialStore, Utils utils, Injector injector,
            TypeLiteral<S> syncApi, TypeLiteral<A> asyncApi, @Provider URI endpoint, @Provider String provider,
            @Identity String identity, @ApiVersion String apiVersion, @Iso3166 Set<String> iso3166Codes) {
      this.credentialStore = credentialStore;
      this.utils = utils;
      this.asyncApi = injector.getInstance(Key.get(asyncApi));
      this.syncApi = injector.getInstance(Key.get(syncApi));
      this.closer = closer;
      this.endpoint = endpoint;
      this.identity = identity;
      this.provider = provider;
      this.apiVersion = apiVersion;
      this.iso3166Codes = iso3166Codes;
   }

   /**
    * {@inheritDoc}
    * 
    * @see Closer
    */
   @Override
   public void close() {
      try {
         closer.close();
      } catch (IOException e) {
         logger.error(e, "error closing content");
      }
   }

   @Override
   public String getIdentity() {
      return identity;
   }

   @Override
   public A getAsyncApi() {
      return asyncApi;
   }

   @Override
   public S getApi() {
      return syncApi;
   }

   @Override
   public URI getEndpoint() {
      return endpoint;
   }

   @Override
   public Utils getUtils() {
      return utils();
   }

   @Override
   public Utils utils() {
      return utils;
   }

   @Override
   public String getApiVersion() {
      return apiVersion;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((apiVersion == null) ? 0 : apiVersion.hashCode());
      result = prime * result + ((endpoint == null) ? 0 : endpoint.hashCode());
      result = prime * result + ((identity == null) ? 0 : identity.hashCode());
      result = prime * result + ((provider == null) ? 0 : provider.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      RestContextImpl<?, ?> other = (RestContextImpl<?, ?>) obj;
      if (apiVersion == null) {
         if (other.apiVersion != null)
            return false;
      } else if (!apiVersion.equals(other.apiVersion))
         return false;
      if (endpoint == null) {
         if (other.endpoint != null)
            return false;
      } else if (!endpoint.equals(other.endpoint))
         return false;
      if (identity == null) {
         if (other.identity != null)
            return false;
      } else if (!identity.equals(other.identity))
         return false;
      if (provider == null) {
         if (other.provider != null)
            return false;
      } else if (!provider.equals(other.provider))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return " [id=" + provider + ", endpoint=" + endpoint + ", apiVersion=" + apiVersion + ", identity=" + identity
               + ", iso3166Codes=" + iso3166Codes + "]";
   }

   @Override
   public Map<String, Credentials> getCredentialStore() {
      return credentialStore;
   }

   @Override
   public Map<String, Credentials> credentialStore() {
      return credentialStore;
   }

   @Override
   public String getDescription() {
      return null;
   }

   @Override
   public String getId() {
      return provider;
   }

   @Override
   public Set<String> getIso3166Codes() {
      return iso3166Codes;
   }

   @Override
   public Map<String, Object> getMetadata() {
      return ImmutableMap.<String, Object> of("endpoint", endpoint, "apiVersion", apiVersion, "identity", identity);
   }

   @Override
   public Location getParent() {
      return null;
   }

   @Override
   public LocationScope getScope() {
      return LocationScope.PROVIDER;
   }
}
