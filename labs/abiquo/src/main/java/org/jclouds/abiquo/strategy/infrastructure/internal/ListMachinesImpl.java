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

package org.jclouds.abiquo.strategy.infrastructure.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.filter;
import static org.jclouds.abiquo.domain.DomainWrapper.wrap;
import static org.jclouds.concurrent.FutureIterables.transformParallel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.domain.DomainWrapper;
import org.jclouds.abiquo.domain.infrastructure.Datacenter;
import org.jclouds.abiquo.domain.infrastructure.Machine;
import org.jclouds.abiquo.strategy.infrastructure.ListDatacenters;
import org.jclouds.abiquo.strategy.infrastructure.ListMachines;
import org.jclouds.logging.Logger;
import org.jclouds.rest.RestContext;

import com.abiquo.server.core.infrastructure.MachineDto;
import com.abiquo.server.core.infrastructure.MachinesDto;
import com.abiquo.server.core.infrastructure.RackDto;
import com.abiquo.server.core.infrastructure.RacksDto;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.inject.Inject;

/**
 * List machines in each datacenter and rack.
 * 
 * @author Ignasi Barrera
 */
@Singleton
public class ListMachinesImpl implements ListMachines {
   protected RestContext<AbiquoApi, AbiquoAsyncApi> context;

   protected ListDatacenters listDatacenters;

   protected final ExecutorService userExecutor;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject(optional = true)
   @Named(Constants.PROPERTY_REQUEST_TIMEOUT)
   protected Long maxTime;

   @Inject
   ListMachinesImpl(final RestContext<AbiquoApi, AbiquoAsyncApi> context,
         @Named(Constants.PROPERTY_USER_THREADS) final ExecutorService userExecutor,
         final ListDatacenters listDatacenters) {
      super();
      this.context = checkNotNull(context, "context");
      this.listDatacenters = checkNotNull(listDatacenters, "listDatacenters");
      this.userExecutor = checkNotNull(userExecutor, "userExecutor");
   }

   @Override
   public Iterable<Machine> execute() {
      // Find machines in concurrent requests
      Iterable<Datacenter> datacenters = listDatacenters.execute();
      Iterable<RackDto> racks = listConcurrentRacks(datacenters);
      Iterable<MachineDto> machines = listConcurrentMachines(racks);

      return wrap(context, Machine.class, machines);
   }

   @Override
   public Iterable<Machine> execute(final Predicate<Machine> selector) {
      return filter(execute(), selector);
   }

   private Iterable<RackDto> listConcurrentRacks(final Iterable<Datacenter> datacenters) {
      Iterable<RacksDto> racks = transformParallel(datacenters, new Function<Datacenter, Future<? extends RacksDto>>() {
         @Override
         public Future<RacksDto> apply(final Datacenter input) {
            return context.getAsyncApi().getInfrastructureApi().listRacks(input.unwrap());
         }
      }, userExecutor, maxTime, logger, "getting racks");

      return DomainWrapper.join(racks);
   }

   private Iterable<MachineDto> listConcurrentMachines(final Iterable<RackDto> racks) {
      Iterable<MachinesDto> machines = transformParallel(racks, new Function<RackDto, Future<? extends MachinesDto>>() {
         @Override
         public Future<MachinesDto> apply(final RackDto input) {
            return context.getAsyncApi().getInfrastructureApi().listMachines(input);
         }
      }, userExecutor, maxTime, logger, "getting machines");

      return DomainWrapper.join(machines);
   }

}
