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
package org.jclouds.blobstore.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

import org.joda.time.DateTime;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

/**
 * System and user Metadata for the {@link Blob}.
 * 
 * @author Adrian Cole
 */
public class BlobMetadata implements Comparable<BlobMetadata>, Serializable {
   /** The serialVersionUID */
   private static final long serialVersionUID = -5932618957134612231L;

   protected String key;
   protected String eTag;
   protected volatile long size = -1;
   private byte[] contentMD5;

   protected Multimap<String, String> allHeaders = HashMultimap.create();
   protected Map<String, String> userMetadata = Maps.newHashMap();
   protected DateTime lastModified;
   protected String dataType = MediaType.APPLICATION_OCTET_STREAM;

   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("BlobMetadata [key=").append(key).append(", eTag=").append(eTag).append(
               ", lastModified=").append(lastModified).append(", size=").append(size).append(
               ", dataType=").append(dataType).append(", userMetadata=").append(userMetadata)
               .append("]");
      return builder.toString();
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((dataType == null) ? 0 : dataType.hashCode());
      result = prime * result + ((eTag == null) ? 0 : eTag.hashCode());
      result = prime * result + ((key == null) ? 0 : key.hashCode());
      result = prime * result + ((lastModified == null) ? 0 : lastModified.hashCode());
      result = prime * result + (int) (size ^ (size >>> 32));
      result = prime * result + ((userMetadata == null) ? 0 : userMetadata.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      BlobMetadata other = (BlobMetadata) obj;
      if (dataType == null) {
         if (other.dataType != null)
            return false;
      } else if (!dataType.equals(other.dataType))
         return false;
      if (eTag == null) {
         if (other.eTag != null)
            return false;
      } else if (!eTag.equals(other.eTag))
         return false;
      if (key == null) {
         if (other.key != null)
            return false;
      } else if (!key.equals(other.key))
         return false;
      if (lastModified == null) {
         if (other.lastModified != null)
            return false;
      } else if (!lastModified.equals(other.lastModified))
         return false;
      if (size != other.size)
         return false;
      if (userMetadata == null) {
         if (other.userMetadata != null)
            return false;
      } else if (!userMetadata.equals(other.userMetadata))
         return false;
      return true;
   }

   @Inject
   public BlobMetadata() {
      super();
   }

   /**
    * @param key
    * @see #getKey()
    */
   public BlobMetadata(String key) {
      setKey(key);
   }

   public void setKey(String key) {
      checkNotNull(key, "key");
      checkArgument(!key.startsWith("/"), "keys cannot start with /");
      this.key = key;
   }

   /**
    * The key is the handle that you assign to an object that allows you retrieve it later. A key is
    * a sequence of Unicode characters whose UTF-8 encoding is at most 1024 bytes long. Each object
    * in a bucket must have a unique key.
    * 
    * @see <a href= "http://docs.amazonwebservices.com/AmazonHTTP/2006-03-01/UsingKeys.html" />
    */
   public String getKey() {
      return key;
   }

   public DateTime getLastModified() {
      return lastModified;
   }

   public void setLastModified(DateTime lastModified) {
      this.lastModified = lastModified;
   }

   /**
    * The size of the object, in bytes.
    * 
    * @see <a href= "http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html?sec14.13." />
    */
   public long getSize() {
      return size;
   }

   public void setSize(long size) {
      this.size = size;
   }

   /**
    * A standard MIME type describing the format of the contents. If none is provided, the default
    * is binary/octet-stream.
    * 
    * @see <a href= "http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html?sec14.17." />
    */
   public String getContentType() {
      return dataType;
   }

   public void setContentType(String dataType) {
      this.dataType = dataType;
   }

   public void setContentMD5(byte[] contentMD5) {
      if (contentMD5 != null) {
         this.contentMD5 = new byte[contentMD5.length];
         System.arraycopy(contentMD5, 0, this.contentMD5, 0, contentMD5.length);
      }
   }

   public byte[] getContentMD5() {
      if (contentMD5 != null) {
         byte[] retval = new byte[contentMD5.length];
         System.arraycopy(this.contentMD5, 0, retval, 0, contentMD5.length);
         return retval;
      } else {
         return null;
      }
   }

   public void setETag(String eTag) {
      this.eTag = eTag;
   }

   /**
    * @return the eTag value stored in the Etag header returned by HTTP.
    */
   public String getETag() {
      return eTag;
   }

   public void setUserMetadata(Map<String, String> userMetadata) {
      this.userMetadata = userMetadata;
   }

   /**
    * Any key-value pairs associated with the object.
    */
   public Map<String, String> getUserMetadata() {
      return userMetadata;
   }

   public void setAllHeaders(Multimap<String, String> allHeaders) {
      this.allHeaders = allHeaders;
   }

   /**
    * @return all http response headers associated with this Value
    */
   public Multimap<String, String> getAllHeaders() {
      return allHeaders;
   }

   public int compareTo(BlobMetadata o) {
      return (this == o) ? 0 : getKey().compareTo(o.getKey());
   }
}