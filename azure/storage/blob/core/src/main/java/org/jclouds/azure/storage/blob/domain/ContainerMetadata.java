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

/**
 * 
 * @author Adrian Cole
 * 
 */
public class ContainerMetadata extends org.jclouds.blobstore.domain.ContainerMetadata {
   private URI url;
   private DateTime lastModified;
   private byte[] eTag;

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + Arrays.hashCode(eTag);
      result = prime * result + ((lastModified == null) ? 0 : lastModified.hashCode());
      result = prime * result + ((url == null) ? 0 : url.hashCode());
      return result;
   }

   @Override
   public String toString() {
      return "ContainerMetadata [eTag=" + Arrays.toString(eTag) + ", lastModified=" + lastModified
               + ", url=" + url + ", name=" + name + "]";
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
      if (lastModified == null) {
         if (other.lastModified != null)
            return false;
      } else if (!lastModified.equals(other.lastModified))
         return false;
      if (url == null) {
         if (other.url != null)
            return false;
      } else if (!url.equals(other.url))
         return false;
      return true;
   }

   public ContainerMetadata(URI url, DateTime lastModified, byte[] eTag) {
      super(url.getPath().substring(1));
      this.url = url;
      this.lastModified = lastModified;
      this.eTag = eTag;
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

}
