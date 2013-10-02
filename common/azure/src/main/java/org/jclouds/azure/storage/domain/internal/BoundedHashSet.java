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
package org.jclouds.azure.storage.domain.internal;

import java.net.URI;
import java.util.HashSet;

import org.jclouds.azure.storage.domain.BoundedSet;

import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class BoundedHashSet<T> extends HashSet<T> implements BoundedSet<T> {

   protected final URI url;
   protected final String prefix;
   protected final String marker;
   protected final Integer maxResults;
   protected final String nextMarker;

   public BoundedHashSet(Iterable<T> contents, URI url, String prefix, String marker,
            Integer maxResults, String nextMarker) {
      Iterables.addAll(this, contents);
      this.url = url;
      this.prefix = prefix;
      this.nextMarker = nextMarker;
      this.maxResults = maxResults;
      this.marker = marker;
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

   public String getNextMarker() {
      return nextMarker;
   }

   public URI getUrl() {
      return url;
   }

}
