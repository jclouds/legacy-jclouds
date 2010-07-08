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
package org.jclouds.azure.storage.blob.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;

import org.jclouds.azure.storage.blob.domain.AzureBlob;
import org.jclouds.azure.storage.blob.domain.MutableBlobProperties;
import org.jclouds.http.Payload;
import org.jclouds.http.PayloadEnclosing;
import org.jclouds.http.internal.PayloadEnclosingImpl;
import org.jclouds.http.payloads.DelegatingPayload;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

/**
 * Default Implementation of {@link AzureBlob}.
 * 
 * @author Adrian Cole
 */
public class AzureBlobImpl extends PayloadEnclosingImpl implements AzureBlob, Comparable<AzureBlob> {

   private final MutableBlobProperties _properties;
   private final SetPayloadPropertiesMutableBlobProperties properties;
   private Multimap<String, String> allHeaders = LinkedHashMultimap.create();

   @Inject
   public AzureBlobImpl(MutableBlobProperties properties) {
      super();
      this.properties = linkMetadataToThis(properties);
      this._properties = this.properties.getDelegate();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public MutableBlobProperties getProperties() {
      return properties;
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
   public int compareTo(AzureBlob o) {
      if (getProperties().getName() == null)
         return -1;
      return (this == o) ? 0 : getProperties().getName().compareTo(o.getProperties().getName());
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((_properties == null) ? 0 : _properties.hashCode());
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
      AzureBlobImpl other = (AzureBlobImpl) obj;
      if (_properties == null) {
         if (other._properties != null)
            return false;
      } else if (!_properties.equals(other._properties))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[properties=" + _properties + "]";
   }

   @Override
   public void setPayload(Payload data) {
      linkPayloadToMetadata(data);
   }

   /**
    * link the new payload to the properties object so that when content-related properties is
    * updated on the payload, it is also copied the properties object.
    */
   void linkPayloadToMetadata(Payload data) {
      if (data instanceof DelegatingPayload)
         super.setPayload(new SetMetadataPropertiesPayload(DelegatingPayload.class.cast(data)
                  .getDelegate(), _properties));
      else
         super.setPayload(new SetMetadataPropertiesPayload(data, _properties));
   }

   static class SetMetadataPropertiesPayload extends DelegatingPayload {

      private transient final MutableBlobProperties properties;

      public SetMetadataPropertiesPayload(Payload delegate, MutableBlobProperties properties) {
         super(delegate);
         this.properties = properties;
         if (properties.getContentLength() != null)
            setContentLength(properties.getContentLength());
         setContentMD5(properties.getContentMD5());
         setContentType(properties.getContentType());
      }

      @Override
      public void setContentLength(Long contentLength) {
         super.setContentLength(contentLength);
         properties.setContentLength(contentLength);
      }

      @Override
      public void setContentMD5(byte[] md5) {
         super.setContentMD5(md5);
         properties.setContentMD5(md5);
      }

      @Override
      public void setContentType(String md5) {
         super.setContentType(md5);
         properties.setContentType(md5);
      }

   }

   /**
    * link the properties object to this so that when content-related properties is updated, it is
    * also copied the currentpayload object.
    */
   SetPayloadPropertiesMutableBlobProperties linkMetadataToThis(MutableBlobProperties properties) {
      return properties instanceof DelegatingMutableBlobProperties ? new SetPayloadPropertiesMutableBlobProperties(
               DelegatingMutableBlobProperties.class.cast(properties).getDelegate(), this)
               : new SetPayloadPropertiesMutableBlobProperties(properties, this);
   }

   static class SetPayloadPropertiesMutableBlobProperties extends DelegatingMutableBlobProperties {
      /** The serialVersionUID */
      private static final long serialVersionUID = -5072270546219814521L;
      private transient final PayloadEnclosing blob;

      public SetPayloadPropertiesMutableBlobProperties(MutableBlobProperties delegate,
               PayloadEnclosing blob) {
         super(delegate);
         this.blob = blob;
      }

      @Override
      public void setContentMD5(byte[] md5) {
         super.setContentMD5(md5);
         if (canSetPayload())
            blob.getPayload().setContentMD5(md5);
      }

      @Override
      public void setContentType(String type) {
         super.setContentType(type);
         if (canSetPayload())
            blob.getPayload().setContentType(type);
      }

      @Override
      public void setContentLength(Long size) {
         super.setContentLength(size);
         if (canSetPayload())
            blob.getPayload().setContentLength(size);
      }

      private boolean canSetPayload() {
         return blob != null && blob.getPayload() != null;
      }
   }
}
