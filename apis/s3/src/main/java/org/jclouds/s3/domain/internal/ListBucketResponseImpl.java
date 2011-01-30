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

package org.jclouds.s3.domain.internal;

import java.util.LinkedHashSet;
import java.util.Set;

import org.jclouds.s3.domain.ListBucketResponse;
import org.jclouds.s3.domain.ObjectMetadata;

import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class ListBucketResponseImpl extends LinkedHashSet<ObjectMetadata> implements ListBucketResponse {
   /** The serialVersionUID */
   private static final long serialVersionUID = -4475709781001190244L;
   protected final String name;
   protected final String prefix;
   protected final int maxKeys;
   protected final String delimiter;
   protected final String marker;
   protected final String nextMarker;
   protected final Set<String> commonPrefixes;
   protected final boolean truncated;

   public ListBucketResponseImpl(String name, Iterable<ObjectMetadata> contents, String prefix, String marker,
            String nextMarker, int maxKeys, String delimiter, boolean isTruncated, Set<String> commonPrefixes) {
      Iterables.addAll(this, contents);
      this.name = name;
      this.prefix = prefix;
      this.marker = marker;
      this.nextMarker = nextMarker;
      this.maxKeys = maxKeys;
      this.truncated = isTruncated;
      this.delimiter = delimiter;
      this.commonPrefixes = commonPrefixes;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<String> getCommonPrefixes() {
      return commonPrefixes;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getDelimiter() {
      return delimiter;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getMarker() {
      return marker;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getNextMarker() {
      return nextMarker;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int getMaxKeys() {
      return maxKeys;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getPrefix() {
      return prefix;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isTruncated() {
      return truncated;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getName() {
      return name;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((commonPrefixes == null) ? 0 : commonPrefixes.hashCode());
      result = prime * result + ((delimiter == null) ? 0 : delimiter.hashCode());
      result = prime * result + ((marker == null) ? 0 : marker.hashCode());
      result = prime * result + maxKeys;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
      result = prime * result + (truncated ? 1231 : 1237);
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
      ListBucketResponseImpl other = (ListBucketResponseImpl) obj;
      if (commonPrefixes == null) {
         if (other.commonPrefixes != null)
            return false;
      } else if (!commonPrefixes.equals(other.commonPrefixes))
         return false;
      if (delimiter == null) {
         if (other.delimiter != null)
            return false;
      } else if (!delimiter.equals(other.delimiter))
         return false;
      if (marker == null) {
         if (other.marker != null)
            return false;
      } else if (!marker.equals(other.marker))
         return false;
      if (maxKeys != other.maxKeys)
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (prefix == null) {
         if (other.prefix != null)
            return false;
      } else if (!prefix.equals(other.prefix))
         return false;
      if (truncated != other.truncated)
         return false;
      return true;
   }

}
