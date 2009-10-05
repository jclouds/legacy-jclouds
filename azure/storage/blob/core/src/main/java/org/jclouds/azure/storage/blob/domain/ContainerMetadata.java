/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 */
package org.jclouds.azure.storage.blob.domain;

import java.net.URI;
import java.util.Arrays;

import org.joda.time.DateTime;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class ContainerMetadata extends org.jclouds.blobstore.domain.ContainerMetadata {
   private URI url;
   private DateTime lastModified;
   private byte[] eTag;
   private Multimap<String, String> userMetadata = HashMultimap.create();

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + Arrays.hashCode(eTag);
      result = prime * result + ((getLastModified() == null) ? 0 : getLastModified().hashCode());
      result = prime * result + ((getUrl() == null) ? 0 : getUrl().hashCode());
      return result;
   }

   @Override
   public String toString() {
      return "ContainerMetadata [eTag=" + Arrays.toString(eTag) + ", lastModified="
               + getLastModified() + ", url=" + getUrl() + ", name=" + name + "]";
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      ContainerMetadata other = (ContainerMetadata) obj;
      if (!Arrays.equals(eTag, other.eTag))
         return false;
      if (getLastModified() == null) {
         if (other.getLastModified() != null)
            return false;
      } else if (!getLastModified().equals(other.getLastModified()))
         return false;
      if (getUrl() == null) {
         if (other.getUrl() != null)
            return false;
      } else if (!getUrl().equals(other.getUrl()))
         return false;
      return true;
   }

   public ContainerMetadata(URI url, DateTime lastModified, byte[] eTag) {
      super(url.getPath().substring(1));
      this.setUrl(url);
      this.lastModified = lastModified;
      this.eTag = eTag;
   }

   public ContainerMetadata() {
      super();
   }

   public ContainerMetadata(String name) {
      super(name);
   }

   public URI getUrl() {
      return url;
   }

   public DateTime getLastModified() {
      return lastModified;
   }

   public byte[] getETag() {
      return eTag;
   }

   public void setUserMetadata(Multimap<String, String> userMetadata) {
      this.userMetadata = userMetadata;
   }

   public Multimap<String, String> getUserMetadata() {
      return userMetadata;
   }

   public void setLastModified(DateTime lastModified) {
      this.lastModified = lastModified;
   }

   public void setETag(byte[] eTag) {
      this.eTag = eTag;
   }

   public void setUrl(URI url) {
      this.url = url;
   }

}
