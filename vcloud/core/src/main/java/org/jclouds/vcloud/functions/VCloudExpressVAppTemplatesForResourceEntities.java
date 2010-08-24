/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import static com.google.common.base.Preconditions.checkNotNull;
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
import org.jclouds.logging.Logger;
import org.jclouds.vcloud.VCloudExpressAsyncClient;
import org.jclouds.vcloud.VCloudExpressMediaType;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.VCloudExpressVAppTemplate;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

/**
 * @author Adrian Cole
 */
@Singleton
public class VCloudExpressVAppTemplatesForResourceEntities implements
         Function<Iterable<? extends ReferenceType>, Iterable<? extends VCloudExpressVAppTemplate>> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   public Logger logger = Logger.NULL;
   private final VCloudExpressAsyncClient aclient;
   private final ExecutorService executor;

   @Inject
   VCloudExpressVAppTemplatesForResourceEntities(VCloudExpressAsyncClient aclient,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor) {
      this.aclient = aclient;
      this.executor = executor;
   }

   @Override
   public Iterable<? extends VCloudExpressVAppTemplate> apply(Iterable<? extends ReferenceType> from) {
      return transformParallel(filter(checkNotNull(from, "named resources"), new Predicate<ReferenceType>() {

         @Override
         public boolean apply(ReferenceType input) {
            return input.getType().equals(VCloudExpressMediaType.VAPPTEMPLATE_XML);
         }

      }), new Function<ReferenceType, Future<VCloudExpressVAppTemplate>>() {

         @SuppressWarnings("unchecked")
         @Override
         public Future<VCloudExpressVAppTemplate> apply(ReferenceType from) {
            return (Future<VCloudExpressVAppTemplate>) aclient.getVAppTemplate(from.getHref());
         }

      }, executor, null, logger, "vappTemplates in");
   }

}