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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.jclouds.aws.s3.S3Constants;
import org.jclouds.aws.s3.S3IntegrationTest;
import org.jclouds.aws.s3.domain.S3Bucket;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.google.inject.Provider;

/**
 * // TODO: Adrian: Document this!
 * 
 * @author Adrian Cole
 */
public abstract class BasePerformance extends S3IntegrationTest {
    protected boolean debugEnabled() {
	return false;
    }

    protected static int LOOP_COUNT = 100;

    protected ExecutorService exec;
    protected final String BUCKET_BYTES = bucketPrefix + "-bytes";
    protected final String BUCKET_INPUTSTREAM = bucketPrefix + "-inputstream";
    protected final String BUCKET_STRING = bucketPrefix + "-string";
    protected final String BUCKET_FILE = bucketPrefix + "-file";
    protected final String[] BUCKETS = { BUCKET_BYTES, BUCKET_INPUTSTREAM,
	    BUCKET_STRING, BUCKET_FILE };
    protected PutBytesCallable putBytesCallable;
    protected PutFileCallable putFileCallable;
    protected PutInputStreamCallable putInputStreamCallable;
    protected PutStringCallable putStringCallable;

    protected CompletionService<Boolean> completer;

    @BeforeTest
    protected void setUpCallables() {
	putBytesCallable = new PutBytesCallable();
	putFileCallable = new PutFileCallable();
	putInputStreamCallable = new PutInputStreamCallable();
	putStringCallable = new PutStringCallable();
	exec = Executors.newCachedThreadPool();
	completer = new ExecutorCompletionService<Boolean>(exec);
    }

    @Override
    @BeforeTest
    @Parameters( { S3Constants.PROPERTY_AWS_ACCESSKEYID,
	    S3Constants.PROPERTY_AWS_SECRETACCESSKEY })
    protected void setUpClient(@Optional String AWSAccessKeyId,
	    @Optional String AWSSecretAccessKey) throws Exception {
	super.setUpClient(AWSAccessKeyId, AWSSecretAccessKey);
	for (String bucket : BUCKETS) {
	    client.createBucketIfNotExists(new S3Bucket(bucket)).get(10,
		    TimeUnit.SECONDS);
	}
    }

    @AfterTest
    protected void tearDownExecutor() throws Exception {
	exec.shutdownNow();
	exec = null;
    }

    @Test(enabled = true)
    public void testPutBytesSerial() throws Exception {
	doSerial(putBytesCallable, LOOP_COUNT / 10);
    }

    @Test(enabled = true)
    public void testPutBytesParallel() throws InterruptedException,
	    ExecutionException, TimeoutException {
	doParallel(putBytesCallable, LOOP_COUNT);
    }

    @Test(enabled = true)
    public void testPutFileSerial() throws Exception {
	doSerial(putFileCallable, LOOP_COUNT / 10);
    }

    @Test(enabled = true)
    public void testPutFileParallel() throws InterruptedException,
	    ExecutionException, TimeoutException {
	doParallel(putFileCallable, LOOP_COUNT);
    }

    @Test(enabled = true)
    public void testPutInputStreamSerial() throws Exception {
	doSerial(putInputStreamCallable, LOOP_COUNT / 10);
    }

    @Test(enabled = true)
    public void testPutInputStreamParallel() throws InterruptedException,
	    ExecutionException, TimeoutException {
	doParallel(putInputStreamCallable, LOOP_COUNT);
    }

    @Test(enabled = true)
    public void testPutStringSerial() throws Exception {
	doSerial(putStringCallable, LOOP_COUNT / 10);
    }

    @Test(enabled = true)
    public void testPutStringParallel() throws InterruptedException,
	    ExecutionException, TimeoutException {
	doParallel(putStringCallable, LOOP_COUNT);
    }

    private void doSerial(Provider<Callable<Boolean>> provider, int loopCount)
	    throws Exception, ExecutionException {
	for (int i = 0; i < loopCount; i++)
	    assert provider.get().call();
    }

    private void doParallel(Provider<Callable<Boolean>> provider, int loopCount)
	    throws InterruptedException, ExecutionException, TimeoutException {
	for (int i = 0; i < loopCount; i++)
	    completer.submit(provider.get());
	for (int i = 0; i < loopCount; i++)
	    assert completer.take().get(10, TimeUnit.SECONDS);
    }

    class PutBytesCallable implements Provider<Callable<Boolean>> {
	final AtomicInteger key = new AtomicInteger(0);
	protected byte[] test = new byte[1024 * 2];

	public Callable<Boolean> get() {
	    return new Callable<Boolean>() {
		public Boolean call() throws Exception {
		    return putByteArray(BUCKET_BYTES, key.getAndIncrement()
			    + "", test, "application/octetstring");
		}
	    };

	}
    }

    class PutFileCallable implements Provider<Callable<Boolean>> {
	final AtomicInteger key = new AtomicInteger(0);
	protected File file = new File(
		"/Users/adriancole/Desktop/charles_ca_certificate.zip");

	public Callable<Boolean> get() {
	    return new Callable<Boolean>() {
		public Boolean call() throws Exception {
		    return putFile(BUCKET_FILE, key.getAndIncrement() + "",
			    file, "application/zip");
		}
	    };

	}
    }

    class PutInputStreamCallable extends PutBytesCallable {
	final AtomicInteger key = new AtomicInteger(0);

	@Override
	public Callable<Boolean> get() {
	    return new Callable<Boolean>() {
		public Boolean call() throws Exception {
		    return putInputStream(BUCKET_INPUTSTREAM, key
			    .getAndIncrement()
			    + "", new ByteArrayInputStream(test),
			    "application/octetstring");
		}
	    };

	}
    }

    class PutStringCallable implements Provider<Callable<Boolean>> {
	final AtomicInteger key = new AtomicInteger(0);
	protected String testString = "hello world!";

	public Callable<Boolean> get() {
	    return new Callable<Boolean>() {
		public Boolean call() throws Exception {
		    return putString(BUCKET_STRING, key.getAndIncrement() + "",
			    testString, "text/plain");
		}
	    };

	}
    }

    protected abstract boolean putByteArray(String bucket, String key,
	    byte[] data, String contentType) throws Exception;

    protected abstract boolean putFile(String bucket, String key, File data,
	    String contentType) throws Exception;

    protected abstract boolean putInputStream(String bucket, String key,
	    InputStream data, String contentType) throws Exception;

    protected abstract boolean putString(String bucket, String key,
	    String data, String contentType) throws Exception;

    // private class BucketDeleter implements Callable<Boolean> {
    // private BucketDeleter(S3Bucket bucket) {
    // this.bucket = bucket;
    // }
    //
    // private S3Bucket bucket;
    //
    // @Override
    // public String toString() {
    // return "BucketDeleter{" + "bucket=" + bucket + '}';
    // }
    //
    // public Boolean call() throws Exception {
    // bucket =
    // clientProvider.get(10,TimeUnit.SECONDS).getBucket(bucket).get(10,TimeUnit.SECONDS);
    // List<Future<Boolean>> deletes = new ArrayList<Future<Boolean>>();
    // for (org.jclouds.aws.s3.domain.S3Object object : bucket
    // .getContents()) {
    // deletes.add(clientProvider.get(10,TimeUnit.SECONDS).deleteObject(bucket,
    // object.getKey()));
    // }
    // for (Future<Boolean> isdeleted : deletes)
    // assert isdeleted.get(10,TimeUnit.SECONDS) :
    // String.format("failed to delete %1s",
    // isdeleted);
    // return
    // clientProvider.get(10,TimeUnit.SECONDS).deleteBucket(bucket).get(10,TimeUnit.SECONDS);
    // }
    // }
}
