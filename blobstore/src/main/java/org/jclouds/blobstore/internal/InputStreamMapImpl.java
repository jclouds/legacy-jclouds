/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.blobstore.internal;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.jclouds.blobstore.BlobMap;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.InputStreamMap;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.strategy.ContainsValueInListStrategy;
import org.jclouds.blobstore.strategy.GetBlobsInListStrategy;
import org.jclouds.blobstore.strategy.PutBlobsStrategy;
import org.jclouds.blobstore.strategy.internal.ListBlobMetadataInContainer;
import org.jclouds.http.Payload;
import org.jclouds.http.Payloads;
import org.jclouds.http.payloads.ByteArrayPayload;
import org.jclouds.http.payloads.FilePayload;
import org.jclouds.http.payloads.InputStreamPayload;
import org.jclouds.http.payloads.StringPayload;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Map representation of a live connection to a BlobStore. All put operations will result in ETag
 * calculation. If this is not desired, use {@link BlobMap} instead.
 * 
 * @author Adrian Cole
 * 
 * @see BlobStore
 * @see InputStreamMap
 * @see BaseBlobMap
 */
public class InputStreamMapImpl extends BaseBlobMap<InputStream> implements InputStreamMap {

   @Inject
   public InputStreamMapImpl(BlobStore connection, Blob.Factory blobFactory,
            GetBlobsInListStrategy getAllBlobs, ListBlobMetadataInContainer listStrategy,
            ContainsValueInListStrategy containsValueStrategy, PutBlobsStrategy putBlobsStrategy,
            String containerName, @Nullable String dir) {
      super(connection, getAllBlobs, containsValueStrategy, putBlobsStrategy, listStrategy,
               containerName, dir);
   }

   @Override
   public InputStream get(Object o) {
      String realKey = prefixer.apply(o.toString());
      Blob blob = blobstore.getBlob(containerName, realKey);
      return blob != null ? blob.getContent() : null;
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
      return Lists.newArrayList(Iterables.transform(getAllBlobs.execute(containerName, options),
               new Function<Blob, InputStream>() {
                  public InputStream apply(Blob from) {
                     return from.getContent();
                  }
               }));
   }

   @Override
   public void putAll(Map<? extends String, ? extends InputStream> map) {
      putAllInternal(map);
   }

   @Override
   public void putAllBytes(Map<? extends String, ? extends byte[]> map) {
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
      putBlobsStrategy.execute(containerName, Iterables.transform(map.entrySet(),
               new Function<Map.Entry<? extends String, ? extends Object>, Blob>() {
                  @Override
                  public Blob apply(Map.Entry<? extends String, ? extends Object> from) {
                     Blob blob = blobstore.newBlob(prefixer.apply(from.getKey()));
                     blob.setPayload(Payloads.newPayload(from.getValue()));
                     blob.generateMD5();
                     return blob;
                  }
               }));
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
      Blob blob = blobstore.newBlob(prefixer.apply(name));
      blob.setPayload(payload);
      blob.generateMD5();
      blobstore.putBlob(containerName, blob);
      return returnVal;
   }

}
