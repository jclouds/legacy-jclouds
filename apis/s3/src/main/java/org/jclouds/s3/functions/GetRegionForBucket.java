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
package org.jclouds.s3.functions;

import java.util.concurrent.ExecutionException;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.logging.Logger;
import org.jclouds.s3.Bucket;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.cache.CacheLoader.InvalidCacheLoadException;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class GetRegionForBucket implements Function<String, Optional<String>> {
   @Resource
   protected Logger logger = Logger.NULL;

   protected final LoadingCache<String, Optional<String>> bucketToRegion;

   @Inject
   public GetRegionForBucket(@Bucket LoadingCache<String, Optional<String>> bucketToRegion) {
      this.bucketToRegion = bucketToRegion;
   }

   @Override
   public Optional<String> apply(String bucket) {
      try {
         return bucketToRegion.get(bucket);
      } catch (ExecutionException e) {
         logger.debug("error looking up region for bucket %s: %s", bucket, e);
      } catch (UncheckedExecutionException e) {
         logger.debug("error looking up region for bucket %s: %s", bucket, e);
      } catch (InvalidCacheLoadException e) {
         logger.trace("bucket %s not found: %s", bucket, e);
      }
      return Optional.absent();
   }
}
