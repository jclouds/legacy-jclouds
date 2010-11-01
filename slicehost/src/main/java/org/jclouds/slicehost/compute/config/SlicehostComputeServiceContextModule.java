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

package org.jclouds.slicehost.compute.config;

import static org.jclouds.compute.domain.OsFamily.UBUNTU;

import java.util.Set;

import javax.inject.Singleton;

import org.jclouds.compute.config.BaseComputeServiceContextModule;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.internal.BaseComputeService;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.internal.LocationImpl;
import org.jclouds.rest.annotations.Provider;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;
import com.google.inject.Provides;

/**
 * Configures the {@link SlicehostComputeServiceContext}; requires {@link BaseComputeService} bound.
 * 
 * @author Adrian Cole
 */
public class SlicehostComputeServiceContextModule extends BaseComputeServiceContextModule {

   @Provides
   @Singleton
   Location getLocation(@Provider String providerName) {
      Location provider = new LocationImpl(LocationScope.PROVIDER, providerName, providerName, null);
      return new LocationImpl(LocationScope.ZONE, "DFW1", "Dallas, TX", provider);
   }

   @Override
   protected TemplateBuilder provideTemplate(Injector injector, TemplateBuilder template) {
      return template.osFamily(UBUNTU).os64Bit(true);
   }

   @Provides
   @Singleton
   Set<? extends Location> provideLocations(Location location) {
      return ImmutableSet.of(location);
   }

   @Override
   protected void configure() {
      install(new SlicehostComputeServiceDependenciesModule());
      install(new SlicehostBindComputeStrategiesByClass());
      install(new SlicehostBindComputeSuppliersByClass());
      super.configure();
   }
}
