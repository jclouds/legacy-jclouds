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
package org.jclouds.internal;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.io.Closeables.closeQuietly;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.jclouds.Context;
import org.jclouds.annotations.Name;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.lifecycle.Closer;
import org.jclouds.location.Provider;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.rest.Utils;

import com.google.common.base.Objects;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Singleton;

/**
 * @author Adrian Cole
 */
@Singleton
public class ContextImpl implements Context {

   private final ProviderMetadata providerMetadata;
   private final Supplier<Credentials> creds;
   private final Utils utils;
   private final Closer closer;
   private final String name;

   @Inject
   protected ContextImpl(@Name String name, ProviderMetadata providerMetadata, @Provider Supplier<Credentials> creds,
         Utils utils, Closer closer) {
      this.providerMetadata = checkNotNull(providerMetadata, "providerMetadata");
      this.creds = checkNotNull(creds, "creds");
      this.utils = checkNotNull(utils, "utils");
      this.closer = checkNotNull(closer, "closer");
      this.name = checkNotNull(name, "name");
    }

   /**
    * {@inheritDoc}
    */
   @Override
   public void close() {
      closeQuietly(closer);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ProviderMetadata getProviderMetadata() {
      return providerMetadata;
   }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return name;
  }

  /**
    * {@inheritDoc}
    */
   @Override
   public String getIdentity() {
      return creds.get().identity;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Utils getUtils() {
      return utils();
   }

   @Override
   public Utils utils() {
      return utils;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(providerMetadata, creds);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      ContextImpl that = ContextImpl.class.cast(obj);
      return equal(this.providerMetadata, that.providerMetadata) && equal(this.creds, that.creds);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return toStringHelper("").add("providerMetadata", providerMetadata).add("identity", getIdentity()).toString();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getDescription() {
      return providerMetadata.getName();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getId() {
      return providerMetadata.getId();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<String> getIso3166Codes() {
      return providerMetadata.getIso3166Codes();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Map<String, Object> getMetadata() {
      return ImmutableMap.<String, Object> of("endpoint", URI.create(providerMetadata.getEndpoint()), "apiVersion",
               providerMetadata.getApiMetadata().getVersion(), "buildVersion", providerMetadata.getApiMetadata()
                        .getBuildVersion().or(""), "identity", getIdentity());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Location getParent() {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public LocationScope getScope() {
      return LocationScope.PROVIDER;
   }

}
