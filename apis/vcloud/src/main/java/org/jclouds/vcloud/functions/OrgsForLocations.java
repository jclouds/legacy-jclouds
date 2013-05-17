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
package org.jclouds.vcloud.functions;

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
import org.jclouds.vcloud.VCloudAsyncClient;
import org.jclouds.vcloud.domain.Org;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

/**
 * @author Adrian Cole
 */
@Singleton
public class OrgsForLocations implements Function<Iterable<Location>, Iterable<Org>> {
   @Resource
   public Logger logger = Logger.NULL;
   private final VCloudAsyncClient aclient;
   private final ListeningExecutorService userExecutor;

   @Inject
   OrgsForLocations(VCloudAsyncClient aclient, @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor) {
      this.aclient = aclient;
      this.userExecutor = userExecutor;
   }

   /**
    * Zones are assignable, but we want regions. so we look for zones, whose
    * parent is region. then, we use a set to extract the unique set.
    */
   @Override
   public Iterable<Org> apply(Iterable<Location> from) {
      FluentIterable<URI> uris = FluentIterable.from(from).filter(new Predicate<Location>() {
         public boolean apply(Location input) {
            return input.getScope() == LocationScope.ZONE;
         }
      }).transform(new Function<Location, URI>() {
         public URI apply(Location from) {
            return URI.create(from.getParent().getId());
         }
      });
      return transformParallel(uris, new Function<URI, ListenableFuture<? extends Org>>() {
         public ListenableFuture<Org> apply(URI from) {
            return aclient.getOrgClient().getOrg(from);
         }
      }, userExecutor, null, logger, "organizations for uris");
   }

}
