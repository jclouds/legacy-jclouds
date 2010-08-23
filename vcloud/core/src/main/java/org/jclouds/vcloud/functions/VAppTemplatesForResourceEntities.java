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
import org.jclouds.vcloud.VCloudAsyncClient;
import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.VAppTemplate;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

/**
 * @author Adrian Cole
 */
@Singleton
public class VAppTemplatesForResourceEntities implements
      Function<Iterable<? extends NamedResource>, Iterable<? extends VAppTemplate>> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   public Logger logger = Logger.NULL;
   private final VCloudAsyncClient aclient;
   private final ExecutorService executor;

   @Inject
   VAppTemplatesForResourceEntities(VCloudAsyncClient aclient,
         @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor) {
      this.aclient = aclient;
      this.executor = executor;
   }

   @Override
   public Iterable<? extends VAppTemplate> apply(Iterable<? extends NamedResource> from) {
      return transformParallel(filter(checkNotNull(from, "named resources"), new Predicate<NamedResource>() {

         @Override
         public boolean apply(NamedResource input) {
            return input.getType().equals(VCloudMediaType.VAPPTEMPLATE_XML);
         }

      }), new Function<NamedResource, Future<VAppTemplate>>() {

         @SuppressWarnings("unchecked")
         @Override
         public Future<VAppTemplate> apply(NamedResource from) {
            return (Future<VAppTemplate>) aclient.getVAppTemplate(from.getId());
         }

      }, executor, null, logger, "vappTemplates in");
   }

}