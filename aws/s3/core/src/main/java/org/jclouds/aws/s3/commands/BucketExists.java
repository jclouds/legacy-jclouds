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

import static org.jclouds.aws.s3.commands.options.ListBucketOptions.Builder.maxResults;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.http.HttpMethod;
import org.jclouds.http.HttpResponseException;
import org.jclouds.http.commands.callables.ReturnTrueIf2xx;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

/**
 * Issues a HEAD command to determine if the bucket exists or not.
 * 
 * @author Adrian Cole
 * 
 */
public class BucketExists extends S3FutureCommand<Boolean> {

   @Inject
   public BucketExists(@Named("jclouds.http.address") String amazonHost, ReturnTrueIf2xx callable,
            @Assisted String s3Bucket) {
      super(HttpMethod.HEAD, "/" + maxResults(0).buildQueryString(), callable, amazonHost, s3Bucket);
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
      if (e.getCause() != null && e.getCause() instanceof HttpResponseException) {
         HttpResponseException responseException = (HttpResponseException) e.getCause();
         if (responseException.getResponse().getStatusCode() == 404) {
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