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

import java.io.Serializable;
import java.net.URI;
import java.util.Date;
import java.util.Map;

import org.jclouds.azure.storage.blob.domain.BlobProperties;
import org.jclouds.azure.storage.blob.domain.BlobType;
import org.jclouds.azure.storage.blob.domain.LeaseStatus;
import org.jclouds.azure.storage.blob.domain.MutableBlobProperties;

/**
 * Allows you to manipulate metadata.
 * 
 * @author Adrian Cole
 */
public class DelegatingMutableBlobProperties implements Serializable, MutableBlobProperties {

   /** The serialVersionUID */
   private static final long serialVersionUID = 9136392820647068818L;

   private final MutableBlobProperties delegate;

   public DelegatingMutableBlobProperties(MutableBlobProperties delegate) {
      this.delegate = delegate;
   }

   @Override
   public int compareTo(BlobProperties o) {
      return delegate.compareTo(o);
   }

   @Override
   public boolean equals(Object obj) {
      return delegate.equals(obj);
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
   public String getETag() {
      return delegate.getETag();
   }

   @Override
   public Date getLastModified() {
      return delegate.getLastModified();
   }

   @Override
   public LeaseStatus getLeaseStatus() {
      return delegate.getLeaseStatus();
   }

   @Override
   public Map<String, String> getMetadata() {
      return delegate.getMetadata();
   }

   @Override
   public String getName() {
      return delegate.getName();
   }

   @Override
   public BlobType getType() {
      return delegate.getType();
   }

   @Override
   public URI getUrl() {
      return delegate.getUrl();
   }

   @Override
   public int hashCode() {
      return delegate.hashCode();
   }

   @Override
   public void setContentEncoding(String encoding) {
      delegate.setContentEncoding(encoding);
   }

   @Override
   public void setContentLanguage(String contentLanguage) {
      delegate.setContentLanguage(contentLanguage);
   }

   @Override
   public void setContentLength(Long size) {
      delegate.setContentLength(size);
   }

   @Override
   public void setContentMD5(byte[] md5) {
      delegate.setContentMD5(md5);
   }

   @Override
   public void setContentType(String contentType) {
      delegate.setContentType(contentType);
   }

   @Override
   public void setETag(String eTag) {
      delegate.setETag(eTag);
   }

   @Override
   public void setLastModified(Date lastModified) {
      delegate.setLastModified(lastModified);
   }

   @Override
   public void setMetadata(Map<String, String> metadata) {
      delegate.setMetadata(metadata);
   }

   @Override
   public void setName(String name) {
      delegate.setName(name);
   }

   @Override
   public void setUrl(URI url) {
      delegate.setUrl(url);
   }

   @Override
   public String toString() {
      return delegate.toString();
   }

   public static long getSerialversionuid() {
      return serialVersionUID;
   }

   public MutableBlobProperties getDelegate() {
      return delegate;
   }

}