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

import com.google.inject.Inject;
import org.jclouds.Logger;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.domain.S3Bucket;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;

/**
 * // TODO: Adrian: Document this!
 *
 * @author Adrian Cole
 */
public class S3ObjectMap implements ConcurrentMap<String, Object> {
    private Logger logger;
    private S3Connection connection;
    private S3Bucket bucket;
    private S3Utils utils;

    @Inject
    public S3ObjectMap(java.util.logging.Logger logger, S3Connection connection, S3Bucket bucket, S3Utils utils) {
        this.logger = new Logger(logger);
        this.connection = connection;
        this.bucket = bucket;
        this.utils = utils;
    }


    public Object putIfAbsent(String s, Object o) {
        return null;  // TODO: Adrian: Customise this generated block
    }

    public boolean remove(Object o, Object o1) {
        return false;  // TODO: Adrian: Customise this generated block
    }

    public boolean replace(String s, Object o, Object o1) {
        return false;  // TODO: Adrian: Customise this generated block
    }

    public Object replace(String s, Object o) {
        return null;  // TODO: Adrian: Customise this generated block
    }

    public int size() {
        try {
            bucket = connection.getBucket(bucket).get();
            return bucket.getContents().size();
        } catch (Exception e) {
            S3Utils.<S3RuntimeException>rethrowIfRuntimeOrSameType(e);
            throw new S3RuntimeException("Error clearing bucket" + bucket, e);
        }
    }

    public boolean isEmpty() {
        return false;  // TODO: Adrian: Customise this generated block
    }

    public boolean containsKey(Object o) {
        return false;  // TODO: Adrian: Customise this generated block
    }

    public boolean containsValue(Object o) {
        return false;  // TODO: Adrian: Customise this generated block
    }

    public Object get(Object o) {
        try {
            return connection.getObject(bucket, o.toString()).get();
        } catch (Exception e) {
            S3Utils.<S3RuntimeException>rethrowIfRuntimeOrSameType(e);
            throw new S3RuntimeException(String.format("Error geting object %1s:%2s", bucket, o), e);
        }
    }

    public Object put(String s, Object o) {
        S3Object object = new S3Object();
        try {
            object.setKey(s);
            object.setContent(o);
            return connection.addObject(bucket, object).get();
        } catch (Exception e) {
            S3Utils.<S3RuntimeException>rethrowIfRuntimeOrSameType(e);
            throw new S3RuntimeException(String.format("Error adding object %1s:%2s", bucket, object), e);
        }
    }

    public Object remove(Object o) {
        return null;  // TODO: Adrian: Customise this generated block
    }

    public void putAll(Map<? extends String, ? extends Object> map) {
        // TODO: Adrian: Customise this generated block
    }

    private class S3RuntimeException extends RuntimeException {
        public S3RuntimeException(String s) {
            super(s);    // TODO: Adrian: Customise this generated block
        }

        public S3RuntimeException(String s, Throwable throwable) {
            super(s, throwable);    // TODO: Adrian: Customise this generated block
        }
    }

    public void clear() {
        try {
            bucket = connection.getBucket(bucket).get();
            List<Future<Boolean>> deletes = new ArrayList<Future<Boolean>>();
            for (S3Object object : bucket.getContents()) {
                deletes.add(connection.deleteObject(bucket, object.getKey()));
            }
            for (Future<Boolean> isdeleted : deletes)
                if (!isdeleted.get()) {
                    throw new S3RuntimeException("failed to delete entry");
                }
        } catch (Exception e) {
            S3Utils.<S3RuntimeException>rethrowIfRuntimeOrSameType(e);
            throw new S3RuntimeException("Error clearing bucket" + bucket, e);
        }
    }

    public Set<String> keySet() {
        return null;  // TODO: Adrian: Customise this generated block
    }

    public Collection<Object> values() {
        return null;  // TODO: Adrian: Customise this generated block
    }

    public Set<Entry<String, Object>> entrySet() {
        return null;  // TODO: Adrian: Customise this generated block
    }
}
