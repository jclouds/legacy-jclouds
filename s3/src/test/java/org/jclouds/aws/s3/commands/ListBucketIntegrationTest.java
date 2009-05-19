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
import static org.jclouds.aws.s3.commands.options.ListBucketOptions.Builder.*;
import org.jclouds.aws.s3.domain.S3Bucket;
import org.jclouds.aws.s3.domain.S3Object;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Tests integrated functionality of all getBucket commands.
 * <p/>
 * Each test uses a different bucket name, so it should be perfectly fine to run
 * in parallel.
 *
 * @author Adrian Cole
 */
@Test(groups = {"integration", "live"}, testName = "s3.ListBucketIntegrationTest")
public class ListBucketIntegrationTest extends S3IntegrationTest {

    @Test()
    void testListBucketDelimiter() throws InterruptedException,
            ExecutionException, TimeoutException, UnsupportedEncodingException {
        String prefix = "apps";
        addTenObjectsUnderPrefix(bucketName, prefix);
        add15UnderRoot(bucketName);
        S3Bucket bucket = client.listBucket(bucketName, delimiter("/")).get(10,
                TimeUnit.SECONDS);
        assertEquals(bucket.getDelimiter(), "/");
        assert !bucket.isTruncated();
        assertEquals(bucket.getContents().size(), 15);
        assertEquals(bucket.getCommonPrefixes().size(), 1);
    }

    private void addAlphabetUnderRoot(String bucketName)
            throws InterruptedException, ExecutionException, TimeoutException {
        for (char letter = 'a'; letter <= 'z'; letter++) {
            client.putObject(bucketName,
                    new S3Object(letter + "", letter + "content")).get(10,
                    TimeUnit.SECONDS);
        }
    }

    @Test
    void testListBucketMarker() throws InterruptedException,
            ExecutionException, TimeoutException, UnsupportedEncodingException {
        addAlphabetUnderRoot(bucketName);
        S3Bucket bucket = client.listBucket(bucketName, afterMarker("y")).get(
                10, TimeUnit.SECONDS);
        assertEquals(bucket.getMarker(), "y");
        assert !bucket.isTruncated();
        assertEquals(bucket.getContents().size(), 1);
    }

    @Test
    void testListBucketMaxResults() throws InterruptedException,
            ExecutionException, TimeoutException, UnsupportedEncodingException {
        addAlphabetUnderRoot(bucketName);
        S3Bucket bucket = client.listBucket(bucketName, maxResults(5)).get(10,
                TimeUnit.SECONDS);
        assertEquals(bucket.getMaxKeys(), 5);
        assert bucket.isTruncated();
        assertEquals(bucket.getContents().size(), 5);
    }

    @Test()
    void testListBucketPrefix() throws InterruptedException,
            ExecutionException, TimeoutException, UnsupportedEncodingException {
        String prefix = "apps";
        addTenObjectsUnderPrefix(bucketName, prefix);
        add15UnderRoot(bucketName);

        S3Bucket bucket = client.listBucket(bucketName, withPrefix("apps/"))
                .get(10, TimeUnit.SECONDS);
        assert !bucket.isTruncated();
        assertEquals(bucket.getContents().size(), 10);
        assertEquals(bucket.getPrefix(), "apps/");

    }

    @Test()
    void testListBucket() throws InterruptedException,
            ExecutionException, TimeoutException, UnsupportedEncodingException {
        String prefix = "apps";
        addTenObjectsUnderPrefix(bucketName, prefix);
        S3Bucket bucket = client.listBucket(bucketName)
                .get(10, TimeUnit.SECONDS);
        assertEquals(bucket.getContents().size(), 10);
    }

    private void add15UnderRoot(String bucketName) throws InterruptedException,
            ExecutionException, TimeoutException {
        for (int i = 0; i < 15; i++)
            client.putObject(bucketName, new S3Object(i + "", i + "content"))
                    .get(10, TimeUnit.SECONDS);
    }

    private void addTenObjectsUnderPrefix(String bucketName, String prefix)
            throws InterruptedException, ExecutionException, TimeoutException {
        for (int i = 0; i < 10; i++)
            client.putObject(bucketName,
                    new S3Object(prefix + "/" + i, i + "content")).get(10,
                    TimeUnit.SECONDS);
    }

}