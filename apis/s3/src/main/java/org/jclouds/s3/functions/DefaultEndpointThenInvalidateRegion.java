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

import java.net.URI;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.s3.Bucket;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.cache.LoadingCache;

/**
 *
 * @author Adrian Cole
 */
@Singleton
public class DefaultEndpointThenInvalidateRegion implements Function<Object, URI> {

   private final LoadingCache<String, Optional<String>> bucketToRegionCache;
   private final AssignCorrectHostnameForBucket r2;

   @Inject
   public DefaultEndpointThenInvalidateRegion(AssignCorrectHostnameForBucket r2,
            @Bucket LoadingCache<String, Optional<String>> bucketToRegionCache) {
      this.r2 = r2;
      this.bucketToRegionCache = bucketToRegionCache;
   }

   @Override
   public URI apply(@Nullable Object from) {
      try {
         return r2.apply(from);
      } finally {
         bucketToRegionCache.invalidate(from.toString());
      }
   }
}
