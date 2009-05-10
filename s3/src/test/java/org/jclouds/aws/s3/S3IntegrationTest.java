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
package org.jclouds.aws.s3;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.jclouds.aws.s3.domain.S3Bucket;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.reference.S3Constants;
import org.jclouds.aws.s3.util.S3Utils;
import org.jclouds.http.HttpConstants;
import org.jclouds.http.config.JavaUrlHttpFutureCommandClientModule;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;


import com.google.inject.Module;

@Test
public class S3IntegrationTest {
    protected static final String TEST_STRING = "<apples><apple name=\"fuji\" /></apples>";

    protected byte[] goodMd5;
    protected byte[] badMd5;

    protected void createBucketAndEnsureEmpty(String sourceBucket)
	    throws InterruptedException, ExecutionException, TimeoutException {
	client.putBucketIfNotExists(sourceBucket).get(10, TimeUnit.SECONDS);
	assertEquals(client.listBucket(sourceBucket).get(10, TimeUnit.SECONDS)
		.getContents().size(), 0);
    }

    protected void addObjectToBucket(String sourceBucket, String key)
	    throws InterruptedException, ExecutionException, TimeoutException,
	    IOException {
	S3Object sourceObject = new S3Object(key);
	sourceObject.getMetadata().setContentType("text/xml");
	sourceObject.setData(TEST_STRING);
	addObjectToBucket(sourceBucket, sourceObject);
    }

    protected void addObjectToBucket(String sourceBucket, S3Object object)
	    throws InterruptedException, ExecutionException, TimeoutException,
	    IOException {
	;
	client.putObject(sourceBucket, object).get(10, TimeUnit.SECONDS);
    }

    protected S3Object validateContent(String sourceBucket, String key)
	    throws InterruptedException, ExecutionException, TimeoutException,
	    IOException {
	assertEquals(client.listBucket(sourceBucket).get(10, TimeUnit.SECONDS)
		.getContents().size(), 1);
	S3Object newObject = client.getObject(sourceBucket, key).get(10,
		TimeUnit.SECONDS);
	assert newObject != S3Object.NOT_FOUND;
	assertEquals(S3Utils.getContentAsStringAndClose(newObject), TEST_STRING);
	return newObject;
    }

    @BeforeTest
    void enableDebug() {
	if (debugEnabled()) {
	    Handler HANDLER = new ConsoleHandler() {
		{
		    setLevel(Level.ALL);
		    setFormatter(new Formatter() {

			@Override
			public String format(LogRecord record) {
			    return String.format(
				    "[%tT %-7s] [%-7s] [%s]: %s %s\n",
				    new Date(record.getMillis()), record
					    .getLevel(), Thread.currentThread()
					    .getName(), record.getLoggerName(),
				    record.getMessage(),
				    record.getThrown() == null ? "" : record
					    .getThrown());
			}
		    });
		}
	    };
	    Logger guiceLogger = Logger.getLogger("org.jclouds");
	    guiceLogger.addHandler(HANDLER);
	    guiceLogger.setLevel(Level.ALL);
	}
    }

    protected S3Connection client;
    protected S3Context context = null;

    protected String bucketPrefix = (System.getProperty("user.name") + "." + this
	    .getClass().getSimpleName()).toLowerCase();

    private static final String sysAWSAccessKeyId = System
	    .getProperty(S3Constants.PROPERTY_AWS_ACCESSKEYID);
    private static final String sysAWSSecretAccessKey = System
	    .getProperty(S3Constants.PROPERTY_AWS_SECRETACCESSKEY);

    @BeforeTest
    @Parameters( { S3Constants.PROPERTY_AWS_ACCESSKEYID,
	    S3Constants.PROPERTY_AWS_SECRETACCESSKEY })
    protected void setUpClient(@Optional String AWSAccessKeyId,
	    @Optional String AWSSecretAccessKey) throws Exception {
	context = createS3Context(AWSAccessKeyId != null ? AWSAccessKeyId
		: sysAWSAccessKeyId,
		AWSSecretAccessKey != null ? AWSSecretAccessKey
			: sysAWSSecretAccessKey);
	client = context.getConnection();
	deleteEverything();
	goodMd5 = S3Utils.md5(TEST_STRING);
	badMd5 = S3Utils.md5("alf");
    }

    protected boolean debugEnabled() {
	return false;
    }

    protected S3Context createS3Context(String AWSAccessKeyId,
	    String AWSSecretAccessKey) {
	return S3ContextFactory.createS3Context(buildS3Properties(
		AWSAccessKeyId, AWSSecretAccessKey), createHttpModule());
    }

    protected Properties buildS3Properties(String AWSAccessKeyId,
	    String AWSSecretAccessKey) {
	Properties properties = new Properties(
		S3ContextFactory.DEFAULT_PROPERTIES);
	properties.setProperty(S3Constants.PROPERTY_AWS_ACCESSKEYID,
		checkNotNull(AWSAccessKeyId, "AWSAccessKeyId"));
	properties.setProperty(S3Constants.PROPERTY_AWS_SECRETACCESSKEY,
		checkNotNull(AWSSecretAccessKey, "AWSSecretAccessKey"));
	properties.setProperty(HttpConstants.PROPERTY_HTTP_SECURE, "false");
	properties.setProperty(HttpConstants.PROPERTY_HTTP_PORT, "80");
	return properties;
    }

    protected Module createHttpModule() {
	return new JavaUrlHttpFutureCommandClientModule();
    }

    protected void deleteEverything() throws Exception {
	try {
	    List<S3Bucket.Metadata> metadata = client.listOwnedBuckets().get(10,
		    TimeUnit.SECONDS);
	    List<Future<Boolean>> results = new ArrayList<Future<Boolean>>();
	    for (S3Bucket.Metadata metaDatum : metadata) {
		if (metaDatum.getName().startsWith(bucketPrefix.toLowerCase())) {
		    S3Bucket bucket = client.listBucket(metaDatum.getName())
			    .get(10, TimeUnit.SECONDS);
		    for (S3Object.Metadata objectMeta : bucket.getContents()) {
			results.add(client.deleteObject(metaDatum.getName(),
				objectMeta.getKey()));
		    }
		    Iterator<Future<Boolean>> iterator = results.iterator();
		    while (iterator.hasNext()) {
			iterator.next().get(10, TimeUnit.SECONDS);
			iterator.remove();
		    }
		    client.deleteBucketIfEmpty(metaDatum.getName()).get(10,
			    TimeUnit.SECONDS);
		}

	    }
	} catch (CancellationException e) {
	    throw e;
	}
    }

    @AfterTest
    protected void tearDownClient() throws Exception {
	deleteEverything();
	context.close();
	context = null;
    }

}