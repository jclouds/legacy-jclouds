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
package org.jclouds.atmosonline.saas.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;

import org.jclouds.atmosonline.saas.domain.AtmosObject;
import org.jclouds.atmosonline.saas.domain.MutableContentMetadata;
import org.jclouds.atmosonline.saas.domain.SystemMetadata;
import org.jclouds.atmosonline.saas.domain.UserMetadata;
import org.jclouds.http.Payload;
import org.jclouds.http.PayloadEnclosing;
import org.jclouds.http.internal.PayloadEnclosingImpl;
import org.jclouds.http.payloads.DelegatingPayload;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

/**
 * Default Implementation of {@link AtmosObject}.
 * 
 * @author Adrian Cole
 */
public class AtmosObjectImpl extends PayloadEnclosingImpl implements AtmosObject,
         Comparable<AtmosObject> {
   private final UserMetadata userMetadata;
   private final SystemMetadata systemMetadata;

   public SystemMetadata getSystemMetadata() {
      return systemMetadata;
   }

   public UserMetadata getUserMetadata() {
      return userMetadata;
   }

   private final MutableContentMetadata _contentMetadata;
   private final SetPayloadPropertiesMutableContentMetadata contentMetadata;
   private Multimap<String, String> allHeaders = LinkedHashMultimap.create();

   public AtmosObjectImpl(MutableContentMetadata contentMetadata, SystemMetadata systemMetadata,
            UserMetadata userMetadata) {
      super();
      this.contentMetadata = linkMetadataToThis(contentMetadata);
      this._contentMetadata = this.contentMetadata.getDelegate();
      this.systemMetadata = systemMetadata;
      this.userMetadata = userMetadata;
   }

   @Inject
   public AtmosObjectImpl(MutableContentMetadata contentMetadata) {
      this(contentMetadata, null, new UserMetadata());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public MutableContentMetadata getContentMetadata() {
      return contentMetadata;
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
   public int compareTo(AtmosObject o) {
      if (getContentMetadata().getName() == null)
         return -1;
      return (this == o) ? 0 : getContentMetadata().getName().compareTo(
               o.getContentMetadata().getName());
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((_contentMetadata == null) ? 0 : _contentMetadata.hashCode());
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
      AtmosObjectImpl other = (AtmosObjectImpl) obj;
      if (_contentMetadata == null) {
         if (other._contentMetadata != null)
            return false;
      } else if (!_contentMetadata.equals(other._contentMetadata))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[contentMetadata=" + _contentMetadata + "]";
   }

   @Override
   public void setPayload(Payload data) {
      linkPayloadToMetadata(data);
   }

   /**
    * link the new payload to the contentMetadata object so that when content-related
    * contentMetadata is updated on the payload, it is also copied the contentMetadata object.
    */
   void linkPayloadToMetadata(Payload data) {
      if (data instanceof DelegatingPayload)
         super.setPayload(new SetMetadataPropertiesPayload(DelegatingPayload.class.cast(data)
                  .getDelegate(), _contentMetadata));
      else
         super.setPayload(new SetMetadataPropertiesPayload(data, _contentMetadata));
   }

   static class SetMetadataPropertiesPayload extends DelegatingPayload {

      private transient final MutableContentMetadata contentMetadata;

      public SetMetadataPropertiesPayload(Payload delegate, MutableContentMetadata contentMetadata) {
         super(delegate);
         this.contentMetadata = contentMetadata;
         setContentType(contentMetadata.getContentType());
      }

      @Override
      public void setContentType(String md5) {
         super.setContentType(md5);
         contentMetadata.setContentType(md5);
      }

   }

   /**
    * link the contentMetadata object to this so that when content-related contentMetadata is
    * updated, it is also copied the currentpayload object.
    */
   SetPayloadPropertiesMutableContentMetadata linkMetadataToThis(
            MutableContentMetadata contentMetadata) {
      return contentMetadata instanceof DelegatingMutableContentMetadata ? new SetPayloadPropertiesMutableContentMetadata(
               DelegatingMutableContentMetadata.class.cast(contentMetadata).getDelegate(), this)
               : new SetPayloadPropertiesMutableContentMetadata(contentMetadata, this);
   }

   static class SetPayloadPropertiesMutableContentMetadata extends DelegatingMutableContentMetadata {
      /** The serialVersionUID */
      private static final long serialVersionUID = -5072270546219814521L;
      private transient final PayloadEnclosing object;

      public SetPayloadPropertiesMutableContentMetadata(MutableContentMetadata delegate,
               PayloadEnclosing object) {
         super(delegate);
         this.object = object;
      }

      @Override
      public void setContentType(String type) {
         super.setContentType(type);
         if (canSetPayload())
            object.getPayload().setContentType(type);
      }

      private boolean canSetPayload() {
         return object != null && object.getPayload() != null;
      }
   }
}
