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

import java.util.List;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class ContainerMetadataList {
   private String prefix;
   private String marker;
   private int maxResults;
   private List<ContainerMetadata> containerMetadata;
   private String nextMarker;

   @Override
   public String toString() {
      return "ContainerMetadataList [containerMetadata=" + containerMetadata + ", marker=" + marker
               + ", maxResults=" + maxResults + ", nextMarker=" + nextMarker + ", prefix=" + prefix
               + "]";
   }

   public ContainerMetadataList(String prefix, String marker, int maxResults,
            List<ContainerMetadata> containerMetadata, String nextMarker) {
      this.prefix = prefix;
      this.marker = marker;
      this.maxResults = maxResults;
      this.containerMetadata = containerMetadata;
      this.nextMarker = nextMarker;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((containerMetadata == null) ? 0 : containerMetadata.hashCode());
      result = prime * result + ((marker == null) ? 0 : marker.hashCode());
      result = prime * result + maxResults;
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
      ContainerMetadataList other = (ContainerMetadataList) obj;
      if (containerMetadata == null) {
         if (other.containerMetadata != null)
            return false;
      } else if (!containerMetadata.equals(other.containerMetadata))
         return false;
      if (marker == null) {
         if (other.marker != null)
            return false;
      } else if (!marker.equals(other.marker))
         return false;
      if (maxResults != other.maxResults)
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

   public void setPrefix(String prefix) {
      this.prefix = prefix;
   }

   public String getPrefix() {
      return prefix;
   }

   public void setMarker(String marker) {
      this.marker = marker;
   }

   public String getMarker() {
      return marker;
   }

   public void setMaxResults(int maxResults) {
      this.maxResults = maxResults;
   }

   public int getMaxResults() {
      return maxResults;
   }

   public void setContainerMetadata(List<ContainerMetadata> containerMetadata) {
      this.containerMetadata = containerMetadata;
   }

   public List<ContainerMetadata> getContainerMetadata() {
      return containerMetadata;
   }

   public void setNextMarker(String nextMarker) {
      this.nextMarker = nextMarker;
   }

   public String getNextMarker() {
      return nextMarker;
   }

}
