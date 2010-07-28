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

import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static org.jclouds.concurrent.ConcurrentUtils.awaitCompletion;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.chef.ChefAsyncClient;
import org.jclouds.chef.ChefClient;
import org.jclouds.chef.domain.Node;
import org.jclouds.chef.reference.ChefConstants;
import org.jclouds.chef.strategy.GetNodes;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;

/**
 * 
 * 
 * @author Adrian Cole
 */
@Singleton
public class GetNodesImpl implements GetNodes {

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
   GetNodesImpl(@Named(Constants.PROPERTY_USER_THREADS) ExecutorService userExecutor, ChefClient getAllNode,
         ChefAsyncClient ablobstore) {
      this.userExecutor = userExecutor;
      this.chefAsyncClient = ablobstore;
      this.chefClient = getAllNode;
   }

   @Override
   public Set<Node> execute() {
      return execute(chefClient.listNodes());
   }

   @Override
   public Set<Node> execute(Predicate<String> nodeNameSelector) {
      return execute(filter(chefClient.listNodes(), nodeNameSelector));
   }

   @Override
   public Set<Node> execute(Iterable<String> toGet) {
      Map<String, Exception> exceptions = newHashMap();
      final Set<Node> nodes = newHashSet();
      Map<String, ListenableFuture<?>> responses = newHashMap();
      for (String nodeName : toGet) {
         final ListenableFuture<? extends Node> future = chefAsyncClient.getNode(nodeName);
         future.addListener(new Runnable() {
            @Override
            public void run() {
               try {
                  nodes.add(future.get());
               } catch (InterruptedException e) {
                  propagate(e);
               } catch (ExecutionException e) {
                  propagate(e);
               }
            }
         }, sameThreadExecutor());
         responses.put(nodeName, future);
      }
      exceptions = awaitCompletion(responses, userExecutor, maxTime, logger, String.format("getting nodes: %s", toGet));
      if (exceptions.size() > 0)
         throw new RuntimeException(String.format("errors getting  nodes: %s: %s", toGet, exceptions));
      return nodes;
   }
}