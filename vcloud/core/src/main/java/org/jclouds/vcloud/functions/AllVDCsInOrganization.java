/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.Organization;
import org.jclouds.vcloud.domain.VDC;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class AllVDCsInOrganization implements Function<Organization, Iterable<? extends VDC>> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   public Logger logger = Logger.NULL;

   private final VCloudAsyncClient aclient;
   private final ExecutorService executor;

   @Inject
   AllVDCsInOrganization(VCloudAsyncClient aclient, @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor) {
      this.aclient = aclient;
      this.executor = executor;
   }

   @Override
   public Iterable<? extends VDC> apply(final Organization org) {

      Iterable<VDC> catalogItems = transformParallel(org.getVDCs().values(),
            new Function<NamedResource, Future<VDC>>() {
               @SuppressWarnings("unchecked")
               @Override
               public Future<VDC> apply(NamedResource from) {
                  return (Future<VDC>) aclient.getVDCInOrg(org.getName(), from.getName());
               }

            }, executor, null, logger, "vdcs in " + org.getName());
      return catalogItems;
   }

}