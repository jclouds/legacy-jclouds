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
package org.jclouds.vcloud.compute.functions;

import java.net.URI;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;
import org.jclouds.vcloud.domain.ReferenceType;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

/**
 * @author Adrian Cole
 */
@Singleton
public class FindLocationForResource implements Function<ReferenceType, Location> {

   @Resource
   protected Logger logger = Logger.NULL;

   final Supplier<Set<? extends Location>> locations;

   @Inject
   public FindLocationForResource(@Memoized Supplier<Set<? extends Location>> locations) {
      this.locations = locations;
   }

   /**
    * searches for a location associated with this resource.
    * 
    * @throws NoSuchElementException
    *            if not found
    */
   public Location apply(ReferenceType resource) {
      for (Location input : locations.get()) {
         do {
            // The "name" isn't always present, ex inside a vApp we have a rel
            // link that only includes href and type.
            if (URI.create(input.getId()).equals(resource.getHref()))
               return input;
         } while ((input = input.getParent()) != null);
      }
      throw new NoSuchElementException(String.format("resource: %s not found in locations: %s", resource,
            locations.get()));
   }
}
