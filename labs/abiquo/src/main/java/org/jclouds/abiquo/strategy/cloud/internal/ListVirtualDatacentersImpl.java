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

package org.jclouds.abiquo.strategy.cloud.internal;

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
import org.jclouds.abiquo.domain.cloud.VirtualDatacenter;
import org.jclouds.abiquo.domain.cloud.options.VirtualDatacenterOptions;
import org.jclouds.abiquo.strategy.cloud.ListVirtualDatacenters;
import org.jclouds.logging.Logger;
import org.jclouds.rest.RestContext;

import com.abiquo.server.core.cloud.VirtualDatacenterDto;
import com.abiquo.server.core.cloud.VirtualDatacentersDto;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

/**
 * List virtual datacenters.
 * 
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 */
@Singleton
public class ListVirtualDatacentersImpl implements ListVirtualDatacenters {
   protected final RestContext<AbiquoApi, AbiquoAsyncApi> context;

   protected final ExecutorService userExecutor;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject(optional = true)
   @Named(Constants.PROPERTY_REQUEST_TIMEOUT)
   protected Long maxTime;

   @Inject
   ListVirtualDatacentersImpl(final RestContext<AbiquoApi, AbiquoAsyncApi> context,
         @Named(Constants.PROPERTY_USER_THREADS) final ExecutorService userExecutor) {
      this.context = checkNotNull(context, "context");
      this.userExecutor = checkNotNull(userExecutor, "userExecutor");
   }

   @Override
   public Iterable<VirtualDatacenter> execute() {
      VirtualDatacenterOptions virtualDatacenterOptions = VirtualDatacenterOptions.builder().build();

      return execute(virtualDatacenterOptions);
   }

   @Override
   public Iterable<VirtualDatacenter> execute(final Predicate<VirtualDatacenter> selector) {
      return filter(execute(), selector);
   }

   @Override
   public Iterable<VirtualDatacenter> execute(final VirtualDatacenterOptions virtualDatacenterOptions) {
      VirtualDatacentersDto result = context.getApi().getCloudApi().listVirtualDatacenters(virtualDatacenterOptions);
      return wrap(context, VirtualDatacenter.class, result.getCollection());
   }

   @Override
   public Iterable<VirtualDatacenter> execute(final Predicate<VirtualDatacenter> selector,
         final VirtualDatacenterOptions virtualDatacenterOptions) {
      return filter(execute(virtualDatacenterOptions), selector);
   }

   @Override
   public Iterable<VirtualDatacenter> execute(final List<Integer> virtualDatacenterIds) {
      // Find virtual datacenters in concurrent requests
      return listConcurrentVirtualDatacenters(virtualDatacenterIds);
   }

   private Iterable<VirtualDatacenter> listConcurrentVirtualDatacenters(final List<Integer> ids) {
      Iterable<VirtualDatacenterDto> vdcs = transformParallel(ids,
            new Function<Integer, Future<? extends VirtualDatacenterDto>>() {
               @Override
               public Future<VirtualDatacenterDto> apply(final Integer input) {
                  return context.getAsyncApi().getCloudApi().getVirtualDatacenter(input);
               }
            }, userExecutor, maxTime, logger, "getting virtual datacenters");

      return DomainWrapper.wrap(context, VirtualDatacenter.class, Lists.newArrayList(vdcs));
   }
}
