/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.s3.functions;

import javax.inject.Inject;

import org.jclouds.aws.AWSResponseException;
import org.jclouds.http.HttpRequest;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.InvocationContext;
import org.jclouds.s3.S3Client;
import org.jclouds.s3.util.S3Utils;
import org.jclouds.util.Throwables2;

import com.google.common.base.Function;
import com.google.common.base.Throwables;

/**
 * 
 * @author Adrian Cole
 */
public class ReturnFalseIfBucketAlreadyOwnedByYouOrOperationAbortedWhenBucketExists implements
         Function<Exception, Boolean>,
         InvocationContext<ReturnFalseIfBucketAlreadyOwnedByYouOrOperationAbortedWhenBucketExists> {

   private final S3Client client;
   private String bucket;

   @Inject
   ReturnFalseIfBucketAlreadyOwnedByYouOrOperationAbortedWhenBucketExists(S3Client client) {
      this.client = client;
   }

   public Boolean apply(Exception from) {
      AWSResponseException exception = Throwables2.getFirstThrowableOfType(from, AWSResponseException.class);
      if (exception != null && exception.getError() != null && exception.getError().getCode() != null) {
         String code = exception.getError().getCode();
         if (code.equals("BucketAlreadyOwnedByYou"))
            return false;
         else if (code.equals("OperationAborted") && bucket != null && client.bucketExists(bucket))
            return false;
      }
      throw Throwables.propagate(from);
   }

   @Override
   public ReturnFalseIfBucketAlreadyOwnedByYouOrOperationAbortedWhenBucketExists setContext(
            @Nullable HttpRequest request) {
      if (request != null)
         this.bucket = S3Utils.getBucketName(request);
      return this;
   }

}
