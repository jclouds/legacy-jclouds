/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.blobstore.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.InputStream;

import javax.inject.Inject;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.MD5InputStreamResult;
import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.domain.ResourceMetadata;
import org.jclouds.blobstore.functions.CalculateSize;
import org.jclouds.blobstore.functions.GenerateMD5;
import org.jclouds.blobstore.functions.GenerateMD5Result;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

/**
 * Value type for an HTTP Blob service. Blobs are stored in {@link ResourceMetadata containers} and consist
 * of a {@link org.jclouds.blobstore.domain.Value#getData() value}, a {@link Blob#getKey key and
 * 
 * @link Blob.Metadata#getUserMetadata() metadata}
 * 
 * @author Adrian Cole
 */
public class BlobImpl implements Blob, Comparable<Blob> {
   private final GenerateMD5Result generateMD5Result;
   private final GenerateMD5 generateMD5;
   private final CalculateSize calculateSize;
   private final MutableBlobMetadata metadata;
   private Object data;
   private Multimap<String, String> allHeaders = LinkedHashMultimap.create();
   private Long contentLength;

   @Inject
   public BlobImpl(GenerateMD5Result generateMD5Result, GenerateMD5 generateMD5,
            CalculateSize calculateSize, MutableBlobMetadata metadata) {
      this.generateMD5Result = generateMD5Result;
      this.generateMD5 = generateMD5;
      this.calculateSize = calculateSize;
      this.metadata = metadata;
   }

   /**
    * generate an MD5 Hash for the current data.
    * <p/>
    * <h2>Note</h2>
    * <p/>
    * If this is an InputStream, it will be converted to a byte array first.
    * 
    */
   public void generateMD5() {
      checkState(data != null, "data");
      if (data instanceof InputStream) {
         MD5InputStreamResult result = generateMD5Result.apply((InputStream) data);
         getMetadata().setContentMD5(result.md5);
         setContentLength(result.length);
         setData(result.data);
      } else {
         getMetadata().setContentMD5(generateMD5.apply(data));
      }
   }

   /**
    * @return InputStream, if downloading, or whatever was set during {@link #setData(Object)}
    */
   public Object getData() {
      return data;
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
      if (getContentLength() == null) {
         Long size = calculateSize.apply(data);
         if (size != null)
            this.setContentLength(size);
      }
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
   public Long getContentLength() {
      return contentLength;
   }

   public void setContentLength(long contentLength) {
      this.contentLength = contentLength;
   }

   /**
    * @return System and User metadata relevant to this object.
    */
   public MutableBlobMetadata getMetadata() {
      return metadata;
   }

   /**
    * @return all http response headers associated with this Value
    */
   public Multimap<String, String> getAllHeaders() {
      return allHeaders;
   }

   public void setAllHeaders(Multimap<String, String> allHeaders) {
      this.allHeaders = checkNotNull(allHeaders, "allHeaders");
   }

   public int compareTo(Blob o) {
      if (getMetadata().getName() == null)
         return -1;
      return (this == o) ? 0 : getMetadata().getName().compareTo(o.getMetadata().getName());
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((allHeaders == null) ? 0 : allHeaders.hashCode());
      result = prime * result + ((contentLength == null) ? 0 : contentLength.hashCode());
      result = prime * result + ((data == null) ? 0 : data.hashCode());
      result = prime * result + ((metadata == null) ? 0 : metadata.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      BlobImpl other = (BlobImpl) obj;
      if (allHeaders == null) {
         if (other.allHeaders != null)
            return false;
      } else if (!allHeaders.equals(other.allHeaders))
         return false;
      if (contentLength == null) {
         if (other.contentLength != null)
            return false;
      } else if (!contentLength.equals(other.contentLength))
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

}
