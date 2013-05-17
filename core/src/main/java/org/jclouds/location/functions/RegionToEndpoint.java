/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.location.functions;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.net.URI;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.Region;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class RegionToEndpoint implements Function<Object, URI> {

   private final Supplier<Map<String, Supplier<URI>>> regionToEndpointSupplier;

   @Inject
   public RegionToEndpoint(@Region Supplier<Map<String, Supplier<URI>>> regionToEndpointSupplier) {
      this.regionToEndpointSupplier = checkNotNull(regionToEndpointSupplier, "regionToEndpointSupplier");
   }

   @Override
   public URI apply(@Nullable Object from) {
      checkArgument(from != null && from instanceof String, "you must specify a region, as a String argument");
      Map<String, Supplier<URI>> regionToEndpoint = regionToEndpointSupplier.get();
      checkState(regionToEndpoint.size() > 0, "no region name to endpoint mappings configured!");
      checkArgument(regionToEndpoint.containsKey(from),
               "requested location %s, which is not in the configured locations: %s", from, regionToEndpoint);
      return regionToEndpoint.get(from).get();
   }
}
