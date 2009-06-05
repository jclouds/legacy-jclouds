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
package org.jclouds.aws.s3;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.aws.s3.domain.S3Bucket;

/**
 * CreateListOwnedBuckets is a class contaning operations to creates a bucket if it doesn't exist
 * and lists all buckets owned by the user.
 * 
 * @author Carlos Fernandes
 */
public class CreateListOwnedBuckets {

   S3Context s3Context;

   public CreateListOwnedBuckets(S3Context context) {
      this.s3Context = context;
   }

   public List<S3Bucket.Metadata> list() throws InterruptedException, ExecutionException,
            TimeoutException {
      return s3Context.getConnection().listOwnedBuckets().get(10, TimeUnit.SECONDS);
   }

   public Boolean createBucket(String bucketName) throws InterruptedException, ExecutionException,
            TimeoutException {
      return s3Context.getConnection().putBucketIfNotExists(bucketName).get(10, TimeUnit.SECONDS);
   }
}
