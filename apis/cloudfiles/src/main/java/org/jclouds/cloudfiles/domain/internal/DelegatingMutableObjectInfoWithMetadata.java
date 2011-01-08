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

package org.jclouds.cloudfiles.domain.internal;

import java.util.Date;
import java.util.Map;

import org.jclouds.io.payloads.BaseMutableContentMetadata;
import org.jclouds.cloudfiles.domain.MutableObjectInfoWithMetadata;
import org.jclouds.cloudfiles.domain.ObjectInfo;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class DelegatingMutableObjectInfoWithMetadata extends BaseMutableContentMetadata implements
         MutableObjectInfoWithMetadata {
   /** The serialVersionUID */
   private static final long serialVersionUID = 5280642704532078500L;
   private final MutableObjectInfoWithMetadata delegate;

   public DelegatingMutableObjectInfoWithMetadata(MutableObjectInfoWithMetadata delegate) {
      this.delegate = delegate;
   }

   @Override
   public Long getContentLength() {
      return delegate.getBytes();
   }

   @Override
   public String getContentType() {
      return delegate.getContentType();
   }

   @Override
   public byte[] getContentMD5() {
      return delegate.getHash();
   }

   @Override
   public int hashCode() {
      return delegate.hashCode();
   }

   @Override
   public void setContentLength(Long bytes) {
      delegate.setBytes(bytes);
   }

   @Override
   public void setContentType(String contentType) {
      delegate.setContentType(contentType);
   }

   @Override
   public void setContentMD5(byte[] hash) {
      delegate.setHash(hash);
   }

   public MutableObjectInfoWithMetadata getDelegate() {
      return delegate;
   }

   @Override
   public Map<String, String> getMetadata() {
      return delegate.getMetadata();
   }

   @Override
   public void setBytes(Long bytes) {
      delegate.setBytes(bytes);
   }

   @Override
   public void setHash(byte[] hash) {
      delegate.setHash(hash);
   }

   @Override
   public void setLastModified(Date lastModified) {
      delegate.setLastModified(lastModified);
   }

   @Override
   public void setName(String name) {
      delegate.setName(name);
   }

   @Override
   public Long getBytes() {
     return  delegate.getBytes();
   }

   @Override
   public byte[] getHash() {
      return delegate.getHash();
   }

   @Override
   public Date getLastModified() {
      return delegate.getLastModified();
   }

   @Override
   public String getName() {
      return delegate.getName();
   }

   @Override
   public int compareTo(ObjectInfo o) {
      return delegate.compareTo(o);
   }
}
