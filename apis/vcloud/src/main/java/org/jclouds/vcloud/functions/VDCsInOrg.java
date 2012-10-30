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
package org.jclouds.vcloud.functions;

import static org.jclouds.concurrent.FutureIterables.transformParallel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.logging.Logger;
import org.jclouds.util.Iterables2;
import org.jclouds.vcloud.VCloudAsyncClient;
import org.jclouds.vcloud.domain.Org;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.VDC;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class VDCsInOrg implements Function<Org, Iterable<VDC>> {
   @Resource
   public Logger logger = Logger.NULL;

   private final VCloudAsyncClient aclient;
   private final ExecutorService executor;

   @Inject
   VDCsInOrg(VCloudAsyncClient aclient, @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor) {
      this.aclient = aclient;
      this.executor = executor;
   }

   @Override
   public Iterable<VDC> apply(final Org org) {

      Iterable<VDC> catalogItems = transformParallel(org.getVDCs().values(),
            new Function<ReferenceType, Future<? extends VDC>>() {
               @Override
               public Future<? extends VDC> apply(ReferenceType from) {
                  return  aclient.getVDCClient().getVDC(from.getHref());
               }

            }, executor, null, logger, "vdcs in org " + org.getName());
      return Iterables2.concreteCopy(catalogItems);
   }

}
