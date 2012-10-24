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

import java.util.List;
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
import org.jclouds.abiquo.strategy.infrastructure.ListDatacenters;
import org.jclouds.logging.Logger;
import org.jclouds.rest.RestContext;

import com.abiquo.server.core.infrastructure.DatacenterDto;
import com.abiquo.server.core.infrastructure.DatacentersDto;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

/**
 * List datacenters.
 * 
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 */
@Singleton
public class ListDatacentersImpl implements ListDatacenters {

   protected final RestContext<AbiquoApi, AbiquoAsyncApi> context;

   protected final ExecutorService userExecutor;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject(optional = true)
   @Named(Constants.PROPERTY_REQUEST_TIMEOUT)
   protected Long maxTime;

   @Inject
   ListDatacentersImpl(final RestContext<AbiquoApi, AbiquoAsyncApi> context,
         @Named(Constants.PROPERTY_USER_THREADS) final ExecutorService userExecutor) {
      this.context = context;
      this.userExecutor = checkNotNull(userExecutor, "userExecutor");
   }

   @Override
   public Iterable<Datacenter> execute() {
      DatacentersDto result = context.getApi().getInfrastructureApi().listDatacenters();
      return wrap(context, Datacenter.class, result.getCollection());
   }

   @Override
   public Iterable<Datacenter> execute(final Predicate<Datacenter> selector) {
      return filter(execute(), selector);
   }

   @Override
   public Iterable<Datacenter> execute(final List<Integer> datacenterIds) {
      // Find virtual datacenters in concurrent requests
      return listConcurrentDatacenters(datacenterIds);
   }

   private Iterable<Datacenter> listConcurrentDatacenters(final List<Integer> ids) {
      Iterable<DatacenterDto> dcs = transformParallel(ids, new Function<Integer, Future<? extends DatacenterDto>>() {
         @Override
         public Future<DatacenterDto> apply(final Integer input) {
            return context.getAsyncApi().getInfrastructureApi().getDatacenter(input);
         }
      }, userExecutor, maxTime, logger, "getting datacenters");

      return DomainWrapper.wrap(context, Datacenter.class, Lists.newArrayList(dcs));
   }

}
