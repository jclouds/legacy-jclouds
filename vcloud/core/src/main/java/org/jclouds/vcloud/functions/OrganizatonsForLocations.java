/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.vcloud.functions;

import static com.google.common.collect.Iterables.filter;
import static org.jclouds.concurrent.FutureIterables.transformParallel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.logging.Logger;
import org.jclouds.vcloud.VCloudAsyncClient;
import org.jclouds.vcloud.domain.Organization;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

/**
 * @author Adrian Cole
 */
@Singleton
public class OrganizatonsForLocations implements
         Function<Iterable<? extends Location>, Iterable<? extends Organization>> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   public Logger logger = Logger.NULL;
   private final VCloudAsyncClient aclient;
   private final ExecutorService executor;

   @Inject
   OrganizatonsForLocations(VCloudAsyncClient aclient, @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor) {
      this.aclient = aclient;
      this.executor = executor;
   }

   /**
    * regions are not currently an assignable location, so they don't show up in the list. As such,
    * we'll blindly look for all zones, and get their "parents"
    */
   @Override
   public Iterable<? extends Organization> apply(Iterable<? extends Location> from) {
      return transformParallel(filter(from, new Predicate<Location>() {

         @Override
         public boolean apply(Location input) {
            return input.getScope() == LocationScope.ZONE;
         }

      }), new Function<Location, Future<Organization>>() {

         @SuppressWarnings("unchecked")
         @Override
         public Future<Organization> apply(Location from) {
            return (Future<Organization>) aclient.getOrganization(from.getParent().getId());
         }

      }, executor, null, logger, "organizations for locations");
   }

}