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
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Adrian Cole
 */
public class UploadSandbox {
   private URI uri;
   private Map<List<Byte>, ChecksumStatus> checksums = Maps.newLinkedHashMap();
   @SerializedName("sandbox_id")
   private String sandboxId;

   public UploadSandbox(URI uri, Map<List<Byte>, ChecksumStatus> checksums, String sandboxId) {
      this.uri = uri;
      this.checksums.putAll(checksums);
      this.sandboxId = sandboxId;
   }

   public UploadSandbox() {

   }

   public URI getUri() {
      return uri;
   }

   public Map<List<Byte>, ChecksumStatus> getChecksums() {
      return checksums;
   }

   public String getSandboxId() {
      return sandboxId;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((checksums == null) ? 0 : checksums.hashCode());
      result = prime * result + ((sandboxId == null) ? 0 : sandboxId.hashCode());
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
      UploadSandbox other = (UploadSandbox) obj;
      if (checksums == null) {
         if (other.checksums != null)
            return false;
      } else if (!checksums.equals(other.checksums))
         return false;
      if (sandboxId == null) {
         if (other.sandboxId != null)
            return false;
      } else if (!sandboxId.equals(other.sandboxId))
         return false;
      if (uri == null) {
         if (other.uri != null)
            return false;
      } else if (!uri.equals(other.uri))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "UploadSite [checksums=" + checksums + ", id=" + sandboxId + ", uri=" + uri + "]";
   }

}