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
 * Unles required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either expres or implied.  See the License for the
 * specific language governing permisions and limitations
 * under the License.
 */
package org.jclouds.joyent.cloudapi.v6_5.suppliers;

import static com.google.common.collect.Maps.filterKeys;
import static com.google.common.collect.Maps.transformValues;

import java.net.URI;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.joyent.cloudapi.v6_5.features.DatacenterApi;
import org.jclouds.location.predicates.fromconfig.AnyOrConfiguredZoneId;
import org.jclouds.location.suppliers.ZoneIdToURISupplier;
import org.jclouds.util.Suppliers2;

import com.google.common.base.Supplier;

@Singleton
public class ZoneIdToURIFromDatacentersApi implements ZoneIdToURISupplier {

   private final DatacenterApi api;
   private final AnyOrConfiguredZoneId filter;

   @Inject
   public ZoneIdToURIFromDatacentersApi(DatacenterApi api, AnyOrConfiguredZoneId filter) {
      this.api = api;
      this.filter = filter;
   }

   @Override
   public Map<String, Supplier<URI>> get() {
      return filterKeys(transformValues(api.getDatacenters(), Suppliers2.<URI> ofInstanceFunction()), filter);
   }

   @Override
   public String toString() {
      return "getDatacenters()";
   }
}
