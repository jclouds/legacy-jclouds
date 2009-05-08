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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.aws.s3.commands.options.CopyObjectOptions;
import org.jclouds.aws.s3.commands.options.GetObjectOptions;
import org.jclouds.aws.s3.commands.options.ListBucketOptions;
import org.jclouds.aws.s3.commands.options.PutBucketOptions;
import org.jclouds.aws.s3.commands.options.PutObjectOptions;
import org.jclouds.aws.s3.domain.S3Bucket;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.domain.S3Bucket.Metadata;

/**
 * // TODO: Adrian: Document this!
 * 
 * @author Adrian Cole
 */
public class StubS3Connection implements S3Connection {
    private static Map<String, Map<String, Object>> bucketToContents = new ConcurrentHashMap<String, Map<String, Object>>();

    public Future<S3Object> getObject(final String s3Bucket, final String key) {
	return new FutureBase<S3Object>() {
	    public S3Object get() throws InterruptedException,
		    ExecutionException {
		if (!bucketToContents.containsKey(s3Bucket))
		    return S3Object.NOT_FOUND;
		Map<String, Object> realContents = bucketToContents
			.get(s3Bucket);
		if (!realContents.containsKey(key))
		    return S3Object.NOT_FOUND;
		S3Object object = new S3Object(key);
		object.setData(realContents.get(key));
		return object;
	    }
	};
    }

    public Future<S3Object.Metadata> headObject(final String s3Bucket,
	    final String key) {
	return new FutureBase<S3Object.Metadata>() {
	    public S3Object.Metadata get() throws InterruptedException,
		    ExecutionException {
		if (!bucketToContents.containsKey(s3Bucket))
		    return S3Object.Metadata.NOT_FOUND;
		Map<String, Object> realContents = bucketToContents
			.get(s3Bucket);
		if (!realContents.containsKey(key))
		    return S3Object.Metadata.NOT_FOUND;
		S3Object.Metadata metaData = new S3Object.Metadata(key);
		return metaData;
	    }
	};
    }

    public Future<Boolean> deleteObject(final String s3Bucket, final String key) {
	return new FutureBase<Boolean>() {
	    public Boolean get() throws InterruptedException,
		    ExecutionException {
		if (bucketToContents.containsKey(s3Bucket)) {
		    bucketToContents.get(s3Bucket).remove(key);
		}
		return true;
	    }
	};
    }

    public Future<byte[]> putObject(final String s3Bucket, final S3Object object) {
	return new FutureBase<byte[]>() {
	    public byte[] get() throws InterruptedException, ExecutionException {
		if (!bucketToContents.containsKey(s3Bucket)) {
		    throw new ExecutionException(new RuntimeException(
			    "bucket not found: " + s3Bucket));
		}
		bucketToContents.get(s3Bucket).put(object.getKey(),
			object.getClass());
		return object.getKey().getBytes();// todo actually md5
	    }
	};
    }

    public Future<Boolean> putBucketIfNotExists(final String s3Bucket) {
	return new FutureBase<Boolean>() {
	    public Boolean get() throws InterruptedException,
		    ExecutionException {
		if (!bucketToContents.containsKey(s3Bucket)) {
		    bucketToContents.put(s3Bucket,
			    new ConcurrentHashMap<String, Object>());
		}
		return bucketToContents.containsKey(s3Bucket);
	    }
	};
    }

    public Future<Boolean> deleteBucketIfEmpty(final String s3Bucket) {
	return new FutureBase<Boolean>() {
	    public Boolean get() throws InterruptedException,
		    ExecutionException {
		if (bucketToContents.containsKey(s3Bucket)) {
		    if (bucketToContents.get(s3Bucket).size() == 0)
			return true;
		}
		return false;
	    }
	};
    }

    public Future<S3Object.Metadata> copyObject(final String sourceBucket,
	    final String sourceObject, final String destinationBucket,
	    final String destinationObject) {
	return new FutureBase<S3Object.Metadata>() {
	    public S3Object.Metadata get() throws InterruptedException,
		    ExecutionException {
		Map<String, Object> source = bucketToContents.get(sourceBucket);
		Map<String, Object> dest = bucketToContents
			.get(destinationBucket);
		if (source.containsKey(sourceObject)) {
		    dest.put(destinationObject, source.get(sourceObject));
		    return new S3Object.Metadata(destinationObject);
		}
		return S3Object.Metadata.NOT_FOUND;
	    }
	};
    }

    public Future<Boolean> bucketExists(final String s3Bucket) {
	return new FutureBase<Boolean>() {
	    public Boolean get() throws InterruptedException,
		    ExecutionException {
		return bucketToContents.containsKey(s3Bucket);
	    }
	};
    }

    public Future<S3Bucket> listBucket(final String s3Bucket) {
	return new FutureBase<S3Bucket>() {
	    public S3Bucket get() throws InterruptedException,
		    ExecutionException {
		Set<S3Object.Metadata> contents = new HashSet<S3Object.Metadata>();
		Map<String, Object> realContents = bucketToContents
			.get(s3Bucket);
		if (realContents != null) {
		    for (String key : realContents.keySet()) {
			S3Object.Metadata metaData = new S3Object.Metadata(key);
			contents.add(metaData);
		    }
		}
		S3Bucket returnVal = new S3Bucket(s3Bucket);
		returnVal.setContents(contents);
		return returnVal;
	    }
	}

	;
    }

    private abstract class FutureBase<V> implements Future<V> {
	public boolean cancel(boolean b) {
	    return false;
	}

	public boolean isCancelled() {
	    return false;
	}

	public boolean isDone() {
	    return true;
	}

	public V get(long l, TimeUnit timeUnit) throws InterruptedException,
		ExecutionException, TimeoutException {
	    return get();
	}
    }

    public Future<List<Metadata>> getOwnedBuckets() {
	return new FutureBase<List<S3Bucket.Metadata>>() {
	    public List<S3Bucket.Metadata> get() throws InterruptedException,
		    ExecutionException {
		List<S3Bucket.Metadata> list = new ArrayList<S3Bucket.Metadata>();
		for (String name : bucketToContents.keySet())
		    list.add(new S3Bucket.Metadata(name));
		return list;
	    }
	};
    }

    public Future<Boolean> putBucketIfNotExists(String name,
	    PutBucketOptions options) {
	throw new UnsupportedOperationException("todo");
    }

    public Future<S3Bucket> listBucket(String name, ListBucketOptions options) {
	throw new UnsupportedOperationException("todo");
    }

    public Future<org.jclouds.aws.s3.domain.S3Object.Metadata> copyObject(
	    String sourceBucket, String sourceObject, String destinationBucket,
	    String destinationObject, CopyObjectOptions options) {
	// TODO Auto-generated method stub
	return null;
    }

    public Future<byte[]> putObject(String bucketName, S3Object object,
	    PutObjectOptions options) {
	// TODO Auto-generated method stub
	return null;
    }

    public Future<S3Object> getObject(String bucketName, String key,
	    GetObjectOptions options) {
	// TODO Auto-generated method stub
	return null;
    }

}
