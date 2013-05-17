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

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.functions.RegionToEndpointOrProviderIfNull;
import org.jclouds.logging.Logger;
import org.jclouds.s3.Bucket;

import com.google.common.base.Function;
import com.google.common.base.Optional;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class AssignCorrectHostnameForBucket implements Function<Object, URI> {
   @Resource
   protected Logger logger = Logger.NULL;

   protected final RegionToEndpointOrProviderIfNull r2;
   protected final Function<String, Optional<String>> bucketToRegion;

   @Inject
   public AssignCorrectHostnameForBucket(RegionToEndpointOrProviderIfNull r2,
            @Bucket Function<String, Optional<String>> bucketToRegion) {
      this.bucketToRegion = bucketToRegion;
      this.r2 = r2;
   }

   @Override
   public URI apply(@Nullable Object from) {
      String bucket = from.toString();
      Optional<String> region = bucketToRegion.apply(bucket);
      if (region.isPresent()) {
         return r2.apply(region.get());
      }
      return r2.apply(null);
   }

}
