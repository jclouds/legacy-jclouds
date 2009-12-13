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

import javax.inject.Inject;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.domain.ResourceMetadata;
import org.jclouds.encryption.EncryptionService;
import org.jclouds.http.internal.BasePayloadEnclosingImpl;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

/**
 * Value type for an HTTP Blob service. Blobs are stored in {@link ResourceMetadata containers} and consist
 * of a {@link org.jclouds.blobstore.domain.Value#getContent() value}, a {@link Blob#getKey key and
 * 
 * @link Blob.Metadata#getUserMetadata() metadata}
 * 
 * @author Adrian Cole
 */
public class BlobImpl extends BasePayloadEnclosingImpl implements Blob, Comparable<Blob> {
   private final MutableBlobMetadata metadata;
   private Multimap<String, String> allHeaders = LinkedHashMultimap.create();

   @Inject
   public BlobImpl(EncryptionService encryptionService, MutableBlobMetadata metadata) {
      super(encryptionService);
      this.metadata = metadata;
   }

   @Override
   protected void setContentMD5(byte[] md5) {
      getMetadata().setContentMD5(checkNotNull(md5, "md5"));
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
      result = prime * result + ((payload == null) ? 0 : payload.hashCode());
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
      if (payload == null) {
         if (other.payload != null)
            return false;
      } else if (!payload.equals(other.payload))
         return false;
      if (metadata == null) {
         if (other.metadata != null)
            return false;
      } else if (!metadata.equals(other.metadata))
         return false;
      return true;
   }

}
