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
import java.util.concurrent.ExecutionException;

import org.jets3t.service.S3Service;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.security.AWSCredentials;
import org.testng.annotations.Test;

/**
 * // TODO: Adrian: Document this!
 * 
 * @author Adrian Cole
 */
@Test(sequential = true, timeOut = 2 * 60 * 1000, testName = "s3.Jets3tPerformance", groups = "performance")
public class Jets3tPerformance extends BasePerformance {
    private S3Service jetClient;

    @Override
    protected void setUpClient(String AWSAccessKeyId, String AWSSecretAccessKey)
	    throws Exception {
	super.setUpClient(AWSAccessKeyId, AWSSecretAccessKey);
	jetClient = new RestS3Service(new AWSCredentials(AWSAccessKeyId,
		AWSSecretAccessKey));
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
    protected void testPutBytesSerial() throws Exception {
	throw new UnsupportedOperationException();
    }

    @Override
    protected void testPutBytesParallel() throws InterruptedException,
	    ExecutionException {
	throw new UnsupportedOperationException();
    }

    @Override
    protected boolean putByteArray(String bucket, String key, byte[] data,
	    String contentType) throws Exception {
	throw new UnsupportedOperationException();

    }

    @Override
    protected boolean putFile(String bucket, String key, File data,
	    String contentType) throws Exception {
	org.jets3t.service.model.S3Object object = new org.jets3t.service.model.S3Object(
		key);
	object.setContentType(contentType);
	object.setDataInputFile(data);
	object.setContentLength(data.length());
	return jetClient.putObject(bucket, object) != null;
    }

    @Override
    protected boolean putInputStream(String bucket, String key,
	    InputStream data, String contentType) throws Exception {
	org.jets3t.service.model.S3Object object = new org.jets3t.service.model.S3Object(
		key);
	object.setContentType(contentType);
	object.setDataInputStream(data);
	object.setContentLength(data.available());
	return jetClient.putObject(bucket, object) != null;
    }

    @Override
    protected boolean putString(String bucket, String key, String data,
	    String contentType) throws Exception {
	throw new UnsupportedOperationException();
    }

}