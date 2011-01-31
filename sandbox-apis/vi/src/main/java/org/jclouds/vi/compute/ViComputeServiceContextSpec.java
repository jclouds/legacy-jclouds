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

package org.jclouds.vi.compute;

import org.jclouds.PropertiesBuilder;
import org.jclouds.compute.ComputeService;
import org.jclouds.rest.RestContextSpec;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * @author Adrian Cole
 */
public class ViComputeServiceContextSpec extends RestContextSpec<ComputeService, ComputeService> {

   @SuppressWarnings("unchecked")
   public ViComputeServiceContextSpec(String endpoint, String identity, String credential, Iterable<Module> modules) {
      super("vi", endpoint, "1", "", identity, credential, ComputeService.class, ComputeService.class,
               PropertiesBuilder.class, (Class) ViComputeServiceContextBuilder.class, modules);
   }

   public ViComputeServiceContextSpec(String endpoint, String identity, String credential) {
      this(endpoint, identity, credential, ImmutableSet.<Module> of());
   }
}