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
package org.jclouds.aws.s3.commands;

import org.jclouds.aws.s3.S3IntegrationTest;
import org.jclouds.aws.s3.domain.S3Bucket;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Tests integrated functionality of all listOwnedBucket commands.
 * <p/>
 * Each test uses a different bucket name, so it should be perfectly fine to run
 * in parallel.
 *
 * @author Adrian Cole
 */
@Test(groups = {"integration", "live"}, testName = "s3.ListOwnedBucketsIntegrationTest")
public class ListOwnedBucketsIntegrationTest extends S3IntegrationTest {

    @Test()
    void bucketDoesntExist() throws Exception {
        String bucketName = bucketPrefix + "shouldntexist";
        List<S3Bucket.Metadata> list = client.listOwnedBuckets().get(10,
                TimeUnit.SECONDS);
        assert !list.contains(new S3Bucket(bucketName));
    }

    @Test()
    void bucketExists() throws Exception {
        String bucketName = bucketPrefix + "needstoexist";
        assert client.putBucketIfNotExists(bucketName)
                .get(10, TimeUnit.SECONDS);
        List<S3Bucket.Metadata> list = client.listOwnedBuckets().get(10,
                TimeUnit.SECONDS);
        S3Bucket.Metadata firstBucket = list.get(0);
        S3Bucket.Metadata toMatch = new S3Bucket.Metadata(bucketName);
        toMatch.setOwner(firstBucket.getOwner());

        assert list.contains(toMatch);
    }
}