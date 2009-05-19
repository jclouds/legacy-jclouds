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
 *   http:www.apache.org/licenses/LICENSE-2.0
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
import org.jclouds.aws.s3.S3ResponseException;
import static org.jclouds.aws.s3.commands.options.GetObjectOptions.Builder.*;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.util.S3Utils;
import org.jclouds.http.HttpResponseException;
import org.joda.time.DateTime;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Tests integrated functionality of all GetObject commands.
 * <p/>
 * Each test uses a different bucket name, so it should be perfectly fine to run
 * in parallel.
 *
 * @author Adrian Cole
 */
@Test(groups = {"integration", "live"}, testName = "s3.GetObjectIntegrationTest")
public class GetObjectIntegrationTest extends S3IntegrationTest {

    @Test
    void testGetIfModifiedSince() throws InterruptedException,
            ExecutionException, TimeoutException, IOException {

        String key = "apples";

        DateTime before = new DateTime();
        addObjectAndValidateContent(bucketName, key);
        DateTime after = new DateTime().plusSeconds(1);

        client.getObject(bucketName, key, ifModifiedSince(before)).get(10,
                TimeUnit.SECONDS);
        validateContent(bucketName, key);

        try {
            client.getObject(bucketName, key, ifModifiedSince(after)).get(10,
                    TimeUnit.SECONDS);
            validateContent(bucketName, key);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof HttpResponseException) {
                HttpResponseException ex = (HttpResponseException) e.getCause();
                assertEquals(ex.getResponse().getStatusCode(), 304);
            } else {
                throw e;
            }
        }

    }

    @Test
    void testGetIfUnmodifiedSince() throws InterruptedException,
            ExecutionException, TimeoutException, IOException {

        String key = "apples";

        DateTime before = new DateTime();
        addObjectAndValidateContent(bucketName, key);
        DateTime after = new DateTime().plusSeconds(1);

        client.getObject(bucketName, key, ifUnmodifiedSince(after)).get(10,
                TimeUnit.SECONDS);
        validateContent(bucketName, key);

        try {
            client.getObject(bucketName, key, ifUnmodifiedSince(before)).get(10,
                    TimeUnit.SECONDS);
            validateContent(bucketName, key);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof HttpResponseException) {
                HttpResponseException ex = (HttpResponseException) e.getCause();
                assertEquals(ex.getResponse().getStatusCode(), 412);
            } else {
                throw e;
            }
        }

    }

    @Test
    void testGetIfMatch() throws InterruptedException, ExecutionException,
            TimeoutException, IOException {

        String key = "apples";

        addObjectAndValidateContent(bucketName, key);

        client.getObject(bucketName, key, ifMd5Matches(goodMd5)).get(10,
                TimeUnit.SECONDS);
        validateContent(bucketName, key);

        try {
            client.getObject(bucketName, key, ifMd5Matches(badMd5)).get(10,
                    TimeUnit.SECONDS);
            validateContent(bucketName, key);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof HttpResponseException) {
                HttpResponseException ex = (HttpResponseException) e.getCause();
                assertEquals(ex.getResponse().getStatusCode(), 412);
            } else {
                throw e;
            }
        }
    }

    @Test
    void testGetIfNoneMatch() throws InterruptedException, ExecutionException,
            TimeoutException, IOException {

        String key = "apples";

        addObjectAndValidateContent(bucketName, key);

        client.getObject(bucketName, key, ifMd5DoesntMatch(badMd5)).get(10,
                TimeUnit.SECONDS);
        validateContent(bucketName, key);

        try {
            client.getObject(bucketName, key, ifMd5DoesntMatch(goodMd5)).get(10,
                    TimeUnit.SECONDS);
            validateContent(bucketName, key);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof HttpResponseException) {
                HttpResponseException ex = (HttpResponseException) e.getCause();
                assertEquals(ex.getResponse().getStatusCode(), 304);
            } else {
                throw e;
            }
        }
    }

    @Test
    void testGetRange() throws InterruptedException, ExecutionException,
            TimeoutException, IOException {

        String key = "apples";

        addObjectAndValidateContent(bucketName, key);
        S3Object object1 = client.getObject(bucketName, key, range(0, 5)).get(10,
                TimeUnit.SECONDS);
        assertEquals(S3Utils.getContentAsStringAndClose(object1), TEST_STRING
                .substring(0, 6));

        S3Object object2 = client.getObject(bucketName, key,
                range(6, TEST_STRING.length())).get(10, TimeUnit.SECONDS);
        assertEquals(S3Utils.getContentAsStringAndClose(object2), TEST_STRING
                .substring(6, TEST_STRING.length()));
    }

    @Test
    void testGetTwoRanges() throws InterruptedException, ExecutionException,
            TimeoutException, IOException {

        String key = "apples";

        addObjectAndValidateContent(bucketName, key);
        S3Object object = client.getObject(bucketName, key,
                range(0, 5).range(6, TEST_STRING.length())).get(10,
                TimeUnit.SECONDS);

        assertEquals(S3Utils.getContentAsStringAndClose(object), TEST_STRING);
    }

    @Test
    void testGetTail() throws InterruptedException, ExecutionException,
            TimeoutException, IOException {

        String key = "apples";

        addObjectAndValidateContent(bucketName, key);
        S3Object object = client.getObject(bucketName, key, tail(5)).get(10,
                TimeUnit.SECONDS);
        assertEquals(S3Utils.getContentAsStringAndClose(object), TEST_STRING
                .substring(TEST_STRING.length() - 5));
        assertEquals(object.getContentLength(), 5);
        assertEquals(object.getMetadata().getSize(), TEST_STRING.length());

    }

    @Test
    void testGetStartAt() throws InterruptedException, ExecutionException,
            TimeoutException, IOException {

        String key = "apples";

        addObjectAndValidateContent(bucketName, key);
        S3Object object = client.getObject(bucketName, key, startAt(5)).get(10,
                TimeUnit.SECONDS);
        assertEquals(S3Utils.getContentAsStringAndClose(object), TEST_STRING
                .substring(5, TEST_STRING.length()));
        assertEquals(object.getContentLength(), TEST_STRING.length() - 5);
        assertEquals(object.getMetadata().getSize(), TEST_STRING.length());
    }

    private void addObjectAndValidateContent(String sourcebucketName, String sourceKey)
            throws InterruptedException, ExecutionException, TimeoutException,
            IOException {
        addObjectToBucket(sourcebucketName, sourceKey);
        validateContent(sourcebucketName, sourceKey);
    }
}