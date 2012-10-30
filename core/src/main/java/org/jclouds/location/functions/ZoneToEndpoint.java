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
package org.jclouds.location.functions;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.net.URI;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.Zone;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ZoneToEndpoint implements Function<Object, URI> {

   private final Supplier<Map<String, Supplier<URI>>> zoneToEndpointSupplier;

   @Inject
   public ZoneToEndpoint(@Zone Supplier<Map<String, Supplier<URI>>> zoneToEndpointSupplier) {
      this.zoneToEndpointSupplier = checkNotNull(zoneToEndpointSupplier, "zoneToEndpointSupplier");
   }

   @Override
   public URI apply(@Nullable Object from) {
      checkArgument(from != null && from instanceof String, "you must specify a zone, as a String argument");
      Map<String, Supplier<URI>> zoneToEndpoint = zoneToEndpointSupplier.get();
      checkState(zoneToEndpoint.size() > 0, "no zone name to endpoint mappings configured!");
      checkArgument(zoneToEndpoint.containsKey(from),
               "requested location %s, which is not in the configured locations: %s", from, zoneToEndpoint);
      return zoneToEndpoint.get(from).get();
   }
}
