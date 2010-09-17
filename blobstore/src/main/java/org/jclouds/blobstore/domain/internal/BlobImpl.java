/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.blobstore.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.http.internal.PayloadEnclosingImpl;
import org.jclouds.io.Payload;
import org.jclouds.io.PayloadEnclosing;
import org.jclouds.io.payloads.DelegatingPayload;

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

   private final MutableBlobMetadata _metadata;
   private final SetPayloadPropertiesMutableBlobMetadata metadata;
   private Multimap<String, String> allHeaders = LinkedHashMultimap.create();

   @Inject
   public BlobImpl(MutableBlobMetadata metadata) {
      super();
      this.metadata = linkMetadataToThis(metadata);
      this._metadata = this.metadata.getDelegate();
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
      result = prime * result + ((_metadata == null) ? 0 : _metadata.hashCode());
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
      if (_metadata == null) {
         if (other._metadata != null)
            return false;
      } else if (!_metadata.equals(other._metadata))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[metadata=" + _metadata + "]";
   }

   @Override
   public void setPayload(Payload data) {
      linkPayloadToMetadata(data);
   }

   /**
    * link the new payload to the metadata object so that when content-related metadata is updated
    * on the payload, it is also copied the metadata object.
    */
   void linkPayloadToMetadata(Payload data) {
      if (data instanceof DelegatingPayload)
         super.setPayload(new SetMetadataPropertiesPayload(DelegatingPayload.class.cast(data)
                  .getDelegate(), _metadata));
      else
         super.setPayload(new SetMetadataPropertiesPayload(data, _metadata));
   }

   static class SetMetadataPropertiesPayload extends DelegatingPayload {

      private transient final MutableBlobMetadata metadata;

      public SetMetadataPropertiesPayload(Payload delegate, MutableBlobMetadata metadata) {
         super(delegate);
         this.metadata = metadata;
         setContentType(metadata.getContentType());
         setContentDisposition(metadata.getContentDisposition());
      }

      @Override
      public void setContentType(String md5) {
         super.setContentType(md5);
         metadata.setContentType(md5);
      }

      @Override
      public void setContentDisposition(String contentDisposition) {
         super.setContentDisposition(contentDisposition);
         metadata.setContentDisposition(contentDisposition);
      }
   }

   /**
    * link the metadata object to this so that when content-related metadata is updated, it is also
    * copied the currentpayload object.
    */
   SetPayloadPropertiesMutableBlobMetadata linkMetadataToThis(MutableBlobMetadata metadata) {
      return metadata instanceof DelegatingMutableBlobMetadata ? new SetPayloadPropertiesMutableBlobMetadata(
               DelegatingMutableBlobMetadata.class.cast(metadata).getDelegate(), this)
               : new SetPayloadPropertiesMutableBlobMetadata(metadata, this);
   }

   static class SetPayloadPropertiesMutableBlobMetadata extends DelegatingMutableBlobMetadata {
      /** The serialVersionUID */
      private static final long serialVersionUID = -5072270546219814521L;
      private transient final PayloadEnclosing blob;

      public SetPayloadPropertiesMutableBlobMetadata(MutableBlobMetadata delegate,
               PayloadEnclosing blob) {
         super(delegate);
         this.blob = blob;
      }

      @Override
      public void setContentType(String type) {
         super.setContentType(type);
         if (canSetPayload())
            blob.getPayload().setContentType(type);
      }

      private boolean canSetPayload() {
         return blob != null && blob.getPayload() != null;
      }
   }
}
