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
package org.jclouds.atmos.domain.internal;

import java.net.URI;
import java.util.Date;

import org.jclouds.atmos.domain.MutableContentMetadata;
import org.jclouds.io.ContentMetadataBuilder;
import org.jclouds.io.payloads.BaseMutableContentMetadata;

/**
 * 
 * @author Adrian Cole
 */
public class DelegatingMutableContentMetadata implements MutableContentMetadata {
   private URI uri;
   private String name;
   private String path;
   private final org.jclouds.io.MutableContentMetadata delegate;

   public DelegatingMutableContentMetadata() {
      this(null, null, null, new BaseMutableContentMetadata());
   }

   public DelegatingMutableContentMetadata(URI uri, String name, String path,
            org.jclouds.io.MutableContentMetadata delegate) {
      this.uri = uri;
      this.name = name;
      this.delegate = delegate;
      this.path = path;
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
      if (uri == null) {
         if (other.uri != null)
            return false;
      } else if (!uri.equals(other.uri))
         return false;
      return true;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((uri == null) ? 0 : uri.hashCode());
      return result;
   }

   @Override
   public String toString() {
      return "[uri=" + uri + ", name=" + name + ", path=" + path + ", delegate=" + delegate + "]";
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
   public void setExpires(Date expires) {
      delegate.setExpires(expires);
   }

   @Override
   public Date getExpires() {
      return delegate.getExpires();
   }

   @Override
   public ContentMetadataBuilder toBuilder() {
      return delegate.toBuilder();
   }

   @Override
   public URI getUri() {
      return uri;
   }

   @Override
   public void setUri(URI uri) {
      this.uri = uri;
   }

   @Override
   public String getPath() {
      return path;
   }

   @Override
   public void setPath(String path) {
      this.path = path;
   }

}
