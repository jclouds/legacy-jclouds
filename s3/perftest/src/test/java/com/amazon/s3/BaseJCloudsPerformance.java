/**
 *
 * Copyright (C) 2009 Adrian Cole <adrian@jclouds.org>
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
package com.amazon.s3;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * // TODO: Adrian: Document this!
 *
 * @author Adrian Cole
 */
public abstract class BaseJCloudsPerformance extends BasePerformance {
    // boolean get
    // (
    // int id) throws Exception {
    // S3Bucket s3Bucket = new S3Bucket();
    // s3Bucket.setName(bucketPrefix + "-jclouds-puts");
    // org.jclouds.aws.s3.domain.S3Object object = new
    // org.jclouds.aws.s3.domain.S3Object();
    // object.setKey(id + "");
    // //object.setContentType("text/plain");
    // object.setContentType("application/octetstream");
    // //object.setData("this is a test");
    // object.setData(test);
    // return clientProvider.getObject(s3Bucket,
    // object.getKey()).get(120,TimeUnit.SECONDS) !=
    // org.jclouds.aws.s3.domain.S3Object.NOT_FOUND;

    // }

    @Override
    protected boolean putByteArray(String bucket, String key, byte[] data,
                                   String contentType) throws Exception {
        org.jclouds.aws.s3.domain.S3Object object = new org.jclouds.aws.s3.domain.S3Object(
                key);
        object.getMetadata().setContentType(contentType);
        object.setData(data);
        return client.putObject(bucket, object).get(120, TimeUnit.SECONDS) != null;
    }

    @Override
    protected boolean putFile(String bucket, String key, File data,
                              String contentType) throws Exception {
        org.jclouds.aws.s3.domain.S3Object object = new org.jclouds.aws.s3.domain.S3Object(
                key);
        object.getMetadata().setContentType(contentType);
        object.setData(data);
        return client.putObject(bucket, object).get(120, TimeUnit.SECONDS) != null;
    }

    @Override
    protected boolean putInputStream(String bucket, String key,
                                     InputStream data, String contentType) throws Exception {
        org.jclouds.aws.s3.domain.S3Object object = new org.jclouds.aws.s3.domain.S3Object(
                key);
        object.getMetadata().setContentType(contentType);
        object.setData(data);
        object.getMetadata().setSize(data.available());
        return client.putObject(bucket, object).get(120, TimeUnit.SECONDS) != null;
    }

    @Override
    protected boolean putString(String bucket, String key, String data,
                                String contentType) throws Exception {
        org.jclouds.aws.s3.domain.S3Object object = new org.jclouds.aws.s3.domain.S3Object(
                key);
        object.getMetadata().setContentType(contentType);
        object.setData(data);
        return client.putObject(bucket, object).get(120, TimeUnit.SECONDS) != null;
    }
}
