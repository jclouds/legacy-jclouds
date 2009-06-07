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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.aws.s3.commands.callables.ParseMetadataFromHeaders;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.http.HttpResponseException;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * Retrieves the metadata associated with the Key or
 * {@link org.jclouds.aws.s3.domain.S3Object.Metadata#NOT_FOUND} if not available.
 * 
 * <p/>
 * The HEAD operation is used to retrieve information about a specific object or object size,
 * without actually fetching the object itself. This is useful if you're only interested in the
 * object metadata, and don't want to waste bandwidth on the object data.
 * 
 * @see GetObject
 * @see <a
 *      href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/index.html?RESTObjectHEAD.html"
 *      />
 * @author Adrian Cole
 * 
 */
public class HeadObject extends S3FutureCommand<S3Object.Metadata> {

   @Inject
   public HeadObject(ParseMetadataFromHeaders callable, @Assisted("bucketName") String bucket,
            @Assisted("key") String key) {
      super("HEAD", "/" + checkNotNull(key), callable, bucket);
      callable.setKey(key);
   }

   @Override
   public S3Object.Metadata get() throws InterruptedException, ExecutionException {
      try {
         return super.get();
      } catch (ExecutionException e) {
         return attemptNotFound(e);
      }
   }

   @VisibleForTesting
   S3Object.Metadata attemptNotFound(ExecutionException e) throws ExecutionException {
      if (e.getCause() != null && e.getCause() instanceof HttpResponseException) {
         HttpResponseException responseException = (HttpResponseException) e.getCause();
         if (responseException.getResponse().getStatusCode() == 404) {
            return S3Object.Metadata.NOT_FOUND;
         }
      }
      throw e;
   }

   @Override
   public S3Object.Metadata get(long l, TimeUnit timeUnit) throws InterruptedException,
            ExecutionException, TimeoutException {
      try {
         return super.get(l, timeUnit);
      } catch (ExecutionException e) {
         return attemptNotFound(e);
      }
   }
}