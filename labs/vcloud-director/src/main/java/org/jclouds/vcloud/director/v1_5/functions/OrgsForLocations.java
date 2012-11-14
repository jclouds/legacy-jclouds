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
package org.jclouds.vcloud.director.v1_5.functions;

import static com.google.common.collect.FluentIterable.from;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.domain.Location;
import org.jclouds.location.predicates.LocationPredicates;
import org.jclouds.vcloud.director.v1_5.domain.org.Org;
import org.jclouds.vcloud.director.v1_5.user.VCloudDirectorApi;

import com.google.common.base.Function;

/**
 * @author danikov, Adrian Cole
 */
@Singleton
public class OrgsForLocations implements Function<Iterable<Location>, Iterable<? extends Org>> {

   private final VCloudDirectorApi api;

   @Inject
   OrgsForLocations(VCloudDirectorApi api) {
      this.api = api;
   }

   /**
    * Zones are assignable, but we want regions. so we look for zones, whose parent is region. then,
    * we use a set to extract the unique set.
    */
   @Override
   public Iterable<? extends Org> apply(Iterable<Location> from) {
      return from(from)
            .filter(LocationPredicates.isZone())
            .transform(new Function<Location, String>() {
               @Override
               public String apply(Location from) {
                  return from.getParent().getId();
               }
            })
            .transform(new Function<String, Org>() {
      
               @Override
               public Org apply(String from) {
                  return api.getOrgApi().get(from);
               }
      
            });
   }

}
