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
package org.jclouds.s3.blobstore.internal;

import static com.google.common.base.Throwables.propagate;

import javax.inject.Inject;

import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.s3.S3Client;
import org.jclouds.s3.domain.AccessControlList;

import com.google.common.annotations.Beta;
import com.google.common.cache.CacheLoader;


@Beta
public class BackoffOnNotFoundWhenGetBucketACL extends CacheLoader<String, AccessControlList> {
   private final S3Client client;
   private static final int maxTries = 5;

   @Inject
   BackoffOnNotFoundWhenGetBucketACL(S3Client client) {
      this.client = client;
   }

   @Override
   public AccessControlList load(String bucketName) {
      ResourceNotFoundException last = null;
      for (int currentTries = 0; currentTries < maxTries; currentTries++) {
         try {
            return client.getBucketACL(bucketName);
         } catch (ResourceNotFoundException e) {
            imposeBackoffExponentialDelay(100l, 200l, 2, currentTries, maxTries);
            last = e;
         }
      }
      throw last;
   }

   private static void imposeBackoffExponentialDelay(long period, long maxPeriod, int pow, int failureCount, int max) {
      long delayMs = (long) (period * Math.pow(failureCount, pow));
      delayMs = delayMs > maxPeriod ? maxPeriod : delayMs;
      try {
         Thread.sleep(delayMs);
      } catch (InterruptedException e) {
         throw propagate(e);
      }
   }

   @Override
   public String toString() {
      return "getBucketAcl()";
   }
}
