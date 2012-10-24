/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.compute.functions;

import javax.inject.Singleton;

import org.jclouds.abiquo.domain.infrastructure.Datacenter;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * Converts a {@link Datacenter} to a {@link Location} one.
 * <p>
 * Physical datacenters will be considered regions.
 * 
 * @author Ignasi Barrera
 */
@Singleton
public class DatacenterToLocation implements Function<Datacenter, Location> {

   @Override
   public Location apply(final Datacenter datacenter) {
      LocationBuilder builder = new LocationBuilder();
      builder.id(datacenter.getId().toString());
      builder.description(datacenter.getName() + " [" + datacenter.getLocation() + "]");
      builder.metadata(ImmutableMap.<String, Object> of());
      builder.scope(LocationScope.REGION);
      builder.iso3166Codes(ImmutableSet.<String> of());

      builder.parent(new LocationBuilder().scope(LocationScope.PROVIDER).id("abiquo").description("abiquo").build());

      return builder.build();
   }
}
