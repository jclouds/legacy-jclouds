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
package org.jclouds.blobstore.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.awt.Container;
import java.io.IOException;
import java.io.InputStream;

import org.jclouds.http.HttpUtils;
import org.jclouds.http.HttpUtils.MD5InputStreamResult;

import javax.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * Value type for an HTTP Blob service. Blobs are stored in {@link Container containers} and consist
 * of a {@link org.jclouds.blobstore.domain.Value#getData() value}, a {@link Blob#getKey key and
 * 
 * @link Blob.Metadata#getUserMetadata() metadata}
 * 
 * @author Adrian Cole
 */
public class Blob<M extends BlobMetadata> {

   @SuppressWarnings("unchecked")
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Blob<M> other = (Blob<M>) obj;
      if (contentLength != other.contentLength)
         return false;
      if (contentRange == null) {
         if (other.contentRange != null)
            return false;
      } else if (!contentRange.equals(other.contentRange))
         return false;
      if (data == null) {
         if (other.data != null)
            return false;
      } else if (!data.equals(other.data))
         return false;
      if (metadata == null) {
         if (other.metadata != null)
            return false;
      } else if (!metadata.equals(other.metadata))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "Blob [contentLength=" + contentLength + ", contentRange=" + contentRange + ", data="
               + data + ", metadata=" + metadata + "]";
   }

   protected Object data;
   protected final M metadata;
   protected long contentLength = -1;
   protected String contentRange;

   @SuppressWarnings("unchecked")
   public Blob(String key) {
      // TODO: why are we getting a generic warning here?
      this((M) new BlobMetadata(key));
   }

   @Inject
   public Blob(@Assisted M metadata) {
      this.metadata = metadata;
   }

   public Blob(M metadata, Object data) {
      this(metadata);
      setData(data);
   }

   public Blob(String key, Object data) {
      this(key);
      setData(data);
   }

   /**
    * @see BlobMetadata#getKey()
    */
   public String getKey() {
      return metadata.getKey();
   }

   /**
    * Sets entity for the request or the content from the response. If size isn't set, this will
    * attempt to discover it.
    * 
    * @param data
    *           typically InputStream for downloads, or File, byte [], String, or InputStream for
    *           uploads.
    */
   public void setData(Object data) {
      this.data = checkNotNull(data, "data");
      if (getMetadata().getSize() == -1)
         this.getMetadata().setSize(HttpUtils.calculateSize(data));
   }

   /**
    * generate an MD5 Hash for the current data.
    * <p/>
    * <h2>Note</h2>
    * <p/>
    * If this is an InputStream, it will be converted to a byte array first.
    * 
    * @throws IOException
    *            if there is a problem generating the hash.
    */
   public void generateMD5() throws IOException {
      checkState(data != null, "data");
      if (data instanceof InputStream) {
         MD5InputStreamResult result = HttpUtils.generateMD5Result((InputStream) data);
         getMetadata().setSize(result.length);
         getMetadata().setContentMD5(result.eTag);
         setData(result.data);
      } else {
         getMetadata().setContentMD5(HttpUtils.md5(data));
      }
   }

   /**
    * @return InputStream, if downloading, or whatever was set during {@link #setData(Object)}
    */
   public Object getData() {
      return data;
   }

   /**
    * @return System and User metadata relevant to this object.
    */
   public M getMetadata() {
      return metadata;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (int) (contentLength ^ (contentLength >>> 32));
      result = prime * result + ((contentRange == null) ? 0 : contentRange.hashCode());
      result = prime * result + ((data == null) ? 0 : data.hashCode());
      result = prime * result + ((metadata == null) ? 0 : metadata.hashCode());
      return result;
   }

   public void setContentLength(long contentLength) {
      this.contentLength = contentLength;
   }

   /**
    * Returns the total size of the downloaded object, or the chunk that's available.
    * <p/>
    * Chunking is only used when org.jclouds.http.GetOptions is called with options like tail,
    * range, or startAt.
    * 
    * @return the length in bytes that can be be obtained from {@link #getData()}
    * @see org.jclouds.http.HttpHeaders#CONTENT_LENGTH
    * @see GetObjectOptions
    */
   public long getContentLength() {
      return contentLength;
   }

   public void setContentRange(String contentRange) {
      this.contentRange = contentRange;
   }

   /**
    * If this is not-null, {@link #getContentLength() } will the size of chunk of the Value available
    * via {@link #getData()}
    * 
    * @see org.jclouds.http.HttpHeaders#CONTENT_RANGE
    * @see GetObjectOptions
    */
   public String getContentRange() {
      return contentRange;
   }

}
