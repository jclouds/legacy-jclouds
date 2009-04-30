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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.io.IOUtils;
import org.jclouds.Utils;
import org.jclouds.aws.s3.S3Connection;
import org.jclouds.aws.s3.S3ObjectMap;
import org.jclouds.aws.s3.S3Utils;
import org.jclouds.aws.s3.domain.S3Bucket;
import org.jclouds.aws.s3.domain.S3Object;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * Map representation of a live connection to S3.
 * 
 * @author Adrian Cole
 */
public class LiveS3ObjectMap implements S3ObjectMap {

    private final S3Connection connection;
    private final S3Bucket bucket;

    @Inject
    public LiveS3ObjectMap(S3Connection connection, @Assisted S3Bucket bucket) {
	this.connection = connection;
	this.bucket = bucket;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jclouds.aws.s3.S3ObjectMapi#size()
     */
    public int size() {
	try {
	    return refreshBucket().getContents().size();
	} catch (Exception e) {
	    Utils.<S3RuntimeException> rethrowIfRuntimeOrSameType(e);
	    throw new S3RuntimeException("Error clearing bucket" + bucket, e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jclouds.aws.s3.S3ObjectMapi#get(java.lang.Object)
     */
    public InputStream get(Object o) {
	try {
	    return (InputStream) (connection.getObject(bucket, o.toString())
		    .get()).getContent();
	} catch (Exception e) {
	    Utils.<S3RuntimeException> rethrowIfRuntimeOrSameType(e);
	    throw new S3RuntimeException(String.format(
		    "Error geting object %1s:%2s", bucket, o), e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jclouds.aws.s3.S3ObjectMapi#remove(java.lang.Object)
     */
    public InputStream remove(Object o) {
	InputStream old = get(o);
	try {
	    connection.deleteObject(bucket, o.toString()).get();
	} catch (Exception e) {
	    Utils.<S3RuntimeException> rethrowIfRuntimeOrSameType(e);
	    throw new S3RuntimeException(String.format(
		    "Error removing object %1s:%2s", bucket, o), e);
	}
	return old;
    }

    public class S3RuntimeException extends RuntimeException {
	S3RuntimeException(String s) {
	    super(s);
	}

	public S3RuntimeException(String s, Throwable throwable) {
	    super(s, throwable);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jclouds.aws.s3.S3ObjectMapi#clear()
     */
    public void clear() {
	try {
	    List<Future<Boolean>> deletes = new ArrayList<Future<Boolean>>();
	    for (String key : keySet()) {
		deletes.add(connection.deleteObject(bucket, key));
	    }
	    for (Future<Boolean> isdeleted : deletes)
		if (!isdeleted.get()) {
		    throw new S3RuntimeException("failed to delete entry");
		}
	} catch (Exception e) {
	    Utils.<S3RuntimeException> rethrowIfRuntimeOrSameType(e);
	    throw new S3RuntimeException("Error clearing bucket" + bucket, e);
	}
    }

    private S3Bucket refreshBucket() throws InterruptedException,
	    ExecutionException {
	return connection.getBucket(bucket).get();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jclouds.aws.s3.S3ObjectMapi#keySet()
     */
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

    /*
     * (non-Javadoc)
     * 
     * @see org.jclouds.aws.s3.S3ObjectMapi#values()
     */
    public Collection<InputStream> values() {
	Collection<InputStream> values = new LinkedList<InputStream>();
	Set<Future<S3Object>> futureObjects = new HashSet<Future<S3Object>>();
	for (String key : keySet()) {
	    futureObjects.add(connection.getObject(bucket, key));
	}
	for (Future<S3Object> futureObject : futureObjects) {
	    S3Object object = null;
	    try {
		object = futureObject.get();
	    } catch (Exception e) {
		Utils.<S3RuntimeException> rethrowIfRuntimeOrSameType(e);
		throw new S3RuntimeException(String.format(
			"Error getting value from bucket %1s:%2s", bucket,
			object != null ? object.getKey() : "unknown"), e);
	    }
	    System.err.printf("key: %1s, MD5: %2s", object.getKey(), object
		    .getContentMD5());
	    values.add((InputStream) object.getContent());
	}
	return values;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jclouds.aws.s3.S3ObjectMapi#entrySet()
     */
    public Set<Map.Entry<String, InputStream>> entrySet() {
	Set<Map.Entry<String, InputStream>> entrySet = new HashSet<Map.Entry<String, InputStream>>();
	for (String key : keySet()) {
	    Map.Entry<String, InputStream> entry = new Entry(key, get(key));
	    entrySet.add(entry);
	}
	return entrySet;
    }

    public class Entry implements java.util.Map.Entry<String, InputStream> {

	private InputStream value;
	private String key;

	Entry(String key, InputStream value) {
	    this.key = key;
	    this.value = value;
	}

	public String getKey() {
	    return key;
	}

	public InputStream getValue() {
	    return value;
	}

	public InputStream setValue(InputStream value) {
	    return put(key, value);
	}

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jclouds.aws.s3.S3ObjectMapi#containsKey(java.lang.Object)
     */
    public boolean containsKey(Object key) {
	try {
	    return connection.headObject(bucket, key.toString()).get() != S3Object.NOT_FOUND;
	} catch (Exception e) {
	    Utils.<S3RuntimeException> rethrowIfRuntimeOrSameType(e);
	    throw new S3RuntimeException(String.format(
		    "Error searching for %1s:%2s", bucket, key), e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jclouds.aws.s3.S3ObjectMapi#containsValue(java.lang.Object)
     */
    public boolean containsValue(Object value) {

	try {
	    byte[] md5;

	    if (value instanceof InputStream) {
		md5 = S3Utils.md5((InputStream) value);
	    } else if (value instanceof byte[]) {
		md5 = S3Utils.md5((byte[]) value);
	    } else if (value instanceof String) {
		md5 = S3Utils.md5(((String) value).getBytes());
	    } else if (value instanceof File) {
		md5 = S3Utils.md5(new FileInputStream((File) value));
	    } else {
		throw new IllegalArgumentException("unsupported value type: "
			+ value.getClass());
	    }
	    String eTagOfValue = S3Utils.getHexString(md5);

	    for (S3Object object : refreshBucket().getContents()) {
		if (object.getETag().equals(eTagOfValue))
		    return true;
	    }
	    return false;
	} catch (Exception e) {
	    Utils.<S3RuntimeException> rethrowIfRuntimeOrSameType(e);
	    throw new S3RuntimeException(String.format(
		    "Error searching for ETAG of value: [%2s] in bucket:%1s",
		    bucket, value), e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jclouds.aws.s3.S3ObjectMapi#isEmpty()
     */
    public boolean isEmpty() {
	return keySet().size() == 0;
    }

    private InputStream putInternal(String s, Object o) {
	S3Object object = new S3Object();
	try {

	    InputStream returnVal = containsKey(s) ? get(s) : null;
	    object.setKey(s);
	    object.setContent(o);
	    setSizeIfContentIsInputStream(object);
	    connection.addObject(bucket, object).get();
	    return returnVal;
	} catch (Exception e) {
	    Utils.<S3RuntimeException> rethrowIfRuntimeOrSameType(e);
	    throw new S3RuntimeException(String.format(
		    "Error adding object %1s:%2s", bucket, object), e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jclouds.aws.s3.S3ObjectMap#putAll(java.util.Map)
     */
    public void putAll(Map<? extends String, ? extends InputStream> map) {
	putAllInternal(map);
    }

    public void putAllBytes(Map<? extends String, ? extends byte[]> map) {
	putAllInternal(map);
    }

    public void putAllFiles(Map<? extends String, ? extends File> map) {
	putAllInternal(map);
    }

    public void putAllStrings(Map<? extends String, ? extends String> map) {
	putAllInternal(map);
    }

    private void putAllInternal(Map<? extends String, ? extends Object> map) {
	try {
	    List<Future<String>> puts = new ArrayList<Future<String>>();
	    for (String key : map.keySet()) {
		S3Object object = new S3Object();
		object.setKey(key);
		object.setContent(map.get(key));
		setSizeIfContentIsInputStream(object);
		puts.add(connection.addObject(bucket, object));
	    }
	    for (Future<String> put : puts)
		put.get();// this will throw an exception if there was a problem
	} catch (Exception e) {
	    Utils.<S3RuntimeException> rethrowIfRuntimeOrSameType(e);
	    throw new S3RuntimeException("Error putting into bucket" + bucket,
		    e);
	}
    }

    private void setSizeIfContentIsInputStream(S3Object object)
	    throws IOException {
	if (object.getContent() instanceof InputStream) {
	    byte[] buffer = IOUtils.toByteArray((InputStream) object
		    .getContent());
	    object.setSize(buffer.length);
	    object.setContent(new ByteArrayInputStream(buffer));
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jclouds.aws.s3.S3ObjectMap#putString(java.lang.String,
     * java.lang.String)
     */
    public InputStream putString(String key, String value) {
	return putInternal(key, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jclouds.aws.s3.S3ObjectMap#putFile(java.lang.String,
     * java.io.File)
     */
    public InputStream putFile(String key, File value) {
	return putInternal(key, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jclouds.aws.s3.S3ObjectMap#putBytes(java.lang.String, byte[])
     */
    public InputStream putBytes(String key, byte[] value) {
	return putInternal(key, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jclouds.aws.s3.S3ObjectMap#put(java.lang.String,
     * java.io.InputStream)
     */
    public InputStream put(String key, InputStream value) {
	return putInternal(key, value);
    }

    public S3Bucket getBucket() {
	return bucket;
    }

    public InputStream put(S3Object object) {
	InputStream returnVal = get(object.getKey());
	try {
	    connection.addObject(bucket, object).get();
	} catch (Exception e) {
	    Utils.<S3RuntimeException> rethrowIfRuntimeOrSameType(e);
	    throw new S3RuntimeException(String.format(
		    "Error putting object %1s:%2s", bucket, object.getKey()), e);
	}
	return returnVal;
    }

    public void putAll(Set<S3Object> objects) {
	try {
	    List<Future<String>> puts = new ArrayList<Future<String>>();
	    for (S3Object object : objects) {
		puts.add(connection.addObject(bucket, object));
	    }
	    for (Future<String> put : puts)
		put.get();// this will throw an exception if there was a problem
	} catch (Exception e) {
	    Utils.<S3RuntimeException> rethrowIfRuntimeOrSameType(e);
	    throw new S3RuntimeException("Error putting into bucket" + bucket,
		    e);
	}
    }

}
