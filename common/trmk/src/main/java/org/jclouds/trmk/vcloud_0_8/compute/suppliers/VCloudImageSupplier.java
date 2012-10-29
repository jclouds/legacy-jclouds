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
package org.jclouds.trmk.vcloud_0_8.compute.suppliers;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static org.jclouds.concurrent.FutureIterables.transformParallel;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.trmk.vcloud_0_8.domain.Org;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

/**
 * @author Adrian Cole
 */
@Singleton
public class VCloudImageSupplier implements Supplier<Set<? extends Image>> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   public Logger logger = Logger.NULL;

   private final Supplier<Map<String, ? extends Org>> orgMap;
   private final Function<Org, Iterable<? extends Image>> imagesInOrg;
   private final ExecutorService executor;

   @Inject
   VCloudImageSupplier(Supplier<Map<String, ? extends Org>> orgMap,
            Function<Org, Iterable<? extends Image>> imagesInOrg,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor) {
      this.orgMap = checkNotNull(orgMap, "orgMap");
      this.imagesInOrg = checkNotNull(imagesInOrg, "imagesInOrg");
      this.executor = checkNotNull(executor, "executor");
   }

   @Override
   public Set<? extends Image> get() {
      Iterable<? extends Org> orgs = checkNotNull(orgMap.get().values(), "orgs");
      Iterable<? extends Iterable<? extends Image>> images = transformParallel(orgs,
               new Function<Org, Future<? extends Iterable<? extends Image>>>() {

                  @Override
                  public Future<Iterable<? extends Image>> apply(final Org from) {
                     checkNotNull(from, "org");
                     return executor.submit(new Callable<Iterable<? extends Image>>() {

                        @Override
                        public Iterable<? extends Image> call() throws Exception {
                           return imagesInOrg.apply(from);
                        }

                        @Override
                        public String toString() {
                           return "imagesInOrg(" + from.getHref() + ")";
                        }
                     });
                  }

               }, executor, null, logger, "images in " + orgs);
      return newLinkedHashSet(concat(images));
   }
}
