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
package org.jclouds.vcloud.director.v1_5.functions;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.concurrent.FutureIterables;
import org.jclouds.logging.Logger;
import org.jclouds.vcloud.director.v1_5.domain.Catalog;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.org.AdminOrg;
import org.jclouds.vcloud.director.v1_5.user.VCloudDirectorAsyncApi;

import com.google.common.base.Function;

/**
 * @author danikov
 */
@Singleton
public class AllCatalogsInOrg implements Function<AdminOrg, Iterable<? extends Catalog>> {
   @Resource
   public Logger logger = Logger.NULL;

   private final VCloudDirectorAsyncApi aapi;
   private final ExecutorService executor;

   @Inject
   AllCatalogsInOrg(VCloudDirectorAsyncApi aapi, @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor) {
      this.aapi = aapi;
      this.executor = executor;
   }

   @Override
   public Iterable<? extends Catalog> apply(final AdminOrg org) {
      Iterable<? extends Catalog> catalogs = FutureIterables.<Reference, Catalog>transformParallel(org.getCatalogs(),
            new Function<Reference, Future<? extends Catalog>>() {
               @Override
               public Future<? extends Catalog> apply(Reference from) {
                  return aapi.getCatalogApi().get(from.getHref());
               }

            }, executor, null, logger, "catalogs in " + org.getName());
      return catalogs;
   }
}
