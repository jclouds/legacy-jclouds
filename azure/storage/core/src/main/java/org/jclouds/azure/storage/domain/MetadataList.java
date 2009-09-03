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
package org.jclouds.azure.storage.domain;

import java.util.List;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class MetadataList<T> {
   private final String prefix;
   private final String marker;
   private final int maxResults;
   private final List<T> metadata;
   private final String nextMarker;

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((marker == null) ? 0 : marker.hashCode());
      result = prime * result + maxResults;
      result = prime * result + ((metadata == null) ? 0 : metadata.hashCode());
      result = prime * result + ((nextMarker == null) ? 0 : nextMarker.hashCode());
      result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
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
      MetadataList<?> other = (MetadataList<?>) obj;
      if (marker == null) {
         if (other.marker != null)
            return false;
      } else if (!marker.equals(other.marker))
         return false;
      if (maxResults != other.maxResults)
         return false;
      if (metadata == null) {
         if (other.metadata != null)
            return false;
      } else if (!metadata.equals(other.metadata))
         return false;
      if (nextMarker == null) {
         if (other.nextMarker != null)
            return false;
      } else if (!nextMarker.equals(other.nextMarker))
         return false;
      if (prefix == null) {
         if (other.prefix != null)
            return false;
      } else if (!prefix.equals(other.prefix))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "MetadataList [metadata=" + metadata + ", marker=" + marker + ", maxResults="
               + maxResults + ", nextMarker=" + nextMarker + ", prefix=" + prefix + "]";
   }

   public MetadataList(String prefix, String marker, int maxResults, List<T> metadata,
            String nextMarker) {
      this.prefix = prefix;
      this.marker = marker;
      this.maxResults = maxResults;
      this.metadata = metadata;
      this.nextMarker = nextMarker;
   }

   public String getPrefix() {
      return prefix;
   }

   public String getMarker() {
      return marker;
   }

   public int getMaxResults() {
      return maxResults;
   }

   public List<T> getMetadata() {
      return metadata;
   }

   public String getNextMarker() {
      return nextMarker;
   }

}
