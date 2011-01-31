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

package org.jclouds.compute;

import org.jclouds.PropertiesBuilder;
import org.jclouds.rest.RestContextSpec;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * @author Adrian Cole
 */
public class StandaloneComputeServiceContextSpec<D, N, H, I, L> extends RestContextSpec<D, D> {
   public StandaloneComputeServiceContextSpec(String provider, String endpoint, String apiVersion, String iso3166Codes,
            String identity, String credential, Class<D> driverClass,
            Class<? extends StandaloneComputeServiceContextBuilder<D>> contextBuilderClass) {
      this(provider, endpoint, apiVersion, iso3166Codes, identity, credential, driverClass, contextBuilderClass,
               ImmutableSet.<Module> of());
   }

   public StandaloneComputeServiceContextSpec(String provider, String endpoint, String apiVersion, String iso3166Codes,
            String identity, String credential, Class<D> driverClass,
            Class<? extends StandaloneComputeServiceContextBuilder<D>> contextBuilderClass, Iterable<Module> modules) {
      this(provider, endpoint, apiVersion, iso3166Codes, identity, credential, driverClass, PropertiesBuilder.class,
               contextBuilderClass, modules);
   }

   @SuppressWarnings( { "unchecked", "rawtypes" })
   public StandaloneComputeServiceContextSpec(String provider, String endpoint, String apiVersion, String iso3166Codes,
            String identity, String credential, Class<D> driverClass,
            Class<? extends PropertiesBuilder> propertiesBuilderClass,
            Class<? extends StandaloneComputeServiceContextBuilder<D>> contextBuilderClass, Iterable<Module> modules) {
      super(provider, endpoint, apiVersion, iso3166Codes, identity, credential, driverClass, driverClass,
               (Class) propertiesBuilderClass, (Class) contextBuilderClass, modules);
   }
}