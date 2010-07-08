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
package org.jclouds.aws.s3.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;

import org.jclouds.aws.s3.domain.AccessControlList;
import org.jclouds.aws.s3.domain.MutableObjectMetadata;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.http.Payload;
import org.jclouds.http.PayloadEnclosing;
import org.jclouds.http.internal.PayloadEnclosingImpl;
import org.jclouds.http.payloads.DelegatingPayload;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

/**
 * Default Implementation of {@link S3Object}.
 * 
 * @author Adrian Cole
 */
public class S3ObjectImpl extends PayloadEnclosingImpl implements S3Object, Comparable<S3Object> {

   private AccessControlList accessControlList;

   /**
    * {@inheritDoc}
    */
   @Override
   public void setAccessControlList(AccessControlList acl) {
      this.accessControlList = acl;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AccessControlList getAccessControlList() {
      return this.accessControlList;
   }

   private final MutableObjectMetadata _metadata;
   private final SetPayloadPropertiesMutableObjectMetadata metadata;
   private Multimap<String, String> allHeaders = LinkedHashMultimap.create();

   @Inject
   public S3ObjectImpl(MutableObjectMetadata metadata) {
      super();
      this.metadata = linkMetadataToThis(metadata);
      this._metadata = this.metadata.getDelegate();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public MutableObjectMetadata getMetadata() {
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
   public int compareTo(S3Object o) {
      if (getMetadata().getKey() == null)
         return -1;
      return (this == o) ? 0 : getMetadata().getKey().compareTo(o.getMetadata().getKey());
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
      S3ObjectImpl other = (S3ObjectImpl) obj;
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
    * metadata link the new payload to the metadata object so that when content-related metadata is
    * updated on the payload, it is also copied the metadata object.
    */
   void linkPayloadToMetadata(Payload data) {
      if (data instanceof DelegatingPayload)
         super.setPayload(new SetMetadataPropertiesPayload(DelegatingPayload.class.cast(data)
                  .getDelegate(), _metadata));
      else
         super.setPayload(new SetMetadataPropertiesPayload(data, _metadata));
   }

   static class SetMetadataPropertiesPayload extends DelegatingPayload {

      private transient final MutableObjectMetadata metadata;

      public SetMetadataPropertiesPayload(Payload delegate, MutableObjectMetadata metadata) {
         super(delegate);
         this.metadata = checkNotNull(metadata, "metadata");
         if (metadata.getSize() != null)
            setContentLength(metadata.getSize());
         setContentMD5(metadata.getContentMD5());
         setContentType(metadata.getContentType());
      }

      @Override
      public void setContentLength(Long contentLength) {
         super.setContentLength(contentLength);
         try {
            metadata.setSize(contentLength);
         } catch (NullPointerException e) {
            e.printStackTrace();
         }
      }

      @Override
      public void setContentMD5(byte[] md5) {
         super.setContentMD5(md5);
         metadata.setContentMD5(md5);
      }

      @Override
      public void setContentType(String md5) {
         super.setContentType(md5);
         metadata.setContentType(md5);
      }

   }

   /**
    * link the metadata object to this so that when content-related metadata is updated, it is also
    * copied the currentpayload object.
    */
   SetPayloadPropertiesMutableObjectMetadata linkMetadataToThis(MutableObjectMetadata metadata) {
      return metadata instanceof DelegatingMutableObjectMetadata ? new SetPayloadPropertiesMutableObjectMetadata(
               DelegatingMutableObjectMetadata.class.cast(metadata).getDelegate(), this)
               : new SetPayloadPropertiesMutableObjectMetadata(metadata, this);
   }

   static class SetPayloadPropertiesMutableObjectMetadata extends DelegatingMutableObjectMetadata {
      /** The serialVersionUID */
      private static final long serialVersionUID = -5072270546219814521L;
      private transient final PayloadEnclosing object;

      public SetPayloadPropertiesMutableObjectMetadata(MutableObjectMetadata delegate,
               PayloadEnclosing object) {
         super(delegate);
         this.object = object;
      }

      @Override
      public void setContentMD5(byte[] md5) {
         super.setContentMD5(md5);
         if (canSetPayload())
            object.getPayload().setContentMD5(md5);
      }

      @Override
      public void setContentType(String type) {
         super.setContentType(type);
         if (canSetPayload())
            object.getPayload().setContentType(type);
      }

      @Override
      public void setSize(Long size) {
         super.setSize(size);
         if (canSetPayload())
            object.getPayload().setContentLength(size);
      }

      private boolean canSetPayload() {
         return object != null && object.getPayload() != null;
      }
   }

}
