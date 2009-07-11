/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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

import java.io.File;
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

import org.jclouds.aws.s3.S3Connection;
import org.jclouds.aws.s3.S3InputStreamMap;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.util.Utils;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * Map representation of a live connection to S3. All put operations will result
 * in ETag calculation. If this is not desired, use {@link LiveS3ObjectMap}
 * instead.
 *
 * @author Adrian Cole
 * @see S3Connection
 * @see BaseS3Map
 */
public class LiveS3InputStreamMap extends BaseS3Map<InputStream> implements
        S3InputStreamMap {

    @Inject
    public LiveS3InputStreamMap(S3Connection connection, @Assisted String bucket) {
        super(connection, bucket);
    }

    /**
     * {@inheritDoc}
     *
     * @see S3Connection#getObject(String, String)
     */
    public InputStream get(Object o) {
        try {
            return (InputStream) (connection.getObject(bucket, o.toString())
                    .get(requestTimeoutMilliseconds, TimeUnit.MILLISECONDS))
                    .getData();
        } catch (Exception e) {
            Utils.<S3RuntimeException>rethrowIfRuntimeOrSameType(e);
            throw new S3RuntimeException(String.format(
                    "Error geting object %1$s:%2$s", bucket, o), e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see S3Connection#deleteObject(String, String)
     */
    public InputStream remove(Object o) {
        InputStream old = get(o);
        try {
            connection.deleteObject(bucket, o.toString()).get(
                    requestTimeoutMilliseconds, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            Utils.<S3RuntimeException>rethrowIfRuntimeOrSameType(e);
            throw new S3RuntimeException(String.format(
                    "Error removing object %1$s:%2$s", bucket, o), e);
        }
        return old;
    }

    /**
     * {@inheritDoc}
     *
     * @see #getAllObjects()
     */
    public Collection<InputStream> values() {
        Collection<InputStream> values = new LinkedList<InputStream>();
        Set<S3Object> objects = getAllObjects();
        for (S3Object object : objects) {
            values.add((InputStream) object.getData());
        }
        return values;
    }

    /**
     * {@inheritDoc}
     *
     * @see #getAllObjects()
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

        /**
         * {@inheritDoc}
         *
         * @see LiveS3InputStreamMap#put(String, InputStream)
         */
        public InputStream setValue(InputStream value) {
            return put(key, value);
        }

    }

    /**
     * {@inheritDoc}
     *
     * @see #putAllInternal(Map)
     */
    public void putAll(Map<? extends String, ? extends InputStream> map) {
        putAllInternal(map);
    }

    /**
     * {@inheritDoc}
     *
     * @see #putAllInternal(Map)
     */
    public void putAllBytes(Map<? extends String, ? extends byte[]> map) {
        putAllInternal(map);
    }

    /**
     * {@inheritDoc}
     *
     * @see #putAllInternal(Map)
     */
    public void putAllFiles(Map<? extends String, ? extends File> map) {
        putAllInternal(map);
    }

    /**
     * {@inheritDoc}
     *
     * @see #putAllInternal(Map)
     */
    public void putAllStrings(Map<? extends String, ? extends String> map) {
        putAllInternal(map);
    }

    /**
     * submits requests to add all objects and collects the results later. All
     * values will have eTag calculated first. As a side-effect of this, the
     * content will be copied into a byte [].
     *
     * @see S3Connection#putObject(String, S3Object)
     */
    @VisibleForTesting
    void putAllInternal(Map<? extends String, ? extends Object> map) {
        try {
            List<Future<byte[]>> puts = new ArrayList<Future<byte[]>>();
            for (Map.Entry<? extends String, ? extends Object> entry : map.entrySet()) {
                S3Object object = new S3Object(entry.getKey());
                object.setData(entry.getValue());
                object.generateETag();
                puts.add(connection.putObject(bucket, object));
                /// ParamExtractor Funcion<?,String>
                /// response transformer  set key on the way out.
                /// ExceptionHandler convert 404 to NOT_FOUND
            }
            for (Future<byte[]> put : puts)
                // this will throw an exception if there was a problem
                put.get(requestTimeoutMilliseconds, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            Utils.<S3RuntimeException>rethrowIfRuntimeOrSameType(e);
            throw new S3RuntimeException("Error putting into bucketName" + bucket,
                    e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see #putInternal(String, Object)
     */
    public InputStream putString(String key, String value) {
        return putInternal(key, value);
    }

    /**
     * {@inheritDoc}
     *
     * @see #putInternal(String, Object)
     */
    public InputStream putFile(String key, File value) {
        return putInternal(key, value);
    }

    /**
     * {@inheritDoc}
     *
     * @see #putInternal(String, Object)
     */
    public InputStream putBytes(String key, byte[] value) {
        return putInternal(key, value);
    }

    /**
     * {@inheritDoc}
     *
     * @see #putInternal(String, Object)
     */
    public InputStream put(String key, InputStream value) {
        return putInternal(key, value);
    }

    /**
     * calculates eTag before adding the object to s3. As a side-effect of this,
     * the content will be copied into a byte []. *
     *
     * @see S3Connection#putObject(String, S3Object)
     */
    @VisibleForTesting
    InputStream putInternal(String s, Object o) {
        S3Object object = new S3Object(s);
        try {
            InputStream returnVal = containsKey(s) ? get(s) : null;
            object.setData(o);
            object.generateETag();
            connection.putObject(bucket, object).get(
                    requestTimeoutMilliseconds, TimeUnit.MILLISECONDS);
            return returnVal;
        } catch (Exception e) {
            Utils.<S3RuntimeException>rethrowIfRuntimeOrSameType(e);
            throw new S3RuntimeException(String.format(
                    "Error adding object %1$s:%2$s", bucket, object), e);
        }
    }

}
