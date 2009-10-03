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
import java.util.List;

import org.jclouds.azure.storage.domain.BoundedTreeSet;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class ArrayListBlobsResponse extends BoundedTreeSet<BlobMetadata> implements
         ListBlobsResponse {
   /** The serialVersionUID */
   private static final long serialVersionUID = -4475709781001190244L;
   protected final URI containerUrl;
   protected final String blobPrefix;
   protected final String delimiter;

   @Override
   public String toString() {
      return "ArrayListBlobsResponse [blobPrefix=" + blobPrefix + ", containerUrl=" + containerUrl
               + ", delimiter=" + delimiter + ", nextMarker=" + nextMarker + ", marker=" + marker
               + ", maxResults=" + maxResults + ", prefix=" + prefix + "]";
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((blobPrefix == null) ? 0 : blobPrefix.hashCode());
      result = prime * result + ((containerUrl == null) ? 0 : containerUrl.hashCode());
      result = prime * result + ((delimiter == null) ? 0 : delimiter.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      ArrayListBlobsResponse other = (ArrayListBlobsResponse) obj;
      if (blobPrefix == null) {
         if (other.blobPrefix != null)
            return false;
      } else if (!blobPrefix.equals(other.blobPrefix))
         return false;
      if (containerUrl == null) {
         if (other.containerUrl != null)
            return false;
      } else if (!containerUrl.equals(other.containerUrl))
         return false;
      if (delimiter == null) {
         if (other.delimiter != null)
            return false;
      } else if (!delimiter.equals(other.delimiter))
         return false;
      return true;
   }

   public ArrayListBlobsResponse(URI containerUrl, List<BlobMetadata> contents, String prefix,
            String marker, int maxResults, String nextMarker, String delimiter, String blobPrefix) {
      super(contents, prefix, marker, maxResults, nextMarker);
      this.containerUrl = containerUrl;
      this.delimiter = delimiter;
      this.blobPrefix = blobPrefix;
   }

   public String getBlobPrefix() {
      return blobPrefix;
   }

   public String getDelimiter() {
      return delimiter;
   }

   public URI getContainerUrl() {
      return containerUrl;
   }

}
