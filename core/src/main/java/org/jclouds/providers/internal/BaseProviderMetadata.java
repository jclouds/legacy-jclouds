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

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.addAll;
import static com.google.common.collect.Sets.newLinkedHashSet;

import java.net.URI;
import java.util.Properties;
import java.util.Set;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.providers.ProviderMetadata;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;

/**
 * The BaseProviderMetadata class is an abstraction of {@link ProviderMetadata} to be extended by
 * those implementing ProviderMetadata.
 * 
 * (Note: This class must be abstract to allow {@link java.util.ServiceLoader} to work properly.
 * 
 * @author Adrian Cole
 */
public abstract class BaseProviderMetadata implements ProviderMetadata {

   @Override
   public ProviderMetadata.Builder toBuilder() {
      return new BaseProviderMetadata.Builder().fromProviderMetadata(this);
   }

   public static class Builder implements ProviderMetadata.Builder {
      protected String id;
      protected String name;
      protected ApiMetadata api;
      protected String endpoint;
      protected Properties defaultProperties = new Properties();
      protected URI console;
      protected URI homepage;
      protected Set<String> linkedServices = newLinkedHashSet();
      protected Set<String> iso3166Codes = newLinkedHashSet();

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder id(String id) {
         this.id = checkNotNull(id, "id");
         return linkedService(id);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder name(String name) {
         this.name = checkNotNull(name, "name");
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder apiMetadata(ApiMetadata api) {
         this.api = checkNotNull(api, "api");
         if (this.endpoint == null)
            this.endpoint = api.getDefaultEndpoint().orNull();
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder endpoint(String endpoint) {
         this.endpoint = checkNotNull(endpoint, "endpoint");
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder defaultProperties(Properties defaultProperties) {
         this.defaultProperties = checkNotNull(defaultProperties, "defaultProperties");
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder console(@Nullable URI console) {
         this.console = console;
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder homepage(URI homepage) {
         this.homepage = homepage;
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder linkedServices(Iterable<String> linkedServices) {
         addAll(this.linkedServices, checkNotNull(linkedServices, "linkedServices"));
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder linkedServices(String... linkedServices) {
         return linkedServices(ImmutableSet.copyOf(checkNotNull(linkedServices, "linkedServices")));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder linkedService(String linkedService) {
         this.linkedServices.add(checkNotNull(linkedService, "linkedService"));
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder iso3166Codes(Iterable<String> iso3166Codes) {
         addAll(this.iso3166Codes, checkNotNull(iso3166Codes, "iso3166Codes"));
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder iso3166Codes(String... iso3166Codes) {
         return iso3166Codes(ImmutableSet.copyOf(checkNotNull(iso3166Codes, "iso3166Codes")));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder iso3166Code(String iso3166Code) {
         this.iso3166Codes.add(checkNotNull(iso3166Code, "iso3166Code"));
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromProviderMetadata(ProviderMetadata in) {
         return id(in.getId()).name(in.getName()).apiMetadata(in.getApiMetadata()).endpoint(in.getEndpoint())
                  .defaultProperties(in.getDefaultProperties()).console(in.getConsole().orNull()).homepage(
                           in.getHomepage().orNull()).linkedServices(in.getLinkedServices()).iso3166Codes(
                           in.getIso3166Codes());
      }

      @Override
      public ProviderMetadata build() {
         return new BaseProviderMetadata(this) {
         };
      }
   }

   protected final String id;
   protected final String name;
   protected final ApiMetadata api;
   protected final String endpoint;
   protected final Properties defaultProperties;
   protected final Optional<URI> homepage;
   protected final Optional<URI> console;
   protected final Set<String> linkedServices;
   protected final Set<String> iso3166Codes;

   public BaseProviderMetadata(Builder builder) {
      this(builder.id, builder.name, builder.api, builder.endpoint, builder.defaultProperties, Optional
               .fromNullable(builder.homepage), Optional.fromNullable(builder.console), builder.linkedServices,
               builder.iso3166Codes);
   }

   public BaseProviderMetadata(String id, String name, ApiMetadata api, String endpoint, Properties defaultProperties,
            Optional<URI> homepage, Optional<URI> console, Set<String> linkedServices, Set<String> iso3166Codes) {
      this.id = checkNotNull(id, "id");
      this.name = checkNotNull(name, "name");
      this.api = checkNotNull(api, "api");
      this.endpoint = checkNotNull(endpoint, "endpoint");
      this.console = checkNotNull(console, "console");
      this.defaultProperties = checkNotNull(defaultProperties, "defaultProperties");
      this.homepage = checkNotNull(homepage, "homepage");
      this.linkedServices = ImmutableSet.copyOf(linkedServices);
      this.iso3166Codes = ImmutableSet.copyOf(iso3166Codes);
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
               && equal(this.getApiMetadata(), that.getApiMetadata()) && equal(this.getEndpoint(), that.getEndpoint());
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(getId(), getName(), getApiMetadata(), getEndpoint());
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public ToStringHelper string() {
      return Objects.toStringHelper("").add("id", getId()).add("name", getName()).add("api", getApiMetadata()).add(
               "endpoint", getEndpoint()).add("console", getConsole()).add("homepage", getHomepage()).add(
               "linkedServices", getLinkedServices()).add("iso3166Codes", getIso3166Codes());
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
   public ApiMetadata getApiMetadata() {
      return api;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getEndpoint() {
      return endpoint;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Optional<URI> getConsole() {
      return console;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Properties getDefaultProperties() {
      return defaultProperties;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Optional<URI> getHomepage() {
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

}
