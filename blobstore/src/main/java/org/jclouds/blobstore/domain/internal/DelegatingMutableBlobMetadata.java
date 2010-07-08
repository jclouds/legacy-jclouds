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
package org.jclouds.blobstore.domain.internal;

import java.io.Serializable;
import java.net.URI;
import java.util.Date;
import java.util.Map;

import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.domain.Location;
import org.jclouds.domain.ResourceMetadata;

/**
 * 
 * @author Adrian Cole
 */
public class DelegatingMutableBlobMetadata implements MutableBlobMetadata, Serializable {
   /** The serialVersionUID */
   private static final long serialVersionUID = -2739517840958218727L;
   protected final MutableBlobMetadata delegate;

   public DelegatingMutableBlobMetadata(MutableBlobMetadata delegate) {
      this.delegate = delegate;
   }

   @Override
   public void setContentMD5(byte[] md5) {
      delegate.setContentMD5(md5);
   }

   @Override
   public void setContentType(String type) {
      delegate.setContentType(type);
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
   public void setETag(String eTag) {
      delegate.setETag(eTag);
   }

   @Override
   public void setLastModified(Date lastModified) {
      delegate.setLastModified(lastModified);
   }

   @Override
   public void setSize(Long size) {
      delegate.setSize(size);
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
   public String getName() {
      return delegate.getName();
   }

   @Override
   public String getProviderId() {
      return delegate.getProviderId();
   }

   @Override
   public Long getSize() {
      return delegate.getSize();
   }

   @Override
   public StorageType getType() {
      return delegate.getType();
   }

   @Override
   public URI getUri() {
      return delegate.getUri();
   }

   @Override
   public Map<String, String> getUserMetadata() {
      return delegate.getUserMetadata();
   }

   @Override
   public void setId(String id) {
      delegate.setId(id);
   }

   @Override
   public void setLocation(Location location) {
      delegate.setLocation(location);
   }

   @Override
   public void setName(String name) {
      delegate.setName(name);
   }

   @Override
   public void setType(StorageType type) {
      delegate.setType(type);
   }

   @Override
   public void setUri(URI url) {
      delegate.setUri(url);
   }

   @Override
   public void setUserMetadata(Map<String, String> userMetadata) {
      delegate.setUserMetadata(userMetadata);
   }

   @Override
   public Location getLocation() {
      return delegate.getLocation();
   }

   @Override
   public int compareTo(ResourceMetadata<StorageType> o) {
      return delegate.compareTo(o);
   }

   @Override
   public boolean equals(Object obj) {
      return delegate.equals(obj);
   }

   @Override
   public int hashCode() {
      return delegate.hashCode();
   }

   @Override
   public String toString() {
      return delegate.toString();
   }

   public MutableBlobMetadata getDelegate() {
      return delegate;
   }

}