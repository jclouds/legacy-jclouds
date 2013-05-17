/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.blobstore.internal;

import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static org.jclouds.io.Payloads.newPayload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import org.jclouds.blobstore.BlobMap;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.InputStreamMap;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobBuilder;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.strategy.ContainsValueInListStrategy;
import org.jclouds.blobstore.strategy.GetBlobsInListStrategy;
import org.jclouds.blobstore.strategy.PutBlobsStrategy;
import org.jclouds.blobstore.strategy.internal.ListContainerAndRecurseThroughFolders;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.io.payloads.ByteArrayPayload;
import org.jclouds.io.payloads.FilePayload;
import org.jclouds.io.payloads.InputStreamPayload;
import org.jclouds.io.payloads.StringPayload;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Throwables;

/**
 * Map representation of a live connection to a BlobStore. All put operations will result in ETag
 * calculation. If this is not desired, use {@link BlobMap} instead.
 * 
 * @author Adrian Cole
 * 
 * @see BlobStore
 * @see InputStreamMap
 * @see BaseBlobMap
 * @deprecated will be removed in jclouds 1.7. Please use {@link BlobStore}
 */
@Deprecated
public class InputStreamMapImpl extends BaseBlobMap<InputStream> implements InputStreamMap {

   @Inject
   public InputStreamMapImpl(BlobStore connection, Provider<BlobBuilder> blobBuilders,
         GetBlobsInListStrategy getAllBlobs, ListContainerAndRecurseThroughFolders listStrategy,
         ContainsValueInListStrategy containsValueStrategy, PutBlobsStrategy putBlobsStrategy, String containerName,
         ListContainerOptions options) {
      super(connection, getAllBlobs, containsValueStrategy, putBlobsStrategy, listStrategy, containerName, options);
   }

   @Override
   public InputStream get(Object o) {
      String realKey = prefixer.apply(o.toString());
      Blob blob = blobstore.getBlob(containerName, realKey);
      return getInputStreamOrNull(blob);
   }

   private InputStream getInputStreamOrNull(Blob blob) {
      return blob != null ? blob.getPayload() != null ? blob.getPayload().getInput() : null : null;
   }

   @Override
   public InputStream remove(Object o) {
      InputStream old = get(o);
      String realKey = prefixer.apply(o.toString());
      blobstore.removeBlob(containerName, realKey);
      return old;
   }

   @Override
   public Collection<InputStream> values() {
      return newArrayList(transform(getAllBlobs.execute(containerName, options), new Function<Blob, InputStream>() {
         public InputStream apply(Blob from) {
            return getInputStreamOrNull(from);
         }
      }));
   }

   @Override
   public void putAll(Map<? extends String, ? extends InputStream> map) {
      putAllInternal(map);
   }

   @Override
   public void putAllBytes(Map<? extends String, byte[]> map) {
      putAllInternal(map);
   }

   @Override
   public void putAllFiles(Map<? extends String, ? extends File> map) {
      putAllInternal(map);
   }

   @Override
   public void putAllStrings(Map<? extends String, ? extends String> map) {
      putAllInternal(map);
   }

   /**
    * submits requests to add all objects and collects the results later. All values will have eTag
    * calculated first. As a side-effect of this, the content will be copied into a byte [].
    * 
    * @see S3Client#put(String, Blob)
    */
   @VisibleForTesting
   void putAllInternal(Map<? extends String, ? extends Object> map) {
      putBlobsStrategy.execute(containerName,
            transform(map.entrySet(), new Function<Map.Entry<? extends String, ? extends Object>, Blob>() {
               @Override
               public Blob apply(Map.Entry<? extends String, ? extends Object> from) {
                  String name = from.getKey();
                  Object value = from.getValue();
                  return newBlobWithMD5(name, value);
               }

            }));
   }

   @VisibleForTesting
   Blob newBlobWithMD5(String name, Object value) {
      Blob blob = blobstore.blobBuilder(prefixer.apply(name)).payload(newPayload(value)).build();
      try {
         Payloads.calculateMD5(blob);
      } catch (IOException e) {
         Throwables.propagate(e);
      }
      return blob;
   }

   @Override
   public InputStream putString(String key, String value) {
      return putInternal(key, new StringPayload(value));
   }

   @Override
   public InputStream putFile(String key, File value) {
      return putInternal(key, new FilePayload(value));
   }

   @Override
   public InputStream putBytes(String key, byte[] value) {
      return putInternal(key, new ByteArrayPayload(value));
   }

   @Override
   public InputStream put(String key, InputStream value) {
      return putInternal(key, new InputStreamPayload(value));
   }

   /**
    * calculates eTag before adding the object to s3. As a side-effect of this, the content will be
    * copied into a byte []. *
    * 
    * @see S3Client#put(String, Blob)
    */
   @VisibleForTesting
   InputStream putInternal(String name, Payload payload) {
      InputStream returnVal = containsKey(name) ? get(name) : null;
      Blob blob = newBlobWithMD5(name, payload);
      blobstore.putBlob(containerName, blob);
      return returnVal;
   }

}
