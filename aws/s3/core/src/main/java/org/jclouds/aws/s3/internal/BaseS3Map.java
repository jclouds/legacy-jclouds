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

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.aws.s3.S3Connection;
import org.jclouds.aws.s3.S3Map;
import org.jclouds.aws.s3.domain.S3Bucket;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.reference.S3Constants;
import org.jclouds.util.Utils;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

/**
 * Implements core Map functionality with an {@link S3Connection}
 * <p/>
 * All commands will wait a maximum of ${jclouds.s3.map.timeout} milliseconds to complete before
 * throwing an exception.
 * 
 * @author Adrian Cole
 * @param <V>
 *           value of the map
 */
public abstract class BaseS3Map<V> implements S3Map<String, V> {

   protected final S3Connection connection;
   protected final String bucket;

   /**
    * maximum duration of an S3 Request
    */
   @Inject(optional = true)
   @Named(S3Constants.PROPERTY_OBJECTMAP_TIMEOUT)
   protected long requestTimeoutMilliseconds = 10000;

   /**
    * time to pause before retrying a transient failure
    */
   @Inject(optional = true)
   @Named(S3Constants.PROPERTY_OBJECTMAP_RETRY)
   protected long requestRetryMilliseconds = 10;

   @Inject
   public BaseS3Map(S3Connection connection, @Assisted String bucket) {
      this.connection = checkNotNull(connection, "connection");
      this.bucket = checkNotNull(bucket, "bucketName");
   }

   /**
    * {@inheritDoc}
    * <p/>
    * This returns the number of keys in the {@link S3Bucket}
    * 
    * @see S3Bucket#getContents()
    */
   public int size() {
      try {
         S3Bucket bucket = refreshBucket();
         Set<S3Object.Metadata> contents = bucket.getContents();
         return contents.size();
      } catch (Exception e) {
         Utils.<S3RuntimeException> rethrowIfRuntimeOrSameType(e);
         throw new S3RuntimeException("Error getting size of bucketName" + bucket, e);
      }
   }

   protected boolean containsETag(byte[] eTag) throws InterruptedException, ExecutionException,
            TimeoutException {
      for (S3Object.Metadata metadata : refreshBucket().getContents()) {
         if (Arrays.equals(eTag, metadata.getETag()))
            return true;
      }
      return false;
   }

   protected byte[] getETag(Object value) throws IOException, FileNotFoundException,
            InterruptedException, ExecutionException, TimeoutException {
      S3Object object = null;
      if (value instanceof S3Object) {
         object = (S3Object) value;
      } else {
         object = new S3Object("dummy", value);
      }
      if (object.getMetadata().getETag() == null)
         object.generateETag();
      return object.getMetadata().getETag();
   }

   /**
    * attempts asynchronous gets on all objects.
    * 
    * @see S3Connection#getObject(String, String)
    */
   protected Set<S3Object> getAllObjects() {
      Set<S3Object> objects = new HashSet<S3Object>();
      Set<Future<S3Object>> futureObjects = new HashSet<Future<S3Object>>();
      for (String key : keySet()) {
         futureObjects.add(connection.getObject(bucket, key));
      }
      for (Future<S3Object> futureObject : futureObjects) {
         try {
            ifNotFoundRetryOtherwiseAddToSet(futureObject, objects);
         } catch (Exception e) {
            Utils.<S3RuntimeException> rethrowIfRuntimeOrSameType(e);
            throw new S3RuntimeException(String.format("Error getting value from bucket %1$s",
                     bucket), e);
         }

      }
      return objects;
   }

   @VisibleForTesting
   void ifNotFoundRetryOtherwiseAddToSet(Future<S3Object> futureObject, Set<S3Object> objects)
            throws InterruptedException, ExecutionException, TimeoutException {
      for (int i = 0; i < 3; i++) {
         S3Object object = futureObject.get(requestTimeoutMilliseconds, TimeUnit.MILLISECONDS);
         if (object != S3Object.NOT_FOUND) {
            objects.add(object);
            break;
         } else {
            Thread.sleep(requestRetryMilliseconds);
         }
      }
   }

   /**
    * {@inheritDoc}
    * <p/>
    * Note that if value is an instance of InputStream, it will be read and closed following this
    * method. To reuse data from InputStreams, pass {@link java.io.InputStream}s inside
    * {@link S3Object}s
    */
   public boolean containsValue(Object value) {
      try {
         byte[] eTag = getETag(value);
         return containsETag(eTag);
      } catch (Exception e) {
         Utils.<S3RuntimeException> rethrowIfRuntimeOrSameType(e);
         throw new S3RuntimeException(String.format(
                  "Error searching for ETAG of value: [%2$s] in bucketName:%1$s", bucket, value), e);
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
            if (!isdeleted.get(requestTimeoutMilliseconds, TimeUnit.MILLISECONDS)) {
               throw new S3RuntimeException("failed to delete entry");
            }
      } catch (Exception e) {
         Utils.<S3RuntimeException> rethrowIfRuntimeOrSameType(e);
         throw new S3RuntimeException("Error clearing bucketName" + bucket, e);
      }
   }

   protected S3Bucket refreshBucket() throws InterruptedException, ExecutionException,
            TimeoutException {
      S3Bucket currentBucket = connection.listBucket(bucket).get(requestTimeoutMilliseconds,
               TimeUnit.MILLISECONDS);
      if (currentBucket == S3Bucket.NOT_FOUND)
         throw new S3RuntimeException("bucketName not found: " + bucket);
      else
         return currentBucket;
   }

   public Set<String> keySet() {
      try {
         Set<String> keys = new HashSet<String>();
         for (S3Object.Metadata object : refreshBucket().getContents())
            keys.add(object.getKey());
         return keys;
      } catch (Exception e) {
         Utils.<S3RuntimeException> rethrowIfRuntimeOrSameType(e);
         throw new S3RuntimeException("Error getting keys in bucketName: " + bucket, e);
      }
   }

   public boolean containsKey(Object key) {
      try {
         return connection.headObject(bucket, key.toString()) != S3Object.Metadata.NOT_FOUND;
      } catch (Exception e) {
         Utils.<S3RuntimeException> rethrowIfRuntimeOrSameType(e);
         throw new S3RuntimeException(String.format("Error searching for %1$s:%2$s", bucket, key),
                  e);
      }
   }

   public boolean isEmpty() {
      return keySet().size() == 0;
   }

   public S3Bucket getBucket() {
      try {
         return refreshBucket();
      } catch (Exception e) {
         Utils.<S3RuntimeException> rethrowIfRuntimeOrSameType(e);
         throw new S3RuntimeException("Error getting bucketName" + bucket, e);
      }
   }
}