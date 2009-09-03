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
   private int maxResults;
   private List<ContainerMetadata> containerMetadata;
   private String nextMarker;

   public ContainerMetadataList(int maxResults, List<ContainerMetadata> containerMetadata,
            String nextMarker) {
      this.maxResults = maxResults;
      this.containerMetadata = containerMetadata;
      this.nextMarker = nextMarker;
   }

   public int getMaxResults() {
      return maxResults;
   }

   public List<ContainerMetadata> getContainerMetadata() {
      return containerMetadata;
   }

   public String getNextMarker() {
      return nextMarker;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((containerMetadata == null) ? 0 : containerMetadata.hashCode());
      result = prime * result + maxResults;
      result = prime * result + ((nextMarker == null) ? 0 : nextMarker.hashCode());
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
      if (maxResults != other.maxResults)
         return false;
      if (nextMarker == null) {
         if (other.nextMarker != null)
            return false;
      } else if (!nextMarker.equals(other.nextMarker))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "ContainerMetadataList [containerMetadata=" + containerMetadata + ", maxResults="
               + maxResults + ", nextMarker=" + nextMarker + "]";
   }

}
