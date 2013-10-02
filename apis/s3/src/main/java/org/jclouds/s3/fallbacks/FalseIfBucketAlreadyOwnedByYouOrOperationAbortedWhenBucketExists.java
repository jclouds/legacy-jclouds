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
package org.jclouds.s3.fallbacks;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.util.concurrent.Futures.immediateFuture;
import static org.jclouds.s3.util.S3Utils.getBucketName;
import static org.jclouds.util.Throwables2.getFirstThrowableOfType;

import javax.inject.Inject;

import org.jclouds.Fallback;
import org.jclouds.aws.AWSResponseException;
import org.jclouds.http.HttpRequest;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.InvocationContext;
import org.jclouds.s3.S3Client;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * 
 * @author Adrian Cole
 */
public class FalseIfBucketAlreadyOwnedByYouOrOperationAbortedWhenBucketExists implements Fallback<Boolean>,
      InvocationContext<FalseIfBucketAlreadyOwnedByYouOrOperationAbortedWhenBucketExists> {

   private final S3Client client;
   private String bucket;

   @Inject
   FalseIfBucketAlreadyOwnedByYouOrOperationAbortedWhenBucketExists(S3Client client) {
      this.client = client;
   }

   @Override
   public ListenableFuture<Boolean> create(Throwable t) throws Exception {
      return immediateFuture(createOrPropagate(t));
   }

   @Override
   public Boolean createOrPropagate(Throwable t) throws Exception {
      AWSResponseException exception = getFirstThrowableOfType(checkNotNull(t, "throwable"), AWSResponseException.class);
      if (exception != null && exception.getError() != null && exception.getError().getCode() != null) {
         String code = exception.getError().getCode();
         if (code.equals("BucketAlreadyOwnedByYou"))
            return false;
         else if (code.equals("OperationAborted") && bucket != null && client.bucketExists(bucket))
            return false;
      }
      throw propagate(t);
   }

   @Override
   public FalseIfBucketAlreadyOwnedByYouOrOperationAbortedWhenBucketExists setContext(@Nullable HttpRequest request) {
      if (request != null)
         this.bucket = getBucketName(request);
      return this;
   }

}
