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

package org.jclouds.abiquo.strategy.cloud;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.filter;
import static org.jclouds.abiquo.domain.DomainWrapper.wrap;
import static org.jclouds.concurrent.FutureIterables.transformParallel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.domain.DomainWrapper;
import org.jclouds.abiquo.domain.cloud.VirtualAppliance;
import org.jclouds.abiquo.domain.cloud.VirtualDatacenter;
import org.jclouds.abiquo.strategy.ListRootEntities;
import org.jclouds.logging.Logger;
import org.jclouds.rest.RestContext;

import com.abiquo.server.core.cloud.VirtualApplianceDto;
import com.abiquo.server.core.cloud.VirtualAppliancesDto;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * List virtual appliances in each virtual datacenter.
 * 
 * @author Ignasi Barrera
 */
@Singleton
public class ListVirtualAppliances implements ListRootEntities<VirtualAppliance> {
   protected final RestContext<AbiquoApi, AbiquoAsyncApi> context;

   protected final ListVirtualDatacenters listVirtualDatacenters;

   protected final ExecutorService userExecutor;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject(optional = true)
   @Named(Constants.PROPERTY_REQUEST_TIMEOUT)
   protected Long maxTime;

   @Inject
   ListVirtualAppliances(final RestContext<AbiquoApi, AbiquoAsyncApi> context,
         @Named(Constants.PROPERTY_USER_THREADS) final ExecutorService userExecutor,
         final ListVirtualDatacenters listVirtualDatacenters) {
      this.context = checkNotNull(context, "context");
      this.listVirtualDatacenters = checkNotNull(listVirtualDatacenters, "listVirtualDatacenters");
      this.userExecutor = checkNotNull(userExecutor, "userExecutor");
   }

   @Override
   public Iterable<VirtualAppliance> execute() {
      // Find virtual appliances in concurrent requests
      Iterable<VirtualDatacenter> vdcs = listVirtualDatacenters.execute();
      Iterable<VirtualApplianceDto> vapps = listConcurrentVirtualAppliances(vdcs);

      return wrap(context, VirtualAppliance.class, vapps);
   }

   @Override
   public Iterable<VirtualAppliance> execute(final Predicate<VirtualAppliance> selector) {
      return filter(execute(), selector);
   }

   private Iterable<VirtualApplianceDto> listConcurrentVirtualAppliances(final Iterable<VirtualDatacenter> vdcs) {
      Iterable<VirtualAppliancesDto> vapps = transformParallel(vdcs,
            new Function<VirtualDatacenter, Future<? extends VirtualAppliancesDto>>() {
               @Override
               public Future<VirtualAppliancesDto> apply(final VirtualDatacenter input) {
                  return context.getAsyncApi().getCloudApi().listVirtualAppliances(input.unwrap());
               }
            }, userExecutor, maxTime, logger, "getting virtual appliances");

      return DomainWrapper.join(vapps);
   }
}
