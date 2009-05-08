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
package org.jclouds.aws.s3.internal;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.jclouds.Utils;
import org.jclouds.aws.s3.S3Connection;
import org.jclouds.aws.s3.S3InputStreamMap;
import org.jclouds.aws.s3.domain.S3Object;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * Map representation of a live connection to S3.
 * 
 * @author Adrian Cole
 */
public class LiveS3InputStreamMap extends BaseS3Map<InputStream> implements
	S3InputStreamMap {

    @Inject
    public LiveS3InputStreamMap(S3Connection connection, @Assisted String bucket) {
	super(connection, bucket);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jclouds.aws.s3.S3ObjectMapi#get(java.lang.Object)
     */
    public InputStream get(Object o) {
	try {
	    return (InputStream) (connection.getObject(bucket, o.toString())
		    .get(requestTimeoutMilliseconds, TimeUnit.MILLISECONDS))
		    .getData();
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
	    connection.deleteObject(bucket, o.toString()).get(
		    requestTimeoutMilliseconds, TimeUnit.MILLISECONDS);
	} catch (Exception e) {
	    Utils.<S3RuntimeException> rethrowIfRuntimeOrSameType(e);
	    throw new S3RuntimeException(String.format(
		    "Error removing object %1s:%2s", bucket, o), e);
	}
	return old;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jclouds.aws.s3.S3ObjectMapi#values()
     */
    public Collection<InputStream> values() {
	Collection<InputStream> values = new LinkedList<InputStream>();
	Set<S3Object> objects = getAllObjects();
	for (S3Object object : objects) {
	    values.add((InputStream) object.getData());
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
	for (S3Object object : getAllObjects()) {
	    entrySet.add(new Entry(object.getKey(), (InputStream) object
		    .getData()));
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

    private InputStream putInternal(String s, Object o) {
	S3Object object = new S3Object(s);
	try {
	    InputStream returnVal = containsKey(s) ? get(s) : null;
	    object.setData(o);
	    setSizeIfContentIsInputStream(object);
	    connection.putObject(bucket, object).get(
		    requestTimeoutMilliseconds, TimeUnit.MILLISECONDS);
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
	    List<Future<byte[]>> puts = new ArrayList<Future<byte[]>>();
	    for (String key : map.keySet()) {
		S3Object object = new S3Object(key);
		object.setData(map.get(key));
		setSizeIfContentIsInputStream(object);
		puts.add(connection.putObject(bucket, object));
	    }
	    for (Future<byte[]> put : puts)
		// this will throw an exception if there was a problem
		put.get(requestTimeoutMilliseconds, TimeUnit.MILLISECONDS);
	} catch (Exception e) {
	    Utils.<S3RuntimeException> rethrowIfRuntimeOrSameType(e);
	    throw new S3RuntimeException("Error putting into bucket" + bucket,
		    e);
	}
    }

    private void setSizeIfContentIsInputStream(S3Object object)
	    throws IOException {
	if (object.getData() instanceof InputStream) {
	    byte[] buffer = IOUtils.toByteArray((InputStream) object.getData());
	    object.getMetaData().setSize(buffer.length);
	    object.setData(new ByteArrayInputStream(buffer));
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

}
