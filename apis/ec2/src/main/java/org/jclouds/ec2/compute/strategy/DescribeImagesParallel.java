/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.ec2.compute.strategy;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.util.concurrent.Futures.allAsList;
import static com.google.common.util.concurrent.Futures.getUnchecked;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.ec2.EC2Api;
import org.jclouds.ec2.options.DescribeImagesOptions;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class DescribeImagesParallel implements
         Function<Iterable<Entry<String, DescribeImagesOptions>>, Iterable<? extends org.jclouds.ec2.domain.Image>> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   protected final EC2Api api;
   final ListeningExecutorService userExecutor;

   @Inject
   public DescribeImagesParallel(EC2Api api, @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor) {
      this.api = api;
      this.userExecutor = userExecutor;
   }

   @Override
   public Iterable<? extends org.jclouds.ec2.domain.Image> apply(
            final Iterable<Entry<String, DescribeImagesOptions>> queries) {
      ListenableFuture<List<Set<? extends org.jclouds.ec2.domain.Image>>> futures
         = allAsList(transform(
                            queries,
                            new Function<Entry<String, DescribeImagesOptions>,
                            ListenableFuture<? extends Set<? extends org.jclouds.ec2.domain.Image>>>() {
                               public ListenableFuture<Set<? extends org.jclouds.ec2.domain.Image>> apply(
                                                                                                          final Entry<String, DescribeImagesOptions> from) {
                                  return userExecutor.submit(new Callable<Set<? extends org.jclouds.ec2.domain.Image>>() {
                                        @Override
                                        public Set<? extends org.jclouds.ec2.domain.Image> call() throws Exception {
                                           return api.getAMIApi().get().describeImagesInRegion(from.getKey(), from.getValue());
                                        }
                                     });
                               }
                            }));
      logger.trace("amis");

      return concat(getUnchecked(futures));
   }
}
