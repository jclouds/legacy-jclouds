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
package org.jclouds.smartos.compute.functions;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import org.jclouds.smartos.compute.domain.SmartOSHost;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Nigel Magnay
 */
@Singleton
public class DatacenterToLocation implements Function<SmartOSHost, Location> {
   private final Provider<Supplier<Location>> provider;

   // allow us to lazy discover the provider of a resource
   @Inject
   public DatacenterToLocation(Provider<Supplier<Location>> provider) {
      this.provider = checkNotNull(provider, "provider");
   }

   @Override
   public Location apply(SmartOSHost from) {
      return new LocationBuilder().scope(LocationScope.ZONE).id(from.getHostname() + "").description(from.getDescription()).parent(
               provider.get().get()).build();
   }

}
