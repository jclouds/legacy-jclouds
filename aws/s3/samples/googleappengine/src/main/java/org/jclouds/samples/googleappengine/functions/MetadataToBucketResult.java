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
package org.jclouds.samples.googleappengine.functions;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.jclouds.aws.s3.S3Connection;
import org.jclouds.aws.s3.domain.S3Bucket;
import org.jclouds.aws.s3.domain.S3Bucket.Metadata;
import org.jclouds.logging.Logger;
import org.jclouds.samples.googleappengine.domain.BucketResult;

import com.google.common.base.Function;
import com.google.inject.Inject;

public class MetadataToBucketResult implements Function<S3Bucket.Metadata, BucketResult> {
   private final S3Connection connection;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public MetadataToBucketResult(S3Connection connection) {
      this.connection = connection;
   }

   public BucketResult apply(Metadata from) {
      BucketResult result = new BucketResult();
      result.setName(from.getName());
      try {
         S3Bucket bucket = connection.listBucket(from.getName()).get(10, TimeUnit.SECONDS);
         if (bucket == S3Bucket.NOT_FOUND) {
            result.setStatus("not found");
         } else {
            result.setSize(bucket.getSize() + "");
         }
      } catch (Exception e) {
         logger.error(e, "Error listing bucket %1$s", result.getName());
         result.setStatus(e.getMessage());
      }
      return result;
   }
}