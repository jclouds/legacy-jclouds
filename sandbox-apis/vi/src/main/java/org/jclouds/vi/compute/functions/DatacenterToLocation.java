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

package org.jclouds.vi.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.vmware.vim25.mo.Datacenter;

/**
 * @author Adrian Cole
 */
@Singleton
public class DatacenterToLocation implements Function<Datacenter, Location> {
   private final Provider<Supplier<Location>> provider;

   // allow us to lazy discover the provider of a resource
   @Inject
   public DatacenterToLocation(Provider<Supplier<Location>> provider) {
      this.provider = checkNotNull(provider, "provider");
   }

   @Override
   public Location apply(Datacenter from) {
      return new LocationBuilder().scope(LocationScope.ZONE).id(from.getName()).description(from.getName()).parent(
               provider.get().get()).build();
   }

}
