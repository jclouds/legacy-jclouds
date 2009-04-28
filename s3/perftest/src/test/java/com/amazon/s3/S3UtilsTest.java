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
package com.amazon.s3;

import org.testng.annotations.Test;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;

@Test(sequential = true, timeOut = 2 * 60 * 1000, testName = "s3.S3UtilsTest", groups = "performance")
public class S3UtilsTest extends org.jclouds.aws.s3.S3UtilsTest {

    @Test(dataProvider = "hmacsha1")
    void testAmazonSampleDigestSerialResponseTime(byte[] key, String message, String base64Digest) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException {
        for (int i = 0; i < 10000; i++)
            testAmazonSampleDigest(key, message, base64Digest);
    }

    @Test(dataProvider = "hmacsha1")
    public void testAmazonSampleDigest(byte[] key, String message, String base64Digest) {
        String encoded = Utils.encode(new String(key), message, false);
        assert encoded.equals(base64Digest);
    }

    @Test(dataProvider = "hmacsha1")
    void testAmazonSampleDigestParallelResponseTime(final byte[] key, final String message, final String base64Digest) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, InterruptedException, ExecutionException {
        CompletionService<Boolean> completer = new ExecutorCompletionService<Boolean>(exec);
        for (int i = 0; i < 10000; i++)
            completer.submit(new Callable<Boolean>() {
                public Boolean call() {
                    try {
                        testAmazonSampleDigest(key, message, base64Digest);
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                }
            });
        for (int i = 0; i < 10000; i++) assert completer.take().get();
    }
}