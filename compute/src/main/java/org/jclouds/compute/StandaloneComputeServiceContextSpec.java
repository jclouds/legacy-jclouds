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
import org.jclouds.compute.config.StandaloneComputeServiceContextModule;
import org.jclouds.rest.RestContextSpec;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Module;

/**
 * @author Adrian Cole
 */
public class StandaloneComputeServiceContextSpec<N, H, I, L> extends RestContextSpec<ComputeService, ComputeService> {

   @SuppressWarnings({ "unchecked", "rawtypes" })
   public StandaloneComputeServiceContextSpec(String provider, String endpoint, String apiVersion, String identity,
         String credential, StandaloneComputeServiceContextModule<N, H, I, L> contextModule, Iterable<Module> modules) {
      super(provider, endpoint, apiVersion, identity, credential, ComputeService.class, ComputeService.class,
            PropertiesBuilder.class, (Class) StandaloneComputeServiceContextBuilder.class, Iterables.concat(
                  ImmutableSet.of(contextModule), modules));
   }

}