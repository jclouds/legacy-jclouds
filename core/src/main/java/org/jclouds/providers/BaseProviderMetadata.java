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

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.addAll;
import static com.google.common.collect.Sets.newLinkedHashSet;

import java.net.URI;
import java.util.Set;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;

/**
 * The BaseProviderMetadata class is an abstraction of {@link ProviderMetadata}
 * to be extended by those implementing ProviderMetadata.
 * 
 * (Note: This class must be abstract to allow {@link java.util.ServiceLoader}
 * to work properly.
 * 
 * @author Adrian Cole
 */
public abstract class BaseProviderMetadata implements ProviderMetadata {

   public static abstract class Builder<B extends Builder<B>> implements ProviderMetadata.Builder<B> {
      protected String id;
      protected String name;
      protected ApiMetadata api;
      protected URI console;
      protected URI homepage;
      protected Set<String> linkedServices = newLinkedHashSet();
      protected Set<String> iso3166Codes = newLinkedHashSet();

      @SuppressWarnings("unchecked")
      protected B self() {
         return (B) this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public B id(String id) {
         this.id = checkNotNull(id, "id");
         return linkedService(id);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public B name(String name) {
         this.name = checkNotNull(name, "name");
         return self();
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public B api(ApiMetadata api) {
         this.api = checkNotNull(api, "api");
         return self();
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public B console(@Nullable URI console) {
         this.console = console;
         return self();
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public B homepage(URI homepage) {
         this.homepage = homepage;
         return self();
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public B linkedServices(Iterable<String> linkedServices) {
         addAll(this.linkedServices, checkNotNull(linkedServices, "linkedServices"));
         return self();
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public B linkedServices(String... linkedServices) {
         return linkedServices(ImmutableSet.copyOf(checkNotNull(linkedServices, "linkedServices")));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public B linkedService(String linkedService) {
         this.linkedServices.add(checkNotNull(linkedService, "linkedService"));
         return self();
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public B iso3166Codes(Iterable<String> iso3166Codes) {
         addAll(this.iso3166Codes, checkNotNull(iso3166Codes, "iso3166Codes"));
         return self();
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public B iso3166Codes(String... iso3166Codes) {
         return iso3166Codes(ImmutableSet.copyOf(checkNotNull(iso3166Codes, "iso3166Codes")));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public B iso3166Code(String iso3166Code) {
         this.iso3166Codes.add(checkNotNull(iso3166Code, "iso3166Code"));
         return self();
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public B fromProviderMetadata(ProviderMetadata in) {
         return id(in.getId()).name(in.getName()).api(in.getApi()).console(in.getConsole()).homepage(in.getHomepage())
               .linkedServices(in.getLinkedServices()).iso3166Codes(in.getIso3166Codes());
      }
   }

   protected final String id;
   protected final String name;
   protected final ApiMetadata api;
   protected final URI homepage;
   protected final URI console;
   protected final Set<String> linkedServices;
   protected final Set<String> iso3166Codes;

   protected BaseProviderMetadata(Builder<?> builder) {
      this.id = builder.id;
      this.name = builder.name;
      this.api = builder.api;
      this.console = builder.console;
      this.homepage = builder.homepage;
      this.linkedServices = ImmutableSet.copyOf(builder.linkedServices);
      this.iso3166Codes = ImmutableSet.copyOf(builder.iso3166Codes);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      // subclass equivalence is ok, since we don't know the classloader
      // we'll get things from
      if (o == null || !(o instanceof ProviderMetadata))
         return false;
      ProviderMetadata that = ProviderMetadata.class.cast(o);
      return equal(this.getId(), that.getId()) && equal(this.getName(), that.getName())
            && equal(this.getApi(), that.getApi()) && equal(this.getConsole(), that.getConsole())
            && equal(this.getHomepage(), that.getHomepage())
            && equal(this.getLinkedServices(), that.getLinkedServices())
            && equal(this.getIso3166Codes(), that.getIso3166Codes());
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(getId(), getName(), getApi(), getConsole(), getHomepage(), getLinkedServices(),
            getIso3166Codes());
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public ToStringHelper string() {
      return Objects.toStringHelper("").add("id", getId()).add("name", getName()).add("api", getApi())
            .add("console", getConsole()).add("homepage", getHomepage()).add("linkedServices", getLinkedServices())
            .add("iso3166Codes", getIso3166Codes());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getId() {
      return id;
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
   public ApiMetadata getApi() {
      return api;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public URI getConsole() {
      return console;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public URI getHomepage() {
      return homepage;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<String> getLinkedServices() {
      return linkedServices;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<String> getIso3166Codes() {
      return iso3166Codes;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getType() {
      return getApi().getType().toString();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getIdentityName() {
      return getApi().getIdentityName();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getCredentialName() {
      return getApi().getCredentialName();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public URI getApiDocumentation() {
      return getApi().getDocumentation();
   }
}
