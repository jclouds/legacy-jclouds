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

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * // TODO: Adrian: Document this!
 * 
 * @author Adrian Cole
 */
@Test(sequential = true, timeOut = 2 * 60 * 1000, testName = "s3.AmazonPerformance", groups = "performance")
public class AmazonPerformance extends BasePerformance {
    private AWSAuthConnection amzClient;

    @Override
    @BeforeTest
    @Parameters( { "jclouds.aws.accesskeyid", "jclouds.aws.secretaccesskey" })
    protected void setUpClient(@Optional String AWSAccessKeyId, @Optional String AWSSecretAccessKey) throws Exception {
	super.setUpClient(AWSAccessKeyId, AWSSecretAccessKey);
	amzClient = new AWSAuthConnection(AWSAccessKeyId, AWSSecretAccessKey,
		false);
    }

    @Override
    protected void testPutFileSerial() throws Exception {
	throw new UnsupportedOperationException();
    }

    @Override
    protected void testPutFileParallel() throws InterruptedException,
	    ExecutionException {
	throw new UnsupportedOperationException();
    }

    @Override
    protected void testPutInputStreamSerial() throws Exception {
	throw new UnsupportedOperationException();
    }

    @Override
    protected void testPutInputStreamParallel() throws InterruptedException,
	    ExecutionException {
	throw new UnsupportedOperationException();
    }

    @Override
    protected void testPutStringSerial() throws Exception {
	throw new UnsupportedOperationException();
    }

    @Override
    protected void testPutStringParallel() throws InterruptedException,
	    ExecutionException {
	throw new UnsupportedOperationException();
    }

    @Override
    protected boolean putByteArray(String bucket, String key, byte[] data,
	    String contentType) throws Exception {
	com.amazon.s3.S3Object object = new com.amazon.s3.S3Object(data, null);
	Map headers = new TreeMap();
	headers
		.put("Content-Type", Arrays
			.asList(new String[] { contentType }));
	return amzClient.put(bucket, key, object, headers).connection
		.getResponseMessage() != null;
    }

    @Override
    protected boolean putFile(String bucket, String key, File data,
	    String contentType) throws Exception {
	throw new UnsupportedOperationException();
    }

    @Override
    protected boolean putInputStream(String bucket, String key,
	    InputStream data, String contentType) throws Exception {
	throw new UnsupportedOperationException();
    }

    @Override
    protected boolean putString(String bucket, String key, String data,
	    String contentType) throws Exception {
	throw new UnsupportedOperationException();
    }

}
