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
package org.jclouds.s3.blobstore.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;
import java.util.concurrent.Callable;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.internal.PageSetImpl;
import org.jclouds.concurrent.FutureIterables;
import org.jclouds.logging.Logger;
import org.jclouds.s3.domain.BucketMetadata;

import com.google.common.base.Function;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

@Singleton
public class BucketsToStorageMetadata implements
         Function<Set<BucketMetadata>, PageSet<? extends StorageMetadata>> {

   @Resource
   protected Logger logger = Logger.NULL;
   
   private final ListeningExecutorService userExecutor;
   private final BucketToResourceMetadata bucket2ResourceMd;

   @Inject
   public BucketsToStorageMetadata(@Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor, BucketToResourceMetadata bucket2ResourceMd) {
      this.userExecutor = checkNotNull(userExecutor, "userExecutor");
      this.bucket2ResourceMd = checkNotNull(bucket2ResourceMd, "bucket2ResourceMd");
   }


   @Override
   public PageSet<? extends StorageMetadata> apply(Set<BucketMetadata> input) {
      // parallel as listing buckets is slow when looking up regions
      Iterable<? extends StorageMetadata> buckets = FutureIterables
               .<BucketMetadata, StorageMetadata> transformParallel(input,
                        new Function<BucketMetadata, ListenableFuture<? extends StorageMetadata>>() {
                           @Override
                           public ListenableFuture<? extends StorageMetadata> apply(final BucketMetadata from) {
                              return userExecutor.submit(new Callable<StorageMetadata>() {

                                 @Override
                                 public StorageMetadata call() throws Exception {
                                    return bucket2ResourceMd.apply(from);
                                 }

                                 @Override
                                 public String toString() {
                                    return "bucket2ResourceMd.apply(" + from + ")";
                                 }
                              });
                           }

                        }, userExecutor, null, logger, "my buckets");
      return new PageSetImpl<StorageMetadata>(buckets, null);
   }

}
