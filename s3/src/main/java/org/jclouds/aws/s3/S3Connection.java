/**
 *
 * Copyright (C) 2009 Adrian Cole <adriancole@jclouds.org>
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

import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.domain.S3Bucket;

import java.io.IOException;
import java.io.Closeable;
import java.util.List;
import java.util.concurrent.Future;

/**
 * // TODO: Adrian: Document this!
 *
 * @author Adrian Cole
 */
public interface S3Connection extends Closeable {
    Future<S3Object> getObject(S3Bucket s3Bucket, String key);

    Future<S3Object> headObject(S3Bucket s3Bucket, String key);

    Future<Boolean> deleteObject(S3Bucket s3Bucket, String key);

    Future<String> addObject(S3Bucket s3Bucket, S3Object object);

    Future<Boolean> createBucketIfNotExists(S3Bucket s3Bucket);

    Future<Boolean> deleteBucket(S3Bucket s3Bucket);

    Future<Boolean> copyObject(S3Bucket sourceBucket, S3Object sourceObject, S3Bucket destinationBucket, S3Object destinationObject);

    Future<Boolean> bucketExists(S3Bucket s3Bucket);

    Future<S3Bucket> getBucket(S3Bucket s3Bucket);

    Future<List<S3Bucket>> getBuckets();

    public void close() throws IOException;
}
