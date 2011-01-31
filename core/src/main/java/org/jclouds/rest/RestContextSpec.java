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

package org.jclouds.rest;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.EMPTY_LIST;

import org.jclouds.PropertiesBuilder;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.inject.Module;

/**
 * @author Adrian Cole
 */
public class RestContextSpec<S, A> {
   protected final String provider;
   protected final String endpoint;
   protected final String apiVersion;
   protected final String iso3166Codes;
   protected final String identity;
   protected final String credential;
   protected final Class<S> sync;
   protected final Class<A> async;
   protected final Class<PropertiesBuilder> propertiesBuilderClass;
   protected final Class<RestContextBuilder<S, A>> contextBuilderClass;
   protected final Iterable<Module> modules;

   public RestContextSpec(String provider, String endpoint, String apiVersion, String iso3166Codes, String identity,
            String credential, Class<S> sync, Class<A> async, Class<PropertiesBuilder> propertiesBuilderClass,
            Class<RestContextBuilder<S, A>> contextBuilderClass, Iterable<Module> modules) {
      this.provider = checkNotNull(provider, "provider");
      this.endpoint = endpoint;
      this.apiVersion = apiVersion;
      this.identity = identity;
      this.credential = credential;
      this.iso3166Codes = iso3166Codes;
      this.sync = sync;
      this.async = async;
      checkArgument(RestContextBuilder.class.isAssignableFrom(contextBuilderClass), contextBuilderClass.getName()
               + " is not a subclass of " + RestContextBuilder.class.getName());
      checkArgument(PropertiesBuilder.class.isAssignableFrom(propertiesBuilderClass), propertiesBuilderClass.getName()
               + " is not a subclass of " + PropertiesBuilder.class.getName());
      this.propertiesBuilderClass = propertiesBuilderClass;
      this.contextBuilderClass = contextBuilderClass;
      this.modules = ImmutableList.copyOf(modules);
   }

   @SuppressWarnings( { "unchecked", "rawtypes" })
   public RestContextSpec(String provider, String endpoint, String apiVersion, String iso3166Codes, String identity,
            String credential, Class<S> sync, Class<A> async) {
      this(provider, endpoint, apiVersion, iso3166Codes, identity, credential, sync, async, PropertiesBuilder.class,
               (Class) RestContextBuilder.class, EMPTY_LIST);
   }

   /**
    * this uses the inefficient {@link Objects} implementation as the object count will be
    * relatively small and therefore efficiency is not a concern.
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(provider, endpoint, apiVersion, iso3166Codes, identity, credential, sync, async,
               propertiesBuilderClass, contextBuilderClass, modules);
   }

   /**
    * this uses the inefficient {@link Objects} implementation as the object count will be
    * relatively small and therefore efficiency is not a concern.
    */
   @Override
   public boolean equals(Object that) {
      if (that == null)
         return false;
      return Objects.equal(this.toString(), that.toString());
   }

   /**
    * this uses the inefficient {@link Objects} implementation as the object count will be
    * relatively small and therefore efficiency is not a concern.
    */
   @Override
   public String toString() {
      return Objects.toStringHelper(this).add("provider", provider).add("endpoint", endpoint).add("apiVersion",
               apiVersion).add("iso3166Codes", iso3166Codes).add("identity", identity).add("sync", sync).add("async",
               async).add("propertiesBuilderClass", propertiesBuilderClass).add("contextBuilderClass",
               contextBuilderClass).add("modules", modules).toString();
   }

}