/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.glesys.domain;

/**
 * Detailed information about an archive volume.
 * 
 * @author Adam Lowe
 * @see <a href= "https://customer.glesys.com/api.php?a=doc#archive_details" />
 */
public class ArchiveDetails extends Archive {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder extends Archive.Builder {
      public ArchiveDetails build() {
         return new ArchiveDetails(username, totalSize, freeSize, locked);
      }

      public Builder fromArchiveDetails(ArchiveDetails in) {
         return username(in.getUsername()).totalSize(in.getTotalSize()).freeSize(in.getFreeSize()).locked(in.isLocked());
      }

      @Override
      public Builder username(String username) {
         return Builder.class.cast(super.username(username));
      }

      @Override
      public Builder totalSize(String size) {
         return Builder.class.cast(super.totalSize(size));
      }

      @Override
      public Builder freeSize(String size) {
         return Builder.class.cast(super.freeSize(size));
      }
      
      @Override
      public Builder locked(boolean locked) {
         return Builder.class.cast(super.locked(locked));
      }
   }
   
   public ArchiveDetails(String username, String totalSize, String freeSize, boolean locked) {
      super(username, totalSize, freeSize, locked);
   }
}
