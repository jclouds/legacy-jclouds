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
package org.jclouds.vcloud.functions;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.concurrent.FutureIterables.transformParallel;

import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.logging.Logger;
import org.jclouds.util.Iterables2;
import org.jclouds.vcloud.VCloudAsyncClient;
import org.jclouds.vcloud.domain.Org;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
@Singleton
public class OrgsForLocations implements Function<Iterable<Location>, Iterable<Org>> {
   @Resource
   public Logger logger = Logger.NULL;
   private final VCloudAsyncClient aclient;
   private final ExecutorService executor;

   @Inject
   OrgsForLocations(VCloudAsyncClient aclient, @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor) {
      this.aclient = aclient;
      this.executor = executor;
   }

   /**
    * Zones are assignable, but we want regions. so we look for zones, whose
    * parent is region. then, we use a set to extract the unique set.
    */
   @Override
   public Iterable<Org> apply(Iterable<Location> from) {

      return Iterables2.concreteCopy(transformParallel(Sets.newLinkedHashSet(transform(filter(from, new Predicate<Location>() {

         @Override
         public boolean apply(Location input) {
            return input.getScope() == LocationScope.ZONE;
         }

      }), new Function<Location, URI>() {

         @Override
         public URI apply(Location from) {
            return URI.create(from.getParent().getId());
         }

      })), new Function<URI, Future<? extends Org>>() {

         @Override
         public Future<Org> apply(URI from) {
            return aclient.getOrgClient().getOrg(from);
         }

      }, executor, null, logger, "organizations for uris"));
   }

}
