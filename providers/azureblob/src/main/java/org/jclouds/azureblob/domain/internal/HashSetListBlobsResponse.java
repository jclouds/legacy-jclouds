/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.azureblob.domain.internal;

import java.net.URI;
import java.util.Set;

import org.jclouds.azure.storage.domain.internal.BoundedHashSet;
import org.jclouds.azureblob.domain.BlobProperties;
import org.jclouds.azureblob.domain.ListBlobsResponse;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class HashSetListBlobsResponse extends BoundedHashSet<BlobProperties> implements
         ListBlobsResponse {

   protected final String delimiter;
   protected final Set<String> blobPrefixes = Sets.newHashSet();

   public HashSetListBlobsResponse(Iterable<BlobProperties> contents, URI url, String prefix,
            String marker, Integer maxResults, String nextMarker, String delimiter,
            Iterable<String> blobPrefixes) {
      super(contents, url, prefix, marker, maxResults, nextMarker);
      this.delimiter = delimiter;
      Iterables.addAll(this.blobPrefixes, blobPrefixes);
   }

   public String getDelimiter() {
      return delimiter;
   }

   public Set<String> getBlobPrefixes() {
      return blobPrefixes;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((blobPrefixes == null) ? 0 : blobPrefixes.hashCode());
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
      HashSetListBlobsResponse other = (HashSetListBlobsResponse) obj;
      if (blobPrefixes == null) {
         if (other.blobPrefixes != null)
            return false;
      } else if (!blobPrefixes.equals(other.blobPrefixes))
         return false;
      if (delimiter == null) {
         if (other.delimiter != null)
            return false;
      } else if (!delimiter.equals(other.delimiter))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[blobPrefixes=" + blobPrefixes + ", delimiter=" + delimiter
               + "]";
   }
}
