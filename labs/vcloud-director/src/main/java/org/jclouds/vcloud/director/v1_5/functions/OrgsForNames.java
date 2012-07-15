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

import static org.jclouds.concurrent.FutureIterables.transformParallel;
import static org.jclouds.vcloud.director.v1_5.predicates.ReferencePredicates.nameEquals;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.logging.Logger;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.org.Org;
import org.jclouds.vcloud.director.v1_5.user.VCloudDirectorAsyncApi;
import org.jclouds.vcloud.director.v1_5.user.VCloudDirectorApi;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * @author danikov
 */
@Singleton
public class OrgsForNames implements Function<Iterable<String>, Iterable<? extends Org>> {
   @Resource
   public Logger logger = Logger.NULL;
   private final VCloudDirectorAsyncApi aapi;
   private final VCloudDirectorApi sapi;
   private final ExecutorService executor;

   @Inject
   OrgsForNames(VCloudDirectorAsyncApi aapi, 
         VCloudDirectorApi sapi, 
         @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor) {
      this.aapi = aapi;
      this.sapi = sapi;
      this.executor = executor;
   }

   @Override
   public Iterable<? extends Org> apply(Iterable<String> from) {
      final Set<Reference> orgs = sapi.getOrgApi().getOrgList().getOrgs();
      
      return transformParallel(from, new Function<String, Future<? extends Org>>() {

         @Override
         public Future<? extends Org> apply(String from) {
            return aapi.getOrgApi().getOrg(Iterables.find(orgs, nameEquals(from)).getHref());
         }

      }, executor, null, logger, "organizations for names");
   }

}
