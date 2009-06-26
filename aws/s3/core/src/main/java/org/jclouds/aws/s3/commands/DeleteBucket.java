/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.aws.s3.commands;

import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.aws.AWSResponseException;
import org.jclouds.http.HttpMethod;
import org.jclouds.http.commands.callables.ReturnTrueIf2xx;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * The DELETE request operation deletes the bucket named in the URI. All objects in the bucket must
 * be deleted before the bucket itself can be deleted.
 * <p />
 * Only the owner of a bucket can delete it, regardless of the bucket's access control policy.
 * 
 * @see <a
 *      href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/index.html?RESTBucketDELETE.html"
 *      />
 * @author Adrian Cole
 */
public class DeleteBucket extends S3FutureCommand<Boolean> {

   @Inject
   public DeleteBucket(URI endPoint, ReturnTrueIf2xx callable, @Assisted String s3Bucket) {
      super(endPoint, HttpMethod.DELETE, "/", callable, s3Bucket);
   }

   @Override
   public Boolean get() throws InterruptedException, ExecutionException {
      try {
         return super.get();
      } catch (ExecutionException e) {
         return attemptNotFound(e);
      }
   }

   @VisibleForTesting
   Boolean attemptNotFound(ExecutionException e) throws ExecutionException {
      if (e.getCause() != null && e.getCause() instanceof AWSResponseException) {
         AWSResponseException responseException = (AWSResponseException) e.getCause();
         if (responseException.getResponse().getStatusCode() == 404) {
            return true;
         } else if ("BucketNotEmpty".equals(responseException.getError().getCode())
                  || responseException.getResponse().getStatusCode() == 409) {
            return false;
         }
      }
      throw e;
   }

   @Override
   public Boolean get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException,
            TimeoutException {
      try {
         return super.get(l, timeUnit);
      } catch (ExecutionException e) {
         return attemptNotFound(e);
      }
   }
}