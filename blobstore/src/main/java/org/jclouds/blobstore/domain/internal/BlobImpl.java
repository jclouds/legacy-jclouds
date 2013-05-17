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
package org.jclouds.blobstore.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.http.internal.PayloadEnclosingImpl;
import org.jclouds.io.Payload;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

/**
 * Value type for an HTTP Blob service. Blobs are stored in {@link StorageMetadata containers} and consist
 * of a {@link org.jclouds.blobstore.domain.Value#getInput() value}, a {@link Blob#getKey key and
 * 
 * @link Blob.Metadata#getUserMetadata() metadata}
 * 
 * @author Adrian Cole
 */
public class BlobImpl extends PayloadEnclosingImpl implements Blob, Comparable<Blob> {

   private final MutableBlobMetadata metadata;
   private Multimap<String, String> allHeaders = LinkedHashMultimap.create();

   @Inject
   public BlobImpl(MutableBlobMetadata metadata) {
      super();
      this.metadata = metadata;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public MutableBlobMetadata getMetadata() {
      return metadata;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Multimap<String, String> getAllHeaders() {
      return allHeaders;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setAllHeaders(Multimap<String, String> allHeaders) {
      this.allHeaders = checkNotNull(allHeaders, "allHeaders");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int compareTo(Blob o) {
      if (getMetadata().getName() == null)
         return -1;
      return (this == o) ? 0 : getMetadata().getName().compareTo(o.getMetadata().getName());
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((metadata == null) ? 0 : metadata.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      BlobImpl other = (BlobImpl) obj;
      if (metadata == null) {
         if (other.metadata != null)
            return false;
      } else if (!metadata.equals(other.metadata))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[metadata=" + metadata + "]";
   }

   @Override
   public void setPayload(Payload data) {
      super.setPayload(data);
      metadata.setContentMetadata(data.getContentMetadata());
   }

}
