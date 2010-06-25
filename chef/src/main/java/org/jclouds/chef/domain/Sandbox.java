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
package org.jclouds.chef.domain;

import java.net.URI;
import java.util.Map;

import com.google.common.collect.Maps;
import com.google.gson.annotations.SerializedName;

/**
 * Cookbook object.
 * 
 * @author Adrian Cole
 */
public class Sandbox {
   private URI uri;
   private Map<String, ChecksumStatus> checksums = Maps.newLinkedHashMap();
   @SerializedName("sandbox_id")
   private String id;

   public Sandbox(URI uri, Map<String, ChecksumStatus> checksums, String id) {
      this.uri = uri;
      this.checksums.putAll(checksums);
      this.id = id;
   }

   public Sandbox() {

   }

   public URI getUri() {
      return uri;
   }

   public Map<String, ChecksumStatus> getChecksums() {
      return checksums;
   }

   public String getId() {
      return id;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result
            + ((checksums == null) ? 0 : checksums.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((uri == null) ? 0 : uri.hashCode());
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
      Sandbox other = (Sandbox) obj;
      if (checksums == null) {
         if (other.checksums != null)
            return false;
      } else if (!checksums.equals(other.checksums))
         return false;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      if (uri == null) {
         if (other.uri != null)
            return false;
      } else if (!uri.equals(other.uri))
         return false;
      return true;
   }

   public static class ChecksumStatus {
      private URI url;
      @SerializedName("needs_upload")
      private boolean needsUpload;

      public ChecksumStatus(URI url, boolean needsUpload) {
         this.url = url;
         this.needsUpload = needsUpload;
      }

      public ChecksumStatus() {

      }

      public URI getUrl() {
         return url;
      }

      public boolean needsUpload() {
         return needsUpload;
      }

      @Override
      public String toString() {
         return "ChecksumStatus [needsUpload=" + needsUpload + ", url=" + url
               + "]";
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + (needsUpload ? 1231 : 1237);
         result = prime * result + ((url == null) ? 0 : url.hashCode());
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
         ChecksumStatus other = (ChecksumStatus) obj;
         if (needsUpload != other.needsUpload)
            return false;
         if (url == null) {
            if (other.url != null)
               return false;
         } else if (!url.equals(other.url))
            return false;
         return true;
      }
   }

   @Override
   public String toString() {
      return "Sandbox [checksums=" + checksums + ", id=" + id + ", uri=" + uri
            + "]";
   }

}