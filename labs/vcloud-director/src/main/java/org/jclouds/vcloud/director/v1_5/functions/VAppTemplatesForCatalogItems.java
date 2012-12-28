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
import org.jclouds.concurrent.ExceptionParsingListenableFuture;
import org.jclouds.concurrent.Futures;
import org.jclouds.logging.Logger;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.CatalogItem;
import org.jclouds.vcloud.director.v1_5.domain.VAppTemplate;
import org.jclouds.vcloud.director.v1_5.user.VCloudDirectorAsyncApi;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;

/**
 * @author danikov
 */
@Singleton
public class VAppTemplatesForCatalogItems implements Function<Iterable<CatalogItem>, Iterable<? extends VAppTemplate>> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   public Logger logger = Logger.NULL;
   private final VCloudDirectorAsyncApi aapi;
   private final ExecutorService executor;
   private final NullOnAuthorizationException NullOnAuthorizationException;

   @Singleton
   static class NullOnAuthorizationException implements Function<Exception, VAppTemplate> {

      public VAppTemplate apply(Exception from) {
         if (from instanceof AuthorizationException) {
            return null;
         }
         throw Throwables.propagate(from);
      }
   }

   @Inject
   VAppTemplatesForCatalogItems(VCloudDirectorAsyncApi aapi,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor,
            NullOnAuthorizationException NullOnAuthorizationException) {
      this.aapi = aapi;
      this.executor = executor;
      this.NullOnAuthorizationException = NullOnAuthorizationException;
   }

   @Override
   public Iterable<? extends VAppTemplate> apply(Iterable<CatalogItem> from) {
      return transformParallel(filter(from, new Predicate<CatalogItem>() {

         @Override
         public boolean apply(CatalogItem input) {
            return input.getEntity().getType().equals(VCloudDirectorMediaType.VAPP_TEMPLATE);
         }

      }), new Function<CatalogItem, Future<? extends VAppTemplate>>() {

         @Override
         public Future<? extends VAppTemplate> apply(CatalogItem from) {
            return new ExceptionParsingListenableFuture<VAppTemplate>(Futures.makeListenable(VCloudDirectorAsyncApi.class
                     .cast(aapi).getVAppTemplateApi().get(from.getEntity().getHref()), executor),
                     NullOnAuthorizationException);
         }

      }, executor, null, logger, "vappTemplates in");
   }

}
