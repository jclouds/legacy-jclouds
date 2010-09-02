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

package org.jclouds.chef.strategy.internal;

import static com.google.common.collect.Iterables.filter;
import static org.jclouds.concurrent.FutureIterables.transformParallel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.chef.ChefAsyncClient;
import org.jclouds.chef.ChefClient;
import org.jclouds.chef.domain.Client;
import org.jclouds.chef.reference.ChefConstants;
import org.jclouds.chef.strategy.ListClients;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.inject.Inject;

/**
 * 
 * 
 * @author Adrian Cole
 */
@Singleton
public class ListClientsImpl implements ListClients {

   protected final ChefClient chefClient;
   protected final ChefAsyncClient chefAsyncClient;
   protected final ExecutorService userExecutor;
   @Resource
   @Named(ChefConstants.CHEF_LOGGER)
   protected Logger logger = Logger.NULL;

   @Inject(optional = true)
   @Named(Constants.PROPERTY_REQUEST_TIMEOUT)
   protected Long maxTime;

   @Inject
   ListClientsImpl(@Named(Constants.PROPERTY_USER_THREADS) ExecutorService userExecutor, ChefClient getAllClient,
            ChefAsyncClient ablobstore) {
      this.userExecutor = userExecutor;
      this.chefAsyncClient = ablobstore;
      this.chefClient = getAllClient;
   }

   @Override
   public Iterable<? extends Client> execute() {
      return execute(chefClient.listClients());
   }

   @Override
   public Iterable<? extends Client> execute(Predicate<String> clientNameSelector) {
      return execute(filter(chefClient.listClients(), clientNameSelector));
   }

   @Override
   public Iterable<? extends Client> execute(Iterable<String> toGet) {
      return transformParallel(toGet, new Function<String, Future<Client>>() {

         @Override
         public Future<Client> apply(String from) {
            return chefAsyncClient.getClient(from);
         }

      }, userExecutor, maxTime, logger, "getting clients");

   }

}