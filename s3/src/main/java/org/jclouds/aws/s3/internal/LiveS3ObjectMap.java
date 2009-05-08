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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.jclouds.Utils;
import org.jclouds.aws.s3.S3Connection;
import org.jclouds.aws.s3.S3ObjectMap;
import org.jclouds.aws.s3.domain.S3Object;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * Map representation of a live connection to S3.
 * 
 * @author Adrian Cole
 */
public class LiveS3ObjectMap extends BaseS3Map<S3Object> implements S3ObjectMap {

    @Inject
    public LiveS3ObjectMap(S3Connection connection, @Assisted String bucket) {
	super(connection, bucket);
    }

    public Set<java.util.Map.Entry<String, S3Object>> entrySet() {
	Set<Map.Entry<String, S3Object>> entrySet = new HashSet<Map.Entry<String, S3Object>>();
	for (S3Object value : values()) {
	    Map.Entry<String, S3Object> entry = new Entry(value.getKey(), value);
	    entrySet.add(entry);
	}
	return entrySet;
    }

    public class Entry implements java.util.Map.Entry<String, S3Object> {

	private S3Object value;
	private String key;

	Entry(String key, S3Object value) {
	    this.key = key;
	    this.value = value;
	}

	public String getKey() {
	    return key;
	}

	public S3Object getValue() {
	    return value;
	}

	public S3Object setValue(S3Object value) {
	    return put(key, value);
	}

    }

    public S3Object get(Object key) {
	try {
	    return connection.getObject(bucket, key.toString()).get(
		    requestTimeoutMilliseconds, TimeUnit.MILLISECONDS);
	} catch (Exception e) {
	    Utils.<S3RuntimeException> rethrowIfRuntimeOrSameType(e);
	    throw new S3RuntimeException(String.format(
		    "Error geting object %1s:%2s", bucket, key), e);
	}
    }

    public S3Object put(String key, S3Object value) {
	S3Object returnVal = get(key);
	try {
	    connection.putObject(bucket, value).get(requestTimeoutMilliseconds,
		    TimeUnit.MILLISECONDS);
	} catch (Exception e) {
	    Utils.<S3RuntimeException> rethrowIfRuntimeOrSameType(e);
	    throw new S3RuntimeException(String.format(
		    "Error putting object %1s:%2s%n%1s", bucket, key, value), e);
	}
	return returnVal;
    }

    public void putAll(Map<? extends String, ? extends S3Object> map) {
	try {
	    List<Future<byte[]>> puts = new ArrayList<Future<byte[]>>();
	    for (S3Object object : map.values()) {
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

    public S3Object remove(Object key) {
	S3Object old = get(key);
	try {
	    connection.deleteObject(bucket, key.toString()).get(
		    requestTimeoutMilliseconds, TimeUnit.MILLISECONDS);
	} catch (Exception e) {
	    Utils.<S3RuntimeException> rethrowIfRuntimeOrSameType(e);
	    throw new S3RuntimeException(String.format(
		    "Error removing object %1s:%2s", bucket, key), e);
	}
	return old;
    }

    public Collection<S3Object> values() {
	return getAllObjects();
    }

}
