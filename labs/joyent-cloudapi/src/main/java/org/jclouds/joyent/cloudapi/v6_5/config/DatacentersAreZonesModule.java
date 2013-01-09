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
package org.jclouds.joyent.cloudapi.v6_5.config;

import static org.jclouds.rest.config.BinderUtils.bindHttpApi;

import org.jclouds.joyent.cloudapi.v6_5.features.DatacenterAsyncApi;
import org.jclouds.joyent.cloudapi.v6_5.features.DatacenterApi;
import org.jclouds.joyent.cloudapi.v6_5.suppliers.ZoneIdToURIFromDatacentersApi;
import org.jclouds.location.suppliers.ImplicitLocationSupplier;
import org.jclouds.location.suppliers.ZoneIdToURISupplier;
import org.jclouds.location.suppliers.ZoneIdsSupplier;
import org.jclouds.location.suppliers.derived.ZoneIdsFromZoneIdToURIKeySet;
import org.jclouds.location.suppliers.implicit.OnlyLocationOrFirstZone;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

/**
 * 
 * @author Adrian Cole
 */
public class DatacentersAreZonesModule extends AbstractModule {

   @Override
   protected void configure() {
      // datacenter api is needed for obtaining zone ids
      bindHttpApi(binder(), DatacenterApi.class, DatacenterAsyncApi.class);
      bind(ImplicitLocationSupplier.class).to(OnlyLocationOrFirstZone.class).in(Scopes.SINGLETON);
      bind(ZoneIdToURISupplier.class).to(ZoneIdToURIFromDatacentersApi.class).in(Scopes.SINGLETON);
      bind(ZoneIdsSupplier.class).to(ZoneIdsFromZoneIdToURIKeySet.class).in(Scopes.SINGLETON);
   }
}
