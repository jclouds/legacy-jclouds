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
package org.jclouds.trmk.vcloud_0_8.functions;

import static org.jclouds.concurrent.FutureIterables.transformParallel;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.logging.Logger;
import org.jclouds.trmk.vcloud_0_8.TerremarkVCloudAsyncClient;
import org.jclouds.trmk.vcloud_0_8.domain.Catalog;
import org.jclouds.trmk.vcloud_0_8.domain.Org;
import org.jclouds.trmk.vcloud_0_8.domain.ReferenceType;

import com.google.common.base.Function;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

/**
 * @author Adrian Cole
 */
@Singleton
public class AllCatalogsInOrg implements Function<Org, Iterable<? extends Catalog>> {
   @Resource
   public Logger logger = Logger.NULL;

   private final TerremarkVCloudAsyncClient aclient;
   private final ListeningExecutorService userExecutor;

   @Inject
   AllCatalogsInOrg(TerremarkVCloudAsyncClient aclient, @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor) {
      this.aclient = aclient;
      this.userExecutor = userExecutor;
   }

   @Override
   public Iterable<? extends Catalog> apply(final Org org) {
      return transformParallel(org.getCatalogs().values(),
            new Function<ReferenceType, ListenableFuture<? extends Catalog>>() {
               public ListenableFuture<? extends Catalog> apply(ReferenceType from) {
                  return aclient.getCatalog(from.getHref());
               }
            }, userExecutor, null, logger, "catalogs in " + org.getName());
   }
}
