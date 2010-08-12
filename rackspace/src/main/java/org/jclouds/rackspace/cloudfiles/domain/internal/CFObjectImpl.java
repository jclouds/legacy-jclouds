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

package org.jclouds.rackspace.cloudfiles.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;

import org.jclouds.http.internal.PayloadEnclosingImpl;
import org.jclouds.io.Payload;
import org.jclouds.io.PayloadEnclosing;
import org.jclouds.io.payloads.DelegatingPayload;
import org.jclouds.rackspace.cloudfiles.domain.CFObject;
import org.jclouds.rackspace.cloudfiles.domain.MutableObjectInfoWithMetadata;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

/**
 * Default Implementation of {@link CFObject}.
 * 
 * @author Adrian Cole
 */
public class CFObjectImpl extends PayloadEnclosingImpl implements CFObject, Comparable<CFObject> {

   private final MutableObjectInfoWithMetadata _info;
   private final SetPayloadPropertiesMutableObjectInfoWithMetadata info;
   private Multimap<String, String> allHeaders = LinkedHashMultimap.create();

   @Inject
   public CFObjectImpl(MutableObjectInfoWithMetadata info) {
      super();
      this.info = linkMetadataToThis(info);
      this._info = this.info.getDelegate();

   }

   /**
    * {@inheritDoc}
    */
   @Override
   public MutableObjectInfoWithMetadata getInfo() {
      return info;
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
   public int compareTo(CFObject o) {
      if (getInfo().getName() == null)
         return -1;
      return (this == o) ? 0 : getInfo().getName().compareTo(o.getInfo().getName());
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((_info == null) ? 0 : _info.hashCode());
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
      CFObjectImpl other = (CFObjectImpl) obj;
      if (_info == null) {
         if (other._info != null)
            return false;
      } else if (!_info.equals(other._info))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[info=" + _info + "]";
   }

   @Override
   public void setPayload(Payload data) {
      linkPayloadToMetadata(data);
   }

   /**
    * link the new payload to the info object so that when content-related info is updated on the
    * payload, it is also copied the info object.
    */
   void linkPayloadToMetadata(Payload data) {
      if (data instanceof DelegatingPayload)
         super.setPayload(new SetMetadataPropertiesPayload(DelegatingPayload.class.cast(data)
                  .getDelegate(), _info));
      else
         super.setPayload(new SetMetadataPropertiesPayload(data, _info));
   }

   static class SetMetadataPropertiesPayload extends DelegatingPayload {

      private transient final MutableObjectInfoWithMetadata info;

      public SetMetadataPropertiesPayload(Payload delegate, MutableObjectInfoWithMetadata info) {
         super(delegate);
         this.info = info;
         setContentType(info.getContentType());
      }

      @Override
      public void setContentType(String md5) {
         super.setContentType(md5);
         info.setContentType(md5);
      }

   }

   /**
    * link the info object to this so that when content-related info is updated, it is also copied
    * the currentpayload object.
    */
   SetPayloadPropertiesMutableObjectInfoWithMetadata linkMetadataToThis(
            MutableObjectInfoWithMetadata info) {
      return info instanceof DelegatingMutableObjectInfoWithMetadata ? new SetPayloadPropertiesMutableObjectInfoWithMetadata(
               DelegatingMutableObjectInfoWithMetadata.class.cast(info).getDelegate(), this)
               : new SetPayloadPropertiesMutableObjectInfoWithMetadata(info, this);
   }

   static class SetPayloadPropertiesMutableObjectInfoWithMetadata extends
            DelegatingMutableObjectInfoWithMetadata {
      /** The serialVersionUID */
      private static final long serialVersionUID = -5072270546219814521L;
      private transient final PayloadEnclosing object;

      public SetPayloadPropertiesMutableObjectInfoWithMetadata(
               MutableObjectInfoWithMetadata delegate, PayloadEnclosing object) {
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
