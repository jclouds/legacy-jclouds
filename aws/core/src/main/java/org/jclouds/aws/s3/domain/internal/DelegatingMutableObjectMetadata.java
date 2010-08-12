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

package org.jclouds.aws.s3.domain.internal;

import java.util.Date;
import java.util.Map;

import org.jclouds.aws.s3.domain.CanonicalUser;
import org.jclouds.aws.s3.domain.MutableObjectMetadata;
import org.jclouds.aws.s3.domain.ObjectMetadata;

/**
 * 
 * @author Adrian Cole
 */
public class DelegatingMutableObjectMetadata implements MutableObjectMetadata {
   private final MutableObjectMetadata delegate;

   public DelegatingMutableObjectMetadata(MutableObjectMetadata delegate) {
      this.delegate = delegate;
   }

   @Override
   public int compareTo(ObjectMetadata o) {
      return delegate.compareTo(o);
   }

   @Override
   public boolean equals(Object obj) {
      return delegate.equals(obj);
   }

   @Override
   public String getCacheControl() {
      return delegate.getCacheControl();
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
   public String getKey() {
      return delegate.getKey();
   }

   @Override
   public Date getLastModified() {
      return delegate.getLastModified();
   }

   @Override
   public CanonicalUser getOwner() {
      return delegate.getOwner();
   }

   @Override
   public Long getSize() {
      return delegate.getSize();
   }

   @Override
   public StorageClass getStorageClass() {
      return delegate.getStorageClass();
   }

   @Override
   public Map<String, String> getUserMetadata() {
      return delegate.getUserMetadata();
   }

   @Override
   public int hashCode() {
      return delegate.hashCode();
   }

   @Override
   public void setCacheControl(String cacheControl) {
      delegate.setCacheControl(cacheControl);
   }

   @Override
   public void setContentDisposition(String contentDisposition) {
      delegate.setContentDisposition(contentDisposition);
   }

   @Override
   public void setContentEncoding(String encoding) {
      delegate.setContentEncoding(encoding);
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
   public void setKey(String key) {
      delegate.setKey(key);
   }

   @Override
   public void setLastModified(Date lastModified) {
      delegate.setLastModified(lastModified);
   }

   @Override
   public void setOwner(CanonicalUser owner) {
      delegate.setOwner(owner);
   }

   @Override
   public void setSize(Long size) {
      delegate.setSize(size);
   }

   @Override
   public void setStorageClass(StorageClass storageClass) {
      delegate.setStorageClass(storageClass);
   }

   @Override
   public void setUserMetadata(Map<String, String> userMetadata) {
      delegate.setUserMetadata(userMetadata);
   }

   @Override
   public String toString() {
      return delegate.toString();
   }

   public MutableObjectMetadata getDelegate() {
      return delegate;
   }

}