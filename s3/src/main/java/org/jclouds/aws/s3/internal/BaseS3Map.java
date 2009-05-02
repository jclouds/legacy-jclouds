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
package org.jclouds.aws.s3.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.Utils;
import org.jclouds.aws.s3.S3Connection;
import org.jclouds.aws.s3.S3Constants;
import org.jclouds.aws.s3.S3Map;
import org.jclouds.aws.s3.S3Utils;
import org.jclouds.aws.s3.domain.S3Bucket;
import org.jclouds.aws.s3.domain.S3Object;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

public abstract class BaseS3Map<T> implements Map<String, T>, S3Map {

    protected final S3Connection connection;
    protected final S3Bucket bucket;

    /**
     * maximum duration of an S3 Request
     */
    @Inject(optional = true)
    @Named(S3Constants.PROPERTY_AWS_MAP_TIMEOUT)
    protected long requestTimeoutMilliseconds = 10000;

    @Inject
    public BaseS3Map(S3Connection connection, @Assisted S3Bucket bucket) {
	this.connection = checkNotNull(connection, "connection");
	this.bucket = checkNotNull(bucket, "bucket");
    }

    public int size() {
	try {
	    S3Bucket bucket = refreshBucket();
	    Set<S3Object> contents = bucket.getContents();
	    return contents.size();
	} catch (Exception e) {
	    Utils.<S3RuntimeException> rethrowIfRuntimeOrSameType(e);
	    throw new S3RuntimeException("Error getting size of bucket"
		    + bucket, e);
	}
    }

    protected boolean containsETag(String eTagOfValue)
	    throws InterruptedException, ExecutionException, TimeoutException {
	for (S3Object object : refreshBucket().getContents()) {
	    if (object.getETag().equals(eTagOfValue))
		return true;
	}
	return false;
    }

    protected byte[] getMd5(Object value) throws IOException,
	    FileNotFoundException, InterruptedException, ExecutionException,
	    TimeoutException {
	byte[] md5;

	if (value instanceof InputStream) {
	    md5 = S3Utils.md5((InputStream) value);
	} else if (value instanceof byte[]) {
	    md5 = S3Utils.md5((byte[]) value);
	} else if (value instanceof String) {
	    md5 = S3Utils.md5(((String) value).getBytes());
	} else if (value instanceof File) {
	    md5 = S3Utils.md5(new FileInputStream((File) value));
	} else if (value instanceof S3Object) {
	    S3Object object = (S3Object) value;
	    object = connection.headObject(bucket, object.getKey()).get(
		    requestTimeoutMilliseconds, TimeUnit.MILLISECONDS);
	    if (S3Object.NOT_FOUND.equals(object))
		throw new FileNotFoundException("not found: " + object.getKey());
	    md5 = S3Utils.fromHexString(object.getETag());
	} else {
	    throw new IllegalArgumentException("unsupported value type: "
		    + value.getClass());
	}
	return md5;
    }

    protected Set<S3Object> getAllObjects() {
	Set<S3Object> objects = new HashSet<S3Object>();
	Set<Future<S3Object>> futureObjects = new HashSet<Future<S3Object>>();
	for (String key : keySet()) {
	    futureObjects.add(connection.getObject(bucket, key));
	}
	for (Future<S3Object> futureObject : futureObjects) {
	    S3Object object = null;
	    try {
		object = futureObject.get(requestTimeoutMilliseconds,
			TimeUnit.MILLISECONDS);
	    } catch (Exception e) {
		Utils.<S3RuntimeException> rethrowIfRuntimeOrSameType(e);
		throw new S3RuntimeException(String.format(
			"Error getting value from bucket %1s:%2s", bucket,
			object != null ? object.getKey() : "unknown"), e);
	    }
	    objects.add(object);
	}
	return objects;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jclouds.aws.s3.S3ObjectMapi#containsValue(java.lang.Object)
     */
    public boolean containsValue(Object value) {

	try {
	    byte[] md5 = getMd5(value);
	    String eTagOfValue = S3Utils.toHexString(md5);
	    return containsETag(eTagOfValue);
	} catch (Exception e) {
	    Utils.<S3RuntimeException> rethrowIfRuntimeOrSameType(e);
	    throw new S3RuntimeException(String.format(
		    "Error searching for ETAG of value: [%2s] in bucket:%1s",
		    bucket, value), e);
	}
    }

    public static class S3RuntimeException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	S3RuntimeException(String s) {
	    super(s);
	}

	public S3RuntimeException(String s, Throwable throwable) {
	    super(s, throwable);
	}
    }

    public void clear() {
	try {
	    List<Future<Boolean>> deletes = new ArrayList<Future<Boolean>>();
	    for (String key : keySet()) {
		deletes.add(connection.deleteObject(bucket, key));
	    }
	    for (Future<Boolean> isdeleted : deletes)
		if (!isdeleted.get(requestTimeoutMilliseconds,
			TimeUnit.MILLISECONDS)) {
		    throw new S3RuntimeException("failed to delete entry");
		}
	} catch (Exception e) {
	    Utils.<S3RuntimeException> rethrowIfRuntimeOrSameType(e);
	    throw new S3RuntimeException("Error clearing bucket" + bucket, e);
	}
    }

    protected S3Bucket refreshBucket() throws InterruptedException,
	    ExecutionException, TimeoutException {
	S3Bucket currentBucket = connection.getBucket(bucket).get(
		requestTimeoutMilliseconds, TimeUnit.MILLISECONDS);
	if (currentBucket == S3Bucket.NOT_FOUND)
	    throw new S3RuntimeException("bucket not found: "
		    + bucket.getName());
	else
	    return currentBucket;
    }

    public Set<String> keySet() {
	try {
	    Set<String> keys = new HashSet<String>();
	    for (S3Object object : refreshBucket().getContents())
		keys.add(object.getKey());
	    return keys;
	} catch (Exception e) {
	    Utils.<S3RuntimeException> rethrowIfRuntimeOrSameType(e);
	    throw new S3RuntimeException("Error getting keys in bucket: "
		    + bucket, e);
	}
    }

    public boolean containsKey(Object key) {
	try {
	    return connection.headObject(bucket, key.toString()).get(
		    requestTimeoutMilliseconds, TimeUnit.MILLISECONDS) != S3Object.NOT_FOUND;
	} catch (Exception e) {
	    Utils.<S3RuntimeException> rethrowIfRuntimeOrSameType(e);
	    throw new S3RuntimeException(String.format(
		    "Error searching for %1s:%2s", bucket, key), e);
	}
    }

    public boolean isEmpty() {
	return keySet().size() == 0;
    }

    public S3Bucket getBucket() {
	return bucket;
    }

}