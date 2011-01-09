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

package org.jclouds.openstack.swift.domain;

/**
 * 
 * @author James Murty
 * 
 */
public class AccountMetadata {

   public AccountMetadata(long containerCount, long bytes) {
      this.containerCount = containerCount;
      this.bytes = bytes;
   }

   public AccountMetadata() {
   }

   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("ResourceMetadata [bytes=").append(bytes)
               .append(", containerCount=").append(containerCount).append("]");
      return builder.toString();
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (int) (bytes ^ (bytes >>> 32));
      result = prime * result + (int) (containerCount ^ (containerCount >>> 32));
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
      AccountMetadata other = (AccountMetadata) obj;
      if (bytes != other.bytes)
         return false;
      if (containerCount != other.containerCount)
         return false;
      return true;
   }

   private long containerCount;
   private long bytes;

   public void setContainerCount(long count) {
      this.containerCount = count;
   }

   public long getContainerCount() {
      return containerCount;
   }

   public void setBytes(long bytes) {
      this.bytes = bytes;
   }

   public long getBytes() {
      return bytes;
   }

}
