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
package org.jclouds.ibm.smartcloud.compute.suppliers;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.ibm.smartcloud.IBMSmartCloudClient;
import org.jclouds.ibm.smartcloud.domain.Location.State;
import org.jclouds.location.Iso3166;
import org.jclouds.location.Provider;
import org.jclouds.location.suppliers.JustProvider;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class IBMSmartCloudLocationSupplier extends JustProvider {

   private final IBMSmartCloudClient sync;
   private final Map<String, Set<String>> isoCodesById;

   @Inject
   IBMSmartCloudLocationSupplier(@Iso3166 Set<String> isoCodes, @Provider String providerName, @Provider URI endpoint,
            IBMSmartCloudClient sync, @Iso3166 Map<String, Set<String>> isoCodesById) {
      super(isoCodes, providerName, endpoint);
      this.sync = checkNotNull(sync, "sync");
      this.isoCodesById = checkNotNull(isoCodesById, "isoCodesById");
   }

   @Override
   public Set<? extends Location> get() {
      Builder<Location> locations = ImmutableSet.builder();
      Set<? extends org.jclouds.ibm.smartcloud.domain.Location> list = sync.listLocations();
      Location provider = Iterables.getOnlyElement(super.get());
      if (list.size() == 0)
         locations.add(provider);
      else
         for (org.jclouds.ibm.smartcloud.domain.Location from : list) {
            if (from.getState() != State.OFFLINE) {
               LocationBuilder builder = new LocationBuilder().scope(LocationScope.ZONE).id(from.getId() + "")
                        .description(from.getName()).parent(provider);
               if (isoCodesById.containsKey(from.getId() + ""))
                  builder.iso3166Codes(isoCodesById.get(from.getId() + ""));
               locations.add(builder.build());
            }
         }
      return locations.build();
   }
}
