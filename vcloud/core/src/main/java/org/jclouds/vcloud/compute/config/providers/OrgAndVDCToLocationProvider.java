/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.vcloud.compute.config.providers;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.internal.LocationImpl;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.internal.VCloudLoginAsyncClient.VCloudSession;

import com.google.common.base.Supplier;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
@Singleton
public class OrgAndVDCToLocationProvider implements Provider<Set<? extends Location>> {
   private final Supplier<VCloudSession> cache;
   private final VCloudClient client;
   private final String providerName;

   @Inject
   OrgAndVDCToLocationProvider(@org.jclouds.rest.annotations.Provider String providerName,
         Supplier<VCloudSession> cache, VCloudClient client) {
      this.providerName = providerName;
      this.cache = cache;
      this.client = client;
   }

   @Override
   public Set<? extends Location> get() {
      Location provider = new LocationImpl(LocationScope.PROVIDER, providerName, providerName, null);
      Set<Location> locations = Sets.newLinkedHashSet();

      for (NamedResource org : cache.get().getOrgs().values()) {
         Location orgL = new LocationImpl(LocationScope.REGION, org.getName(), org.getLocation().toASCIIString(),
               provider);
         for (NamedResource vdc : client.getOrganizationNamed(org.getName()).getVDCs().values()) {
            locations.add(new LocationImpl(LocationScope.ZONE, vdc.getId(), vdc.getName(), orgL));
         }
      }
      return locations;
   }
}