/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.atmos.domain.internal;

import org.jclouds.atmos.domain.MutableContentMetadata;
import org.jclouds.io.ContentMetadataBuilder;
import org.jclouds.io.payloads.BaseMutableContentMetadata;

import com.google.common.collect.Multimap;

/**
 * 
 * @author Adrian Cole
 */
public class DelegatingMutableContentMetadata implements MutableContentMetadata {
   private String name;
   private final org.jclouds.io.MutableContentMetadata delegate;

   public DelegatingMutableContentMetadata() {
      this(null, new BaseMutableContentMetadata());
   }

   public DelegatingMutableContentMetadata(String name, org.jclouds.io.MutableContentMetadata delegate) {
      this.name = name;
      this.delegate = delegate;
   }

   @Override
   public Long getContentLength() {
      return delegate.getContentLength();
   }

   @Override
   public byte[] getContentMD5() {
      return delegate.getContentMD5();
   }

   @Override
   public String getContentType() {
      return delegate.getContentType();
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public void setContentLength(Long contentLength) {
      delegate.setContentLength(contentLength);
   }

   @Override
   public void setContentMD5(byte[] contentMD5) {
      delegate.setContentMD5(contentMD5);
   }

   @Override
   public void setContentType(String contentType) {
      delegate.setContentType(contentType);
   }

   @Override
   public void setName(String name) {
      this.name = name;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      DelegatingMutableContentMetadata other = (DelegatingMutableContentMetadata) obj;
      if (delegate == null) {
         if (other.delegate != null)
            return false;
      } else if (!delegate.equals(other.delegate))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      return true;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((delegate == null) ? 0 : delegate.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      return result;
   }

   @Override
   public String toString() {
      return "[name=" + name + ", delegate=" + delegate + "]";
   }

   public org.jclouds.io.MutableContentMetadata getDelegate() {
      return delegate;
   }

   @Override
   public void setContentDisposition(String contentDisposition) {
      delegate.setContentDisposition(contentDisposition);

   }

   @Override
   public void setContentEncoding(String contentEncoding) {
      delegate.setContentEncoding(contentEncoding);
   }

   @Override
   public void setContentLanguage(String contentLanguage) {
      delegate.setContentLanguage(contentLanguage);
   }

   @Override
   public String getContentDisposition() {
      return delegate.getContentDisposition();
   }

   @Override
   public String getContentEncoding() {
      return delegate.getContentEncoding();
   }

   @Override
   public String getContentLanguage() {
      return delegate.getContentLanguage();
   }

   @Override
   public void setPropertiesFromHttpHeaders(Multimap<String, String> headers) {
      delegate.setPropertiesFromHttpHeaders(headers);
   }

   @Override
   public ContentMetadataBuilder toBuilder() {
      return delegate.toBuilder();
   }

}
