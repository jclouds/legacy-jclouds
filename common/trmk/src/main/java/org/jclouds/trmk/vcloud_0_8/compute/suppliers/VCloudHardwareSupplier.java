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
package org.jclouds.trmk.vcloud_0_8.compute.suppliers;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static org.jclouds.concurrent.FutureIterables.transformParallel;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.trmk.vcloud_0_8.domain.Org;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

/**
 * @author Adrian Cole
 */
@Singleton
public class VCloudHardwareSupplier implements Supplier<Set<? extends Hardware>> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   public Logger logger = Logger.NULL;

   private final Supplier<Map<String, ? extends Org>> orgMap;
   private final Function<Org, Iterable<? extends Hardware>> sizesInOrg;
   private final ListeningExecutorService userExecutor;

   @Inject
   VCloudHardwareSupplier(Supplier<Map<String, ? extends Org>> orgMap,
            Function<Org, Iterable<? extends Hardware>> sizesInOrg,
            @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor) {
      this.orgMap = checkNotNull(orgMap, "orgMap");
      this.sizesInOrg = checkNotNull(sizesInOrg, "sizesInOrg");
      this.userExecutor = checkNotNull(userExecutor, "userExecutor");
   }

   @Override
   public Set<? extends Hardware> get() {
      Iterable<? extends Org> orgs = checkNotNull(orgMap.get().values(), "orgs");
      Iterable<? extends Iterable<? extends Hardware>> sizes = transformParallel(orgs,
               new Function<Org, ListenableFuture<? extends Iterable<? extends Hardware>>>() {

                  @Override
                  public ListenableFuture<Iterable<? extends Hardware>> apply(final Org from) {
                     checkNotNull(from, "org");
                     return userExecutor.submit(new Callable<Iterable<? extends Hardware>>() {

                        @Override
                        public Iterable<? extends Hardware> call() throws Exception {
                           return sizesInOrg.apply(from);
                        }

                        @Override
                        public String toString() {
                           return "sizesInOrg(" + from.getHref() + ")";
                        }
                     });
                  }

               }, userExecutor, null, logger, "sizes in " + orgs);
      return newLinkedHashSet(concat(sizes));
   }
}
