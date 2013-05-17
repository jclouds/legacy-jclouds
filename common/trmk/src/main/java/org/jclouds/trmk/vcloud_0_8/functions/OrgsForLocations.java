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
package org.jclouds.trmk.vcloud_0_8.functions;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.concurrent.FutureIterables.transformParallel;

import java.net.URI;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.logging.Logger;
import org.jclouds.trmk.vcloud_0_8.TerremarkVCloudAsyncClient;
import org.jclouds.trmk.vcloud_0_8.domain.Org;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

/**
 * @author Adrian Cole
 */
@Singleton
public class OrgsForLocations implements Function<Iterable<? extends Location>, Iterable<? extends Org>> {
   @Resource
   public Logger logger = Logger.NULL;
   private final TerremarkVCloudAsyncClient aclient;
   private final ListeningExecutorService userExecutor;

   @Inject
   OrgsForLocations(TerremarkVCloudAsyncClient aclient, @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor) {
      this.aclient = aclient;
      this.userExecutor = userExecutor;
   }

   /**
    * Zones are assignable, but we want regions. so we look for zones, whose
    * parent is region. then, we use a set to extract the unique set.
    */
   @Override
   public Iterable<? extends Org> apply(Iterable<? extends Location> from) {
      return transformParallel(Sets.newLinkedHashSet(transform(filter(from, new Predicate<Location>() {
         public boolean apply(Location input) {
            return input.getScope() == LocationScope.ZONE;
         }
      }), new Function<Location, URI>() {
         public URI apply(Location from) {
            return URI.create(from.getParent().getId());
         }
      })), new Function<URI, ListenableFuture<? extends Org>>() {
         public ListenableFuture<? extends Org> apply(URI from) {
            return aclient.getOrg(from);
         }
      }, userExecutor, null, logger, "organizations for uris");
   }
}
