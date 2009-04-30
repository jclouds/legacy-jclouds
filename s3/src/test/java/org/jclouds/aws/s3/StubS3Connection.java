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

import org.jclouds.aws.s3.domain.S3Bucket;
import org.jclouds.aws.s3.domain.S3Object;

/**
 * // TODO: Adrian: Document this!
 * 
 * @author Adrian Cole
 */
public class StubS3Connection implements S3Connection {
    private static Map<S3Bucket, Map<String, Object>> bucketToContents = new ConcurrentHashMap<S3Bucket, Map<String, Object>>();

    public Future<S3Object> getObject(final S3Bucket s3Bucket, final String key) {
	return new FutureBase<S3Object>() {
	    public S3Object get() throws InterruptedException,
		    ExecutionException {
		if (!bucketToContents.containsKey(s3Bucket))
		    return S3Object.NOT_FOUND;
		Map<String, Object> realContents = bucketToContents
			.get(s3Bucket);
		if (!realContents.containsKey(key))
		    return S3Object.NOT_FOUND;
		S3Object object = new S3Object();
		object.setKey(key);
		object.setContent(realContents.get(key));
		return object;
	    }
	};
    }

    public Future<S3Object> headObject(S3Bucket s3Bucket, String key) {
	return getObject(s3Bucket, key);
    }

    public Future<Boolean> deleteObject(final S3Bucket s3Bucket,
	    final String key) {
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

    public Future<String> addObject(final S3Bucket s3Bucket,
	    final S3Object object) {
	return new FutureBase<String>() {
	    public String get() throws InterruptedException, ExecutionException {
		if (!bucketToContents.containsKey(s3Bucket)) {
		    throw new ExecutionException(new RuntimeException(
			    "bucket not found: " + s3Bucket.getName()));
		}
		bucketToContents.get(s3Bucket).put(object.getKey(),
			object.getClass());
		return object.getKey();
	    }
	};
    }

    public Future<Boolean> createBucketIfNotExists(final S3Bucket s3Bucket) {
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

    public Future<Boolean> deleteBucket(final S3Bucket s3Bucket) {
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

    public Future<Boolean> copyObject(final S3Bucket sourceBucket,
	    final S3Object sourceObject, final S3Bucket destinationBucket,
	    final S3Object destinationObject) {
	return new FutureBase<Boolean>() {
	    public Boolean get() throws InterruptedException,
		    ExecutionException {
		Map<String, Object> source = bucketToContents.get(sourceBucket);
		Map<String, Object> dest = bucketToContents
			.get(destinationBucket);
		if (source.containsKey(sourceObject.getKey())) {
		    dest.put(destinationObject.getKey(), source
			    .get(sourceObject.getKey()));
		    return true;
		}
		return false;
	    }
	};
    }

    public Future<Boolean> bucketExists(final S3Bucket s3Bucket) {
	return new FutureBase<Boolean>() {
	    public Boolean get() throws InterruptedException,
		    ExecutionException {
		return bucketToContents.containsKey(s3Bucket);
	    }
	};
    }

    public Future<S3Bucket> getBucket(final S3Bucket s3Bucket) {
	return new FutureBase<S3Bucket>() {
	    public S3Bucket get() throws InterruptedException,
		    ExecutionException {
		Set<S3Object> contents = new HashSet<S3Object>();
		Map<String, Object> realContents = bucketToContents
			.get(s3Bucket);
		if (realContents != null) {
		    for (String key : realContents.keySet()) {
			S3Object object = new S3Object();
			object.setKey(key);
			object.setContent(realContents.get(key));
			contents.add(object);
		    }
		}
		s3Bucket.setContents(contents);
		return s3Bucket;
	    }
	}

	;
    }

    public Future<List<S3Bucket>> getBuckets() {
	return new FutureBase<List<S3Bucket>>() {
	    public List<S3Bucket> get() throws InterruptedException,
		    ExecutionException {
		return new ArrayList<S3Bucket>(bucketToContents.keySet());
	    }
	};
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

}
